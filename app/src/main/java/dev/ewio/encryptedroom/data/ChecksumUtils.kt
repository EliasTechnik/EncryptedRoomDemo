package dev.ewio.encryptedroom.data

import java.nio.charset.StandardCharsets
import java.util.zip.CRC32

object ChecksumUtils {
    fun checksum(value: String): Long {
        val crc32 = CRC32()
        crc32.update(value.toByteArray(StandardCharsets.UTF_8))
        return crc32.value
    }
}
