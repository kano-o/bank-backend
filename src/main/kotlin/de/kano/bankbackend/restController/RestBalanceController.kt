package de.kano.bankbackend.restController

import de.kano.bankbackend.checkBalance
import de.kano.bankbackend.security.tokens.DatabaseTokenStore
import de.kano.bankbackend.security.tokens.Tokenstore
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/balance")
class RestBalanceController {
	var tokenStore: Tokenstore = DatabaseTokenStore()

	@PostMapping("/deposit")
	fun depositBalance(@RequestHeader (value = "Token") token: String, @RequestBody input: HashMap<String, String>): ResponseEntity<Any> {
		if (!tokenStore.tokenIsValid(token)) {
			return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)
		}

		if (!input.containsKey("deposit") && !input.containsKey("accountNumber")) {
			return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
		}

		val deposit = input["deposit"]!!.toDouble()
		val accountNumber = input["accountNumber"]!!.toLong()

		if (deposit <= 0) {
			return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
		}

		de.kano.bankbackend.depositBalance(accountNumber, deposit)

		return ResponseEntity<Any>("Deposit complete", HttpStatus.OK)
	}

	@PostMapping("/withdrawal")
	fun withdrawBalance(@RequestHeader (value = "Token") token: String, @RequestBody input: HashMap<String, String>): ResponseEntity<Any> {
		if (!tokenStore.tokenIsValid(token)) {
			return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)
		}

		if (!input.containsKey("withdrawal") && !input.containsKey("accountNumber")) {
			return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
		}

		val withdrawal = input["withdrawal"]!!.toDouble()
		val accountNumber = input["accountNumber"]!!.toLong()

		if (withdrawal <= 0 && withdrawal <= checkBalance(accountNumber)) {
			return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
		}

		de.kano.bankbackend.withdrawBalance(accountNumber, withdrawal)

		return ResponseEntity<Any>("Withdrawal complete", HttpStatus.OK)
	}

	@PostMapping("/transfer")
	fun transferBalance(@RequestHeader (value = "Token") token: String, @RequestBody input: HashMap<String, String>): ResponseEntity<Any> {
		if (!tokenStore.tokenIsValid(token)) {
			return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)
		}

		if (!input.containsKey("transfer") && !input.containsKey("withdrawAccountNumber") && !input.containsKey("depositAccountNumber")) {
			return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
		}

		val transfer = input["transfer"]!!.toDouble()
		val withdrawAccountNumber = input["withdrawAccountNumber"]!!.toLong()
		val depositAccountNumber = input["depositAccountNumber"]!!.toLong()

		if (transfer <= 0 && transfer <= checkBalance(withdrawAccountNumber)) {
			return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
		}

		de.kano.bankbackend.transferBalance(withdrawAccountNumber, depositAccountNumber, transfer)

		return ResponseEntity<Any>("Transfer complete", HttpStatus.OK)
	}
}
