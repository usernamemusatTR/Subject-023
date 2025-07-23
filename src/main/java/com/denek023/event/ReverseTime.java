package com.denek023.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;


import java.util.*;

@Mod.EventBusSubscriber(modid = "denek023", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ReverseTime {
    private static final int MAX_TICKS = 400;
    private static final Map<UUID, Deque<ReverseTimeState>> stateHistory = new HashMap<>();
    private static final int TICK_COOLDOWN = 1000;
    private static final double SPAWN_CHANCE = 0.15;
    private static int tickCounter = 0;
    private static final Random random = new Random();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        UUID id = player.getUUID();
        stateHistory.putIfAbsent(id, new ArrayDeque<>());
        Deque<ReverseTimeState> history = stateHistory.get(id);
        ReverseTimeState snap = snapshotPlayerState(player);
        history.addLast(snap);
        if (history.size() > MAX_TICKS)
            history.removeFirst();
    }

    private static ReverseTimeState snapshotPlayerState(ServerPlayer player) {
        Vec3 pos = player.position();
        float health = player.getHealth();
        return new ReverseTimeState(pos, health);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (tickCounter >= TICK_COOLDOWN) {
            tickCounter = 0;
            if (random.nextDouble() < SPAWN_CHANCE) {
                MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
                List<ServerPlayer> allPlayers = new ArrayList<>();
                if (server != null) {
                    for (ServerLevel level : server.getAllLevels()) {
                        allPlayers.addAll(level.players());
                    }
                }
                if (allPlayers.isEmpty()) return;
                ServerPlayer player = allPlayers.get(random.nextInt(allPlayers.size()));
                triggerReverseTime(player);
            }
        }
    }

    public static void triggerReverseTime(ServerPlayer player) {
        Deque<ReverseTimeState> history = stateHistory.get(player.getUUID());
        if (history == null || history.isEmpty()) return;
        int ticksToGoBack = 80;
        if (history.size() < ticksToGoBack) return;
        ReverseTimeState[] states = history.toArray(new ReverseTimeState[0]);
        ReverseTimeState rewind = states[history.size() - ticksToGoBack];
        player.teleportTo(rewind.playerPos.x, rewind.playerPos.y, rewind.playerPos.z);
        player.setHealth(rewind.playerHealth);
    }
}
