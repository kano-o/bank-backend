package de.kano.bankbackend.security.controllers

import de.kano.bankbackend.databaseManager.getAccountNumberFromEmailAddress
import de.kano.bankbackend.databaseManager.passwordMatches
import de.kano.bankbackend.security.tokens.DatabaseTokenStore
import de.kano.bankbackend.security.tokens.Tokenstore
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.NoSuchAlgorithmException
import java.sql.SQLException


@RestController
@RequestMapping("/security/token")
class TokenController {
	@PostMapping(produces = [MediaType.TEXT_PLAIN_VALUE])
	fun createToken(@RequestBody loginDetails: HashMap<String, String>): ResponseEntity<*> {
		if (!loginDetails.containsKey("emailAddress") && !loginDetails.containsKey("password")) {
			return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
		}
		val emailAddress = loginDetails["emailAddress"]!!
		val password = loginDetails["password"]!!
		try {
			val accountNumber: Long = getAccountNumberFromEmailAddress(emailAddress)
			if (accountNumber == -1L) {
				return ResponseEntity<Any>(HttpStatus.NO_CONTENT)
			}
			if (passwordMatches(accountNumber, password)) {
				val newToken = tokenStore.createToken(accountNumber)
				return ResponseEntity(newToken, HttpStatus.OK)
			}
			return ResponseEntity("Incorrect email address or password", HttpStatus.FORBIDDEN)
		} catch (exception: SQLException) {
			exception.printStackTrace()
		} catch (exception: NoSuchAlgorithmException) {
			exception.printStackTrace()
		}
		return ResponseEntity<Any>(HttpStatus.INTERNAL_SERVER_ERROR)
	}

	@GetMapping(produces = [MediaType.TEXT_PLAIN_VALUE])
	@RequestMapping("/checkValid")
	fun tokenIsValid(@RequestHeader(value = "Token") token: String?): ResponseEntity<*> {
		return if (tokenStore.tokenIsValid(token!!)) {
			ResponseEntity(true, HttpStatus.OK)
		} else ResponseEntity(false, HttpStatus.OK)
	}

	companion object {
		private val tokenStore: Tokenstore = DatabaseTokenStore()
	}
}