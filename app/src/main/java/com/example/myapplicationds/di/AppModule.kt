package com.example.myapplicationds.di

import android.content.Context
import androidx.room.Room
import com.example.myapplicationds.data.local.dao.BillDao
import com.example.myapplicationds.data.local.dao.CategoryDao
import com.example.myapplicationds.data.local.dao.PaymentHistoryDao
import com.example.myapplicationds.data.local.db.BillDatabase
import com.example.myapplicationds.data.preferences.UserPreferencesManager
import com.example.myapplicationds.data.repository.BillRepository
import com.example.myapplicationds.data.repository.BillRepositoryImpl
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideBillDatabase(
        @ApplicationContext context: Context,
        categoryDaoProvider: Provider<CategoryDao>
    ): BillDatabase {
        return Room.databaseBuilder(
            context,
            BillDatabase::class.java,
            "billbuddy_db"
        )
            .addCallback(BillDatabase.SeedDatabaseCallback(categoryDaoProvider))
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideBillDao(db: BillDatabase): BillDao = db.billDao()

    @Provides
    fun provideCategoryDao(db: BillDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun providePaymentHistoryDao(db: BillDatabase): PaymentHistoryDao = db.paymentHistoryDao()

    @Provides
    @Singleton
    fun provideBillRepository(
        billDao: BillDao,
        categoryDao: CategoryDao,
        paymentHistoryDao: PaymentHistoryDao,
        userPreferencesManager: UserPreferencesManager,
        gson: Gson
    ): BillRepository {
        return BillRepositoryImpl(
            billDao,
            categoryDao,
            paymentHistoryDao,
            userPreferencesManager,
            gson
        )
    }
}
