package net.noobsters.core.paper;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

/**
 * Core
 */
public class Core extends JavaPlugin{
    // GUI tutorial: https://github.com/MrMicky-FR/FastInv
    // Scoreboard Tutorial: https://github.com/MrMicky-FR/FastBoard
    // Commands Tutorial: https://github.com/aikar/commands/wiki/Using-ACF

    public static @Getter Core instance;
    
    @Override
    public void onEnable() {
        instance = this;

        //Listeners
        Bukkit.getPluginManager().registerEvents(new GlobalListeners(this), this);

    }

    @Override
    public void onDisable() {

    }
    
}