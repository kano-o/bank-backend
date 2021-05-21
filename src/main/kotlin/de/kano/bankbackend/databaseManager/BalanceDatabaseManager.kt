package de.kano.bankbackend

import java.sql.DriverManager

fun checkBalance(accountNumber: Long): Double {
	val dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db")
	dbConnection.use {
		val statement = dbConnection.prepareStatement(
			"SELECT balance FROM accounts WHERE account_number = ?"
		)
		statement.setLong(1, accountNumber)
		statement.execute()

		return statement.resultSet.getDouble("balance")
	}
}

fun depositBalance(accountNumber: Long, deposit: Double) {
	val dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db")
	dbConnection.use {
		val statement = dbConnection.prepareStatement(
			"UPDATE accounts SET balance = balance + ? WHERE account_number = ?"
		)
		statement.setDouble(1, deposit)
		statement.setLong(2, accountNumber)
		statement.execute()

		val statement2 = dbConnection.prepareStatement(
			"SELECT balance FROM accounts WHERE account_number = ?"
		)
		statement2.setLong(1, accountNumber)
		statement2.execute()
	}
}

fun withdrawBalance(accountNumber: Long, withdrawal: Double) {
	val dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db")
	dbConnection.use {

		val statement = dbConnection.prepareStatement(
			"UPDATE accounts SET balance = balance - ? WHERE account_number = ?"
		)
		statement.setDouble(1, withdrawal)
		statement.setLong(2, accountNumber)
		statement.execute()

		val statement2 = dbConnection.prepareStatement(
			"SELECT balance FROM accounts WHERE account_number = ?"
		)
		statement2.setLong(1, accountNumber)
		statement2.execute()
	}
}

fun transferBalance(withdrawAccountNumber: Long, depositAccountNumber: Long, transfer: Double) {
	depositBalance(depositAccountNumber, transfer)
	withdrawBalance(withdrawAccountNumber, transfer)
}
