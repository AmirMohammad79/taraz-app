package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AccountEntity
import com.example.data.local.BudgetEntity
import com.example.data.local.CATEGORY_CARD_TRANSFER
import com.example.data.local.TarazRepository
import com.example.data.local.TransactionEntity
import com.example.data.local.TransactionStatus
import com.example.data.local.WorkspaceScope
import com.example.domain.FinancialPlanReport
import com.example.domain.FinancialPlannerEngine
import com.example.domain.SavingsScenario
import com.example.util.CurrencyUnit
import com.example.util.ShamsiDate
import com.example.util.ShamsiDateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TarazDestination(val labelFa: String, val fullTitleFa: String) {
    DASHBOARD("خانه", "داشبورد تراز"),
    LEDGER("تراکنش‌ها", "دفتر کل تراکنش‌ها"),
    BUDGETS("بودجه", "مدیریت بودجه"),
    ACCOUNTS("کارت‌ها", "کارت‌های بانکی"),
}

enum class LedgerFilter(val labelFa: String) {
    ALL("همه"),
    INCOME("درآمدها"),
    EXPENSE("هزینه‌ها"),
    PENDING("در انتظار"),
    VERIFIED("تایید شده"),
}

enum class BudgetsSubTab(val labelFa: String) {
    PLANNER("برنامه‌ریز هوشمند"),
    LIMITS("سقف بودجه‌ها"),
}

data class BudgetProgressUi(
    val budget: BudgetEntity,
    val spentToman: Long,
    val ratio: Float,
)

data class AccountSummaryUi(
    val account: AccountEntity,
    val currentBalanceToman: Long,
    val transactionDeltaToman: Long,
    val monthlyInflowToman: Long,
    val monthlyOutflowToman: Long,
    val pendingCount: Int,
    val transactionCount: Int,
)

data class MonthlyCashFlowPoint(
    val year: Int,
    val month: Int,
    val monthLabel: String,
    val inflowToman: Long,
    val outflowToman: Long,
)

data class TarazUiState(
    val activeDestination: TarazDestination = TarazDestination.DASHBOARD,
    val workspaceFilter: WorkspaceScope = WorkspaceScope.ALL,
    val currencyUnit: CurrencyUnit = CurrencyUnit.TOMAN,
    val usePersianDigits: Boolean = true,
    val isSidebarCollapsed: Boolean = false,
    val currentShamsiDate: ShamsiDate = ShamsiDateUtils.nowShamsi(),
    val accounts: List<AccountEntity> = emptyList(),
    val accountSummaries: List<AccountSummaryUi> = emptyList(),
    val allAccountSummaries: List<AccountSummaryUi> = emptyList(),
    val budgets: List<BudgetProgressUi> = emptyList(),
    val allTransactions: List<TransactionEntity> = emptyList(),
    val filteredTransactions: List<TransactionEntity> = emptyList(),
    val cashFlowPoints: List<MonthlyCashFlowPoint> = emptyList(),
    val totalBalanceToman: Long = 0L,
    val monthlyInflowToman: Long = 0L,
    val monthlyOutflowToman: Long = 0L,
    val monthlyNetToman: Long = 0L,
    val balanceTrendPercent: Double = 0.0,
    val inflowTrendPercent: Double = 0.0,
    val outflowTrendPercent: Double = 0.0,
    val savingsRatePercent: Double = 0.0,
    val pendingCountTotal: Int = 0,
    val searchQuery: String = "",
    val ledgerFilter: LedgerFilter = LedgerFilter.ALL,
    val selectedCardFilterId: Long? = null,
    val selectedTransactionIds: Set<Long> = emptySet(),
    val editingTransaction: TransactionEntity? = null,
    val preselectedAccountId: Long? = null,
    val preselectedIsIncome: Boolean? = null,
    val showTransactionModal: Boolean = false,
    val editingBudget: BudgetEntity? = null,
    val showBudgetModal: Boolean = false,
    val editingAccount: AccountEntity? = null,
    val editingAccountDeltaToman: Long = 0L,
    val showAccountModal: Boolean = false,
    val showTransferModal: Boolean = false,
    val transferDefaultFromId: Long? = null,
    val targetSavingsPercent: Int = 25,
    val budgetsSubTab: BudgetsSubTab = BudgetsSubTab.PLANNER,
    val financialPlan: FinancialPlanReport? = null,
    val snackbarMessage: String? = null,
)

