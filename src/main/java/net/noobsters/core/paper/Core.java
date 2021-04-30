package net.noobsters.core.paper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import net.noobsters.core.paper.commands.kitCMD;
import net.noobsters.core.paper.mechanics.Timer;

/**
 * Core
 */
public class Core extends JavaPlugin{
    // GUI tutorial: https://github.com/MrMicky-FR/FastInv
    // Scoreboard Tutorial: https://github.com/MrMicky-FR/FastBoard
    // Commands Tutorial: https://github.com/aikar/commands/wiki/Using-ACF

    public static @Getter Core instance;

    private @Getter Timer timer;
    
    private @Getter HashMap<String, Long> coolDown1 = new HashMap<>();
    private @Getter HashMap<String, Long> coolDown2 = new HashMap<>();
    
    @Override
    public void onEnable() {
        instance = this;

        timer = new Timer(this);
        timer.runTaskTimerAsynchronously(this, 0L, 20L);

        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            Iterator<Entry<String, Long>> iterator = coolDown1.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, Long> entry = iterator.next();
                long differential = entry.getValue() - System.currentTimeMillis();
                if (differential <= 0) {
                    iterator.remove();
                }
            }
        }, 2L, 2L);

        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            Iterator<Entry<String, Long>> iterator = coolDown2.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, Long> entry = iterator.next();
                long differential = entry.getValue() - System.currentTimeMillis();
                if (differential <= 0) {
                    iterator.remove();
                }
            }
        }, 2L, 2L);

        //Listeners
        Bukkit.getPluginManager().registerEvents(new GlobalListeners(this), this);

        //commands
        this.getCommand("kit").setExecutor(new kitCMD());

    }

    @Override
    public void onDisable() {

    }
    
}