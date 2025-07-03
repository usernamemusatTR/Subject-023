package com.denek023.denek023.entity;

import com.denek023.denek023.Denek023;
import com.denek023.denek023.entity.ai.LookAtPlayerGoal;
import com.denek023.denek023.init.ModEntityTypes;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.ClipContext;
import net.minecraft.core.BlockPos;

import javax.annotation.Nonnull;
import java.util.List;

public class Denek023WatcherEntity extends Mob {
    private int lifetime = 0;

    public Denek023WatcherEntity(EntityType<? extends Denek023WatcherEntity> type, Level level) {
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

        BlockPos posBelow = this.blockPosition().below();
        if (this.level().getBlockState(posBelow).getFluidState().isSource()) {
            this.setOnGround(true);
            this.setDeltaMovement(this.getDeltaMovement().x, 0, this.getDeltaMovement().z);
            this.setPos(this.getX(), posBelow.getY() + 1.0, this.getZ());
        }

        if (!this.level().isClientSide) {
            ServerLevel world = (ServerLevel) this.level();
            List<ServerPlayer> players = world.getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(128.0));
            for (ServerPlayer player : players) {
                Vec3 entityHead = this.position().add(0, this.getBbHeight() * 0.9, 0);
                Vec3 playerEyes = player.getEyePosition();
                Vec3 toEntity = entityHead.subtract(playerEyes).normalize();
                Vec3 lookVec = player.getLookAngle().normalize();
                double dot = lookVec.dot(toEntity);
                double angle = Math.acos(dot) * (180.0 / Math.PI);

                HitResult hit = world.clip(new ClipContext(
                    playerEyes, entityHead,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    player
                ));
                boolean engelYok = hit.getType() == HitResult.Type.MISS || hit.getLocation().distanceTo(entityHead) < 1.0;

                if (angle < 5.0 && engelYok) {
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
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.27D);
    }

    protected void onSpawnCustom() {
        System.out.println("Denek023WatcherEntity spawned at: " + this.getX() + ", " + this.getY() + ", " + this.getZ());
    }

    public static void trySpawnWatcher(ServerLevel level, Player player) {
        double radius = 64.0;
        boolean watcherNearby = !level.getEntitiesOfClass(Denek023WatcherEntity.class,
            player.getBoundingBox().inflate(radius)).isEmpty();

        if (player.tickCount >= 800 && !watcherNearby && player.tickCount % 800 == 0 && level.random.nextDouble() < 0.2) {
            double minDist = 15.0;
            double maxDist = 60.0;
            double angle = level.random.nextDouble() * 2 * Math.PI;
            double distance = minDist + level.random.nextDouble() * (maxDist - minDist);

            double spawnX = player.getX() + Math.cos(angle) * distance;
            double spawnZ = player.getZ() + Math.sin(angle) * distance;
            double spawnY = player.getY() + 8;

            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(spawnX, spawnY, spawnZ);

            while (level.isEmptyBlock(pos) && pos.getY() > level.getMinBuildHeight()) {
                pos.move(0, -1, 0);
            }
            pos.move(0, 1, 0);

            boolean canSpawn =
                level.isEmptyBlock(pos) &&
                level.isEmptyBlock(pos.above()) &&
                level.getBlockState(pos.below()).isCollisionShapeFullBlock(level, pos.below());

            if (!canSpawn) return;

            double chance = level.random.nextDouble();
            if (chance >= 0.6) {
                int searchRadius = 8;
                BlockPos treePos = null;
                for (BlockPos checkPos : BlockPos.betweenClosed(
                        pos.offset(-searchRadius, -3, -searchRadius),
                        pos.offset(searchRadius, 3, searchRadius))) {
                    if (level.getBlockState(checkPos).is(net.minecraft.tags.BlockTags.LOGS)) {
                        treePos = checkPos.immutable();
                        break;
                    }
                }
                if (treePos != null) {
                    double dx = pos.getX() - player.getX();
                    double dz = pos.getZ() - player.getZ();
                    double len = Math.sqrt(dx * dx + dz * dz);
                    if (len > 0) {
                        dx /= len;
                        dz /= len;
                    }
                    BlockPos spawnPos = treePos.offset((int)(dx * 2), 0, (int)(dz * 2));
                    pos.set(spawnPos.getX(), treePos.getY() + 1, spawnPos.getZ());

                    canSpawn =
                        level.isEmptyBlock(pos) &&
                        level.isEmptyBlock(pos.above()) &&
                        level.getBlockState(pos.below()).isCollisionShapeFullBlock(level, pos.below());
                    if (!canSpawn) return;
                }
            }

            Denek023WatcherEntity watcher = ModEntityTypes.DENEK023_WATCHER.get().create(level);
            if (watcher != null) {
                watcher.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
                level.addFreshEntity(watcher);
            }
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return true;
    }
}