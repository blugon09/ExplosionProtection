package io.github.blugon0921.explosionprotection

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.data.type.Bed
import org.bukkit.block.data.type.RespawnAnchor
import org.bukkit.block.data.type.TNT
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class ExplosionProtection : JavaPlugin(),Listener {

    var config = File(dataFolder, "config.yml")
    val yaml = YamlConfiguration.loadConfiguration(config)

    var blockExplosion = false
    var explosionDamage = true

    override fun onEnable() {
        logger.info("Plugin Enable")
        Bukkit.getPluginManager().registerEvents(this, this)

        if(!config.exists()) {
            yaml.set("blockExplosion", false)
            yaml.set("explosionDamage", false)
            yaml.save(config)
        }
        yaml.load(config)

        blockExplosion = yaml.getBoolean("blockExplosion")
        explosionDamage = yaml.getBoolean("explosionDamage")
    }

    override fun onDisable() {
        yaml.set("blockExplosion", blockExplosion)
        yaml.set("explosionDamage", explosionDamage)
        yaml.save(config)
        logger.info("Plugin Disable")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(command.name == "ep") {
            if (sender.isOp) {
                if(args.size == 2) {
                    when(args[0]) {
                        "block" -> {
                            when(args[1]) {
                                "on" -> {
                                    sender.sendMessage("??7[??9ExplosionProtection??7] ??f????????? ?????? ??????????????? ???????????? ????????????")
                                    blockExplosion = false
                                }
                                "off" -> {
                                    sender.sendMessage("??7[??9ExplosionProtection??7] ??f????????? ?????? ??????????????? ????????? ????????????")
                                    blockExplosion = true
                                }
                            }
                        }
                        "damage" -> {
                            when(args[1]) {
                                "on" -> {
                                    sender.sendMessage("??7[??9ExplosionProtection??7] ??f????????? ?????? ???????????? ?????? ????????? ???????????? ????????????")
                                    explosionDamage = false
                                }
                                "off" -> {
                                    sender.sendMessage("??7[??9ExplosionProtection??7] ??f????????? ?????? ???????????? ?????? ????????? ????????? ????????????")
                                    explosionDamage = true
                                }
                            }
                        }
                    }
                } else {
                    sender.sendMessage("${ChatColor.RED}??? ??? ????????? ???????????? ????????? ?????????")
                }
            } else {
                sender.sendMessage("${ChatColor.RED}????????? ???????????????")
            }
        }
        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String>? {
        if(command.name == "ep") {
            if(args.size == 1) {
                val returns1 = mutableListOf("block", "damage")

                val returns2 = mutableListOf<String>()
                for (returns in returns1) {
                    if(returns.lowercase().startsWith(args[0].lowercase())) {
                        returns2.add(returns)
                    }
                }
                return returns2
            } else if(args.size == 2) {
                val returns1 = mutableListOf("on", "off")

                val returns2 = mutableListOf<String>()
                for (returns in returns1) {
                    if(returns.lowercase().startsWith(args[1].lowercase())) {
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
        if(!blockExplosion) {
            event.isCancelled = true
            event.block.world.spawnParticle(Particle.EXPLOSION_LARGE, event.block.location, 100, 1.0, 1.0, 1.0, 0.0, null, true)
            event.block.world.playSound(event.block.location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f)
        }
    }

    @EventHandler
    fun entityExplode(event : EntityExplodeEvent) {
        if(!blockExplosion) {
            event.isCancelled = true
            event.entity.world.spawnParticle(Particle.EXPLOSION_LARGE, event.entity.location, 100, 1.0, 1.0, 1.0, 0.0, null, true)
            event.entity.world.playSound(event.entity.location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f)
        }
    }

    @EventHandler
    fun explosionDamage(event : EntityDamageByEntityEvent) {
        if(event.damager !is TNTPrimed && event.damager !is Creeper && event.damager !is Fireball && event.damager !is Wither) return
        if(explosionDamage) return
        event.isCancelled = true
    }
    @EventHandler
    fun explosionDamage(event : EntityDamageByBlockEvent) {
        if(event.damager !is TNT && event.damager !is RespawnAnchor && event.damager !is Bed) return
        if(explosionDamage) return
        event.isCancelled = true
    }
}