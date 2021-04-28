package net.noobsters.core.paper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class GlobalListeners implements Listener{
    Core instance;

    public GlobalListeners(Core instance){
        this.instance = instance;
    }

    @EventHandler
    public void onGiants(CreatureSpawnEvent e){
        LivingEntity entity = e.getEntity();
        if(entity instanceof Giant){
            Bukkit.broadcastMessage("GIANT");
        }

    }
}
