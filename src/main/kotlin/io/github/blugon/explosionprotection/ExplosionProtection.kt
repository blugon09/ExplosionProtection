package io.github.blugon.explosionprotection

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*

class ExplosionProtection : JavaPlugin(),Listener {

    var config = File(dataFolder, "config.yml")
    val yaml = YamlConfiguration.loadConfiguration(config)

    var explosion = true

    override fun onEnable() {
        logger.info("Plugin Enable")
        Bukkit.getPluginManager().registerEvents(this, this)

        if(!config.exists()) {
            saveConfig()
            getConfig().options().copyDefaults(true)
            saveConfig()
        }
        yaml.load(config)

        explosion = yaml.getBoolean("explode")
    }

    override fun onDisable() {
        logger.info("Plugin Disable")
        yaml.set("explode", explosion)
        saveConfig()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(command.name == "explode") {
            if (sender.isOp) {
                if(args.size == 1) {
                    return when(args[0]) {
                        "true" -> {
                            sender.sendMessage("폭발을 활성화 했습니다")
                            explosion = true
                            true
                        }

                        "false" -> {
                            sender.sendMessage("폭발을 비활성화 했습니다")
                            explosion = false
                            true
                        }

                        else -> true
                    }
                } else {
                    sender.sendMessage("${ChatColor.RED}알 수 없거나 불완전한 명령어 입니다")
                }
            } else {
                sender.sendMessage("${ChatColor.RED}권한이 부족합니다")
            }
        }
        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String>? {
        if(command.name == "explode") {
            if(args.size == 1) {
                val returns1 = mutableListOf("true", "false")

                val returns2 = mutableListOf<String>()
                for (returns in returns1) {
                    if(returns.lowercase().startsWith(args[0].lowercase())) {
                        returns2.add(returns)
                    }
                }
                return returns2
            } else {
                mutableListOf("")
            }
        }
        return null
    }

    @EventHandler
    fun blockExplode(event : BlockExplodeEvent) {
        if(!explosion) {
            event.isCancelled = true
            event.block.world.spawnParticle(Particle.EXPLOSION_LARGE, event.block.location, 100, 1.0, 1.0, 1.0, 0.0, null, true)
            event.block.world.playSound(event.block.location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f)
        }
    }

    @EventHandler
    fun entityExplode(event : EntityExplodeEvent) {
        if(!explosion) {
            event.isCancelled = true
            event.entity.world.spawnParticle(Particle.EXPLOSION_LARGE, event.entity.location, 100, 1.0, 1.0, 1.0, 0.0, null, true)
            event.entity.world.playSound(event.entity.location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f)
        }
    }
}