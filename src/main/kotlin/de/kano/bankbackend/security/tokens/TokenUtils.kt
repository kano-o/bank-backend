package de.kano.bankbackend.security.tokens

import java.security.SecureRandom
import java.util.*


private const val TOKEN_SIZE = 32 //how many bytes the token should consist of
fun generateToken(): String {
	val randomBytes = ByteArray(TOKEN_SIZE)
	SecureRandom().nextBytes(randomBytes)
	return Base64.getUrlEncoder().encodeToString(randomBytes)
}