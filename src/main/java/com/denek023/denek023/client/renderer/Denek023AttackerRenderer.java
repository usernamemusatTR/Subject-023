package com.denek023.denek023.client.renderer;

import com.denek023.denek023.client.model.Denek023;
import com.denek023.denek023.entity.Denek023AttackerEntity;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;

public class Denek023AttackerRenderer extends MobRenderer<Denek023AttackerEntity, Denek023<Denek023AttackerEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("denek023", "textures/entity/denek023.png");
    private static final ResourceLocation EMISSIVE = new ResourceLocation("denek023", "textures/entity/denek023_eyes.png");

    public Denek023AttackerRenderer(EntityRendererProvider.Context context) {
        super(context, new Denek023<>(context.bakeLayer(Denek023.LAYER_LOCATION)), 0.5f);
        
        this.addLayer(new RenderLayer<>(this) {
            @Override
            public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Denek023AttackerEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                RenderType renderType = RenderType.eyes(EMISSIVE);
                getParentModel().renderToBuffer(poseStack, buffer.getBuffer(renderType), 0xF000F0, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        });

    }

    @Override
    public ResourceLocation getTextureLocation(Denek023AttackerEntity entity) {
         return TEXTURE;
    }
}