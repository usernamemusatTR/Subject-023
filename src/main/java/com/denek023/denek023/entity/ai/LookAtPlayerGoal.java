package com.denek023.denek023.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

public class LookAtPlayerGoal extends Goal {
    private final Mob entity;
    private final double maxDistance;
    private Player targetPlayer;

    public LookAtPlayerGoal(Mob entity, double maxDistance) {
        this.entity = entity;
        this.maxDistance = maxDistance;
    }

    @Override
    public boolean canUse() {
        this.targetPlayer = entity.level().getNearestPlayer(entity, maxDistance);
        return targetPlayer != null;
    }

    @Override
    public void tick() {
        if (targetPlayer != null) {
            entity.getLookControl().setLookAt(targetPlayer, 60.0F, 60.0F);
        }
    }
}

// In the Denek023Entity class, add the following line in the appropriate method (e.g., constructor or initGoals):
// this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, 6.0F));
