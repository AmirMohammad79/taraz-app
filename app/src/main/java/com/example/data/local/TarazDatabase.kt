package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.util.PersianFormatUtils
import com.example.util.ShamsiDateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [AccountEntity::class, BudgetEntity::class, TransactionEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class TarazDatabase : RoomDatabase() {
    abstract fun tarazDao(): TarazDao

    companion object {
        @Volatile
        private var INSTANCE: TarazDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE accounts ADD COLUMN cardNumber TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE accounts ADD COLUMN cardColorIndex INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getInstance(context: Context): TarazDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context.applicationContext).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(appContext: Context): TarazDatabase {
            lateinit var db: TarazDatabase
            db = Room.databaseBuilder(
                appContext,
                TarazDatabase::class.java,
                "taraz_precision.db",
            )
                .addMigrations(MIGRATION_1_2)
                .addCallback(object : Callback() {
                    override fun onCreate(sqliteDb: SupportSQLiteDatabase) {
                        super.onCreate(sqliteDb)
                        CoroutineScope(Dispatchers.IO).launch {
                            seedInitialData(db.tarazDao())
                        }
                    }
                })
                .build()
            return db
        }

        suspend fun seedInitialData(dao: TarazDao) {
            val now = ShamsiDateUtils.nowShamsi()
            val todaySlash = now.toFormattedSlash(usePersianDigits = true)
            val twoDaysAgo = PersianFormatUtils.toPersianDigits(
                "${now.year}/${now.month.toString().padStart(2, '0')}/${(now.day - 2).coerceAtLeast(1).toString().padStart(2, '0')}"
            )

            val accounts = listOf(
                AccountEntity(
                    name = "کارت جاری ملت",
                    bankName = "بانک ملت",
                    accountKind = AccountKind.CHECKING.name,
                    ibanMasked = "IR74 0120 •••• •••• 4821",
                    initialBalanceToman = 148_500_000L,
                    workspace = WorkspaceScope.PERSONAL.name,
                    lastReconciledDate = twoDaysAgo,
                    cardNumber = "6104 3379 •••• 4821",
                    cardColorIndex = 0,
                ),
                AccountEntity(
                    name = "کارت پس‌انداز سامان",
                    bankName = "بانک سامان",
                    accountKind = AccountKind.SAVINGS.name,
                    ibanMasked = "IR19 0560 •••• •••• 9034",
                    initialBalanceToman = 320_000_000L,
                    workspace = WorkspaceScope.PERSONAL.name,
                    lastReconciledDate = twoDaysAgo,
                    cardNumber = "6219 8619 •••• 9034",
                    cardColorIndex = 1,
                ),
                AccountEntity(
                    name = "کارت کسب‌وکار پاسارگاد",
                    bankName = "بانک پاسارگاد",
                    accountKind = AccountKind.BUSINESS.name,
                    ibanMasked = "IR52 0570 •••• •••• 1179",
                    initialBalanceToman = 215_000_000L,
                    workspace = WorkspaceScope.BUSINESS.name,
                    lastReconciledDate = todaySlash,
                    cardNumber = "5022 2910 •••• 1179",
                    cardColorIndex = 2,
                ),
                AccountEntity(
                    name = "کیف پول نقدی",
                    bankName = "صندوق نقدی",
                    accountKind = AccountKind.CASH.name,
                    ibanMasked = "CASH-WALLET-01",
                    initialBalanceToman = 14_200_000L,
                    workspace = WorkspaceScope.PERSONAL.name,
                    lastReconciledDate = todaySlash,
                    cardNumber = "کیف پول نقدی",
                    cardColorIndex = 3,
                ),
            )
            val accountIds = dao.insertAccounts(accounts)
            val mellatId = accountIds.getOrElse(0) { 1L }
            val samanId = accountIds.getOrElse(1) { 2L }
            val pasargadId = accountIds.getOrElse(2) { 3L }
            val cashId = accountIds.getOrElse(3) { 4L }

            val budgets = listOf(
                BudgetEntity(
                    category = "مسکن و اجاره",
                    monthlyLimitToman = 35_000_000L,
                    workspace = WorkspaceScope.PERSONAL.name,
                    iconKey = BudgetIconKey.HOME.name,
                ),
                BudgetEntity(
                    category = "خوراک و خرید",
                    monthlyLimitToman = 18_000_000L,
                    workspace = WorkspaceScope.PERSONAL.name,
                    iconKey = BudgetIconKey.FOOD.name,
                ),
                BudgetEntity(
                    category = "نرم‌افزار و اشتراک",
                    monthlyLimitToman = 12_000_000L,
                    workspace = WorkspaceScope.BUSINESS.name,
                    iconKey = BudgetIconKey.TECH.name,
                ),
                BudgetEntity(
                    category = "حمل‌ونقل",
                    monthlyLimitToman = 8_000_000L,
                    workspace = WorkspaceScope.PERSONAL.name,
                    iconKey = BudgetIconKey.TRANSPORT.name,
                ),
                BudgetEntity(
                    category = "بهداشت و درمان",
                    monthlyLimitToman = 10_000_000L,
                    workspace = WorkspaceScope.PERSONAL.name,
                    iconKey = BudgetIconKey.HEALTH.name,
                ),
                BudgetEntity(
                    category = "سرمایه‌گذاری",
                    monthlyLimitToman = 25_000_000L,
                    workspace = WorkspaceScope.PERSONAL.name,
                    iconKey = BudgetIconKey.INVEST.name,
                ),
            )
            dao.insertBudgets(budgets)

            val recentSixMonths = ShamsiDateUtils.recentMonths(now.year, now.month, 6)
            val nowMs = System.currentTimeMillis()
            val dayMs = 86_400_000L

            fun formatShamsi(y: Int, m: Int, d: Int): String =
                PersianFormatUtils.toPersianDigits("$y/${m.toString().padStart(2, '0')}/${d.toString().padStart(2, '0')}")

            val seedTransactions = mutableListOf<TransactionEntity>()

            val historicalInflows = listOf(84_000_000L, 91_500_000L, 88_000_000L, 102_000_000L, 96_500_000L)
            val historicalOutflows = listOf(58_000_000L, 62_400_000L, 67_200_000L, 61_500_000L, 59_800_000L)
            for (idx in 0 until 5) {
                val (y, m) = recentSixMonths[idx]
                val tsBase = nowMs - ((5 - idx) * 30L * dayMs)
                seedTransactions += TransactionEntity(
                    title = "تسویه قرارداد و حقوق ماهانه",
                    amountToman = historicalInflows[idx],
                    isIncome = true,
                    category = "درآمد اصلی",
                    accountId = if (idx % 2 == 0) mellatId else pasargadId,
                    workspace = if (idx % 2 == 0) WorkspaceScope.PERSONAL.name else WorkspaceScope.BUSINESS.name,
                    status = TransactionStatus.VERIFIED.name,
                    shamsiDateText = formatShamsi(y, m, 25),
                    shamsiYear = y,
                    shamsiMonth = m,
                    timestampMs = tsBase,
                    note = "واریز دوره‌ای حسابداری",
                )
                seedTransactions += TransactionEntity(
                    title = "مجموع هزینه‌های جاری ماه",
                    amountToman = historicalOutflows[idx],
                    isIncome = false,
                    category = "مسکن و اجاره",
                    accountId = mellatId,
                    workspace = WorkspaceScope.PERSONAL.name,
                    status = TransactionStatus.VERIFIED.name,
                    shamsiDateText = formatShamsi(y, m, 18),
                    shamsiYear = y,
                    shamsiMonth = m,
                    timestampMs = tsBase - dayMs,
                    note = "هزینه‌های ثابت و جاری",
                )
            }

            val cy = now.year
            val cm = now.month
            val cd = now.day

            seedTransactions += listOf(
                TransactionEntity(
                    title = "واریز حق‌الزحمه طراحی سیستم",
                    amountToman = 78_000_000L,
                    isIncome = true,
                    category = "پروژه و مشاوره",
                    accountId = pasargadId,
                    workspace = WorkspaceScope.BUSINESS.name,
                    status = TransactionStatus.VERIFIED.name,
                    shamsiDateText = formatShamsi(cy, cm, cd),
                    shamsiYear = cy,
                    shamsiMonth = cm,
                    timestampMs = nowMs - (2 * 3_600_000L),
                    note = "فاکتور شماره ۱۴۰۵-۴۱۲",
                ),
                TransactionEntity(
                    title = "اجاره دفتر و واحد مسکونی",
                    amountToman = 32_000_000L,
                    isIncome = false,
                    category = "مسکن و اجاره",
                    accountId = mellatId,
                    workspace = WorkspaceScope.PERSONAL.name,
                    status = TransactionStatus.VERIFIED.name,
                    shamsiDateText = formatShamsi(cy, cm, (cd - 1).coerceAtLeast(1)),
                    shamsiYear = cy,
                    shamsiMonth = cm,
                    timestampMs = nowMs - dayMs,
                    note = "پرداخت خودکار ماهانه",
                ),
                TransactionEntity(
                    title = "تمدید سرور ابری و اشتراک‌ها",
                    amountToman = 13_400_000L,
                    isIncome = false,
                    category = "نرم‌افزار و اشتراک",
                    accountId = pasargadId,
                    workspace = WorkspaceScope.BUSINESS.name,
                    status = TransactionStatus.OVERDUE.name,
                    shamsiDateText = formatShamsi(cy, cm, (cd - 2).coerceAtLeast(1)),
                    shamsiYear = cy,
                    shamsiMonth = cm,
                    timestampMs = nowMs - (2 * dayMs),
                    note = "بیش از سقف بودجه ماهانه نرم‌افزار",
                ),
                TransactionEntity(
                    title = "سود سپرده بانکی سامان",
                    amountToman = 42_500_000L,
                    isIncome = true,
                    category = "سرمایه‌گذاری",
                    accountId = samanId,
                    workspace = WorkspaceScope.PERSONAL.name,
                    status = TransactionStatus.VERIFIED.name,
                    shamsiDateText = formatShamsi(cy, cm, (cd - 3).coerceAtLeast(1)),
                    shamsiYear = cy,
                    shamsiMonth = cm,
                    timestampMs = nowMs - (3 * dayMs),
                    note = "واریز سود دوره‌ای",
                ),
                TransactionEntity(
                    title = "خرید ماهانه فروشگاه شهروند",
                    amountToman = 14_850_000L,
                    isIncome = false,
                    category = "خوراک و خرید",
                    accountId = mellatId,
                    workspace = WorkspaceScope.PERSONAL.name,
                    status = TransactionStatus.PENDING.name,
                    shamsiDateText = formatShamsi(cy, cm, (cd - 4).coerceAtLeast(1)),
                    shamsiYear = cy,
                    shamsiMonth = cm,
                    timestampMs = nowMs - (4 * dayMs),
                    note = "نیاز به تطبیق با رسید پایانه",
                ),
                TransactionEntity(
                    title = "انتقال به صندوق ETF درآمد ثابت",
                    amountToman = 18_000_000L,
                    isIncome = false,
                    category = "سرمایه‌گذاری",
                    accountId = samanId,
                    workspace = WorkspaceScope.PERSONAL.name,
                    status = TransactionStatus.VERIFIED.name,
                    shamsiDateText = formatShamsi(cy, cm, (cd - 5).coerceAtLeast(1)),
                    shamsiYear = cy,
                    shamsiMonth = cm,
                    timestampMs = nowMs - (5 * dayMs),
                    note = "پس‌انداز برنامه‌ریزی‌شده",
                ),
                TransactionEntity(
                    title = "هزینه سوخت و سرویس خودرو",
                    amountToman = 4_200_000L,
                    isIncome = false,
                    category = "حمل‌ونقل",
                    accountId = cashId,
                    workspace = WorkspaceScope.PERSONAL.name,
                    status = TransactionStatus.VERIFIED.name,
                    shamsiDateText = formatShamsi(cy, cm, (cd - 6).coerceAtLeast(1)),
                    shamsiYear = cy,
                    shamsiMonth = cm,
                    timestampMs = nowMs - (6 * dayMs),
                    note = "پرداخت نقدی",
                ),
                TransactionEntity(
                    title = "بیمه تکمیلی و چکاپ دندان‌پزشکی",
                    amountToman = 8_100_000L,
                    isIncome = false,
                    category = "بهداشت و درمان",
                    accountId = mellatId,
                    workspace = WorkspaceScope.PERSONAL.name,
                    status = TransactionStatus.PENDING.name,
                    shamsiDateText = formatShamsi(cy, cm, (cd - 7).coerceAtLeast(1)),
                    shamsiYear = cy,
                    shamsiMonth = cm,
                    timestampMs = nowMs - (7 * dayMs),
                    note = "در انتظار تایید فاکتور بیمه",
                ),
            )

            dao.insertTransactions(seedTransactions)
        }
    }
}
