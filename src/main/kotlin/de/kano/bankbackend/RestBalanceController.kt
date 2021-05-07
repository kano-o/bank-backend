package de.kano.bankbackend

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity as ResponseEntity

@RestController
@RequestMapping("/balance")
class RestBalanceController {

    @PostMapping("/deposit")
    fun depositBalance(@RequestBody input: HashMap<String, String>): ResponseEntity<Any> {

        if (!input.containsKey("deposit") && !input.containsKey("accountNumber")) {
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        }

        val deposit = input["deposit"]!!.toDouble()
        val accountNumber = input["accountNumber"]!!.toLong()

        if (deposit <= 0) {
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        }

        depositBalance(accountNumber, deposit)

        return ResponseEntity<Any>(HttpStatus.OK)
    }

    @PostMapping("/withdrawal")
    fun withdrawBalance(@RequestBody input: HashMap<String, String>): ResponseEntity<Any> {

        if (!input.containsKey("withdrawal") && !input.containsKey("accountNumber")) {
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        }

        val withdrawal = input["withdrawal"]!!.toDouble()
        val accountNumber = input["accountNumber"]!!.toLong()

        if (withdrawal <= 0 && withdrawal <= checkBalance(accountNumber)) {
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        }

        withdrawBalance(accountNumber, withdrawal)

        return ResponseEntity<Any>(HttpStatus.OK)
    }

    @PostMapping("/transfer")
    fun transferBalance(@RequestBody input: HashMap<String, String>): ResponseEntity<Any> {

        if (!input.containsKey("transfer") && !input.containsKey("withdrawAccountNumber") && !input.containsKey("depositAccountNumber")) {
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        }

        val transfer = input["transfer"]!!.toDouble()
        val withdrawAccountNumber = input["withdrawAccountNumber"]!!.toLong()
        val depositAccountNumber = input["depositAccountNumber"]!!.toLong()

        if (transfer <= 0 && transfer <= checkBalance(withdrawAccountNumber)) {
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        }

        transferBalance(withdrawAccountNumber, depositAccountNumber, transfer)

        return ResponseEntity<Any>(HttpStatus.OK)
    }
}
