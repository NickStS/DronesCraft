package com.example.basicdrone.client.gui;

import com.example.basicdrone.menu.RemoteControllerMenu;
import com.example.basicdrone.network.NetworkHandler;
import com.example.basicdrone.network.LiftDronePacket;
import com.example.basicdrone.network.LandDronePacket;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

public class RemoteControllerScreen extends AbstractContainerScreen<RemoteControllerMenu> {
    private static final ResourceLocation BG =
            ResourceLocation.parse("basicdrone:textures/gui/remote_controller.png");

    private static final int TITLE_OFFSET_X = -33;
    private static final int TITLE_OFFSET_Y = 69;

    private static final int LIFT_BTN_X = 70;
    private static final int LIFT_BTN_Y = 100;
    private static final int LAND_BTN_X = 70;
    private static final int LAND_BTN_Y = 140;

    private static final int BAR_OFFSET_X = 180;
    private static final int BAR_OFFSET_Y = 80;
    private static final int BAR_WIDTH    = 12;
    private static final int BAR_HEIGHT   = 80;

    public RemoteControllerScreen(RemoteControllerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 256;
        this.imageHeight = 256;
    }

    @Override
    protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new Button(leftPos + LIFT_BTN_X, topPos + LIFT_BTN_Y, 60, 20,
                new TextComponent("Lift"),
                b -> NetworkHandler.CHANNEL.sendToServer(new LiftDronePacket())));
        addRenderableWidget(new Button(leftPos + LAND_BTN_X, topPos + LAND_BTN_Y, 60, 20,
                new TextComponent("Land"),
                b -> NetworkHandler.CHANNEL.sendToServer(new LandDronePacket())));
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTicks);
        this.renderTooltip(pose, mouseX, mouseY);
        drawCustomLabels(pose);
        drawBatteryBar(pose);
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

        int textColor = 0xFF404040;

        String titleText = "Remote Controller";
        int titleW = font.width(titleText);
        int titleX = left + (imageWidth - titleW) / 2 + TITLE_OFFSET_X;
        int titleY = top + TITLE_OFFSET_Y;
        font.draw(pose, titleText, titleX, titleY, textColor);
    }

    private void drawBatteryBar(PoseStack pose) {
        int left = (this.width - this.imageWidth) / 2;
        int top  = (this.height - this.imageHeight) / 2;
        int barX = left + BAR_OFFSET_X;
        int barY = top + BAR_OFFSET_Y;

        int fullH = BAR_HEIGHT;
        int barW  = BAR_WIDTH;

        int current = menu.getBatteryTicks();
        int max     = menu.getMaxBatteryTicks();
        int h = max > 0 ? (current * fullH / max) : 0;

        fill(pose, barX, barY, barX + barW, barY + fullH, 0xFF333333);
        fill(pose, barX, barY + (fullH - h), barX + barW, barY + fullH, 0xFF00FF00);

        String text = (max > 0) ? ((current * 100 / max) + "%") : "0%";
        int textW = font.width(text);
        int tx = barX + (barW / 2) - (textW / 2);
        int ty = barY + fullH + 6;
        font.draw(pose, text, tx, ty, 0xFF404040);
    }
}
