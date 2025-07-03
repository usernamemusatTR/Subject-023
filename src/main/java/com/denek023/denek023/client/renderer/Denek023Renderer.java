package com.denek023.denek023.client.renderer;

import com.denek023.denek023.entity.Denek023Entity;
import com.denek023.denek023.client.model.Denek023Model;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;

public class Denek023Renderer extends MobRenderer<Denek023Entity, Denek023Model<Denek023Entity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("denek023", "textures/entity/denek023.png");
    private static final ResourceLocation EMISSIVE = new ResourceLocation("denek023", "textures/entity/denek023_eyes.png");

    public Denek023Renderer(EntityRendererProvider.Context context) {
        super(context, new Denek023Model<>(context.bakeLayer(Denek023Model.LAYER_LOCATION)), 0.5f);

        this.addLayer(new RenderLayer<>(this) {
            @Override
            public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Denek023Entity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                RenderType renderType = RenderType.eyes(EMISSIVE);
                getParentModel().renderToBuffer(poseStack, buffer.getBuffer(renderType), 0xF000F0, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(Denek023Entity entity) {
        return TEXTURE;
    }
}