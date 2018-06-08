package net.practice.practice.listener.listeners;

import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.DuelState;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.player.data.ProfileState;
import net.practice.practice.inventory.inventories.RankedInv;
import net.practice.practice.inventory.inventories.UnrankedInv;
import net.practice.practice.spawn.SpawnHandler;
import net.practice.practice.util.chat.C;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
        new Profile(event.getUniqueId(), true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        Profile profile = Profile.getByPlayer(event.getPlayer());

        SpawnHandler.spawn(event.getPlayer());
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        Profile profile = Profile.getByPlayer(player);

        switch (profile.getProfileState()) {
            case PLAYING:
                break;
            default:
                player.setFoodLevel(20);
                player.setSaturation(0);
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Profile profile = Profile.getByPlayer(event.getPlayer());

        if(event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        switch (profile.getProfileState()) {
            case PLAYING:
                break;
            case BUILDING:
                break;
            default:
                event.setCancelled(true);
                event.getPlayer().updateInventory();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(event.getEntityType() != EntityType.PLAYER)
            return;

        Player player = (Player) event.getEntity();
        Profile profile = Profile.getByPlayer(player);

        switch(profile.getProfileState()) {
            case PLAYING: {
                if(event instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
                    Duel duel = profile.getCurrentDuel();
                    if(duel.getState() != DuelState.PLAYING) {
                        event.setCancelled(true);
                        return;
                    }

                    Player other = (Player) e.getDamager();
                    if(!duel.hasPlayer(other))
                        event.setCancelled(true);
                    else
                        event.setCancelled(false);
                }
                break;
            }
            default:
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        ItemStack item = event.getItem();

        if (item == null || item.getItemMeta() == null
                || !(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                || player.getGameMode().equals(GameMode.CREATIVE)) return;

        if(item.getItemMeta().hasDisplayName()) {
            String display = item.getItemMeta().getDisplayName();

            switch(profile.getProfileState()) {
                case LOBBY: {
                    if(display.contains("Unranked"))
                        UnrankedInv.openInventory(player);
                    else if(display.contains("Ranked"))
                        RankedInv.openInventory(player);
                    else if(display.contains("Stats"))
                        Bukkit.dispatchCommand(player, "stats");
                    else if(display.contains("Last Queue")) {
                        profile.addToQueue(profile.getLastQueue());
                    }
                    event.setCancelled(true);
                    break;
                }
                case QUEUING: {
                    if(display.contains("Leave Queue"))
                        profile.leaveQueue(true, false);
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getCurrentItem() == null || event.getClickedInventory() == null)
            return;
        if(event.getWhoClicked().getType() != EntityType.PLAYER)
            return;

        Player player = (Player) event.getWhoClicked();
        Profile profile = Profile.getByPlayer(player);

        ItemStack item = event.getCurrentItem();

        if(event.getClickedInventory().getTitle() != null && event.getClickedInventory().getTitle().contains("'s Inventory")) {
            event.setCancelled(true);
            return;
        }

        if(profile.getProfileState() == ProfileState.LOBBY && event.getClickedInventory().getName() != null && !player.getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);

            if(item == null || item.getItemMeta() == null) return;

            if(event.getClickedInventory().getTitle().contains("Unranked")) {
                if(item.getItemMeta().hasDisplayName()) {
                    Ladder ladder = Ladder.getLadder(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                    if(ladder != null) {
                        profile.addToQueue(ladder.getUnrankedQueue());

                        player.sendMessage(C.color("&f\u00BB &eJoined the queue for Unranked " + ladder.getDisplayName() + "."));
                        player.closeInventory();
                    }
                }
            } else if(event.getClickedInventory().getTitle().contains("Ranked")) {
                if(item.getItemMeta().hasDisplayName()) {
                    Ladder ladder = Ladder.getLadder(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                    if(ladder != null) {
                        profile.addToQueue(ladder.getRankedQueue());

                        player.sendMessage(C.color("&f\u00BB &eJoined the queue for Ranked " + ladder.getDisplayName() + "."));
                        player.closeInventory();
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        handleLeave(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onKick(PlayerKickEvent event) {
        handleLeave(event);
    }

    private void handleLeave(PlayerEvent event) {
        Profile profile = Profile.getRemovedProfile(event.getPlayer());

        if(profile.isQueueing())
            profile.removeFromQueue();

        profile.save();
    }
}
