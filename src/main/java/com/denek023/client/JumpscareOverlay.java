package com.denek023.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class JumpscareOverlay {
    public static boolean showJumpscare = false;
    public static long jumpscareStartTime = 0;
    private static final long DURATION = 4000;

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!showJumpscare) return;
        long now = System.currentTimeMillis();
        if (now - jumpscareStartTime > DURATION) {
            showJumpscare = false;
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        ResourceLocation img = new ResourceLocation("denek023", "textures/gui/jumpscare.png");
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        RenderSystem.enableBlend();
        event.getGuiGraphics().blit(img, 0, 0, 0, 0, width, height, width, height);
        RenderSystem.disableBlend();
    }
}