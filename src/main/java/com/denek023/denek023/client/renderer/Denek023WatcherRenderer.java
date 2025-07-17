package com.denek023.denek023.client.renderer;

import com.denek023.denek023.client.model.Denek023;
import com.denek023.denek023.entity.Denek023WatcherEntity;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;

public class Denek023WatcherRenderer extends Denek023Renderer<Denek023WatcherEntity> {

    public Denek023WatcherRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}