package net.noobsters.core.paper;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityDismountEvent;

import net.md_5.bungee.api.ChatColor;
import net.noobsters.core.paper.mechanics.TimerTickEvent;

public class GlobalListeners implements Listener {
    Core instance;

    Random random = new Random();

    public GlobalListeners(Core instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onGiants(CreatureSpawnEvent e) {
        LivingEntity entity = e.getEntity();
        if (entity instanceof Giant) {
            Giant giant = (Giant) entity;
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*10000, 3, false, false));
            Husk titanRider = (Husk) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.HUSK);
            titanRider.setCustomName("Titan");
            titanRider.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*10000, 1, false, false));
            giant.addPassenger(titanRider);
        }

    }

    @EventHandler
    public void onAttack(PlayerDeathEvent e){
        Player player = e.getEntity();
        if(player.getLastDamageCause().getCause() == DamageCause.CUSTOM){
            Bukkit.broadcastMessage(player.getName() + " ha sido deborado por un Titan");
        }

    }

    @EventHandler
    public void killPassengers(EntityDismountEvent e) {
        if (e.getEntity() instanceof Husk) {
            Husk husk = (Husk) e.getEntity();
            husk.damage(100);
        }
    }

    @EventHandler
    public void onTick(TimerTickEvent e) {
        Bukkit.getScheduler().runTask(instance, () -> {
            refreshAI();
        });
    }

    public float getDistance(Location val1, Location val2){

        double x1 = val1.getX();
        double z1 = val1.getZ();

        double x2 = val2.getX();
        double z2 = val2.getZ();

        return (float) Math.abs(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(z2 - z1, 2)));
    }

    public void refreshAI() {
        List<Player> players = Bukkit.getOnlinePlayers().parallelStream().collect(Collectors.toList());

        players.forEach(player -> {

            World world = player.getWorld();
            Location loc = player.getLocation();

            int y = (int) loc.getY();

            List<Entity> list = world.getEntities().parallelStream().filter(entity -> entity instanceof Giant 
                && (getDistance(entity.getLocation(), loc) <= 20) && (Math.abs(y)-Math.abs(entity.getLocation().getY())) <= 20).collect(Collectors.toList());
            
            list.forEach(entity -> {

                Giant giant = (Giant) entity;

                int timer = (int) instance.getTimer().getGameTime();
                if (timer % 3 == 0) {
                    giant.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 2, 19));
                }

                if(getDistance(giant.getLocation(), loc) <= 3){
                    player.damage(10);
                }

                Husk rider = (Husk) giant.getPassengers().get(0);
                if (rider.getCustomName() != null && rider.getCustomName() == "Titan") {

                    if (rider.getTarget() == null || !(rider.getTarget() instanceof Player)) {
                        Player randomPlayer = players.get(random.nextInt(players.size()));
                        rider.setTarget(randomPlayer);
                    }
                }

            });
        });
    }
}
