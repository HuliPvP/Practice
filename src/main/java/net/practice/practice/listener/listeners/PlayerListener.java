package net.practice.practice.listener.listeners;

import net.practice.practice.game.player.Profile;
import net.practice.practice.spawn.SpawnHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
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
    public void onInteract(PlayerInteractEvent event) {
        Profile profile = Profile.getByPlayer(event.getPlayer());
        ItemStack item = event.getItem();

        if (item == null) return;

        switch (profile.getProfileState()) {
            case LOBBY:
                if (item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains("Ranked")) {
                    // TODO: xd
                }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        handleLeave(event);
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        handleLeave(event);
    }

    private void handleLeave(PlayerEvent event) {
        Profile profile = Profile.getRemovedProfile(event.getPlayer());

        profile.save();
    }
}
