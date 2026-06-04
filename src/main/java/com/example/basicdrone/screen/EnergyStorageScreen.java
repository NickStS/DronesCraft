package com.example.basicdrone.screen;

import com.example.basicdrone.menu.EnergyStorageMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EnergyStorageScreen extends AbstractContainerScreen<EnergyStorageMenu> {
    private static final ResourceLocation BG =
            ResourceLocation.parse("basicdrone:textures/gui/energy_storage.png");

    public EnergyStorageScreen(EnergyStorageMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 256;
        this.imageHeight = 256;
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTicks);
        this.renderTooltip(pose, mouseX, mouseY);
        drawCustomLabels(pose);
    }

    @Override
    protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
    }

    @Override
    protected void renderBg(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, BG);
        int left = (this.width - this.imageWidth) / 2;
        int top  = (this.height - this.imageHeight) / 2;
        this.blit(pose, left, top, 0, 0, imageWidth, imageHeight);
    }

    private void drawCustomLabels(PoseStack pose) {
        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;

        int titleOffsetX = -40;
        int titleOffsetY = 36;
        int invOffsetX = -55;
        int invOffsetY = -57;

        int barOffsetX = 37;
        int barOffsetY = 42;
        int energyTextOffsetX = 1;
        int energyTextOffsetY = 3;

        int textColor = 0xFF404040;

        String titleText = "Energy Storage";
        int titleW = font.width(titleText);
        int titleX = left + (imageWidth - titleW) / 2 + titleOffsetX;
        int titleY = top + 15 + titleOffsetY;
        font.draw(pose, titleText, titleX, titleY, textColor);

        String invText = "Inventory";
        int invW = font.width(invText);
        int invX = left + (imageWidth - invW) / 2 + invOffsetX;
        int invY = top + 175 + invOffsetY;
        font.draw(pose, invText, invX, invY, textColor);

        int energy = menu.getEnergy();
        int max    = menu.getMaxEnergy();

        int barX = left + 32 + barOffsetX;
        int barY = top + 40 + barOffsetY;
        int barW = 120;
        int barH = 10;
        int w = max > 0 ? (energy * barW / max) : 0;

        fill(pose, barX, barY, barX + barW, barY + barH, 0xFF333333);
        fill(pose, barX, barY, barX + w, barY + barH, 0xFF00FF00);

        String energyText = energy + " EU / " + max + " EU";
        int energyTextX = barX + (barW / 2) - (font.width(energyText) / 2) + energyTextOffsetX;
        int energyTextY = barY + barH + 5 + energyTextOffsetY;
        font.draw(pose, energyText, energyTextX, energyTextY, textColor);
    }
}
