package net.noobsters.core.paper;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
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

    @EventHandler(priority = EventPriority.LOW)
    public void setFormat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        Bukkit.getScheduler().runTask(instance, () -> {

            Player player = e.getPlayer();
            String name = player.getName();
            String msg = e.getMessage();

            int stat = player.getStatistic(Statistic.KILL_ENTITY, EntityType.ZOMBIE);

            if (player.hasPermission("prefix.fundador")) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&r[&e&l" + stat
                        + "&r] &r★ &6✦&r ★ &6&k!!!&r &lFUNDADOR&r &6&k!!!&r ★ &6✦&r ★" + name + ": &f" + msg));

            } else if (player.hasPermission("prefix.capitan")) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&r[&e&l" + stat
                        + "&r] &r★ &6★&r &k!!!&r &6&lCAPITÁN&r &k!!!&r &6★&r ★ " + name + ": &f" + msg));

            } else {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f" + name + ": &f" + msg));
            }
        });
    }

    @EventHandler
    public void stats(PlayerStatisticIncrementEvent e) {
        if (e.getStatistic() == Statistic.KILL_ENTITY && (e.getEntityType() == EntityType.ZOMBIE && e.getEntityType() != EntityType.GIANT)) {
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onGiants(CreatureSpawnEvent e) {
        LivingEntity entity = e.getEntity();
        if (entity instanceof Giant) {
            Giant giant = (Giant) entity;
            giant.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 10000, 1, false, false));
            giant.setHealth(60);
            Husk titanRider = (Husk) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.HUSK);
            titanRider.setCustomName("Titan");
            titanRider.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 10000, 1, false, false));
            giant.addPassenger(titanRider);

        } else if (entity.getType() == EntityType.ZOMBIE && entity.getLocation().getY() > 50 && random.nextBoolean()) {
            entity.getWorld().spawnEntity(entity.getLocation(), EntityType.GIANT);
        }

    }

    @EventHandler
    public void killGiant(EntityDeathEvent e){
        LivingEntity entity =  e.getEntity();
        if(entity.getType() ==  EntityType.GIANT && entity.getKiller() != null){
            Player player = entity.getKiller();
            player.setStatistic(Statistic.KILL_ENTITY, EntityType.ZOMBIE, player.getStatistic(Statistic.KILL_ENTITY, EntityType.ZOMBIE) +1);

        }
    }

    @EventHandler
    public void onAttack(PlayerDeathEvent e) {
        Player player = e.getEntity();
        if (player.getLastDamageCause().getCause() == DamageCause.CUSTOM) {
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

    public float getDistance(Location val1, Location val2) {

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

            List<Entity> list = world.getEntities().parallelStream()
                    .filter(entity -> entity instanceof Giant && (getDistance(entity.getLocation(), loc) <= 20)
                            && (Math.abs(y) - Math.abs(entity.getLocation().getY())) <= 20)
                    .collect(Collectors.toList());

            list.forEach(entity -> {

                Giant giant = (Giant) entity;

                int timer = (int) instance.getTimer().getGameTime();
                if (timer % 5 == 0) {
                    giant.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 2, 25));
                }

                if (getDistance(giant.getLocation(), loc) <= 5) {
                    player.damage(5);
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
