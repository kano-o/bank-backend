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
    var lastName: String = ""
    var firstName: String = ""
    var emailAddress: String = ""
    var phoneNumber: String = ""
    var password: String = ""
    var balance: Double = 0.0
    var createDate: Long = 0

    override fun toString(): String {
        return "account number: $accountNumber\n" +
                "last name: $lastName\n" +
                "first name: $firstName\n" +
                "email address: $emailAddress\n" +
                "phone number: $phoneNumber\n" +
                "password: $password\n" +
                "balance: $balance"
    }

    fun isInvalid(): Boolean {
        return emailIsValid(emailAddress)
    }

}
