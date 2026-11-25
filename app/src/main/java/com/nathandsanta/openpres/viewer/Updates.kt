package com.nathandsanta.openpres.viewer

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

/**
 * Looks for a newer app when it opens, among this app's releases on GitHub (OpenPres-V): stable
 * ones, and betas too when the server used last follows the beta program. If there is one, the
 * screen in front offers to download it (opening the file installs it). A build by hand
 * (version 0.1.0) doesn't look.
 */
object Updates {
    private const val RELEASES = "https://api.github.com/repos/NathanDSanta/OpenPres-V/releases?per_page=30"
    private const val TEST_BUILD = "0.1.0"

    /** `version` as people see it ("0.8.0.2 beta"), `tag` as on GitHub. */
    data class Release(val version: String, val url: String, val tag: String = "v$version")

    private var started = false
    private var found: Release? = null
    private var offered = false
    private var front: WeakReference<Activity>? = null

    /** Once per run of the app. `server`: the address used last, to ask about the beta program. */
    fun start(server: String?) {
        if (started || BuildConfig.VERSION_NAME == TEST_BUILD) return
        started = true
        Thread {
            val beta = server != null && runCatching { JSONObject(get("$server/api/health", 3_000)).optBoolean("beta_program") }.getOrDefault(false)
            val release = runCatching { newer(get(RELEASES, 10_000), BuildConfig.VERSION_NAME, beta) }.getOrNull()
            Handler(Looper.getMainLooper()).post {
                found = release
                front?.get()?.let { offer(it) }
            }
        }.start()
    }

    fun resumed(activity: Activity) {
        front = WeakReference(activity)
        offer(activity)
    }

    fun paused(activity: Activity) {
        if (front?.get() === activity) front = null
    }

    /**
     * From GitHub's list of releases: the newest one with an APK, if it's newer than `current`.
     * Betas (pre-releases) count only with `beta`.
     */
    fun newer(json: String, current: String, beta: Boolean = false): Release? {
        val releases = JSONArray(json)
        var best: Release? = null
        for (r in 0 until releases.length()) {
            val release = releases.getJSONObject(r)
            if (release.optBoolean("draft") || (release.optBoolean("prerelease") && !beta)) continue
            val tag = release.optString("tag_name")
            if (!Versions.isNewer(tag, current) || (best != null && !Versions.isNewer(tag, best.tag))) continue
            val assets = release.optJSONArray("assets") ?: continue
            for (i in 0 until assets.length()) {
                val asset = assets.getJSONObject(i)
                if (asset.optString("name").endsWith(".apk")) {
                    best = Release(Versions.display(tag), asset.optString("browser_download_url"), tag)
                    break
                }
            }
        }
        return best
    }

    private fun get(url: String, timeout: Int): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.setRequestProperty("Accept", "application/vnd.github+json")
        connection.setRequestProperty("User-Agent", "openpres-android")
        connection.connectTimeout = timeout
        connection.readTimeout = timeout
        try {
            if (connection.responseCode != 200) throw IllegalStateException("$url answered ${connection.responseCode}")
            return connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun offer(activity: Activity) {
        val release = found ?: return
        if (offered || activity.isFinishing) return
        offered = true
        AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.update_title, release.version))
            .setMessage(activity.getString(R.string.update_message, BuildConfig.VERSION_NAME))
            .setPositiveButton(R.string.download) { _, _ -> activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(release.url))) }
            .setNegativeButton(R.string.later, null)
            .show()
    }
}
