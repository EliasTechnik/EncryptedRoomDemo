package dev.ewio.encryptedroom.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.ewio.encryptedroom.BuildConfig
import net.zetetic.database.sqlcipher.SQLiteConnection
import net.zetetic.database.sqlcipher.SQLiteDatabaseHook
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.util.concurrent.Executors

@Database(
    entities = [SyntheticRow::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun syntheticRowDao(): SyntheticRowDao

    companion object {
        private const val DB_NAME = "encrypted-room.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            Log.d("AppDatabase", "building encrypted database...")

            val hook = object : SQLiteDatabaseHook {
                override fun preKey(connection: SQLiteConnection) {}

                override fun postKey(connection: SQLiteConnection) {
                    //Note: Changing this breaks existing databases. This is not fixable by common migrations!
                    connection.execute("PRAGMA cipher_default_page_size = 8192;", null, null)
                    connection.execute("PRAGMA cipher_use_hmac = OFF;",  null, null)// see: https://www.zetetic.net/sqlcipher/performance/#:~:text=entropy%20key%20values.-,Disable,-Page%20Data%20Validation
                    connection.execute("PRAGMA cipher_memory_security = OFF;",  null, null) //turns improved sanitization of in-memory data used by the database off.
                    //This increases the risk of memory readouts but again improves performance. This risk is generally accepted: https://www.zetetic.net/sqlcipher/performance/#:~:text=Turn%20Off%20Memory%20Security
                }
            }

            val passphrase: ByteArray = BuildConfig.DB_PASSPHRASE.toByteArray()

            val factory = SupportOpenHelperFactory(passphrase, hook, true)

            val queryExecutor = Executors.newFixedThreadPool(1) //change this to 2 or more and the database breaks!
            val transactionExecutor = Executors.newSingleThreadExecutor()

            return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                .openHelperFactory(factory)
                .setQueryExecutor(queryExecutor)
                .setTransactionExecutor(transactionExecutor)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        db.execSQL("PRAGMA foreign_keys=ON")
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        db.execSQL("PRAGMA foreign_keys=ON")
                    }
                })
                .build()
        }
    }
}
