package com.denek023.event;

import com.denek023.Denek023WarningScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

@Mod.EventBusSubscriber(modid = "denek023", value = Dist.CLIENT)
public class WorldJoinGuiHandler {
    private static boolean shownThisSession = false;

    @SubscribeEvent
    public static void onPlayerJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        shownThisSession = false;
    }

    @SubscribeEvent
    public static void onFirstWorldRender(ViewportEvent.ComputeCameraAngles event) {
        if (!shownThisSession && Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            shownThisSession = true;
            Screen current = Minecraft.getInstance().screen;
            Minecraft.getInstance().setScreen(new Denek023WarningScreen(current));
        }
    }
}
