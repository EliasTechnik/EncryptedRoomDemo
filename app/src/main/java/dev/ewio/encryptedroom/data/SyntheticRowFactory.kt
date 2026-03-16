package dev.ewio.encryptedroom.data

object SyntheticRowFactory {
    private const val BATCH_SIZE = 10_000

    fun createBatch(startSequence: Long, nowEpochMs: Long): List<SyntheticRow> {
        return buildList(BATCH_SIZE) {
            repeat(BATCH_SIZE) { index ->
                val sequence = startSequence + index + 1
                val payload = buildPayload(sequence)
                add(
                    SyntheticRow(
                        sequence = sequence,
                        payload = payload,
                        payloadChecksum = ChecksumUtils.checksum(payload),
                        createdAtEpochMs = nowEpochMs,
                    )
                )
            }
        }
    }

    private fun buildPayload(sequence: Long): String {
        val a = (sequence * 1103515245L + 12345L) and 0x7fffffff
        val b = (sequence * 214013L + 2531011L) and 0x7fffffff
        val c = sequence * sequence + 17L
        return "row=$sequence|a=$a|b=$b|c=$c|tag=synthetic"
    }
}
