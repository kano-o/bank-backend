package de.kano.bankbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BankBackendApplication


fun main(args: Array<String>) {
	runApplication<BankBackendApplication>(*args)
}