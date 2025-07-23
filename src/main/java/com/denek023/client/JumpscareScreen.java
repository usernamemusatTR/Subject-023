package com.denek023.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JumpscareScreen extends Screen {
    private static final ResourceLocation JUMPSCARE_IMG = new ResourceLocation("denek023", "textures/gui/jumpscare.png");
    private static final long DURATION = 3000; // ms
    private final long startTime;

    public JumpscareScreen() {
        super(Component.empty());
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void render(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        RenderSystem.enableBlend();
        guiGraphics.blit(JUMPSCARE_IMG, 0, 0, 0, 0, width, height, width, height);
        RenderSystem.disableBlend();
        // No super.render! Don't draw anything else!
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false; // ESC does not close
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause the game
    }

    @Override
    public void tick() {
        if (System.currentTimeMillis() - startTime > DURATION) {
            Minecraft.getInstance().setScreen(null); // Close jumpscare screen
        }
    }

    // Block all input
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) { return true; }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) { return true; }
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) { return true; }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) { return true; }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) { return true; }
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) { return true; }
    @Override
    public boolean charTyped(char codePoint, int modifiers) { return true; }
}