private data class TarazControlsState(
    val activeDestination: TarazDestination = TarazDestination.DASHBOARD,
    val workspaceFilter: WorkspaceScope = WorkspaceScope.ALL,
    val currencyUnit: CurrencyUnit = CurrencyUnit.TOMAN,
    val usePersianDigits: Boolean = true,
    val isSidebarCollapsed: Boolean = false,
    val searchQuery: String = "",
    val ledgerFilter: LedgerFilter = LedgerFilter.ALL,
    val selectedCardFilterId: Long? = null,
    val selectedTransactionIds: Set<Long> = emptySet(),
    val editingTransaction: TransactionEntity? = null,
    val preselectedAccountId: Long? = null,
    val preselectedIsIncome: Boolean? = null,
    val showTransactionModal: Boolean = false,
    val editingBudget: BudgetEntity? = null,
    val showBudgetModal: Boolean = false,
    val editingAccount: AccountEntity? = null,
    val editingAccountDeltaToman: Long = 0L,
    val showAccountModal: Boolean = false,
    val showTransferModal: Boolean = false,
    val transferDefaultFromId: Long? = null,
    val targetSavingsPercent: Int = 25,
    val budgetsSubTab: BudgetsSubTab = BudgetsSubTab.PLANNER,
    val snackbarMessage: String? = null,
)

class TarazViewModel(private val repository: TarazRepository) : ViewModel() {

    private val controls = MutableStateFlow(TarazControlsState())

