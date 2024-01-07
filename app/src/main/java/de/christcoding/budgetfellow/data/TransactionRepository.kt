package de.christcoding.budgetfellow.data

class TransactionRepository(private val transactionDao: TransactionDao) {

    suspend fun addATransaction(transaction: Transaction): Long {
        return transactionDao.addATransaction(transaction)
    }

    fun getAllTransactions() = transactionDao.getAllTransactions()

    fun getATransactionById(id: Long) = transactionDao.getATransactionById(id)

    suspend fun updateATransaction(transaction: Transaction): Int {
        return transactionDao.updateATransaction(transaction)
    }

    suspend fun deleteATransaction(transaction: Transaction): Int {
        return transactionDao.deleteATransaction(transaction)
    }
}