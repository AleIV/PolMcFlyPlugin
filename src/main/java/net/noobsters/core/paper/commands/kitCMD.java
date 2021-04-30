package net.noobsters.core.paper.commands;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.noobsters.core.paper.Core;

public class kitCMD implements CommandExecutor {

    DecimalFormat numberFormat = new DecimalFormat("#.00");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            double x = player.getLocation().getX();
            double y = player.getLocation().getY();
            double z = player.getLocation().getZ();

            HashMap<String, Long> map1 = Core.getInstance().getCoolDown1();
            HashMap<String, Long> map2 = Core.getInstance().getCoolDown2();

            String uuid = player.getUniqueId().toString();

            switch (args[0].toLowerCase()) {
                case "soldado": {
                    if (!map1.containsKey(uuid)) {

                        map1.put(player.getUniqueId().toString(), System.currentTimeMillis() + 86400_000);

                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                "clone 239 67 -90 239 67 -90 " + x + " " + y + 1 + " " + z);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                "setblock " + x + " " + y + 1 + " " + z + " minecraft:air 0 destroy");

                        player.sendMessage(ChatColor.GREEN + "Has tomado el kit soldado.");
                        
                    } else {
                        long differential = map1.get(uuid) - System.currentTimeMillis();
                        player.sendMessage(ChatColor.RED + "Quedan " + numberFormat.format((differential / 1000.0D) / 60)
                                + " minutos para que puedas volver a tomar este kit.");
                    }
                }

                    break;
                case "equipo": {
                    if (!map2.containsKey(uuid)) {

                        map2.put(player.getUniqueId().toString(), System.currentTimeMillis() + 1800_000);

                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                "clone 241 67 -90 241 67 -90 " + x + " " + y + 1 + " " + z);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                "setblock " + x + " " + y + 1 + " " + z + " minecraft:air 0 destroy");
                        
                        player.sendMessage(ChatColor.GREEN + "Has tomado el kit equipo.");

                    } else {
                        long differential = map2.get(uuid) - System.currentTimeMillis();
                        player.sendMessage(ChatColor.RED + "Quedan " + numberFormat.format((differential / 1000.0D) / 60)
                                + " minutos para que puedas volver a tomar este kit.");
                    }
                }
                default:
                    break;
            }

        }
        return false;
    }

}
