package net.practice.practice.game.queue.type;

import net.practice.practice.game.arena.Arena;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.type.SoloDuel;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.queue.Queue;
import net.practice.practice.game.queue.QueueType;
import org.bukkit.Bukkit;

public class UnrankedQueue extends Queue {

    public UnrankedQueue(Ladder ladder) {
        super(ladder, QueueType.UNRANKED);
    }

    @Override
    public void setup() {
        switch(getLadder().getDuelType()) {
            case ONE_VS_ONE: {
                Arena arena = Arena.getRandomArena(getLadder());
                if(arena != null && Bukkit.getPlayer(getQueued().get(0)) != null && Bukkit.getPlayer(getQueued().get(1)) != null) {
                    Profile profileOne = Profile.getByUuid(getQueued().get(0)), profileTwo = Profile.getByUuid(getQueued().get(1));
                    profileOne.leaveQueue(false);
                    profileTwo.leaveQueue(false);

                    Duel duel = new SoloDuel(Arena.getRandomArena(), getLadder(), profileOne.getPlayer(), profileTwo.getPlayer());
                    duel.preStart();
                }
            }
        }
    }
}
