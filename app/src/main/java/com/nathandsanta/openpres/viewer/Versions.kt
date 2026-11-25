package com.nathandsanta.openpres.viewer

/**
 * OpenPres versions: stable "0.8.0" (tag v0.8.0) and betas "0.8.0.2 beta" (tag v0.8.0.2-beta,
 * the 2nd beta since 0.8.0). They sort 0.8.0 < 0.8.0.2 beta < 0.8.1 < 0.9.0.
 */
object Versions {
    private fun parts(version: String): List<Int>? {
        val m = Regex("""^v?(\d+)\.(\d+)\.(\d+)(?:\.(\d+))?""").find(version.trim()) ?: return null
        return m.groupValues.drop(1).map { it.ifEmpty { "0" }.toInt() }
    }

    /** Whether `version` is newer than `than` (unknown versions never are). */
    fun isNewer(version: String, than: String): Boolean {
        val a = parts(version) ?: return false
        val b = parts(than) ?: return false
        for (i in 0 until 4) if (a[i] != b[i]) return a[i] > b[i]
        return false
    }

    /** A tag or version as people see it: "v0.8.0" -> "0.8.0", "v0.8.0.2-beta" -> "0.8.0.2 beta". */
    fun display(version: String): String {
        val p = parts(version) ?: return version
        return if (p[3] > 0) "${p.joinToString(".")} beta" else p.take(3).joinToString(".")
    }
}
