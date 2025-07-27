package com.denek023.event;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.server.level.ServerLevel;

import java.util.*;

public class TorchBlowEvent {
    private static final Random random = new Random();
    private static final double SPAWN_CHANCE = 0.05;
    private static final int TICK_COOLDOWN = 1000;
    private static final Map<UUID, Integer> cooldowns = new HashMap<>();
    private static final Map<ServerLevel, Queue<BlockPos>> breakingQueue = new HashMap<>();
    private static final Map<ServerLevel, Integer> breakTickCounter = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (player.level().isClientSide || event.phase != TickEvent.Phase.END) return;
        ServerLevel level = (ServerLevel) player.level();
        UUID playerUUID = player.getUUID();
        int cooldown = cooldowns.getOrDefault(playerUUID, 0);
        if (cooldown > 0) {
            cooldowns.put(playerUUID, cooldown - 1);
            return;
        }
        if (player.tickCount % TICK_COOLDOWN == 0 && random.nextDouble() < SPAWN_CHANCE) {
            List<BlockPos> torches = new ArrayList<>();
            BlockPos base = player.blockPosition();
            for (int x = -10; x <= 10; x++) {
                for (int y = -3; y <= 3; y++) {
                    for (int z = -10; z <= 10; z++) {
                        BlockPos pos = base.offset(x, y, z);
                        if (base.distSqr(pos) <= 100) {
                            if (level.getBlockState(pos).is(Blocks.TORCH) || level.getBlockState(pos).is(Blocks.WALL_TORCH)) {
                                torches.add(pos.immutable());
                            }
                        }
                    }
                }
            }
            if (!torches.isEmpty()) {
                breakingQueue.computeIfAbsent(level, l -> new LinkedList<>()).addAll(torches);
                breakTickCounter.put(level, 0);
            }
        }
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.level instanceof ServerLevel level)) return;

        Queue<BlockPos> queue = breakingQueue.get(level);
        if (queue == null || queue.isEmpty()) return;

        int tick = breakTickCounter.compute(level, (l, v) -> v == null ? 1 : v + 1);
        if (tick >= 40) {
            BlockPos pos = queue.poll();
            if (pos != null && (level.getBlockState(pos).is(Blocks.TORCH) || level.getBlockState(pos).is(Blocks.WALL_TORCH))) {
                level.destroyBlock(pos, true);
            }
            breakTickCounter.put(level, 0);
        }
    }
}