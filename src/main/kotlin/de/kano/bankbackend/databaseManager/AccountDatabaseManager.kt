package de.kano.bankbackend.databaseManager

import de.kano.bankbackend.Account
import de.kano.bankbackend.utils.*
import java.sql.Driver
import java.sql.DriverManager
import kotlin.random.Random


fun createAccount(
	lastName: String,
	firstName: String,
	emailAddress: String,
	phoneNumber: String,
	password: String,
	createDate: Long
): Long {
	val dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db")
	dbConnection.use {
		val salt = bytesToHex(getSalt())
		val hash = getHash(password, salt)

		val randomAccountNumber = Random.nextLong(50000000000, 56000000000)

		val checkAccountNumber = dbConnection.prepareStatement(
			"SELECT COUNT(*) FROM accounts WHERE account_number = ?"
		)
		checkAccountNumber.setLong(1, randomAccountNumber)
		checkAccountNumber.execute()

		if (checkAccountNumber.resultSet.getInt(1) == 1) {
			return -1
		}

		val statement = dbConnection.prepareStatement(
			"INSERT INTO accounts (account_number, last_name, first_name, email_address, phone_number, password, balance, create_date) VALUES (?,?,?,?,?,?,?,?)"
		)
		statement.setLong(1, randomAccountNumber)
		statement.setString(2, lastName)
		statement.setString(3, firstName)
		statement.setString(4, emailAddress)
		statement.setString(5, phoneNumber)
		statement.setString(6, hash)
		statement.setDouble(7, 0.0)
		statement.setLong(8, createDate)
		statement.execute()

		val insertSalt = dbConnection.prepareStatement(
			"INSERT INTO salts (account, salt) VALUES (?,?)"
		)

		insertSalt.setLong(1, randomAccountNumber)
		insertSalt.setString(2, salt)
		insertSalt.execute()

		return randomAccountNumber
	}
}

fun getAccountNumberFromEmailAddress(emailAddress: String): Long {

	val dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db")
	dbConnection.use {
		val getNumberStatement = dbConnection.prepareStatement(
			"SELECT account_number FROM accounts WHERE email_address = ?"
		)

		getNumberStatement.setString(1, emailAddress)
		getNumberStatement.execute()

		val accountNumberResultSet = getNumberStatement.resultSet

		if (accountNumberResultSet.next()) {
			return accountNumberResultSet.getLong("account_number")
		}
	}
	return -1
}

fun getAccountFromAccountNumber(accountNumber: Long): Account? {
	val dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db")
	dbConnection.use {
		val getAccountStatement = dbConnection.prepareStatement(
			"SELECT account_number, last_name, first_name, email_address, phone_number, balance, create_date FROM accounts WHERE account_number = ?"
		)

		getAccountStatement.setLong(1, accountNumber)
		getAccountStatement.execute()

		val accountResultSet = getAccountStatement.resultSet

		if (!accountResultSet.next()) {
			return null
		}

		return Account(
			accountResultSet.getLong("account_number"),
			accountResultSet.getString("last_name"),
			accountResultSet.getString("first_name"),
			accountResultSet.getString("email_address"),
			accountResultSet.getString("phone_number"),
			accountResultSet.getDouble("balance"),
			accountResultSet.getLong("create_date")
		)
	}
}

fun passwordMatches(accountNumber: Long, password: String): Boolean {
	val dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db")
	dbConnection.use {
		val getPasswordStatement = dbConnection.prepareStatement(
			"SELECT password FROM accounts WHERE account_number = ?"
		)
		getPasswordStatement.setLong(1, accountNumber)
		getPasswordStatement.execute()

		val getSaltStatement = dbConnection.prepareStatement(
			"SELECT salt FROM salts WHERE account = ?"
		)
		getSaltStatement.setLong(1, accountNumber)
		getSaltStatement.execute()

		val getPasswordResultSet = getPasswordStatement.resultSet
		val getSaltResultSet = getSaltStatement.resultSet
		val passwordHashed = getPasswordResultSet.getString("password")
		val salt = getSaltResultSet.getString("salt")

		return isExpectedPassword(password, salt, passwordHashed)
	}
}

fun changePassword(accountNumber: Long, newPassword: String) {
	val dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db")
	dbConnection.use {
		val changePasswordStatement = dbConnection.prepareStatement(
			"UPDATE accounts SET password = ? WHERE account_number = ?"
		)
		val changeSaltStatement = dbConnection.prepareStatement(
			"UPDATE salts SET salt = ? WHERE account = ?"
		)
		val newSalt = getBase64Salt()
		val newHash = getHash(newPassword, newSalt)

		changePasswordStatement.setString(1, newHash)
		changePasswordStatement.setLong(2, accountNumber)
		changePasswordStatement.execute()

		changeSaltStatement.setString(1, newSalt)
		changeSaltStatement.setLong(2, accountNumber)
		changeSaltStatement.execute()
	}
}

fun changeEmail(accountNumber: Long, newEmailAddress: String) {
	val dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db")
	dbConnection.use {
		val changeEmailStatement = dbConnection.prepareStatement(
			"UPDATE accounts SET email_address = ? WHERE account_number = ?"
		)
		changeEmailStatement.setString(1, newEmailAddress)
		changeEmailStatement.setLong(2, accountNumber)
		changeEmailStatement.execute()
	}
}

fun checkEmailAddress(emailAddress: String):Int {
	val dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db")
	dbConnection.use {
		val checkEmailStatement = dbConnection.prepareStatement(
			"SELECT COUNT(*) FROM accounts WHERE email_address = ?"
		)
		checkEmailStatement.setString(1, emailAddress)
		checkEmailStatement.execute()

		if (checkEmailStatement.resultSet.getInt(1) == 1) {
			return -1
		}

		return 1
	}
}

fun checkPhoneNumber(phoneNumber: String):Int {
	val dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db")
	dbConnection.use {
		val checkPhoneStatement = dbConnection.prepareStatement(
			"SELECT COUNT(*) FROM accounts WHERE phone_number = ?"
		)
		checkPhoneStatement.setString(1, phoneNumber)
		checkPhoneStatement.execute()

		if (checkPhoneStatement.resultSet.getInt(1) == 1) {
			return -1
		}

		return 1
	}
}