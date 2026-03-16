package dev.ewio.encryptedroom.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "synthetic_rows",
    indices = [Index(value = ["sequence"], unique = true)]
)
data class SyntheticRow(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sequence: Long,
    val payload: String,
    val payloadChecksum: Long,
    val createdAtEpochMs: Long,
)
