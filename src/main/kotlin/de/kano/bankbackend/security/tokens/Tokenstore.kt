package de.kano.bankbackend.security.tokens

interface Tokenstore {
	fun getTokenByAccountNumber(accountId: Long): List<String?>
	fun getTokensByEmail(emailAddress: String): List<String?>
	fun createToken(accountId: Long): String
	fun invalidateToken(token: String)
	fun invalidateAllAccountTokens(accountId: Long)
	fun tokenIsValid(token: String): Boolean
	fun getAccountNumberByToken(token: String): Long
}
