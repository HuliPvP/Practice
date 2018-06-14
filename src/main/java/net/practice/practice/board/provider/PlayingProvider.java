package net.practice.practice.board.provider;

import net.practice.practice.board.BoardProvider;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.DuelState;
import net.practice.practice.game.duel.type.SoloDuel;
import net.practice.practice.game.player.Profile;
import net.practice.practice.util.PlayerUtils;
import net.practice.practice.util.TimeUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayingProvider implements BoardProvider {

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();

        Profile profile = Profile.getByPlayer(player);
        Duel duel = profile.getCurrentDuel();
        switch(duel.getType()) {
            case ONE_VS_ONE: {
                SoloDuel soloDuel = (SoloDuel) duel;
                Player opponent = (soloDuel.getPlayerOne() == player ? soloDuel.getPlayerTwo() : soloDuel.getPlayerOne());
                if(duel.getState() == DuelState.PLAYING) {
                    lines.add("&6Opponent: &7" + opponent.getName());
                    lines.add("&6Duration: &7" + TimeUtils.msToMMSS(System.currentTimeMillis() - duel.getStartTime()));
                    lines.add("&6Ping: &7" + PlayerUtils.getPing(player) + " &f| &7" + PlayerUtils.getPing(opponent));
                } else if(duel.getState() == DuelState.STARTING) {
                    lines.add("&6Opponent: &7" + opponent.getName());
                    lines.add("&6Starting: &7" + duel.getCountDown());
                } else if(duel.getState() == DuelState.ENDED) {
                    lines.add("&6Winner: &7" + soloDuel.getWinner().getName());
                }
                break;
            }
            case TWO_VS_TWO: {

                break;
            }
            case TEAM_VS_TEAM: {

                break;
            }
            case FREE_FOR_ALL:
                break;
        }

        return lines;
    }
}
