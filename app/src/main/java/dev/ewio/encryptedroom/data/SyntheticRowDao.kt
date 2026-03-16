package dev.ewio.encryptedroom.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface SyntheticRowDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(rows: List<SyntheticRow>)

    @Query("SELECT * FROM synthetic_rows ORDER BY sequence ASC")
    suspend fun getAll(): List<SyntheticRow>

    @Query("SELECT COUNT(*) FROM synthetic_rows")
    suspend fun count(): Int

    @Query("SELECT COALESCE(MAX(sequence), 0) FROM synthetic_rows")
    suspend fun maxSequence(): Long

    @Transaction
    suspend fun insertBatch(rows: List<SyntheticRow>) {
        insertAll(rows)
    }
}
