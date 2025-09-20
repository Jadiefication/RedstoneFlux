package io.github.Jadiefication.redstoneflux

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.*
import java.util.logging.Logger

/**
 * This class is used to check if the plugin is up to date
 */
class Updater
/**
     * Create a new Updater
     *
     * @param name The name of the plugin
     */
    private constructor(
        /**
         * The name of the plugin
         */
        private val name: String,
    ) {
        /**
         * Check if the plugin is up to date and log a warning if it's not
         */
        private fun checkUpdates() {
            if (!this.isUpToDate) {
                Logger
                    .getLogger(name)
                    .warning(
                        "The framework is not up to date, " +
                            "the latest version is " + this.fetchLatestVersion(),
                    )
            }
        }

        private val version: String?
            /**
             * Get the version of the plugin
             *
             * @return The version of the plugin
             */
            get() {
                val prop = Properties()
                try {
                    prop.load(
                        Updater::class.java
                            .getClassLoader()
                            .getResourceAsStream("version.properties"),
                    )
                    return "v${prop.getProperty("version")}"
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }

        private val isUpToDate: Boolean
            /**
             * Check if the plugin is up to date
             *
             * @return True if the plugin is up to date, false otherwise
             */
            get() {
                try {
                    val latestVersion = fetchLatestVersion()
                    return this.version == latestVersion
                } catch (e: Exception) {
                    return false
                }
            }

        /**
         * Get the latest version of the plugin
         *
         * @return The latest version of the plugin
         */
        private fun fetchLatestVersion(): String? {
            try {
                val url = URI.create(API_URL.replace("{name}", this.name)).toURL()
                val responseString = getString(url)
                val tagNameIndex = responseString.indexOf("\"tag_name\"")
                val start = responseString.indexOf('\"', tagNameIndex + 10) + 1
                val end = responseString.indexOf('\"', start)
                return responseString.substring(start, end)
            } catch (e: Exception) {
                return null
            }
        }

        /**
         * Get the latest version of the plugin
         *
         * @return The latest version of the plugin
         */
        @Throws(IOException::class)
        private fun getString(url: URL): String {
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestMethod("GET")

            val response = StringBuilder()
            try {
                Scanner(connection.getInputStream()).use { scanner ->
                    while (scanner.hasNext()) {
                        response.append(scanner.nextLine())
                    }
                }
            } finally {
                connection.disconnect()
            }

            return response.toString()
        }

        companion object {
            /**
             * Check updates the plugin
             *
             * @param name The name of the plugin
             */
            fun update(name: String) {
                Updater(name).checkUpdates()
            }

            /**
             * The URL of the GitHub API
             */
            private const val API_URL = "https://api.github.com/repos/Jadiefication/{name}/releases/latest"
        }
    }
