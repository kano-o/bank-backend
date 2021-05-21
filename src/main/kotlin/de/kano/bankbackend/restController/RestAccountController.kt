package de.kano.bankbackend.restController

import de.kano.bankbackend.databaseManager.createAccount
import de.kano.bankbackend.emailIsValid
import de.kano.bankbackend.passwordIsValid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/account")
class RestAccountController {

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

}
