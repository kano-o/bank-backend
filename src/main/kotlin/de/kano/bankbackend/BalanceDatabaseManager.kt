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

fun depositBalance(accountNumber: Long, deposit: Double): Double {
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

        return statement2.resultSet.getDouble("balance")
    }
}

fun withdrawBalance(accountNumber: Long, withdrawal: Double): Double {
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

        return statement2.resultSet.getDouble("balance")
    }
}

fun transferBalance(withdrawAccountNumber: Long, depositAccountNumber: Long, transfer: Double): Double {
    val dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db")
    dbConnection.use {

        val statement = dbConnection.prepareStatement(
            "UPDATE accounts SET balance = balance - ? WHERE account_number = ?;" +
                    "UPDATE accounts SET balance = balance + ? WHERE account_number = ?"
        )
        statement.setDouble(1, transfer)
        statement.setLong(2, withdrawAccountNumber)
        statement.setDouble(3, transfer)
        statement.setLong(4, depositAccountNumber)
        statement.execute()

        val statement2 = dbConnection.prepareStatement(
            "SELECT balance FROM accounts WHERE account_number = ?;" +
                    "SELECT balance FROM accounts WHERE account_number = ?"
        )
        statement2.setLong(1, withdrawAccountNumber)
        statement2.setLong(2, depositAccountNumber)
        statement2.execute()


}
