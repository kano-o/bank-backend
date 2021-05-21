package de.kano.bankbackend.utils

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*


fun getSalt(): ByteArray {
	val bytes = ByteArray(16)
	SecureRandom().nextBytes(bytes)
	return bytes
}

fun passwordIsValid(password: String): Boolean {
	return password.length >= 7 //more constraints follow (like must contain special chars etc.)
}

@Throws(NoSuchAlgorithmException::class)
fun isExpectedPassword(password: String, base64Salt: String, expectedHash: String): Boolean {
	return isExpectedPassword(password, Base64.getDecoder().decode(base64Salt), expectedHash)
}

@Throws(NoSuchAlgorithmException::class)
fun isExpectedPassword(password: String, salt: ByteArray, expectedHash: String): Boolean {
	return getHash(password, salt) == expectedHash
}

@Throws(NoSuchAlgorithmException::class)
fun getHash(input: String, base64Salt: String): String {
	return getHash(input, Base64.getDecoder().decode(base64Salt))
}

@Throws(NoSuchAlgorithmException::class)
fun getHash(input: String, salt: ByteArray): String {
	val digest: MessageDigest = MessageDigest.getInstance("SHA3-512")
	digest.update(input.toByteArray(StandardCharsets.UTF_8))
	digest.update(salt)
	val encodedHash: ByteArray = digest.digest()
	return bytesToHex(encodedHash)
}

@Throws(NoSuchAlgorithmException::class)
fun getHash(input: String): String {
	val digest: MessageDigest = MessageDigest.getInstance("SHA3-512")
	val encodedHash: ByteArray = digest.digest(input.toByteArray(StandardCharsets.UTF_8))
	return bytesToHex(encodedHash)
}

fun bytesToHex(hash: ByteArray): String {
	val hexString = StringBuilder(2 * hash.size)
	for (b in hash) {
		val hex = Integer.toHexString(0xff and b.toInt())
		if (hex.length == 1) {
			hexString.append('0')
		}
		hexString.append(hex)
	}
	return hexString.toString()
}