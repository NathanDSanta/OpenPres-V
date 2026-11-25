package com.nathandsanta.openpres.viewer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UpdatesTest {
    // GitHub's list of releases, newest first.
    private val releases = """
        [
          {"tag_name": "v0.9.0.2-beta", "prerelease": true, "assets": [
            {"name": "OpenPres-Viewer-0.9.0.2-beta.apk", "browser_download_url": "https://x/beta.apk"}]},
          {"tag_name": "v0.9.1-draft", "draft": true, "assets": [{"name": "x.apk", "browser_download_url": "https://x/draft.apk"}]},
          {"tag_name": "v0.9.0", "prerelease": false, "assets": [
            {"name": "SHA256SUMS.txt", "browser_download_url": "https://x/sums"},
            {"name": "OpenPres-Viewer-0.9.0.apk", "browser_download_url": "https://x/stable.apk"}]},
          {"tag_name": "v0.8.0", "prerelease": false, "assets": [
            {"name": "OpenPres-Viewer-0.8.0.apk", "browser_download_url": "https://x/old.apk"}]}
        ]
    """.trimIndent()

    @Test
    fun theNewestStableOne() {
        assertEquals(Updates.Release("0.9.0", "https://x/stable.apk", "v0.9.0"), Updates.newer(releases, "0.8.2"))
    }

    @Test
    fun betasOnlyForTheBetaProgram() {
        assertEquals(Updates.Release("0.9.0.2 beta", "https://x/beta.apk", "v0.9.0.2-beta"), Updates.newer(releases, "0.8.2", beta = true))
        assertEquals("0.9.0.2 beta", Updates.newer(releases, "0.9.0.1 beta", beta = true)?.version)
    }

    @Test
    fun theSameOrAnOlderOneDoesnt() {
        assertNull(Updates.newer(releases, "0.9.0"))
        assertNull(Updates.newer(releases, "1.0.0", beta = true))
        assertNull(Updates.newer(releases, "0.9.0.2 beta", beta = true))
        assertNull(Updates.newer("""[{"tag_name": "v0.9.0", "assets": []}]""", "0.8.0"))
    }
}
