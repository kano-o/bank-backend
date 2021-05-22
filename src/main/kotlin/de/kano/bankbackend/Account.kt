package de.kano.bankbackend

import java.util.*


class Account {
	var accountNumber: Long = 0
	var lastName: String? = null
	var firstName: String? = null
	var emailAddress: String? = null
	var phoneNumber: String? = null
	var balance = 0.0
	var createDate: Long? = null

	constructor() {}
	constructor(
		accountNumber: Long,
		lastName: String?,
		firstName: String?,
		emailAddress: String?,
		phoneNumber: String?,
		balance: Double,
		createDate: Long?
	) {
		this.accountNumber = accountNumber
		this.lastName = lastName
		this.firstName = firstName
		this.emailAddress = emailAddress
		this.phoneNumber = phoneNumber
		this.balance = balance
		this.createDate = createDate
	}

	override fun toString(): String {
		return "Account{" +
				"accountNumber=" + accountNumber +
				", lastName='" + lastName + '\'' +
				", firstName='" + firstName + '\'' +
				", emailAddress='" + emailAddress + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", balance=" + balance +
				", createDate=" + createDate +
				'}'
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Account) return false
		return accountNumber == other.accountNumber && other.balance.compareTo(balance) == 0 && lastName == other.lastName && firstName == other.firstName && emailAddress == other.emailAddress && phoneNumber == other.phoneNumber && createDate == other.createDate
	}

	override fun hashCode(): Int {
		return Objects.hash(
			accountNumber,
			lastName,
			firstName,
			emailAddress,
			phoneNumber,
			balance,
			createDate
		)
	}
}

