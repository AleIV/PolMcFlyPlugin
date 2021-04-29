package net.noobsters.core.paper.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import net.noobsters.core.paper.Core;

public class Timer extends BukkitRunnable{
    Core instance;
    
    @Getter long gameTime = 0;
    @Getter long startTime = 0;

    public Timer(Core instance) {
        this.instance = instance;
        this.startTime = System.currentTimeMillis();

    }

    @Override
    public void run() {

        int new_time = (int) (Math.floor((System.currentTimeMillis() - startTime) / 1000.0));

        // set new gametime
        gameTime = new_time;

        Bukkit.getPluginManager().callEvent(new TimerTickEvent(new_time, true));
    }
}
