package land.nub.practice.board.provider;

import land.nub.practice.board.BoardProvider;
import land.nub.practice.game.duel.Duel;
import land.nub.practice.game.duel.DuelState;
import land.nub.practice.game.duel.type.SoloDuel;
import land.nub.practice.game.player.Profile;
import land.nub.practice.util.PlayerUtils;
import land.nub.practice.util.TimeUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpectatingProvider implements BoardProvider {

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();

        Profile profile = Profile.getByPlayer(player);
        Duel duel = profile.getSpectating();
        switch(duel.getType()) {
            case ONE_VS_ONE: {
                SoloDuel soloDuel = (SoloDuel) duel;
                if(duel.getState() == DuelState.PLAYING) {
                    lines.add("&eDuration: &7" + TimeUtils.msToMMSS(System.currentTimeMillis() - duel.getStartTime()));
                    lines.add("&a" + soloDuel.getPlayerOne().getName() + " &7(" + PlayerUtils.getPing(soloDuel.getPlayerOne()) + "ms)");
                    lines.add("&c" + soloDuel.getPlayerTwo().getName() + " &7(" + PlayerUtils.getPing(soloDuel.getPlayerTwo()) + "ms)");
                } else if(duel.getState() == DuelState.STARTING) {
                    lines.add("&eStarting: &7" + duel.getCountDown());
                    lines.add("&a" + soloDuel.getPlayerOne().getName() + " &7(" + PlayerUtils.getPing(soloDuel.getPlayerOne()) + "ms)");
                    lines.add("&c" + soloDuel.getPlayerTwo().getName() + " &7(" + PlayerUtils.getPing(soloDuel.getPlayerTwo()) + "ms)");
                } else if(duel.getState() == DuelState.ENDED) {
                    lines.add("&eWinner: &7" + soloDuel.getWinner().getName());
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
