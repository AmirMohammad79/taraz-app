package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.TarazDatabase
import com.example.data.local.TarazRepository
import com.example.ui.components.TarazFloatingBottomNavBar
import com.example.ui.components.TarazGlassBackdrop
import com.example.ui.components.TarazSidebarContent
import com.example.ui.components.TarazTopHeaderBar
import com.example.ui.screens.AccountModalDialog
import com.example.ui.screens.AccountsScreen
import com.example.ui.screens.BudgetModalDialog
import com.example.ui.screens.BudgetsScreen
import com.example.ui.screens.CardTransferModalDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LedgerScreen
import com.example.ui.screens.TransactionModalDialog
import com.example.ui.theme.TarazPrecisionTheme
import com.example.ui.viewmodel.TarazDestination
import com.example.ui.viewmodel.TarazUiState
import com.example.ui.viewmodel.TarazViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TarazPrecisionTheme {
                TarazAppRoot()
            }
        }
    }
}

@Composable
fun TarazAppRoot() {
    val context = LocalContext.current
    val repository = remember(context.applicationContext) {
        val db = TarazDatabase.getInstance(context.applicationContext)
        TarazRepository(db.tarazDao())
    }
    val viewModel: TarazViewModel = viewModel(factory = TarazViewModel.Factory(repository))
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Back button from secondary destinations returns cleanly to Dashboard
    BackHandler(enabled = uiState.activeDestination != TarazDestination.DASHBOARD) {
        viewModel.selectDestination(TarazDestination.DASHBOARD)
    }

    TarazGlassBackdrop(
        modifier = Modifier.fillMaxSize(),
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
        ) {
            val isExpanded = maxWidth >= 840.dp
            val isMedium = maxWidth in 600.dp..839.dp
            val isCompact = maxWidth < 600.dp

            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            if (isCompact) {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            drawerContainerColor = Color.Transparent,
                            modifier = Modifier.width(272.dp),
                        ) {
                            TarazSidebarContent(
                                activeDestination = uiState.activeDestination,
                                pendingCount = uiState.pendingCountTotal,
                                collapsed = false,
                                usePersianDigits = uiState.usePersianDigits,
                                workspaceFilter = uiState.workspaceFilter,
                                onSelectWorkspace = {
                                    viewModel.setWorkspaceFilter(it)
                                    scope.launch { drawerState.close() }
                                },
                                onSelectDestination = {
                                    viewModel.selectDestination(it)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
                            )
                        }
                    },
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding(),
                        ) {
                            TarazTopHeaderBar(
                                activeDestination = uiState.activeDestination,
                                shamsiDate = uiState.currentShamsiDate,
                                workspaceFilter = uiState.workspaceFilter,
                                currencyUnit = uiState.currencyUnit,
                                usePersianDigits = uiState.usePersianDigits,
                                showMenuButton = true,
                                isCompact = true,
                                onOpenMenu = { scope.launch { drawerState.open() } },
                                onSelectWorkspace = viewModel::setWorkspaceFilter,
                                onToggleCurrency = viewModel::toggleCurrencyUnit,
                                onNewTransaction = { viewModel.openNewTransactionModal() },
                            )
                            TarazScreenContent(
                                uiState = uiState,
                                isExpanded = false,
                                isCompact = true,
                                viewModel = viewModel,
                                modifier = Modifier.weight(1f),
                            )
                        }

                        // Snackbar floats cleanly above the Floating Glass Nav Dock
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 96.dp, start = 16.dp, end = 16.dp),
                        )

                        // Signature Floating Frosted-Glass Fintech Navigation Dock
                        TarazFloatingBottomNavBar(
                            activeDestination = uiState.activeDestination,
                            pendingCount = uiState.pendingCountTotal,
                            usePersianDigits = uiState.usePersianDigits,
                            onSelectDestination = viewModel::selectDestination,
                            onQuickAddTransaction = { viewModel.openNewTransactionModal() },
                            modifier = Modifier.align(Alignment.BottomCenter),
                        )
                    }
                }
            } else {
                // Tablet / Foldable / Desktop RTL persistent right sidebar + Main Canvas
                val sidebarWidth = if (isMedium || uiState.isSidebarCollapsed) 76.dp else 260.dp
                Scaffold(
                    containerColor = Color.Transparent,
                    contentWindowInsets = WindowInsets.safeDrawing,
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                ) { innerPadding ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {
                        TarazSidebarContent(
                            activeDestination = uiState.activeDestination,
                            pendingCount = uiState.pendingCountTotal,
                            collapsed = isMedium || uiState.isSidebarCollapsed,
                            usePersianDigits = uiState.usePersianDigits,
                            workspaceFilter = uiState.workspaceFilter,
                            onSelectWorkspace = viewModel::setWorkspaceFilter,
                            onSelectDestination = viewModel::selectDestination,
                            onToggleCollapse = if (isExpanded) viewModel::toggleSidebarCollapsed else null,
                            modifier = Modifier
                                .width(sidebarWidth)
                                .fillMaxHeight(),
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        ) {
                            TarazTopHeaderBar(
                                activeDestination = uiState.activeDestination,
                                shamsiDate = uiState.currentShamsiDate,
                                workspaceFilter = uiState.workspaceFilter,
                                currencyUnit = uiState.currencyUnit,
                                usePersianDigits = uiState.usePersianDigits,
                                showMenuButton = false,
                                isCompact = false,
                                onOpenMenu = {},
                                onSelectWorkspace = viewModel::setWorkspaceFilter,
                                onToggleCurrency = viewModel::toggleCurrencyUnit,
                                onNewTransaction = { viewModel.openNewTransactionModal() },
                            )
                            TarazScreenContent(
                                uiState = uiState,
                                isExpanded = isExpanded,
                                isCompact = false,
                                viewModel = viewModel,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }

    // Level 3 Modal Dialogs
    if (uiState.showTransactionModal) {
        TransactionModalDialog(
            initial = uiState.editingTransaction,
            preselectedIsIncome = uiState.preselectedIsIncome,
            preselectedAccountId = uiState.preselectedAccountId,
            accountSummaries = uiState.allAccountSummaries,
            currencyUnit = uiState.currencyUnit,
            usePersianDigits = uiState.usePersianDigits,
            onDismiss = viewModel::closeTransactionModal,
            onSave = viewModel::saveTransaction,
            onDelete = viewModel::deleteTransaction,
            onRequestAddCard = {
                viewModel.closeTransactionModal()
                viewModel.openAccountModal(null)
            },
        )
    }

    if (uiState.showBudgetModal) {
        BudgetModalDialog(
            initial = uiState.editingBudget,
            currencyUnit = uiState.currencyUnit,
            usePersianDigits = uiState.usePersianDigits,
            onDismiss = viewModel::closeBudgetModal,
            onSave = viewModel::saveBudget,
        )
    }

    if (uiState.showAccountModal) {
        AccountModalDialog(
            initial = uiState.editingAccount,
            existingTransactionDeltaToman = uiState.editingAccountDeltaToman,
            currencyUnit = uiState.currencyUnit,
            usePersianDigits = uiState.usePersianDigits,
            onDismiss = viewModel::closeAccountModal,
            onSave = viewModel::saveAccount,
        )
    }

    if (uiState.showTransferModal) {
        CardTransferModalDialog(
            accountSummaries = uiState.allAccountSummaries,
            defaultFromId = uiState.transferDefaultFromId,
            currencyUnit = uiState.currencyUnit,
            usePersianDigits = uiState.usePersianDigits,
            onDismiss = viewModel::closeTransferModal,
            onExecuteTransfer = viewModel::executeCardTransfer,
        )
    }
}

@Composable
private fun TarazScreenContent(
    uiState: TarazUiState,
    isExpanded: Boolean,
    isCompact: Boolean,
    viewModel: TarazViewModel,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Crossfade(
            targetState = uiState.activeDestination,
            label = "screen_crossfade",
        ) { destination ->
            when (destination) {
                TarazDestination.DASHBOARD -> DashboardScreen(
                    uiState = uiState,
                    isExpanded = isExpanded,
                    isCompact = isCompact,
                    onNavigate = viewModel::selectDestination,
                    onEditTransaction = viewModel::openEditTransactionModal,
                    onOpenNewTransaction = { isInc, accId -> viewModel.openNewTransactionModal(isInc, accId) },
                    onOpenAddCard = { viewModel.openAccountModal(null) },
                    onOpenTransfer = { viewModel.openTransferModal(null) },
                    onOpenCardLedger = viewModel::openLedgerForCard,
                    onOpenSmartPlanner = viewModel::openSmartPlanner,
                )
                TarazDestination.LEDGER -> LedgerScreen(
                    uiState = uiState,
                    isCompact = isCompact,
                    onSearchChange = viewModel::setSearchQuery,
                    onSelectFilter = viewModel::setLedgerFilter,
                    onSelectCardFilter = viewModel::setSelectedCardFilter,
                    onToggleSelection = viewModel::toggleTransactionSelection,
                    onClearSelection = viewModel::clearTransactionSelection,
                    onVerifySelected = viewModel::verifySelectedTransactions,
                    onDeleteSelected = viewModel::deleteSelectedTransactions,
                    onEditTransaction = viewModel::openEditTransactionModal,
                    onToggleStatus = viewModel::toggleTransactionStatus,
                    onDeleteTransaction = viewModel::deleteTransaction,
                    onOpenNewTransaction = { viewModel.openNewTransactionModal() },
                )
                TarazDestination.BUDGETS -> BudgetsScreen(
                    uiState = uiState,
                    isExpanded = isExpanded,
                    isCompact = isCompact,
                    onSelectSubTab = viewModel::setBudgetsSubTab,
                    onTargetSavingsPercentChange = viewModel::setTargetSavingsPercent,
                    onApplyScenario = viewModel::applyFinancialScenario,
                    onAddIncome = { viewModel.openNewTransactionModal(isIncome = true, accountId = null) },
                    onAddBudget = { viewModel.openBudgetModal(null) },
                    onEditBudget = { viewModel.openBudgetModal(it) },
                    onDeleteBudget = viewModel::deleteBudget,
                )
                TarazDestination.ACCOUNTS -> AccountsScreen(
                    uiState = uiState,
                    isExpanded = isExpanded,
                    isCompact = isCompact,
                    onAddAccount = { viewModel.openAccountModal(null) },
                    onEditAccount = { viewModel.openAccountModal(it) },
                    onDeleteAccount = viewModel::deleteAccount,
                    onExpenseFromCard = { cardId -> viewModel.openNewTransactionModal(isIncome = false, accountId = cardId) },
                    onIncomeToCard = { cardId -> viewModel.openNewTransactionModal(isIncome = true, accountId = cardId) },
                    onTransferFromCard = { cardId -> viewModel.openTransferModal(cardId) },
                    onViewCardLedger = viewModel::openLedgerForCard,
                    onReconcileAccount = viewModel::reconcileAccount,
                    onSetCurrencyUnit = viewModel::setCurrencyUnit,
                    onTogglePersianDigits = viewModel::togglePersianDigits,
                    onRestoreSampleData = viewModel::restoreSampleData,
                    onClearAllData = viewModel::clearAllData,
                )
            }
        }
    }
}
