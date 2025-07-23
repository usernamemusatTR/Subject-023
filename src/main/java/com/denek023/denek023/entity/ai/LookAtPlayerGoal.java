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
            entity.getLookControl().setLookAt(targetPlayer, 200.0F, 200.0F);
        }
    }
}
