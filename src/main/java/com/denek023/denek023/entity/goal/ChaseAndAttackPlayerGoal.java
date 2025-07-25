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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;

public class ChaseAndAttackPlayerGoal extends Goal {
    private final Denek023AttackerEntity mob;
    private Player target;
    private int attackCooldown = 0;
    private int stuckTicks = 0;
    private int pathResetTicks = 0;
    private double lastX = 0, lastY = 0, lastZ = 0;

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
        this.lastX = mob.getX();
        this.lastY = mob.getY();
        this.lastZ = mob.getZ();
        stuckTicks = 0;
    }

    @Override
    public void stop() {
        this.target = null;
    }

    @Override
    public void tick() {
        if (attackCooldown > 0) attackCooldown--;
        if (target != null && target.isAlive()) {
            mob.getLookControl().setLookAt(target, 60.0F, 60.0F);
            double distance = mob.distanceTo(target);

            double dx = mob.getX() - lastX;
            double dy = mob.getY() - lastY;
            double dz = mob.getZ() - lastZ;
            double movedSq = dx * dx + dy * dy + dz * dz;
            if (movedSq < 0.01) {
                stuckTicks++;
            } else {
                stuckTicks = 0;
                pathResetTicks = 0;
            }
            lastX = mob.getX();
            lastY = mob.getY();
            lastZ = mob.getZ();

            if (stuckTicks > 20 && pathResetTicks == 0) {
                mob.getNavigation().stop();
                pathResetTicks = 10;
            }
            if (pathResetTicks > 0) {
                pathResetTicks--;
                if (pathResetTicks == 0) {
                    mob.getNavigation().moveTo(target, 1.2D);
                    stuckTicks = 0;
                }
                return;
            }
            mob.getNavigation().moveTo(target, 1.2D);


            if (Math.abs(mob.getDeltaMovement().y) < 0.01) {
                Vec3 look = mob.getLookAngle().normalize();
                double checkX = mob.getX() + look.x * 0.8;
                double checkY = mob.getY();
                double checkZ = mob.getZ() + look.z * 0.8;
                Level level = mob.level();
                BlockPos frontPos = BlockPos.containing(checkX, checkY, checkZ);
                BlockPos upPos = frontPos.above();
                boolean blockInFront = !level.getBlockState(frontPos).isAir();
                boolean spaceAbove = level.getBlockState(upPos).isAir();
                if (blockInFront && spaceAbove) {
                    mob.getJumpControl().jump();
                }
            }

            if (distance <= 2.0D && attackCooldown <= 0) {
                boolean didAttack = mob.doHurtTarget(target);
                attackCooldown = 10;
                if (didAttack && !mob.getPersistentData().getBoolean("jumpscareDone")) {
                    if (target instanceof ServerPlayer player && !target.isAlive()) {
                        if (mob.getJumpscareType().equals("attacker")) {
                            mob.level().playSound(null, mob.blockPosition(), ModSounds.JUMPSCARE2.get(), SoundSource.HOSTILE, 4.0F, 1.0F);
                        } else {
                            mob.level().playSound(null, mob.blockPosition(), ModSounds.JUMPSCARE.get(), SoundSource.HOSTILE, 4.0F, 1.0F);
                        }
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0));
                        com.denek023.denek023.Denek023.CHANNEL.send(
                            net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                            new com.denek023.client.JumpscarePacket()
                        );
                        mob.getPersistentData().putBoolean("jumpscareDone", true);
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new com.denek023.event.ChaseMusicEvent(false));
                    }
                }
            }
        }
    }
}