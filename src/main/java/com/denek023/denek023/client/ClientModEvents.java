package com.denek023.denek023.client;

import com.denek023.denek023.client.model.Denek023;
import com.denek023.denek023.client.renderer.Denek023AttackerRenderer;
import com.denek023.denek023.client.renderer.Denek023WatcherRenderer;
import com.denek023.denek023.client.renderer.Denek023BehindYouRenderer;
import com.denek023.denek023.client.renderer.Denek023Renderer;
import com.denek023.denek023.init.ModEntityTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraft.client.renderer.entity.EntityRenderers;

@Mod.EventBusSubscriber(modid = "denek023", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(Denek023.LAYER_LOCATION, Denek023::createBodyLayer);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntityTypes.DENEK023_ATTACKER.get(), Denek023AttackerRenderer::new);
        EntityRenderers.register(ModEntityTypes.DENEK023_WATCHER.get(), Denek023WatcherRenderer::new);
        EntityRenderers.register(ModEntityTypes.DENEK023_BEHIND_YOU.get(), Denek023BehindYouRenderer::new);
    }
}