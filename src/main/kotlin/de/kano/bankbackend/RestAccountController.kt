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

    @PostMapping("/register")
    fun newAccount(@RequestBody input: HashMap<String, String>): ResponseEntity<Any> {

        if(!emailIsValid(input["emailAddress"]!!)) {
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        }

        val newAccountNumber = createAccount(input["lastName"]!!, input["firstName"]!!, input["emailAddress"]!!, input["phoneNumber"]!!, input["password"]!!, System.currentTimeMillis())

        return ResponseEntity<Any>(newAccountNumber, HttpStatus.CREATED)
    }

    //@PostMapping("/login")
    //fun login(@RequestBody )
}
