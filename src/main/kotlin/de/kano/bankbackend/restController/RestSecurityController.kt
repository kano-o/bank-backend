package de.kano.bankbackend.restController

import de.kano.bankbackend.Account
import de.kano.bankbackend.databaseManager.getAccountNumberFromEmailAddress
import de.kano.bankbackend.databaseManager.passwordMatches
import de.kano.bankbackend.security.tokens.DatabaseTokenStore
import de.kano.bankbackend.security.tokens.Tokenstore
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
		if (!input.containsKey("emailAddress") && !input.containsKey("oldPassword") && !input.containsKey("newPassword")) {
			return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
		}

		val accountNumber = getAccountNumberFromEmailAddress(input["emailAddress"]!!)
		if (accountNumber.toInt() == -1) {
			return ResponseEntity<Any>("Incorrect email address supplied", HttpStatus.BAD_REQUEST)
		}

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
}