package de.kano.bankbackend.restController

import de.kano.bankbackend.databaseManager.checkEmailAddress
import de.kano.bankbackend.databaseManager.checkPhoneNumber
import de.kano.bankbackend.databaseManager.createAccount
import de.kano.bankbackend.databaseManager.getAccountFromAccountNumber
import de.kano.bankbackend.security.tokens.DatabaseTokenStore
import de.kano.bankbackend.security.tokens.Tokenstore
import de.kano.bankbackend.utils.emailIsValid
import de.kano.bankbackend.utils.passwordIsValid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/account")
class RestAccountController {
	var tokenStore: Tokenstore = DatabaseTokenStore()

	@PostMapping("/register")
	fun newAccount(@RequestBody input: HashMap<String, String>): ResponseEntity<Any> {

		if (!input.containsKey("lastName") &&
			!input.containsKey("firstName") &&
			!input.containsKey("emailAddress") &&
			!input.containsKey("phoneNumber") &&
			!input.containsKey("password")
		) {
			return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
		}

		if (!emailIsValid(input["emailAddress"]!!)) {
			return ResponseEntity<Any>("Invalid email-address", HttpStatus.BAD_REQUEST)
		}

		if (!passwordIsValid(input["password"]!!)) {
			return ResponseEntity<Any>("Invalid password", HttpStatus.BAD_REQUEST)
		}

		if (checkEmailAddress(input["emailAddress"]!!) == -1) {
			return ResponseEntity<Any>("Email address is already used", HttpStatus.BAD_REQUEST)
		}

		if (checkPhoneNumber(input["phoneNumber"]!!) == -1) {
			return ResponseEntity<Any>("Phone number is already used", HttpStatus.BAD_REQUEST)
		}

		val newAccountNumber = createAccount(
			input["lastName"]!!,
			input["firstName"]!!,
			input["emailAddress"]!!,
			input["phoneNumber"]!!,
			input["password"]!!,
			System.currentTimeMillis()
		)

		if (newAccountNumber == -1L) {
			return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
		}

		return ResponseEntity<Any>("New account created \nNr: $newAccountNumber", HttpStatus.CREATED)
	}


	@GetMapping("/profile")
	fun showProfile(@RequestHeader(value = "Token") token: String): ResponseEntity<Any> {
		if (!tokenStore.tokenIsValid(token)) {
			return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)
		}

		val accountNumber = tokenStore.getAccountNumberByToken(token)

		val showProfile = getAccountFromAccountNumber(accountNumber)

		return ResponseEntity<Any>(showProfile, HttpStatus.OK)
	}
}