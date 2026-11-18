package app.revera.patches.branding

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility
import app.morphe.patcher.patch.resourcePatch
import org.w3c.dom.Element

private const val ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android"
private const val APP_NAME = "Spotify"

// Released Revera builds (GitHub Releases, v1.0.x) ship as "app.revera.dev".
private val reveraDev = Compatibility(
    name = "Revera",
    packageName = "app.revera.dev",
    apkFileType = ApkFileType.APK,
    appIconColor = 0x1ED760,
    targets = listOf(AppTarget(version = null)), // Resources only: any Revera version.
)

// Stable channel id used by Revera's build script for non-beta releases.
private val reveraStable = Compatibility(
    name = "Revera (stable)",
    packageName = "app.revera",
    apkFileType = ApkFileType.APK,
    appIconColor = 0x1ED760,
    targets = listOf(AppTarget(version = null)),
)

private const val SPOTIFY_MARK_PATH = """M70.563,50.822C61.537,45.462 46.65,44.97 38.034,47.585C36.65,48.005 35.187,47.223 34.768,45.84C34.348,44.455 35.129,42.993 36.513,42.573C46.404,39.57 62.846,40.15 73.236,46.318C74.481,47.057 74.889,48.664 74.151,49.907C73.413,51.151 71.804,51.561 70.563,50.822ZM70.267,58.761C69.634,59.789 68.29,60.111 67.264,59.48C59.74,54.855 48.266,53.515 39.363,56.217C38.209,56.566 36.99,55.915 36.639,54.763C36.291,53.609 36.942,52.391 38.095,52.041C48.264,48.954 60.907,50.449 69.549,55.76C70.575,56.392 70.898,57.736 70.267,58.761ZM66.841,66.386C66.338,67.211 65.264,67.469 64.441,66.966C57.866,62.948 49.59,62.04 39.844,64.267C38.905,64.482 37.969,63.893 37.755,62.955C37.539,62.015 38.126,61.079 39.067,60.865C49.733,58.427 58.882,59.476 66.262,63.986C67.085,64.488 67.344,65.563 66.841,66.386ZM54,26C38.536,26 26,38.536 26,53.999C26,69.465 38.536,82 54,82C69.464,82 82,69.465 82,53.999C82,38.536 69.464,26 54,26L54,26Z"""

private fun markVector(color: String) = """
    <?xml version="1.0" encoding="utf-8"?>
    <vector xmlns:android="http://schemas.android.com/apk/res/android"
        android:width="108dp"
        android:height="108dp"
        android:viewportWidth="108"
        android:viewportHeight="108">
        <path
            android:fillColor="$color"
            android:pathData="$SPOTIFY_MARK_PATH" />
    </vector>
""".trimIndent() + "\n"

private val spotifyBackground = """
    <?xml version="1.0" encoding="utf-8"?>
    <vector xmlns:android="http://schemas.android.com/apk/res/android"
        android:width="108dp"
        android:height="108dp"
        android:viewportWidth="108"
        android:viewportHeight="108">
        <path
            android:fillColor="#000000"
            android:pathData="M0,0h108v108h-108z" />
    </vector>
""".trimIndent() + "\n"

private val spotifyNotification = """
    <?xml version="1.0" encoding="utf-8"?>
    <vector xmlns:android="http://schemas.android.com/apk/res/android"
        android:width="24dp"
        android:height="24dp"
        android:viewportWidth="108"
        android:viewportHeight="108">
        <group
            android:scaleX="1.4464286"
            android:scaleY="1.4464286"
            android:translateX="-24.107143"
            android:translateY="-24.107143">
            <path
                android:fillColor="#FFFFFFFF"
                android:pathData="$SPOTIFY_MARK_PATH" />
        </group>
    </vector>
""".trimIndent() + "\n"

/** Renames Revera to Spotify and replaces the launcher, themed, splash, widget and notification marks. */
@Suppress("unused")
val spotifyBrandingPatch = resourcePatch(
    name = "Spotify branding",
    description = "Renames Revera to Spotify and replaces the launcher (all color variants), themed, splash, widget and notification icons with the Spotify mark.",
    default = true,
) {
    compatibleWith(reveraDev, reveraStable)

    execute {
        // Application label (launcher + every launcher alias inherit it) and the home screen widget label.
        document("AndroidManifest.xml").use { manifest ->
            val application = manifest.getElementsByTagName("application").item(0) as? Element
                ?: error("Revera manifest has no application element")
            application.setAttributeNS(ANDROID_NAMESPACE, "android:label", APP_NAME)

            val receivers = manifest.getElementsByTagName("receiver")
            (0 until receivers.length)
                .mapNotNull { receivers.item(it) as? Element }
                .firstOrNull {
                    it.getAttributeNS(ANDROID_NAMESPACE, "name") ==
                        "com.music.spotui.widget.NowPlayingWidget"
                }
                ?.setAttributeNS(ANDROID_NAMESPACE, "android:label", APP_NAME)
        }

        // app_name is translated in several locales; rename it everywhere it exists.
        var foundAppName = false
        val appNameElementPattern = Regex("""<string\b(?=[^>]*\bname\s*=\s*["']app_name["'])[^>]*>""")
        listDecodableFiles("res/values*/strings.xml").forEach { path ->
            if (!appNameElementPattern.containsMatchIn(get(path).readText())) return@forEach

            document(path).use { strings ->
                val resources = strings.getElementsByTagName("string")
                (0 until resources.length)
                    .mapNotNull { resources.item(it) as? Element }
                    .firstOrNull { it.getAttribute("name") == "app_name" }
                    ?.let {
                        it.textContent = APP_NAME
                        foundAppName = true
                    }
            }
        }
        check(foundAppName) { "Revera app_name string was not found" }

        // Overwrites an existing drawable only: a new, unreferenced file would not be in the resource table.
        fun replace(path: String, content: String, required: Boolean = false) {
            val file = get(path)
            if (file.exists()) file.writeText(content)
            else check(!required) { "$path was not found in Revera" }
        }

        // Launcher foreground: the default (green) plus the blue/purple/pink/orange alias icons.
        // The splash screen (splash_icon = inset of ic_launcher_foreground) follows automatically.
        val foreground = markVector("#1ED760")
        replace("res/drawable/ic_launcher_foreground.xml", foreground, required = true)
        listOf("blue", "purple", "pink", "orange").forEach {
            replace("res/drawable/ic_launcher_foreground_$it.xml", foreground)
        }

        replace("res/drawable/ic_launcher_background.xml", spotifyBackground, required = true)

        // Android 13+ themed icon layer.
        replace("res/drawable/ic_launcher_monochrome.xml", markVector("#FFFFFFFF"))

        // Status bar / media notification mark.
        replace("res/drawable/msp_ic_notification.xml", spotifyNotification)
    }
}
