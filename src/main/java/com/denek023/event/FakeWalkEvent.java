package com.denek023.event;

import com.denek023.denek023.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber
public class FakeWalkEvent {

    private static final HashMap<UUID, Integer> cooldowns = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.level().isClientSide || EventControl.eventActive) {
            return;
        }

        ServerPlayer player = (ServerPlayer) event.player;
        UUID playerUUID = player.getUUID();

        int cooldown = cooldowns.getOrDefault(playerUUID, 0);
        if (cooldown > 0) {
            cooldowns.put(playerUUID, cooldown - 1);
            return;
        }

        if (new Random().nextDouble() < 0.001) {
            if (isPlayerNearGrass(player)) {
                cooldowns.put(playerUUID, 20 * 180);
                player.level().playSound(null, player.blockPosition(), ModSounds.FAKEWALK.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
            }
        }
    }

    private static boolean isPlayerNearGrass(ServerPlayer player) {
        Level level = player.level();
        BlockPos playerPos = player.blockPosition();
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    if (level.getBlockState(playerPos.offset(x, y, z)).is(Blocks.GRASS_BLOCK)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}