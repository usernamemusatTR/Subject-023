package com.denek023.event;

import com.denek023.denek023.entity.Denek023AttackerEntity;
import com.denek023.denek023.Denek023;
import com.denek023.denek023.ModSounds;
import com.denek023.denek023.init.ModEntityTypes;
import com.denek023.denek023.event.ChaseMusicEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = Denek023.MODID)
public class Denek023JumpscareEvent {
    private static Denek023AttackerEntity attacker = null;
    private static final double SPAWN_CHANCE_PER_TICK = 1.0 / 4800.0;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        Player player = event.player;
        Level level = player.level();

        if (player.getY() > -15 || player.getY() < -65) {
            if (attacker != null) {
                attacker.discard();
                attacker = null;
            }
            return;
        }


        if (attacker != null && attacker.isAlive()) {
            if (!attacker.getPersistentData().getBoolean("chaseStarted")) {
                Vec3 look = player.getLookAngle().normalize();
                Vec3 diff = attacker.position().subtract(player.position()).normalize();
                double dot = look.dot(diff);

                if (dot > 0.95 && player.hasLineOfSight(attacker)) {
                    attacker.getPersistentData().putBoolean("chaseStarted", true);
                    attacker.setTarget(player);
                    attacker.resetChaseTicks();
                    level.playSound(null, attacker.blockPosition(), ModSounds.CHASE.get(), SoundSource.HOSTILE, 5.0F, 1.0F);
                    level.playSound(null, player.blockPosition(), ModSounds.SCREAM.get(), SoundSource.HOSTILE, 5.0f, 1.0f);
                    MinecraftForge.EVENT_BUS.post(new ChaseMusicEvent(true));
                }
            }
        } else {
            attacker = null;
            if (level.random.nextDouble() < SPAWN_CHANCE_PER_TICK) {
                spawnAttacker(player, level);
            }
        }
    }

    private static void spawnAttacker(Player player, Level level) {
        Vec3 spawnPos = findValidSpawnPos(player, level);
        if (spawnPos != null) {
            level.playSound(null, player.blockPosition(), ModSounds.RUN.get(), SoundSource.HOSTILE, 5.0F, 1.0F);
            attacker = new Denek023AttackerEntity(ModEntityTypes.DENEK023_ATTACKER.get(), level);
            attacker.moveTo(spawnPos.x, spawnPos.y, spawnPos.z);
            level.addFreshEntity(attacker);
        }
    }

    private static Vec3 findValidSpawnPos(Player player, Level level) {
        Random rand = new Random();
        for (int i = 0; i < 50; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double distance = 20 + rand.nextDouble() * 12;
            double dx = Math.cos(angle) * distance;
            double dz = Math.sin(angle) * distance;
            double baseY = player.getY() - 2 + rand.nextDouble() * 4;
            int x = (int) (player.getX() + dx);
            int z = (int) (player.getZ() + dz);

            for (int y = (int) baseY; y > baseY - 6; y--) {
                BlockPos pos = new BlockPos(x, y, z);
                BlockPos above = pos.above();
                BlockPos above2 = above.above();
                if (
                    !level.isEmptyBlock(pos) &&
                    level.isEmptyBlock(above) &&
                    level.isEmptyBlock(above2) &&
                    level.getBlockState(pos).getFluidState().isEmpty() &&
                    level.getBlockState(above).getFluidState().isEmpty() &&
                    level.getBlockState(above2).getFluidState().isEmpty()
                ) {
                    return new Vec3(x + 0.5, y + 1, z + 0.5);
                }
            }
        }
        return null;
    }

}