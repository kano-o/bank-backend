package de.kano.bankbackend.security.tokens


import java.sql.DriverManager


class DatabaseTokenStore : Tokenstore {
	//Days, hours, minutes, seconds, milliseconds
	private val VALID_DURATION = 14 * 24 * 60 * 60 * 1000

	override fun getTokenByAccountNumber(accountId: Long): List<String> {
		val tokens: MutableList<String> = ArrayList()

		val dbConenction = DriverManager.getConnection("jdbc:sqlite:database.db")
		dbConenction.use {
			val getTokenStatement = dbConenction.prepareStatement(
				"SELECT * FROM tokens WHERE account_id = ?"
			)
			getTokenStatement.setLong(1, accountId)
			getTokenStatement.execute()

			val resultSetStatement = getTokenStatement.resultSet
			while (resultSetStatement.next()) {
				tokens.add(resultSetStatement.getString("token"))
			}
		}
		return tokens
	}

	override fun getTokensByEmail(emailAddress: String): List<String> {
		val tokens: MutableList<String> = ArrayList()

		val dbConenction = DriverManager.getConnection("jdbc:sqlite:database.db")
		dbConenction.use {
			val getTokenStatement = dbConenction.prepareStatement(
				"SELECT * FROM tokens WHERE account_id = (SELECT account_id FROM accounts WHERE email_address = ?)"
			)
			getTokenStatement.setString(1, emailAddress)
			getTokenStatement.execute()

			val resultSetStatement = getTokenStatement.resultSet
			while (resultSetStatement.next()) {
				tokens.add(resultSetStatement.getString("token"))
			}
			return tokens
		}
	}

	override fun createToken(accountId: Long): String {
		val dbConenction = DriverManager.getConnection("jdbc:sqlite:database.db")
		dbConenction.use {
			val newTokenStatment = dbConenction.prepareStatement(
				"INSERT INTO tokens (account_id, token, valid_until) VALUES (?, ?, ?)"
			)

			val newToken: String = generateToken()
			newTokenStatment.setLong(1, accountId)
			newTokenStatment.setString(2, newToken)
			newTokenStatment.setLong(3, System.currentTimeMillis() + VALID_DURATION)
			newTokenStatment.execute()

			return newToken
		}
	}

	override fun invalidateToken(token: String) {
		val dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db")
		dbConnection.use {
			val deleteTokenStatement = dbConnection.prepareStatement(
				"DELETE FROM tokens WHERE token = ?"
			)
			deleteTokenStatement.setString(1, token)
			deleteTokenStatement.execute()
		}
	}

	override fun invalidateAllAccountTokens(accountId: Long) {
		val dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db")
		dbConnection.use {
			val deleteAllTokenStatement = dbConnection.prepareStatement(
				"DELETE FROM tokens WHERE account_id = ?"
			)
			deleteAllTokenStatement.setLong(1, accountId)
			deleteAllTokenStatement.execute()
		}
	}

	override fun tokenIsValid(token: String): Boolean {
		val dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db")
		dbConnection.use {
			val checkTokenStatement = dbConnection.prepareStatement(
				"SELECT * FROM tokens WHERE token = ?"
			)
			checkTokenStatement.setString(1, token)
			checkTokenStatement.execute()

			val checkTokenResultSet = checkTokenStatement.resultSet
			if (checkTokenResultSet.next()) {
				val validUntil: Long = checkTokenResultSet.getLong("valid_until")
				if (System.currentTimeMillis() < validUntil) {
					return true
				}
			}

		}
		return false
	}

	override fun getAccountNumberByToken(token: String): Long {
		val dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db")
		dbConnection.use {
			val getAccountNumberStatement = dbConnection.prepareStatement(
				"SELECT account_id FROM tokens WHERE token = ?"
			)
			getAccountNumberStatement.setString(1, token)
			getAccountNumberStatement.execute()

			val getAccountNumberResultSet = getAccountNumberStatement.resultSet
			if (getAccountNumberResultSet.next()) {
				return getAccountNumberResultSet.getLong("account_id")
			}
		}
		return -1
	}
}