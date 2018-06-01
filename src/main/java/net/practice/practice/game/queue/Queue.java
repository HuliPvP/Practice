package net.practice.practice.game.queue;

import lombok.Getter;
import net.practice.practice.game.ladder.Ladder;

public abstract class Queue {

    @Getter public Ladder ladder;
    @Getter public QueueType queueType;
    @Getter public QueueRange queueRange;

    public Queue(Ladder ladder, QueueType queueType, int elo) {
        this.ladder = ladder;
        this.queueType = queueType;
        this.queueRange = new QueueRange(elo, 5);
    }

    public boolean canQueueWith(Queue other) {
        if (!getLadder().getName().equals(other.getLadder().getName()))
            return false;
        if (!getQueueType().equals(other.getQueueType()))
            return false;
        if (!getQueueRange().isInRange(other.getQueueRange().getMiddle()))
            return false;
        if (getLadder().isBuildable() != other.getLadder().isBuildable())
            return false;
        if (getLadder().isEditable() != other.getLadder().isEditable())
            return false;
        if (getLadder().isCombo() != other.getLadder().isCombo())
            return false;
        if (getLadder().isRanked() != other.getLadder().isRanked())
            return false;
        return true;
    }
}
