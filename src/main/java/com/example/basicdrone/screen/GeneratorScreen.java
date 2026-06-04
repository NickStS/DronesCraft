package com.example.basicdrone.screen;

import com.example.basicdrone.menu.GeneratorMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GeneratorScreen extends AbstractContainerScreen<GeneratorMenu> {
    private static final ResourceLocation BG =
            ResourceLocation.parse("basicdrone:textures/gui/generator.png");

    private static final ResourceLocation LIGHTNING_OFF =
            ResourceLocation.parse("basicdrone:textures/gui/lightning_off.png");
    private static final ResourceLocation LIGHTNING_ON =
            ResourceLocation.parse("basicdrone:textures/gui/lightning_on.png");

    public GeneratorScreen(GeneratorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 256;
        this.imageHeight = 256;
    }

    @Override
    protected void renderBg(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;
        RenderSystem.setShaderTexture(0, BG);
        this.blit(pose, left, top, 0, 0, imageWidth, imageHeight);

        drawLightning(pose, left, top);
        drawBurningBar(pose, left, top);
    }

    @Override
    protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTicks);
        this.renderTooltip(pose, mouseX, mouseY);
        drawCustomLabels(pose);
    }

    private void drawLightning(PoseStack pose, int left, int top) {
        int iconX = left + 150;
        int iconY = top + 87;
        int size = 18;
        RenderSystem.setShaderTexture(0, this.menu.getFuelTimeRemaining() > 0 ? LIGHTNING_ON : LIGHTNING_OFF);
        blit(pose, iconX, iconY, 0, 0, size, size, size, size);
    }

    private void drawBurningBar(PoseStack pose, int left, int top) {
        int barX = left + 120;
        int barY = top + 88;
        int barW = 16;
        int barH = 16;
        int burn = this.menu.getFuelTimeRemaining();
        int maxBurn = this.menu.getFuelTimeTotal();

        if (maxBurn > 0 && burn > 0) {
            int h = burn * barH / maxBurn;
            fill(pose, barX, barY, barX + barW, barY + barH, 0xFF8b8b8b);
            fill(pose, barX, barY + (barH - h), barX + barW, barY + barH, 0xFFc5c5c5);
        } else {
            fill(pose, barX, barY, barX + barW, barY + barH, 0xFF8b8b8b);
        }
    }

    private void drawCustomLabels(PoseStack pose) {
        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;

        int titleOffsetX = -55;
        int titleOffsetY = 36;
        int invOffsetX = -55;
        int invOffsetY = -57;
        int feOffsetX = 60;
        int feOffsetY = -39;

        int textColor = 0xFF404040;

        String titleText = "Generator";
        int titleW = font.width(titleText);
        int titleX = left + (imageWidth - titleW) / 2 + titleOffsetX;
        int titleY = top + 15 + titleOffsetY;
        font.draw(pose, titleText, titleX, titleY, textColor);

        String invText = "Inventory";
        int invW = font.width(invText);
        int invX = left + (imageWidth - invW) / 2 + invOffsetX;
        int invY = top + 175 + invOffsetY;
        font.draw(pose, invText, invX, invY, textColor);

        int gen = this.menu.getFuelTimeRemaining() > 0 ? 10 : 0;
        String feText = gen + " EU/t";
        int feTextW = font.width(feText);
        int feTx = left + (imageWidth - feTextW) / 2 + feOffsetX;
        int feTy = top + 130 + feOffsetY;
        font.draw(pose, feText, feTx, feTy, textColor);
    }
}
