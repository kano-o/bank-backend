package de.kano.bankbackend


open class Account(
	accountNumber: Long,
	lastName: String,
	firstName: String,
	emailAddress: String,
	phoneNumber: String,
	balance: Double,
	createDate: Long
) {
	var accountNumber: Long = 0
	var lastName: String = "cum"
	var firstName: String = ""
	var emailAddress: String = ""
	var phoneNumber: String = ""
	var password: String = ""
	var balance: Double = 0.0
	var createDate: Long = 0

	override fun toString(): String {
		return "accountNumber: $accountNumber\n" +
				"lastName: $lastName\n" +
				"firstName: $firstName\n" +
				"emailAddress: $emailAddress\n" +
				"phoneNumber: $phoneNumber\n" +
				"balance: $balance"
	}
}
