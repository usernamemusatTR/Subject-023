package com.denek023.event;

import com.denek023.denek023.entity.Denek023WatcherEntity;
import com.denek023.denek023.init.ModEntityTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "denek023", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DoorScareEvent {
    private static final int TICK_COOLDOWN = 800;
    private static final double SPAWN_CHANCE = 0.15;
    private static final HashMap<UUID, Integer> cooldowns = new HashMap<>();
    private static final HashMap<UUID, Integer> scareTicks = new HashMap<>();
    private static final HashMap<UUID, Denek023WatcherEntity> spawnedEntities = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof ServerPlayer player)) return;
        Level level = player.level();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        UUID uuid = player.getUUID();
        int cooldown = cooldowns.getOrDefault(uuid, 0);
        if (cooldown > 0) return;
        if (!(block instanceof DoorBlock)) return;
        boolean open = state.hasProperty(DoorBlock.OPEN) && state.getValue(DoorBlock.OPEN);
        if (!open) {
            if (new Random().nextDouble() < SPAWN_CHANCE) {
                net.minecraft.core.Direction clickFace = event.getFace().getOpposite();
                BlockPos spawnPos = pos.relative(clickFace, 2);
                int maxDown = 2;
                boolean found = false;
                for (int i = 0; i <= maxDown; i++) {
                    BlockPos checkPos = spawnPos.below(i);
                    if (level.isEmptyBlock(checkPos) && !level.isEmptyBlock(checkPos.below())) {
                        spawnPos = checkPos;
                        found = true;
                        break;
                    }
                }
                if (found) {
                    Denek023WatcherEntity watcher = ModEntityTypes.DENEK023_WATCHER.get().create(level);
                    if (watcher != null) {
                        watcher.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, player.getYRot(), 0);
                        level.addFreshEntity(watcher);
                        spawnedEntities.put(uuid, watcher);
                        scareTicks.put(uuid, 30);
                        cooldowns.put(uuid, TICK_COOLDOWN);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.level().isClientSide) return;
        UUID uuid = event.player.getUUID();
        // Cooldown azalt
        if (cooldowns.containsKey(uuid)) {
            int cd = cooldowns.get(uuid) - 1;
            if (cd <= 0) cooldowns.remove(uuid);
            else cooldowns.put(uuid, cd);
        }
        // Scare tick azalt ve entity sil
        if (scareTicks.containsKey(uuid)) {
            int ticks = scareTicks.get(uuid) - 1;
            if (ticks <= 0) {
                scareTicks.remove(uuid);
                Denek023WatcherEntity watcher = spawnedEntities.remove(uuid);
                if (watcher != null && watcher.isAlive()) {
                    watcher.discard();
                }
            } else {
                scareTicks.put(uuid, ticks);
            }
        }
    }
}
