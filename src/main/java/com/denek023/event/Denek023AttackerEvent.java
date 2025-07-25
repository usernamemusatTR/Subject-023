package com.denek023.event;

import com.denek023.denek023.entity.Denek023AttackerEntity;
import com.denek023.denek023.Denek023;
import com.denek023.denek023.ModSounds;
import com.denek023.denek023.init.ModEntityTypes;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundSource;
import java.util.Random;

@Mod.EventBusSubscriber(modid = Denek023.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Denek023AttackerEvent {
    private static Denek023AttackerEntity attacker = null;

    private static final double SPAWN_CHANCE_PER_TICK = 1.0 / 2000.0;
    private static final int MIN_DIST = 20;
    private static final int MAX_DIST = 35;

    private static final Random rand = new Random();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END || event.player.level().isClientSide) return;
        Player player = event.player;
        Level level = player.level();

        boolean isNight = !level.isDay() && level.canSeeSky(player.blockPosition());
        if (!isNight) {
            resetAttacker(level);
            return;
        }

        if ((attacker == null || !attacker.isAlive()) && rand.nextDouble() < SPAWN_CHANCE_PER_TICK) {
            Vec3 spawnPos = findSafeSpawn(player, level);
            if (spawnPos != null) {
                attacker = new Denek023AttackerEntity(ModEntityTypes.DENEK023_ATTACKER.get(), level);
                attacker.setJumpscareType("attacker");
                attacker.moveTo(spawnPos.x, spawnPos.y, spawnPos.z);
                level.addFreshEntity(attacker);
                level.playSound(null, player.blockPosition(), ModSounds.COMECLOSER.get(), SoundSource.HOSTILE, 5.0F, 1.0F);
            }
        }

        if (attacker != null && attacker.isAlive()) {
            if (!player.isAlive() && !attacker.getPersistentData().getBoolean("jumpscareDone")) {
                attacker.getPersistentData().putBoolean("jumpscareDone", true);
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new com.denek023.event.ChaseMusicEvent(false));
                level.playSound(null, attacker.blockPosition(), ModSounds.JUMPSCARE2.get(), SoundSource.HOSTILE, 5.0F, 1.0F);
                if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
                    com.denek023.denek023.Denek023.CHANNEL.sendTo(
                        new com.denek023.client.JumpscarePacket(),
                        sp.connection.connection,
                        net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT
                    );
                }
                attacker.discard();
                resetAttacker(level);
                return;
            }
            if (!attacker.getPersistentData().getBoolean("chaseStarted")) {
                Vec3 look = player.getLookAngle().normalize();
                Vec3 diff = attacker.position().subtract(player.position()).normalize();
                double dot = look.dot(diff);
                if (dot > 0.95 && player.hasLineOfSight(attacker)) {
                    attacker.getPersistentData().putBoolean("chaseStarted", true);
                    attacker.setTarget(player);

                    level.playSound(null, attacker.blockPosition(), ModSounds.CHASE.get(), SoundSource.HOSTILE, 5.0F, 1.0F);
                    level.playSound(null, player.blockPosition(), ModSounds.SCREAM2.get(), SoundSource.HOSTILE, 5.0F, 1.0F);
                }
            }
        }
    }

    private static void resetAttacker(Level level) {
        attacker = null;
    }

    private static Vec3 findSafeSpawn(Player player, Level level) {
        for (int tries = 0; tries < 40; tries++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double distance = MIN_DIST + rand.nextDouble() * (MAX_DIST - MIN_DIST);
            double dx = Math.cos(angle) * distance;
            double dz = Math.sin(angle) * distance;
            int px = (int) (player.getX() + dx);
            int pz = (int) (player.getZ() + dz);
            int baseY = (int) player.getY();
            for (int dy = 3; dy >= -3; dy--) {
                BlockPos pos = new BlockPos(px, baseY + dy, pz);
                BlockPos below = pos.below();
                if (
                    level.isEmptyBlock(pos) &&
                    level.isEmptyBlock(pos.above()) &&
                    level.getBlockState(below).isCollisionShapeFullBlock(level, below) &&
                    level.getBlockState(pos).getFluidState().isEmpty() &&
                    level.getBlockState(pos.above()).getFluidState().isEmpty()
                ) {
                    return new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                }
            }
        }
        return null;
    }

}