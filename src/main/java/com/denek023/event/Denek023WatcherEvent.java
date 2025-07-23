package com.denek023.event;

import com.denek023.denek023.ModSounds;
import com.denek023.denek023.entity.Denek023WatcherEntity;
import com.denek023.denek023.init.ModEntityTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import com.denek023.denek023.Denek023;
import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Denek023.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Denek023WatcherEvent {
    private static final int TICK_COOLDOWN = 800;
    private static final double SPAWN_CHANCE = 0.15;
    private static final HashMap<UUID, Integer> cooldowns = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        Player player = event.player;
        Level level = player.level();

        UUID playerUUID = player.getUUID();
        int cooldown = cooldowns.getOrDefault(playerUUID, 0);
        if (cooldown > 0) {
            cooldowns.put(playerUUID, cooldown - 1);
            return;
        }
        if (player.tickCount > 0 && player.tickCount % TICK_COOLDOWN == 0 && level.random.nextDouble() < SPAWN_CHANCE) {
            boolean watcherNearby = !level.getEntitiesOfClass(Denek023WatcherEntity.class, player.getBoundingBox().inflate(128.0)).isEmpty();
            if (watcherNearby) {
                return;
            }
            trySpawnWatcher(level, player);
            cooldowns.put(playerUUID, TICK_COOLDOWN);
        }
    }

    private static void trySpawnWatcher(Level level, Player player) {
        double minDist = 15.0;
        double maxDist = 50.0;
        double angle = level.random.nextDouble() * 2 * Math.PI;
        double distance = minDist + level.random.nextDouble() * (maxDist - minDist);

        double spawnX = player.getX() + Math.cos(angle) * distance;
        double spawnZ = player.getZ() + Math.sin(angle) * distance;
        
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(spawnX, player.getY() + 5, spawnZ);

        while (pos.getY() > level.getMinBuildHeight() && !level.getBlockState(pos.below()).isSolidRender(level, pos.below())) {
            pos.move(0, -1, 0);
        }

        if (!level.isEmptyBlock(pos) || !level.isEmptyBlock(pos.above())) {
            return; 
        }

        Denek023WatcherEntity watcher = ModEntityTypes.DENEK023_WATCHER.get().create(level);
        if (watcher != null) {
            watcher.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, level.random.nextFloat() * 360.0F, 0);
            level.addFreshEntity(watcher);
            level.playSound(null, watcher.getX(), watcher.getY(), watcher.getZ(), ModSounds.CAVE_AMBIENT.get(), SoundSource.AMBIENT, 5.0f, 1.0f);
        }
    }
}
