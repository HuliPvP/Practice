package net.practice.practice.command.commands;

import net.practice.practice.game.player.Profile;
import net.practice.practice.game.player.data.InventorySnapshot;
import net.practice.practice.util.command.Command;
import net.practice.practice.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class InventoryCommand {

    @Command(name = "inventory", aliases = { "inv" }, playerOnly = true, description = "View a player's inventory.")
    public void onInventory(CommandArgs args) {
        if(args.length() != 1) {
            args.getPlayer().sendMessage(ChatColor.RED + "/" + args.getLabel() + " <player>");
            return;
        }

        Player player = Bukkit.getPlayer(args.getArgs(0));
        if(player == null || !player.isOnline()) {
            args.getPlayer().sendMessage(ChatColor.RED + "The player '" + args.getArgs(0) + "' is not online.");
            return;
        }

        Profile profile = Profile.getByPlayer(player);
        if(profile.getRecentDuel() == null) {
            args.getPlayer().sendMessage(ChatColor.RED + "The player " + args.getArgs(0) + " doesn't have a recent inventory.");
            return;
        }

        InventorySnapshot recentSnapshot = profile.getRecentDuel().getSnapshots().stream()
                .filter(snapshot -> snapshot.getName().equals(player.getName()))
                .findFirst()
                .orElse(null);

        recentSnapshot.open(args.getPlayer());
    }
}
