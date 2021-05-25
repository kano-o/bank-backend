package de.kano.bankbackend.databaseManager

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
		val depositStatement = dbConnection.prepareStatement(
			"UPDATE accounts SET balance = balance + ? WHERE account_number = ?"
		)
		depositStatement.setDouble(1, deposit)
		depositStatement.setLong(2, accountNumber)
		depositStatement.execute()

		val historyStatement = dbConnection.prepareStatement(
			"INSERT INTO transaction_history (account_number, balance_change, description) VALUES (?,?,?)"
		)
		historyStatement.setLong(1, accountNumber)
		historyStatement.setDouble(2, deposit)
		historyStatement.setString(3, "deposit")
		historyStatement.execute()
	}
}

fun withdrawBalance(accountNumber: Long, withdrawal: Double) {
	val dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db")
	dbConnection.use {
		val withdrawStatement = dbConnection.prepareStatement(
			"UPDATE accounts SET balance = balance - ? WHERE account_number = ?"
		)
		withdrawStatement.setDouble(1, withdrawal)
		withdrawStatement.setLong(2, accountNumber)
		withdrawStatement.execute()

		val historyStatement = dbConnection.prepareStatement(
			"INSERT INTO transaction_history (account_number, balance_change, description) VALUES (?,?,?)"
		)
		historyStatement.setLong(1, accountNumber)
		historyStatement.setDouble(2, withdrawal)
		historyStatement.setString(3, "withdrawal")
		historyStatement.execute()
	}
}

fun transferBalance(withdrawAccountNumber: Long, depositAccountNumber: Long, transfer: Double) {
	val dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db")
	dbConnection.use {
		val withdrawStatement = dbConnection.prepareStatement(
			"UPDATE accounts SET balance = balance - ? WHERE account_number = ?"
		)
		withdrawStatement.setDouble(1, transfer)
		withdrawStatement.setLong(2, withdrawAccountNumber)
		withdrawStatement.execute()

		val depositStatement = dbConnection.prepareStatement(
			"UPDATE accounts SET balance = balance + ? WHERE account_number = ?"
		)
		depositStatement.setDouble(1, transfer)
		depositStatement.setLong(2, depositAccountNumber)
		depositStatement.execute()

		val historyStatement1 = dbConnection.prepareStatement(
			"INSERT INTO transaction_history (account_number, balance_change, description) VALUES (?,?,?)"
		)
		historyStatement1.setLong(1, withdrawAccountNumber)
		historyStatement1.setDouble(2, transfer * -1)
		historyStatement1.setString(3, "transferred to $depositAccountNumber")
		historyStatement1.execute()

		val historyStatement2 = dbConnection.prepareStatement(
			"INSERT INTO transaction_history (account_number, balance_change, description) VALUES (?,?,?)"
		)
		historyStatement2.setLong(1, depositAccountNumber)
		historyStatement2.setDouble(2, transfer)
		historyStatement2.setString(3, "received from $withdrawAccountNumber")
		historyStatement2.execute()
	}
}