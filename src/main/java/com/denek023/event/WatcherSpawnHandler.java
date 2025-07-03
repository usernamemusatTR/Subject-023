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

@Mod.EventBusSubscriber(modid = Denek023.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WatcherSpawnHandler {
    private static final int TICK_INTERVAL = 800; // 40 saniye
    private static final double SPAWN_CHANCE = 0.20;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        Player player = event.player;
        Level level = player.level();

        if (player.tickCount > 0 && player.tickCount % TICK_INTERVAL == 0) {
            if (level.random.nextDouble() < SPAWN_CHANCE) {
                // Oyuncunun 128 blok çevresinde başka watcher var mı diye kontrol et
                boolean watcherNearby = !level.getEntitiesOfClass(Denek023WatcherEntity.class, player.getBoundingBox().inflate(128.0)).isEmpty();
                if (watcherNearby) {
                    return;
                }

                trySpawnWatcher(level, player);
            }
        }
    }

    private static void trySpawnWatcher(Level level, Player player) {
        double minDist = 15.0;
        double maxDist = 60.0;
        double angle = level.random.nextDouble() * 2 * Math.PI;
        double distance = minDist + level.random.nextDouble() * (maxDist - minDist);

        double spawnX = player.getX() + Math.cos(angle) * distance;
        double spawnZ = player.getZ() + Math.sin(angle) * distance;
        
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(spawnX, player.getY() + 5, spawnZ);

        // Yere doğru güvenli bir yer bul
        while (pos.getY() > level.getMinBuildHeight() && !level.getBlockState(pos.below()).isSolidRender(level, pos.below())) {
            pos.move(0, -1, 0);
        }

        // Hava boşluğu kontrolü
        if (!level.isEmptyBlock(pos) || !level.isEmptyBlock(pos.above())) {
            return; // Güvenli değil
        }

        Denek023WatcherEntity watcher = ModEntityTypes.DENEK023_WATCHER.get().create(level);
        if (watcher != null) {
            watcher.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, level.random.nextFloat() * 360.0F, 0);
            level.addFreshEntity(watcher);
            level.playSound(null, watcher.getX(), watcher.getY(), watcher.getZ(), ModSounds.CAVE_AMBIENT.get(), SoundSource.AMBIENT, 5.0f, 1.0f);
        }
    }
}