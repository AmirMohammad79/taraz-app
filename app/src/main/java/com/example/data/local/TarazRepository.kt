package com.example.data.local

import com.example.util.ShamsiDateUtils
import kotlinx.coroutines.flow.Flow

class TarazRepository(private val dao: TarazDao) {
    val accounts: Flow<List<AccountEntity>> = dao.observeAccounts()
    val budgets: Flow<List<BudgetEntity>> = dao.observeBudgets()
    val transactions: Flow<List<TransactionEntity>> = dao.observeTransactions()

    suspend fun saveTransaction(transaction: TransactionEntity) {
        if (transaction.id == 0L) {
            dao.insertTransaction(transaction)
        } else {
            dao.updateTransaction(transaction)
        }
    }

    suspend fun deleteTransaction(id: Long) {
        dao.deleteTransactionById(id)
    }

    suspend fun deleteTransactions(ids: List<Long>) {
        if (ids.isNotEmpty()) {
            dao.deleteTransactionsByIds(ids)
        }
    }

    suspend fun verifyTransactions(ids: List<Long>) {
        if (ids.isNotEmpty()) {
            dao.updateTransactionsStatus(ids, TransactionStatus.VERIFIED.name)
        }
    }

    suspend fun toggleTransactionStatus(transaction: TransactionEntity) {
        val next = when (transaction.status) {
            TransactionStatus.VERIFIED.name -> TransactionStatus.PENDING.name
            else -> TransactionStatus.VERIFIED.name
        }
        dao.updateTransaction(transaction.copy(status = next))
    }

    suspend fun saveBudget(budget: BudgetEntity) {
        if (budget.id == 0L) {
            dao.insertBudget(budget)
        } else {
            dao.updateBudget(budget)
        }
    }

    suspend fun deleteBudget(id: Long) {
        dao.deleteBudgetById(id)
    }

    suspend fun applyScenarioBudgets(
        allocations: List<com.example.domain.CategoryPlanAllocation>,
        existingBudgets: List<BudgetEntity>,
        defaultWorkspace: String,
    ) {
        val byCategory = existingBudgets.associateBy { it.category }
        allocations.forEach { alloc ->
            val targetLimit = alloc.recommendedLimitToman.coerceAtLeast(0L)
            val existing = byCategory[alloc.category]
            if (existing != null) {
                dao.updateBudget(existing.copy(monthlyLimitToman = targetLimit))
            } else if (targetLimit > 0L) {
                dao.insertBudget(
                    BudgetEntity(
                        category = alloc.category,
                        monthlyLimitToman = targetLimit,
                        workspace = defaultWorkspace,
                        iconKey = alloc.iconKey,
                    )
                )
            }
        }
    }

    suspend fun saveAccount(account: AccountEntity) {
        if (account.id == 0L) {
            dao.insertAccount(account)
        } else {
            dao.updateAccount(account)
        }
    }

    suspend fun deleteAccountWithTransactions(id: Long) {
        dao.deleteTransactionsByAccountId(id)
        dao.deleteAccountById(id)
    }

    suspend fun transferBetweenCards(
        fromAccount: AccountEntity,
        toAccount: AccountEntity,
        amountToman: Long,
        note: String,
    ) {
        val now = ShamsiDateUtils.nowShamsi()
        val nowMs = System.currentTimeMillis()
        val dateText = now.toFormattedSlash(usePersianDigits = true)
        val cleanNote = note.trim()

        val outgoing = TransactionEntity(
            title = "انتقال به ${toAccount.name}",
            amountToman = amountToman,
            isIncome = false,
            category = CATEGORY_CARD_TRANSFER,
            accountId = fromAccount.id,
            workspace = fromAccount.workspace,
            status = TransactionStatus.VERIFIED.name,
            shamsiDateText = dateText,
            shamsiYear = now.year,
            shamsiMonth = now.month,
            timestampMs = nowMs,
            note = cleanNote.ifEmpty { "کارت به کارت به ${toAccount.bankName}" },
        )
        val incoming = TransactionEntity(
            title = "واریز از ${fromAccount.name}",
            amountToman = amountToman,
            isIncome = true,
            category = CATEGORY_CARD_TRANSFER,
            accountId = toAccount.id,
            workspace = toAccount.workspace,
            status = TransactionStatus.VERIFIED.name,
            shamsiDateText = dateText,
            shamsiYear = now.year,
            shamsiMonth = now.month,
            timestampMs = nowMs + 1L,
            note = cleanNote.ifEmpty { "کارت به کارت از ${fromAccount.bankName}" },
        )
        dao.insertTransactions(listOf(outgoing, incoming))
    }

    suspend fun reconcileAccount(accountId: Long) {
        val today = ShamsiDateUtils.nowShamsi().toFormattedSlash(usePersianDigits = true)
        dao.reconcileAccountTransactions(accountId)
        dao.updateAccountReconciledDate(accountId, today)
    }

    suspend fun clearAllData() {
        dao.clearTransactions()
        dao.clearBudgets()
        dao.clearAccounts()
    }

    suspend fun restoreSampleData() {
        clearAllData()
        TarazDatabase.seedInitialData(dao)
    }
}
