package net.practice.practice.listener.listeners;

import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.practice.practice.Practice;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.DuelEndReason;
import net.practice.practice.game.duel.type.SoloDuel;
import net.practice.practice.game.player.Profile;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DuelListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Profile profile = Profile.getByPlayer(event.getEntity());
        if(profile.getCurrentDuel() == null)
            return;

        Duel duel = profile.getCurrentDuel();
        duel.saveInventory(profile.getUuid());
        switch(duel.getType()) {
            case ONE_VS_ONE: {
                SoloDuel soloDuel = (SoloDuel) duel;

                soloDuel.setWinner(soloDuel.getPlayerOne() != profile.getPlayer() ? soloDuel.getPlayerOne() : soloDuel.getPlayerTwo());
                soloDuel.saveInventory(soloDuel.getWinner().getUniqueId());
                soloDuel.end(DuelEndReason.DIED);
                break;
            }
            case TWO_VS_TWO: {

                break;
            }
            case TEAM_VS_TEAM: {

                break;
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if(event.getEntity().isDead())
                    ((CraftPlayer) event.getEntity()).getHandle().playerConnection.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
            }
        }.runTaskLater(Practice.getInstance(), 5L);

        new BukkitRunnable() {
            @Override
            public void run() {
                event.getDrops().clear();
            }
        }.runTaskLaterAsynchronously(Practice.getInstance(), 40L);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent event) {
        handleLeave(event);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onKick(PlayerKickEvent event) {
        handleLeave(event);
    }

    private void handleLeave(PlayerEvent event) {
        Profile profile = Profile.getByPlayer(event.getPlayer());
        if(profile.getCurrentDuel() == null)
            return;

        Duel duel = profile.getCurrentDuel();
        duel.saveInventory(profile.getUuid());
        switch(duel.getType()) {
            case ONE_VS_ONE: {
                SoloDuel soloDuel = (SoloDuel) duel;

                soloDuel.setWinner(soloDuel.getPlayerOne() != profile.getPlayer() ? soloDuel.getPlayerOne() : soloDuel.getPlayerTwo());
                soloDuel.end(DuelEndReason.QUIT);
                break;
            }
            case TWO_VS_TWO: {

                break;
            }
            case TEAM_VS_TEAM: {

                break;
            }
        }
    }
}
