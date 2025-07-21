package com.denek023;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class Denek023WarningScreen extends Screen {
    private final Screen parent;
    private Button continueButton;

    public Denek023WarningScreen(Screen parent) {
        super(Component.literal("⚠ WARNING ⚠"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        continueButton = Button.builder(Component.literal("Continue"), btn -> {
            Minecraft.getInstance().setScreen(parent);
        }).pos(centerX - 60, centerY + 40).size(120, 20).build();
        this.addRenderableWidget(continueButton);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        int y = this.height / 2 - 50;
        int color1 = 0xFF0000;
        int color2 = 0xFFFFFF;
        int color3 = 0xFFFFFF;
        for (net.minecraft.util.FormattedCharSequence line : this.font.split(Component.literal("⚠ WARNING ⚠"), this.width - 40)) {
            int lineWidth = this.font.width(line);
            guiGraphics.drawString(this.font, line, (this.width - lineWidth) / 2, y, color1, false);
            y += 12;
        }
        for (net.minecraft.util.FormattedCharSequence line : this.font.split(Component.literal("This mod may simulate features such as reading your IP address or launching apps. No personal data is collected or transmitted. Your system remains safe and secure."), this.width - 40)) {
            int lineWidth = this.font.width(line);
            guiGraphics.drawString(this.font, line, (this.width - lineWidth) / 2, y, color2, false);
            y += 12;
        }
        for (net.minecraft.util.FormattedCharSequence line : this.font.split(Component.literal("By clicking continue, you agree to proceed with full awareness."), this.width - 40)) {
            int lineWidth = this.font.width(line);
            guiGraphics.drawString(this.font, line, (this.width - lineWidth) / 2, y, color3, false);
            y += 12;
        }    
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
