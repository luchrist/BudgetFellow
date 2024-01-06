package de.christcoding.budgetfellow.data

class TransactionRepository(private val transactionDao: TransactionDao) {

    suspend fun addATransaction(transaction: Transaction) {
        transactionDao.addATransaction(transaction)
    }

    fun getAllTransactions() = transactionDao.getAllTransactions()

    fun getATransactionById(id: Long) = transactionDao.getATransactionById(id)

    suspend fun updateATransaction(transaction: Transaction) {
        transactionDao.updateATransaction(transaction)
    }

    suspend fun deleteATransaction(transaction: Transaction) {
        transactionDao.deleteATransaction(transaction)
    }
}