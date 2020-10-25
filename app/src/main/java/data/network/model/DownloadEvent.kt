package data.network.model

data class OnDownload(
    val data: ByteArray,
    val count: Int,
    val fileSizeDownloaded: Int,
    val fileSize: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OnDownload

        if (!data.contentEquals(other.data)) return false
        if (count != other.count) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + count
        return result
    }
}
