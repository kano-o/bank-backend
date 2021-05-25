package de.kano.bankbackend.restController

import de.kano.bankbackend.Account
import de.kano.bankbackend.databaseManager.getAccountNumberFromEmailAddress
import de.kano.bankbackend.databaseManager.passwordMatches
import de.kano.bankbackend.security.tokens.DatabaseTokenStore
import de.kano.bankbackend.security.tokens.Tokenstore
import de.kano.bankbackend.utils.emailIsValid
import de.kano.bankbackend.utils.passwordIsValid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/security")
class RestSecurityController {
	var tokenStore: Tokenstore = DatabaseTokenStore()

	@PutMapping("/changePassword")
	fun changePassword(@RequestHeader(value = "Token") token: String, @RequestBody input: HashMap<String, String>): ResponseEntity<Any> {
		if (!tokenStore.tokenIsValid(token)) {
			return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)
		}

		if (!input.containsKey("oldPassword") && !input.containsKey("newPassword")) {
			return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
		}

		val accountNumber = tokenStore.getAccountNumberByToken(token)

		if (!passwordMatches(accountNumber, input["oldPassword"]!!)) {
			return ResponseEntity<Any>("Incorrect Email Address or Password supplied", HttpStatus.FORBIDDEN)
		}

		if (!passwordIsValid(input["newPassword"]!!)) {
			return ResponseEntity<Any>("New password is Invalid", HttpStatus.BAD_REQUEST)
		}

		tokenStore.invalidateAllAccountTokens(accountNumber)
		de.kano.bankbackend.databaseManager.changePassword(accountNumber, input["newPassword"]!!)
		return ResponseEntity<Any>(HttpStatus.OK)
	}

	@PutMapping("/changeEmailAddress")
	fun changeEmail(@RequestHeader(value = "Token") token : String, @RequestBody input: HashMap<String, String>): ResponseEntity<Any> {
		if (!tokenStore.tokenIsValid(token)) {
			return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)
		}

		if (!input.containsKey("newEmailAddress") && !input.containsKey("password")){
			return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)
		}

		val accountNumber = tokenStore.getAccountNumberByToken(token)

		if (!passwordMatches(accountNumber, input["password"]!!)) {
			return ResponseEntity<Any>("Incorrect password", HttpStatus.FORBIDDEN)
		}

		if (!emailIsValid(input["newEmailAddress"]!!)) {
			return ResponseEntity<Any>("New email address is invalid", HttpStatus.BAD_REQUEST)
		}

		tokenStore.invalidateAllAccountTokens(accountNumber)
		de.kano.bankbackend.databaseManager.changeEmail(accountNumber, input["newEmailAddress"]!!)
		return ResponseEntity<Any>(HttpStatus.OK)
	}
}