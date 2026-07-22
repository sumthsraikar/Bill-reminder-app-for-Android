package com.example.myapplicationds.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplicationds.data.local.dao.BillDao
import com.example.myapplicationds.data.local.dao.CategoryDao
import com.example.myapplicationds.data.local.dao.PaymentHistoryDao
import com.example.myapplicationds.data.local.entity.BillEntity
import com.example.myapplicationds.data.local.entity.CategoryEntity
import com.example.myapplicationds.data.local.entity.PaymentHistoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider

@Database(
    entities = [
        BillEntity::class,
        CategoryEntity::class,
        PaymentHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BillDatabase : RoomDatabase() {

    abstract fun billDao(): BillDao
    abstract fun categoryDao(): CategoryDao
    abstract fun paymentHistoryDao(): PaymentHistoryDao

    class SeedDatabaseCallback(
        private val provider: Provider<CategoryDao>
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val categoryDao = provider.get()
                val defaultCategories = listOf(
                    CategoryEntity(categoryName = "Utilities", icon = "Bolt", color = 0xFFF59E0BL),
                    CategoryEntity(categoryName = "Rent & Mortgage", icon = "Home", color = 0xFF3B82F6L),
                    CategoryEntity(categoryName = "Subscriptions", icon = "Subscriptions", color = 0xFF8B5CF6L),
                    CategoryEntity(categoryName = "Insurance", icon = "Shield", color = 0xFF10B981L),
                    CategoryEntity(categoryName = "Loans & Credit", icon = "CreditCard", color = 0xFFEF4444L),
                    CategoryEntity(categoryName = "Taxes", icon = "AccountBalance", color = 0xFF64748BL),
                    CategoryEntity(categoryName = "Internet & Phone", icon = "Wifi", color = 0xFF06B6D4L),
                    CategoryEntity(categoryName = "Other", icon = "MoreHoriz", color = 0xFF94A3B8L)
                )
                categoryDao.insertAllCategories(defaultCategories)
            }
        }
    }
}