    val uiState: StateFlow<TarazUiState> = combine(
        repository.accounts,
        repository.budgets,
        repository.transactions,
        controls,
    ) { accounts, budgets, transactions, ctrl ->
        val nowShamsi = ShamsiDateUtils.nowShamsi()
        val scope = ctrl.workspaceFilter

        val scopedAccounts = if (scope == WorkspaceScope.ALL) {
            accounts
        } else {
            accounts.filter { it.workspace == scope.name }
        }

        val scopedTransactions = if (scope == WorkspaceScope.ALL) {
            transactions
        } else {
            transactions.filter { it.workspace == scope.name }
        }

        val scopedBudgets = if (scope == WorkspaceScope.ALL) {
            budgets
        } else {
            budgets.filter { it.workspace == scope.name }
        }

        fun buildSummary(acc: AccountEntity): AccountSummaryUi {
            val accTx = transactions.filter { it.accountId == acc.id }
            val delta = accTx.sumOf { if (it.isIncome) it.amountToman else -it.amountToman }
            val monthAccTx = accTx.filter { it.shamsiYear == nowShamsi.year && it.shamsiMonth == nowShamsi.month }
            val monthIn = monthAccTx.filter { it.isIncome }.sumOf { it.amountToman }
            val monthOut = monthAccTx.filter { !it.isIncome }.sumOf { it.amountToman }
            val pending = accTx.count {
                it.status == TransactionStatus.PENDING.name || it.status == TransactionStatus.OVERDUE.name
            }
            return AccountSummaryUi(
                account = acc,
                currentBalanceToman = acc.initialBalanceToman + delta,
                transactionDeltaToman = delta,
                monthlyInflowToman = monthIn,
                monthlyOutflowToman = monthOut,
                pendingCount = pending,
                transactionCount = accTx.size,
            )
        }

        val allAccountSummaries = accounts.map(::buildSummary)
        val scopedIds = scopedAccounts.map { it.id }.toSet()
        val accountSummaries = allAccountSummaries.filter { it.account.id in scopedIds }

        val totalBalance = accountSummaries.sumOf { it.currentBalanceToman }

        // Exclude internal card-to-card transfers from monthly inflow/outflow KPIs
        val realScopedTransactions = scopedTransactions.filter { it.category != CATEGORY_CARD_TRANSFER }
        val currentMonthRealTx = realScopedTransactions.filter {
            it.shamsiYear == nowShamsi.year && it.shamsiMonth == nowShamsi.month
        }
        val monthlyInflow = currentMonthRealTx.filter { it.isIncome }.sumOf { it.amountToman }
        val monthlyOutflow = currentMonthRealTx.filter { !it.isIncome }.sumOf { it.amountToman }
        val monthlyNet = monthlyInflow - monthlyOutflow
        val savingsRate = if (monthlyInflow > 0L) {
            (monthlyNet.toDouble() / monthlyInflow.toDouble()) * 100.0
        } else {
            0.0
        }

        val recent6 = ShamsiDateUtils.recentMonths(nowShamsi.year, nowShamsi.month, 6)
        val cashFlowPoints = recent6.map { (y, m) ->
            val mTx = realScopedTransactions.filter { it.shamsiYear == y && it.shamsiMonth == m }
            MonthlyCashFlowPoint(
                year = y,
                month = m,
                monthLabel = ShamsiDate.MONTH_NAMES.getOrElse(m - 1) { "" },
                inflowToman = mTx.filter { it.isIncome }.sumOf { it.amountToman },
                outflowToman = mTx.filter { !it.isIncome }.sumOf { it.amountToman },
            )
        }

        val prevMonthPoint = cashFlowPoints.getOrNull(cashFlowPoints.size - 2)
        val inflowTrend = calcTrendPercent(monthlyInflow, prevMonthPoint?.inflowToman ?: 0L)
        val outflowTrend = calcTrendPercent(monthlyOutflow, prevMonthPoint?.outflowToman ?: 0L)
        val balanceTrend = if (totalBalance > 0L && monthlyNet != 0L) {
            (monthlyNet.toDouble() / totalBalance.toDouble()) * 100.0
        } else {
            0.0
        }

        val budgetProgress = scopedBudgets.map { budget ->
            val spent = currentMonthRealTx
                .filter { !it.isIncome && it.category == budget.category }
                .sumOf { it.amountToman }
            val ratio = if (budget.monthlyLimitToman > 0L) {
                (spent.toDouble() / budget.monthlyLimitToman.toDouble()).toFloat()
            } else {
                0f
            }
            BudgetProgressUi(
                budget = budget,
                spentToman = spent,
                ratio = ratio,
            )
        }

        val accountNameMap = accounts.associate { it.id to "${it.name} ${it.bankName}" }
        val query = ctrl.searchQuery.trim()
        val activeCardFilter = ctrl.selectedCardFilterId?.takeIf { id -> accounts.any { it.id == id } }
        val filteredTx = scopedTransactions.filter { tx ->
            val matchesCard = activeCardFilter == null || tx.accountId == activeCardFilter
            val matchesFilter = when (ctrl.ledgerFilter) {
                LedgerFilter.ALL -> true
                LedgerFilter.INCOME -> tx.isIncome
                LedgerFilter.EXPENSE -> !tx.isIncome
                LedgerFilter.PENDING -> tx.status == TransactionStatus.PENDING.name || tx.status == TransactionStatus.OVERDUE.name
                LedgerFilter.VERIFIED -> tx.status == TransactionStatus.VERIFIED.name
            }
            val matchesQuery = query.isEmpty() ||
                tx.title.contains(query, ignoreCase = true) ||
                tx.category.contains(query, ignoreCase = true) ||
                tx.note.contains(query, ignoreCase = true) ||
                tx.shamsiDateText.contains(query, ignoreCase = true) ||
                (accountNameMap[tx.accountId]?.contains(query, ignoreCase = true) == true)
            matchesCard && matchesFilter && matchesQuery
        }

        val pendingCountTotal = scopedTransactions.count {
            it.status == TransactionStatus.PENDING.name || it.status == TransactionStatus.OVERDUE.name
        }

        // Compute real per-category spending for current month + historical fallback income
        val categorySpends = currentMonthRealTx
            .filter { !it.isIncome }
            .groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amountToman } }

        val nonZeroPastInflows = cashFlowPoints.dropLast(1).map { it.inflowToman }.filter { it > 0L }
        val fallbackAverageInflow = if (nonZeroPastInflows.isNotEmpty()) {
            nonZeroPastInflows.sum() / nonZeroPastInflows.size
        } else {
            0L
        }

        val financialPlan = FinancialPlannerEngine.generatePlan(
            monthlyInflowToman = monthlyInflow,
            monthlyOutflowToman = monthlyOutflow,
            fallbackAverageInflowToman = fallbackAverageInflow,
            categorySpends = categorySpends,
            existingBudgets = scopedBudgets,
            totalCardBalanceToman = totalBalance,
            currentShamsiDate = nowShamsi,
            userSavingsPercent = ctrl.targetSavingsPercent,
            currencyUnit = ctrl.currencyUnit,
            usePersianDigits = ctrl.usePersianDigits,
        )

        TarazUiState(
            activeDestination = ctrl.activeDestination,
            workspaceFilter = ctrl.workspaceFilter,
            currencyUnit = ctrl.currencyUnit,
            usePersianDigits = ctrl.usePersianDigits,
            isSidebarCollapsed = ctrl.isSidebarCollapsed,
            currentShamsiDate = nowShamsi,
            accounts = accounts,
            accountSummaries = accountSummaries,
            allAccountSummaries = allAccountSummaries,
            budgets = budgetProgress,
            allTransactions = scopedTransactions,
            filteredTransactions = filteredTx,
            cashFlowPoints = cashFlowPoints,
            totalBalanceToman = totalBalance,
            monthlyInflowToman = monthlyInflow,
            monthlyOutflowToman = monthlyOutflow,
            monthlyNetToman = monthlyNet,
            balanceTrendPercent = balanceTrend,
            inflowTrendPercent = inflowTrend,
            outflowTrendPercent = outflowTrend,
            savingsRatePercent = savingsRate,
            pendingCountTotal = pendingCountTotal,
            searchQuery = ctrl.searchQuery,
            ledgerFilter = ctrl.ledgerFilter,
            selectedCardFilterId = activeCardFilter,
            selectedTransactionIds = ctrl.selectedTransactionIds,
            editingTransaction = ctrl.editingTransaction,
            preselectedAccountId = ctrl.preselectedAccountId,
            preselectedIsIncome = ctrl.preselectedIsIncome,
            showTransactionModal = ctrl.showTransactionModal,
            editingBudget = ctrl.editingBudget,
            showBudgetModal = ctrl.showBudgetModal,
            editingAccount = ctrl.editingAccount,
            editingAccountDeltaToman = ctrl.editingAccountDeltaToman,
            showAccountModal = ctrl.showAccountModal,
            showTransferModal = ctrl.showTransferModal,
            transferDefaultFromId = ctrl.transferDefaultFromId,
            targetSavingsPercent = ctrl.targetSavingsPercent,
            budgetsSubTab = ctrl.budgetsSubTab,
            financialPlan = financialPlan,
            snackbarMessage = ctrl.snackbarMessage,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = TarazUiState(),
    )

    private fun calcTrendPercent(current: Long, previous: Long): Double {
        if (previous <= 0L) return if (current > 0L) 100.0 else 0.0
        return ((current - previous).toDouble() / previous.toDouble()) * 100.0
    }

    fun selectDestination(destination: TarazDestination) {
        controls.update { it.copy(activeDestination = destination, selectedTransactionIds = emptySet()) }
    }

    fun openSmartPlanner() {
        controls.update {
            it.copy(
                activeDestination = TarazDestination.BUDGETS,
                budgetsSubTab = BudgetsSubTab.PLANNER,
                selectedTransactionIds = emptySet(),
            )
        }
    }

    fun setBudgetsSubTab(tab: BudgetsSubTab) {
        controls.update { it.copy(budgetsSubTab = tab) }
    }

    fun setTargetSavingsPercent(percent: Int) {
        controls.update { it.copy(targetSavingsPercent = percent.coerceIn(0, 100)) }
    }

    fun applyFinancialScenario(scenario: SavingsScenario) {
        val currentState = uiState.value
        val existingBudgets = currentState.budgets.map { it.budget }
        val defaultWs = if (currentState.workspaceFilter == WorkspaceScope.BUSINESS) {
            WorkspaceScope.BUSINESS.name
        } else {
            WorkspaceScope.PERSONAL.name
        }
        viewModelScope.launch {
            repository.applyScenarioBudgets(
                allocations = scenario.categoryAllocations,
                existingBudgets = existingBudgets,
                defaultWorkspace = defaultWs,
            )
            controls.update {
                it.copy(
                    budgetsSubTab = BudgetsSubTab.LIMITS,
                    snackbarMessage = "سقف بودجه‌ها مطابق «${scenario.titleFa}» تنظیم شد",
                )
            }
        }
    }

    fun setWorkspaceFilter(scope: WorkspaceScope) {
        controls.update { it.copy(workspaceFilter = scope, selectedTransactionIds = emptySet()) }
    }

    fun toggleCurrencyUnit() {
        controls.update {
            val next = if (it.currencyUnit == CurrencyUnit.TOMAN) CurrencyUnit.RIAL else CurrencyUnit.TOMAN
            it.copy(currencyUnit = next)
        }
    }

    fun setCurrencyUnit(unit: CurrencyUnit) {
        controls.update { it.copy(currencyUnit = unit) }
    }

    fun togglePersianDigits() {
        controls.update { it.copy(usePersianDigits = !it.usePersianDigits) }
    }

    fun toggleSidebarCollapsed() {
        controls.update { it.copy(isSidebarCollapsed = !it.isSidebarCollapsed) }
    }

    fun setSearchQuery(query: String) {
        controls.update { it.copy(searchQuery = query) }
    }

    fun setLedgerFilter(filter: LedgerFilter) {
        controls.update { it.copy(ledgerFilter = filter) }
    }

    fun setSelectedCardFilter(accountId: Long?) {
        controls.update { it.copy(selectedCardFilterId = accountId) }
    }

    fun openLedgerForCard(accountId: Long) {
        controls.update {
            it.copy(
                activeDestination = TarazDestination.LEDGER,
                selectedCardFilterId = accountId,
                ledgerFilter = LedgerFilter.ALL,
                selectedTransactionIds = emptySet(),
            )
        }
    }

    fun toggleTransactionSelection(id: Long) {
        controls.update { state ->
            val next = state.selectedTransactionIds.toMutableSet()
            if (!next.add(id)) next.remove(id)
            state.copy(selectedTransactionIds = next)
        }
    }

    fun clearTransactionSelection() {
        controls.update { it.copy(selectedTransactionIds = emptySet()) }
    }

    fun openNewTransactionModal(
        isIncome: Boolean? = null,
        accountId: Long? = null,
    ) {
        controls.update {
            it.copy(
                editingTransaction = null,
                preselectedIsIncome = isIncome,
                preselectedAccountId = accountId,
                showTransactionModal = true,
            )
        }
    }

    fun openEditTransactionModal(transaction: TransactionEntity) {
        controls.update {
            it.copy(
                editingTransaction = transaction,
                preselectedIsIncome = null,
                preselectedAccountId = null,
                showTransactionModal = true,
            )
        }
    }

    fun closeTransactionModal() {
        controls.update {
            it.copy(
                editingTransaction = null,
                preselectedIsIncome = null,
                preselectedAccountId = null,
                showTransactionModal = false,
            )
        }
    }

    fun openBudgetModal(budget: BudgetEntity? = null) {
        controls.update { it.copy(editingBudget = budget, showBudgetModal = true) }
    }

    fun closeBudgetModal() {
        controls.update { it.copy(editingBudget = null, showBudgetModal = false) }
    }

    fun openAccountModal(summary: AccountSummaryUi? = null) {
        controls.update {
            it.copy(
                editingAccount = summary?.account,
                editingAccountDeltaToman = summary?.transactionDeltaToman ?: 0L,
                showAccountModal = true,
            )
        }
    }

    fun closeAccountModal() {
        controls.update {
            it.copy(
                editingAccount = null,
                editingAccountDeltaToman = 0L,
                showAccountModal = false,
            )
        }
    }

    fun openTransferModal(fromAccountId: Long? = null) {
        controls.update {
            it.copy(
                showTransferModal = true,
                transferDefaultFromId = fromAccountId,
            )
        }
    }

    fun closeTransferModal() {
        controls.update {
            it.copy(
                showTransferModal = false,
                transferDefaultFromId = null,
            )
        }
    }

    fun clearSnackbar() {
        controls.update { it.copy(snackbarMessage = null) }
    }

    fun saveTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.saveTransaction(transaction)
            controls.update {
                it.copy(
                    editingTransaction = null,
                    preselectedAccountId = null,
                    preselectedIsIncome = null,
                    showTransactionModal = false,
                    snackbarMessage = if (transaction.id == 0L) {
                        if (transaction.isIncome) "درآمد ثبت و به موجودی کارت اضافه شد"
                        else "هزینه ثبت و از موجودی کارت کسر شد"
                    } else {
                        "تراکنش بروزرسانی شد"
                    },
                )
            }
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
            controls.update {
                it.copy(
                    editingTransaction = null,
                    showTransactionModal = false,
                    selectedTransactionIds = it.selectedTransactionIds - id,
                    snackbarMessage = "تراکنش حذف و موجودی کارت اصلاح شد",
                )
            }
        }
    }

    fun toggleTransactionStatus(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.toggleTransactionStatus(transaction)
        }
    }

    fun verifySelectedTransactions() {
        val ids = controls.value.selectedTransactionIds.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            repository.verifyTransactions(ids)
            controls.update {
                it.copy(
                    selectedTransactionIds = emptySet(),
                    snackbarMessage = "تراکنش‌های انتخاب‌شده تایید شدند",
                )
            }
        }
    }

    fun deleteSelectedTransactions() {
        val ids = controls.value.selectedTransactionIds.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            repository.deleteTransactions(ids)
            controls.update {
                it.copy(
                    selectedTransactionIds = emptySet(),
                    snackbarMessage = "تراکنش‌های انتخاب‌شده حذف شدند",
                )
            }
        }
    }

    fun saveBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.saveBudget(budget)
            controls.update {
                it.copy(
                    editingBudget = null,
                    showBudgetModal = false,
                    snackbarMessage = "سقف بودجه ذخیره شد",
                )
            }
        }
    }

    fun deleteBudget(id: Long) {
        viewModelScope.launch {
            repository.deleteBudget(id)
            controls.update { it.copy(snackbarMessage = "ردیف بودجه حذف شد") }
        }
    }

    fun saveAccount(account: AccountEntity) {
        viewModelScope.launch {
            repository.saveAccount(account)
            controls.update {
                it.copy(
                    editingAccount = null,
                    editingAccountDeltaToman = 0L,
                    showAccountModal = false,
                    snackbarMessage = if (account.id == 0L) "کارت بانکی جدید اضافه شد" else "اطلاعات و موجودی کارت بروزرسانی شد",
                )
            }
        }
    }

    fun deleteAccount(accountId: Long) {
        viewModelScope.launch {
            repository.deleteAccountWithTransactions(accountId)
            controls.update {
                it.copy(
                    editingAccount = null,
                    showAccountModal = false,
                    selectedCardFilterId = if (it.selectedCardFilterId == accountId) null else it.selectedCardFilterId,
                    snackbarMessage = "کارت و تراکنش‌های مرتبط با آن حذف شد",
                )
            }
        }
    }

    fun executeCardTransfer(
        fromAccount: AccountEntity,
        toAccount: AccountEntity,
        amountToman: Long,
        note: String,
    ) {
        viewModelScope.launch {
            repository.transferBetweenCards(fromAccount, toAccount, amountToman, note)
            controls.update {
                it.copy(
                    showTransferModal = false,
                    transferDefaultFromId = null,
                    snackbarMessage = "انتقال بین کارت‌ها با موفقیت انجام شد",
                )
            }
        }
    }

    fun reconcileAccount(accountId: Long) {
        viewModelScope.launch {
            repository.reconcileAccount(accountId)
            controls.update { it.copy(snackbarMessage = "مغایرت‌گیری کارت انجام شد") }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            controls.update {
                it.copy(
                    selectedCardFilterId = null,
                    selectedTransactionIds = emptySet(),
                    snackbarMessage = "همه داده‌های نمونه پاک شدند — آماده ثبت کارت‌های واقعی شما!",
                )
            }
        }
    }

    fun restoreSampleData() {
        viewModelScope.launch {
            repository.restoreSampleData()
            controls.update {
                it.copy(
                    selectedCardFilterId = null,
                    snackbarMessage = "داده‌های نمونه بازیابی شد",
                )
            }
        }
    }

    class Factory(private val repository: TarazRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TarazViewModel(repository) as T
        }
    }
}
