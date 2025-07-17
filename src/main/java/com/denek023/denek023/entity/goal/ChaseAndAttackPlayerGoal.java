package com.denek023.denek023.entity.goal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;

import com.denek023.denek023.ModSounds;
import com.denek023.denek023.entity.Denek023AttackerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.sounds.SoundSource;

import java.util.EnumSet;

public class ChaseAndAttackPlayerGoal extends Goal {
    private final Denek023AttackerEntity mob;
    private Player target;

    public ChaseAndAttackPlayerGoal(Denek023AttackerEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        return target instanceof Player && target.isAlive();
    }

    @Override
    public void start() {
        this.target = (Player) mob.getTarget();
    }

    @Override
    public void stop() {
        this.target = null;
    }

    @Override
    public void tick() {
        if (target != null && target.isAlive()) {
            mob.getLookControl().setLookAt(target, 60.0F, 60.0F);
            double distance = mob.distanceTo(target);
            mob.getNavigation().moveTo(target, 1.2D);
            if (distance <= 2.0D) {
                if (mob.doHurtTarget(target)) {
                    if (!mob.getPersistentData().getBoolean("jumpscareDone")) {
                        mob.level().playSound(null, mob.blockPosition(), ModSounds.JUMPSCARE.get(), SoundSource.HOSTILE, 4.0F, 1.0F);
                        if (target instanceof ServerPlayer player) {
                            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0));
                            com.denek023.denek023.Denek023.CHANNEL.send(
                                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                                new com.denek023.client.JumpscarePacket()
                            );
                        }
                        mob.getPersistentData().putBoolean("jumpscareDone", true);
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new com.denek023.denek023.event.ChaseMusicEvent(false));
                        mob.discard();
                    }
                }
            }
        }
    }
}