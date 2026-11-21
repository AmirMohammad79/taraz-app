package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

const val CATEGORY_CARD_TRANSFER = "انتقال بین کارت‌ها"

enum class WorkspaceScope(val labelFa: String) {
    ALL("همه فضاها"),
    PERSONAL("شخصی"),
    BUSINESS("کسب‌وکار"),
}

enum class AccountKind(val labelFa: String) {
    CHECKING("کارت بانکی"),
    SAVINGS("سپرده پس‌انداز"),
    CASH("کیف پول نقدی"),
    BUSINESS("کارت کسب‌وکار"),
}

enum class TransactionStatus(val labelFa: String) {
    VERIFIED("تایید شده"),
    PENDING("در انتظار"),
    OVERDUE("بیش از بودجه"),
}

enum class BudgetIconKey(val labelFa: String) {
    HOME("مسکن و اجاره"),
    FOOD("خوراک و خرید"),
    TRANSPORT("حمل‌ونقل"),
    TECH("نرم‌افزار و اشتراک"),
    HEALTH("بهداشت و درمان"),
    INVEST("سرمایه‌گذاری"),
    GENERAL("سایر هزینه‌ها"),
}

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val bankName: String,
    val accountKind: String, // AccountKind.name
    val ibanMasked: String,
    val initialBalanceToman: Long,
    val workspace: String, // WorkspaceScope.name (PERSONAL / BUSINESS)
    val lastReconciledDate: String,
    val cardNumber: String = "",
    val cardColorIndex: Int = 0,
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val category: String,
    val monthlyLimitToman: Long,
    val workspace: String, // WorkspaceScope.name
    val iconKey: String,   // BudgetIconKey.name
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val amountToman: Long,
    val isIncome: Boolean,
    val category: String,
    val accountId: Long,
    val workspace: String, // WorkspaceScope.name
    val status: String,    // TransactionStatus.name
    val shamsiDateText: String,
    val shamsiYear: Int,
    val shamsiMonth: Int,
    val timestampMs: Long,
    val note: String = "",
)
