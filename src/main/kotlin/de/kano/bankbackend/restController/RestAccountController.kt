package de.kano.bankbackend.restController

import de.kano.bankbackend.databaseManager.createAccount
import de.kano.bankbackend.databaseManager.getAccountFromAccountNumber
import de.kano.bankbackend.emailIsValid
import de.kano.bankbackend.utils.passwordIsValid
import de.kano.bankbackend.security.tokens.DatabaseTokenStore
import de.kano.bankbackend.security.tokens.Tokenstore
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/account")
class RestAccountController {
	var tokenStore: Tokenstore = DatabaseTokenStore()

	@PostMapping("/register")
	fun newAccount(@RequestBody input: HashMap<String, String>): ResponseEntity<Any> {

		if (!emailIsValid(input["emailAddress"]!!)) {
			return ResponseEntity<Any>("Invalid email-address", HttpStatus.BAD_REQUEST)
		}

		if (!passwordIsValid(input["password"]!!)) {
			return ResponseEntity<Any>("Invalid password", HttpStatus.BAD_REQUEST)
		}

		val newAccountNumber = createAccount(
			input["lastName"]!!,
			input["firstName"]!!,
			input["emailAddress"]!!,
			input["phoneNumber"]!!,
			input["password"]!!,
			System.currentTimeMillis()
		)

		return ResponseEntity<Any>("New account created \nNr: $newAccountNumber", HttpStatus.CREATED)
	}

	@GetMapping("/profile")
	fun showProfile(@RequestHeader (value = "Token") token: String): ResponseEntity<Any> {
		if (!tokenStore.tokenIsValid(token)) {
			return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)
		}

		val accountNumber = tokenStore.getAccountNumberByToken(token)

		val showProfile = getAccountFromAccountNumber(accountNumber)

		return ResponseEntity<Any>(showProfile, HttpStatus.OK)
	}
}