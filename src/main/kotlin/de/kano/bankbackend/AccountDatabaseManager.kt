package de.kano.bankbackend

import java.sql.DriverManager


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
        val statement = dbConnection.prepareStatement(
            "INSERT INTO accounts (last_name, first_name, email_address, phone_number, password, balance, create_date) VALUES (?,?,?,?,?,?,?)"
        )
        statement.setString(1, lastName)
        statement.setString(2, firstName)
        statement.setString(3, emailAddress)
        statement.setString(4, phoneNumber)
        statement.setString(5, password)
        statement.setDouble(6, 0.0)
        statement.setLong(7, createDate)
        statement.execute()
        return statement.generatedKeys.getLong(1)
    }
}


fun createAccount(newAccount: Account): Long {
    return createAccount(
        newAccount.lastName,
        newAccount.firstName,
        newAccount.emailAddress,
        newAccount.phoneNumber,
        newAccount.password,
        System.currentTimeMillis()
    )
}

//    fun getAccountNumberFromEmailAddress(emailAddress: String): Long {
//
//        val dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db")
//        dbConnection.use {
//            val getNumberStatement = dbConnection.prepareStatement(
//                "SELECT account_number FROM accounts WHERE email_address = ?"
//            )
//
//            getNumberStatement.setString(1, emailAddress)
//            getNumberStatement.execute()
//
//            val accountNumberResultSet = getNumberStatement.resultSet
//
//            if (accountNumberResultSet.next()) {
//                return accountNumberResultSet.getLong("account_number")
//            }
//
//        }
//
//        return -1
//
//    }
//
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


