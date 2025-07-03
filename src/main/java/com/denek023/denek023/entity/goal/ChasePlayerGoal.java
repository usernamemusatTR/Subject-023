package com.denek023.denek023.entity.goal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import com.denek023.denek023.entity.Denek023AttackerEntity;

import java.util.EnumSet;

public class ChasePlayerGoal extends Goal {
    private final Denek023AttackerEntity mob;
    private Player target;
    private int stuckTicks = 0;
    private double lastX, lastY, lastZ;

    public ChasePlayerGoal(Denek023AttackerEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        return target instanceof Player && target.isAlive();
    }

    @Override
    public void start() {
        this.target = (Player) mob.getTarget();
        this.lastX = mob.getX();
        this.lastY = mob.getY();
        this.lastZ = mob.getZ();
        stuckTicks = 0;
    }

    @Override
    public void stop() {
        this.target = null;
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (target != null && target.isAlive()) {
            double distance = mob.distanceTo(target);
            if (distance > 2.5D) {
                mob.getNavigation().moveTo(target, 1.2D);
            } else {
                mob.getNavigation().stop();
            }
            mob.getLookControl().setLookAt(target, 60.0F, 60.0F);
        }
    }
}