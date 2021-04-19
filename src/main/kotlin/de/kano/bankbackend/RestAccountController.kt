package de.kano.bankbackend

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.sql.DriverManager
import java.sql.ResultSet
import org.springframework.http.ResponseEntity as ResponseEntity

@RestController
@RequestMapping("/account")
class RestAccountController {

    @PostMapping
    fun newAccount(@RequestBody newAccount: Account): ResponseEntity<Any> {

        if (newAccount.isInvalid()) {
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        }

        val newAccountNumber = createAccount(newAccount)

        return ResponseEntity<Any>(newAccountNumber, HttpStatus.CREATED)

    }
}
