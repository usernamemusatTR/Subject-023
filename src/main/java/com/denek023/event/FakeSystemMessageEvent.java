package com.denek023.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = "denek023", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FakeSystemMessageEvent {
    private static final Random random = new Random();
    private static final double SPAWN_CHANCE = 0.01;
    private static final int TICK_COOLDOWN = 8000;
    private static final Map<UUID, Integer> cooldowns = new HashMap<>();

    private static final List<String> FAKE_MC_ERROR_MESSAGES = Arrays.asList(
            "§eOpenGL Error:§f 1280 (Invalid enum)",
            "§eOpenGL Error:§f 1281 (Invalid value)",
            "§eOpenGL Error:§f 1282 (Invalid operation)",
            "§eOpenGL Error:§f 1283 (Stack overflow)",
            "§eOpenGL Error:§f 1284 (Stack underflow)",
            "§eOpenGL Error:§f 1285 (Out of memory)",
            "§eOpenGL Error:§f 1286 (Invalid framebuffer operation)",
            "§eOpenGL Error:§f 1287 (Context lost)",
            "§eOpenGL Error:§f 1288 (Table too large)",
            "§eOpenGL Warning:§f Shaders not supported on your graphics card.",
            "§eOpenGL Warning:§f Display mode not accelerated.",
            "§eOpenGL Warning:§f FBOs are not supported, rendering may be slow."
    );

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.level().isClientSide) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        UUID id = player.getUUID();
        int cooldown = cooldowns.getOrDefault(id, 0);
        if (cooldown > 0) {
            cooldowns.put(id, cooldown - 1);
            return;
        }
        if (random.nextDouble() < SPAWN_CHANCE) {
            String msg = FAKE_MC_ERROR_MESSAGES.get(random.nextInt(FAKE_MC_ERROR_MESSAGES.size()));
            player.sendSystemMessage(Component.literal(msg));
            cooldowns.put(id, TICK_COOLDOWN);
        }
    }
}
