package com.denek023.event;

import com.denek023.denek023.ModSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber
public class CreeperTssSound {
    private static final HashMap<UUID, Integer> cooldowns = new HashMap<>();
    private static final double SPAWN_CHANCE = 0.15;
    private static final int TICK_COOLDOWN = 800;

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
        if (player.tickCount % TICK_COOLDOWN == 0 && player.level().random.nextDouble() < SPAWN_CHANCE) {
            if (new Random().nextDouble() < 0.001) {
                cooldowns.put(playerUUID, 20 * 180);
                player.level().playSound(null, player.blockPosition(), ModSounds.CREEPER_TSS.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
            }
        }
    }
}
