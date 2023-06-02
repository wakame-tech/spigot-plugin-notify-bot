package tech.wakame.notifybot.handlers

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import tech.wakame.notifybot.NotifyBot
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class PlayerLogInOutHandler(private val plugin: NotifyBot) : Listener {
    companion object {
        private val client = HttpClient.newHttpClient()

        private const val LoginTemplate = "`%s` logged in"
        private const val LogoutTemplate = "`%s` logged out"
    }

    private fun Map<String, String>.toJson() = entries.joinToString(",", prefix = "{", postfix = "}") { (k, v) -> "\"$k\": \"$v\"" }

    private fun HttpClient.postJson(url: String, body: Map<String, String>): HttpResponse<String> {
        val req = HttpRequest.newBuilder()
            .header("Content-Type", "application/json")
            .uri(URI.create(url))
            .POST(HttpRequest.BodyPublishers.ofString(body.toJson()))
            .build()
        return send(req, HttpResponse.BodyHandlers.ofString())
    }

    @EventHandler
    fun onPlayerLogin(e: PlayerLoginEvent) {
        val url = plugin.config.getString("discordWebHookUrl") ?: return
        val res = client.postJson(url, mapOf("content" to LoginTemplate.format(e.player.displayName)))
        e.player.server.logger.info(res.body())
    }

    @EventHandler
    fun onPlayerLogout(e: PlayerQuitEvent) {
        val url = plugin.config.getString("discordWebHookUrl") ?: return
        val res = client.postJson(url, mapOf("content" to LogoutTemplate.format(e.player.displayName)))
        e.player.server.logger.info(res.body())
    }
}