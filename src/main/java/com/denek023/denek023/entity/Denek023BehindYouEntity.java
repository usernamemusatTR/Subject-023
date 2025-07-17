package com.denek023.denek023.entity;

import com.denek023.denek023.entity.ai.LookAtPlayerGoal;
import com.denek023.denek023.init.ModEntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;

import java.util.List;

public class Denek023BehindYouEntity extends Denek023Entity {
    private int lifetime = 0;

    public Denek023BehindYouEntity(EntityType<? extends Denek023Entity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, 60.0D));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            ServerLevel world = (ServerLevel) this.level();
            List<ServerPlayer> players = world.getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(32.0));
            for (ServerPlayer player : players) {
                Vec3 entityCenter = this.position().add(0, this.getBbHeight() / 2.0, 0);
                Vec3 playerEyes = player.getEyePosition();
                Vec3 toEntity = entityCenter.subtract(playerEyes).normalize();
                Vec3 lookVec = player.getLookAngle().normalize();
                double dot = lookVec.dot(toEntity);
                double angle = Math.acos(dot) * (180.0 / Math.PI);

                HitResult hit = world.clip(new ClipContext(
                    playerEyes, entityCenter, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player
                ));
                boolean engelYok = hit.getType() == HitResult.Type.MISS || hit.getLocation().distanceTo(entityCenter) < 1.0;

                if (angle < 30.0 && engelYok) {
                    this.discard();
                    break;
                }
            }
        }

        lifetime++;
        if (lifetime >= 400) {
            this.discard();
        }
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 1.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    public static void spawnEntity(Level level) {
        EntityType<Denek023BehindYouEntity> type = ModEntityTypes.DENEK023_BEHIND_YOU.get();
        if (type != null) {
            Denek023BehindYouEntity entity = type.create(level);
            if (entity != null) {
            }
        }
    }
}