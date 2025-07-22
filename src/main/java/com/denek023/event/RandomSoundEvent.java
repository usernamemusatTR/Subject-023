package com.denek023.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.*;

@Mod.EventBusSubscriber(modid = "denek023")
public class RandomSoundEvent {
    private static final int TICK_COOLDOWN = 20 * 30;
    private static final double TRIGGER_CHANCE = 0.035;
    private static final Map<UUID, Integer> cooldowns = new HashMap<>();

    private static final List<net.minecraftforge.registries.RegistryObject<SoundEvent>> SOUNDS = List.of(
            com.denek023.denek023.ModSounds.REVERSEMUSICBOX,
            com.denek023.denek023.ModSounds.BREATHLEFT,
            com.denek023.denek023.ModSounds.BREATHRIGHT,
            com.denek023.denek023.ModSounds.CREEPY_WHISPER
    );

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof ServerPlayer player) || event.phase != TickEvent.Phase.END) return;
        UUID uuid = player.getUUID();
        int cooldown = cooldowns.getOrDefault(uuid, 0);
        if (cooldown > 0) {
            cooldowns.put(uuid, cooldown - 1);
            return;
        }
        if (player.tickCount % 20 == 0 && player.level().random.nextDouble() < TRIGGER_CHANCE) {
            net.minecraftforge.registries.RegistryObject<SoundEvent> soundObject = SOUNDS.get(player.level().random.nextInt(SOUNDS.size()));
            SoundEvent sound = soundObject.get();
            player.level().playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS, 4.0f, 1.0f);
            cooldowns.put(uuid, TICK_COOLDOWN);
        }
    }
}
