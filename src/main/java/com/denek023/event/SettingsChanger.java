package com.denek023.event;

import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.client.Minecraft;
import java.util.Random;

@Mod.EventBusSubscriber(modid = "denek023", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SettingsChanger {
    private static final int TICK_COOLDOWN = 3000;
    private static final double SPAWN_CHANCE = 0.035;
    private static int tickCounter = 0;
    private static final Random random = new Random();

    public static void triggerSettingsChange() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        tickCounter++;
        if (tickCounter >= TICK_COOLDOWN) {
            tickCounter = 0;
            if (random.nextDouble() < SPAWN_CHANCE) {
                Minecraft mc = Minecraft.getInstance();
                int option = random.nextInt(3);
                if (option == 0) {
                    double newGamma = 0.0 + (0.8 - 0.0) * random.nextDouble();
                    mc.options.gamma().set(newGamma);
                } else if (option == 1) {
                    int newRD = 2 + random.nextInt(7);
                    mc.options.renderDistance().set(newRD);
                } else {
                    double newFov = 30.0 + (60.0 - 30.0) * random.nextDouble();
                    mc.options.fov().set((int) newFov);
                }
            }
        }
    }
}