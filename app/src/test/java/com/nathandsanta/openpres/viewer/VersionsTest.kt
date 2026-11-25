package com.nathandsanta.openpres.viewer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VersionsTest {
    @Test
    fun comparesStableReleasesAndBetas() {
        assertTrue(Versions.isNewer("v0.8.0", "0.7.9"))
        assertTrue(Versions.isNewer("0.10.0", "0.9.3"))
        assertFalse(Versions.isNewer("0.7.0", "0.7.0"))
        assertFalse(Versions.isNewer("0.6.9", "0.7.0"))
        assertFalse(Versions.isNewer("nightly", "0.7.0"))
        // 0.7.0 < 0.7.0.3 beta < 0.7.1
        assertTrue(Versions.isNewer("v0.7.0.3-beta", "0.7.0"))
        assertTrue(Versions.isNewer("0.7.1", "0.7.0.3 beta"))
        assertTrue(Versions.isNewer("0.7.0.10 beta", "0.7.0.9 beta"))
    }

    @Test
    fun asPeopleSeeThem() {
        assertEquals("0.8.0", Versions.display("v0.8.0"))
        assertEquals("0.8.0.2 beta", Versions.display("v0.8.0.2-beta"))
        assertEquals("nightly", Versions.display("nightly"))
    }
}
