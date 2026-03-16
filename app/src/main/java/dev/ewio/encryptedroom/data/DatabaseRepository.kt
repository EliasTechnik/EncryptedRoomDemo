package dev.ewio.encryptedroom.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseRepository private constructor(
    private val database: AppDatabase,
) {
    private val dao = database.syntheticRowDao()

    suspend fun verifyAll(): VerificationResult = withContext(Dispatchers.IO) {
        val rows = dao.getAll()
        var mismatches = 0

        rows.forEach { row ->
            val expected = ChecksumUtils.checksum(row.payload)
            if (expected != row.payloadChecksum) {
                mismatches++
            }
        }

        VerificationResult(
            rowCount = rows.size,
            mismatchCount = mismatches,
            ok = mismatches == 0,
        )
    }

    suspend fun addBatch(): BatchInsertResult = withContext(Dispatchers.IO) {
        val startSequence = dao.maxSequence()
        val rows = SyntheticRowFactory.createBatch(
            startSequence = startSequence,
            nowEpochMs = System.currentTimeMillis(),
        )
        dao.insertBatch(rows)
        BatchInsertResult(
            inserted = rows.size,
            totalRows = dao.count(),
        )
    }

    companion object {
        fun create(applicationContext: android.content.Context): DatabaseRepository {
            val db = AppDatabase.getInstance(
                context = applicationContext
            )
            return DatabaseRepository(db)
        }
    }
}

data class VerificationResult(
    val rowCount: Int,
    val mismatchCount: Int,
    val ok: Boolean,
)

data class BatchInsertResult(
    val inserted: Int,
    val totalRows: Int,
)
