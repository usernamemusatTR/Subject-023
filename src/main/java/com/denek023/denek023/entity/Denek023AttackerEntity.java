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
import net.minecraft.world.entity.Entity;

public class Denek023AttackerEntity extends net.minecraft.world.entity.PathfinderMob {
    public int chaseTicks = 0;
    public int waitForChaseTicks = 0;

    public Denek023AttackerEntity(EntityType<? extends net.minecraft.world.entity.PathfinderMob> type, Level world) {
        super(type, world);
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
        if (source.getEntity() instanceof net.minecraft.world.entity.player.Player) {
            return false;
        }
        return super.hurt(source, amount);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 40.0D)
            .add(Attributes.ATTACK_DAMAGE, 10.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.5D);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            if (this.getPersistentData().getBoolean("chaseStarted")) {
                chaseTicks++;
                if (chaseTicks >= 400) {
                    this.level().playSound(null, this.blockPosition(), ModSounds.DISCARDED.get(), SoundSource.HOSTILE, 4.0F, 1.0F);
                    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new com.denek023.denek023.event.ChaseMusicEvent(false));
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

            if (this.getTarget() != null && !this.getTarget().isAlive()) {
                this.setTarget(null);
            }
        }
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