package com.denek023.denek023.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundSource;
import com.denek023.denek023.ModSounds;
import com.denek023.denek023.entity.goal.ChaseAndAttackPlayerGoal;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import com.denek023.denek023.entity.DoorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import net.minecraft.world.entity.ai.navigation.PathNavigation;

public class Denek023AttackerEntity extends Denek023Entity {


    private boolean hasJumpscared = false;
    public int chaseTicks = 0;
    public int waitForChaseTicks = 0;

    public Denek023AttackerEntity(EntityType<? extends Denek023Entity> type, Level level) {
        super(type, level);
    }

    public void startChase() {
        this.getPersistentData().putBoolean("chaseStarted", true);
        this.chaseTicks = 0;
        this.waitForChaseTicks = 0;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new ChaseAndAttackPlayerGoal(this));
    }

    private static final ResourceKey<DamageType> OUT_OF_WORLD_KEY =
        ResourceKey.create(net.minecraft.core.registries.Registries.DAMAGE_TYPE, new ResourceLocation("out_of_world"));
    private static final ResourceKey<DamageType> GENERIC_KEY =
        ResourceKey.create(net.minecraft.core.registries.Registries.DAMAGE_TYPE, new ResourceLocation("generic"));

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 70.0D)
            .add(Attributes.ATTACK_DAMAGE, 4.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.42D);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            if (this.getPersistentData().getBoolean("chaseStarted")) {
                chaseTicks++;
                if (chaseTicks >= 400) {
                    this.level().playSound(null, this.blockPosition(), ModSounds.DISCARDED.get(), SoundSource.HOSTILE, 4.0F, 1.0F);
                    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new com.denek023.event.ChaseMusicEvent(false));
                    this.discard();
                    return;
                }
                if (this.getTarget() == null || !this.getTarget().isAlive()) {
                    this.level().playSound(null, this.blockPosition(), ModSounds.DISCARDED.get(), SoundSource.HOSTILE, 4.0F, 1.0F);
                    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new com.denek023.event.ChaseMusicEvent(false));
                    this.discard();
                    return;
                }
            } else {
                waitForChaseTicks++;
                if (waitForChaseTicks >= 400) {
                    this.discard();
                    return;
                }
            }
        }
        if (!this.level().isClientSide && this.getTarget() != null && this.getTarget().isAlive()) {
            Vec3 look = this.getLookAngle().normalize();
            BlockPos base = BlockPos.containing(this.getX(), this.getY(), this.getZ());
            Level level = this.level();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos check = base.offset(dx + (int)Math.round(look.x), 0, dz + (int)Math.round(look.z));
                    DoorHelper.tryOpenDoor(level, check);
                }
            }
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, net.minecraft.world.damagesource.DamageSource source) {
        return false;
    }

    private boolean canSeePlayer(net.minecraft.world.entity.player.Player player) {
        Vec3 look = player.getLookAngle().normalize();
        Vec3 diff = this.position().subtract(player.position()).normalize();
        double dot = look.dot(diff);
        return dot > 0.95;
    }

    public void resetChaseTicks() {
        this.chaseTicks = 0;
    }

    public void customDiscard() {
        super.discard();
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        return super.doHurtTarget(target);
    }
}