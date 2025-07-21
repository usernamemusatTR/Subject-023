package com.denek023.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.Random;
import com.denek023.event.CameraOpener;

@Mod.EventBusSubscriber(modid = "denek023", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CameraOpenEventHandler {
    private static final int TICK_COOLDOWN = 3000;
    private static final double SPAWN_CHANCE = 0.05; 
    private static int tickCounter = 0;
    private static final Random random = new Random();

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (event.phase != ClientTickEvent.Phase.END) return;
        tickCounter++;
        if (tickCounter >= TICK_COOLDOWN) {
            tickCounter = 0;
            if (random.nextDouble() < SPAWN_CHANCE) {
                CameraOpener.openCamera();
            }
        }
    }
}
