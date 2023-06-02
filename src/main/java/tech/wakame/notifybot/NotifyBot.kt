package tech.wakame.notifybot

import org.bukkit.plugin.java.JavaPlugin
import tech.wakame.notifybot.handlers.PlayerLogInOutHandler

class NotifyBot : JavaPlugin() {
    private val handlers = arrayOf(PlayerLogInOutHandler(this))

    override fun onEnable() {
        saveDefaultConfig()

        handlers.forEach {
            server.pluginManager.registerEvents(it, this)
        }
    }

    override fun onDisable() {}
}