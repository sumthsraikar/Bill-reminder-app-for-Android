package com.example.myapplicationds.data.local.dao

import androidx.room.*
import com.example.myapplicationds.data.local.entity.BillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {

    @Query("SELECT * FROM bills ORDER BY dueDate ASC")
    fun getAllBills(): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills WHERE id = :id")
    fun getBillById(id: Long): Flow<BillEntity?>

    @Query("SELECT * FROM bills WHERE id = :id")
    suspend fun getBillByIdOneShot(id: Long): BillEntity?

    @Query("SELECT * FROM bills WHERE paymentStatus = :status ORDER BY dueDate ASC")
    fun getBillsByStatus(status: String): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills WHERE billName LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR CAST(amount AS TEXT) LIKE '%' || :query || '%' ORDER BY dueDate ASC")
    fun searchBills(query: String): Flow<List<BillEntity>>

    @Query("SELECT COUNT(*) FROM bills WHERE paymentStatus = 'UPCOMING'")
    fun getUpcomingCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM bills WHERE paymentStatus = 'OVERDUE'")
    fun getOverdueCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM bills WHERE paymentStatus = 'PAID'")
    fun getPaidCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: BillEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBills(bills: List<BillEntity>)

    @Update
    suspend fun updateBill(bill: BillEntity)

    @Delete
    suspend fun deleteBill(bill: BillEntity)

    @Query("DELETE FROM bills WHERE id = :id")
    suspend fun deleteBillById(id: Long)

    @Query("DELETE FROM bills")
    suspend fun deleteAllBills()
}
