package com.maor.roboticarm.client.screen;

import com.maor.roboticarm.blockentity.RoboticArmBlockEntity;
import com.maor.roboticarm.menu.RoboticArmMenu;
import com.maor.roboticarm.network.ArmControlPayload;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class RoboticArmScreen extends AbstractContainerScreen<RoboticArmMenu> {
    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath("roboticarm", "textures/gui/robotic_arm.png");
    public RoboticArmScreen(RoboticArmMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); imageWidth=176; imageHeight=222; }
    @Override protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.literal("Start"), b -> PacketDistributor.sendToServer(new ArmControlPayload(menu.pos(), 0))).bounds(leftPos+8, topPos+108, 50, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Stop"), b -> PacketDistributor.sendToServer(new ArmControlPayload(menu.pos(), 1))).bounds(leftPos+63, topPos+108, 50, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Task >"), b -> PacketDistributor.sendToServer(new ArmControlPayload(menu.pos(), 2))).bounds(leftPos+118, topPos+108, 50, 20).build());
    }
    @Override protected void renderBg(GuiGraphics g, float partial, int mouseX, int mouseY) {
        g.fill(leftPos, topPos, leftPos+imageWidth, topPos+imageHeight, 0xff202b30);
        for(int x=0;x<9;x++) { g.fill(leftPos+7+x*18, topPos+139, leftPos+24+x*18, topPos+156, 0xff4c6269); }
        g.fill(leftPos+10, topPos+10, leftPos+166, topPos+14, 0xff397b83);
        g.fill(leftPos+10, topPos+16, leftPos+10+(int)(156.0*menu.energy()/Math.max(1,menu.maxEnergy())), topPos+20, 0xff48c6a6);
        for(int i=0;i<21;i++) { int x=leftPos+menu.getSlot(i).x, y=topPos+menu.getSlot(i).y; g.fill(x-1,y-1,x+17,y+17,0xff71858a); }
    }
    @Override protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(font, "ROBOTIC ARM", 8, 4, 0xffffff, false);
        g.drawString(font, "Energy: %d/%d".formatted(menu.energy(), menu.maxEnergy()), 8, 22, 0xffffff, false);
        g.drawString(font, "Task: "+menu.task().name(), 8, 32, 0xffffff, false);
        g.drawString(font, "Target: "+menu.targetName(), 8, 42, 0xffffff, false);
        g.drawString(font, "Status: "+menu.status().name(), 8, 52, 0xffffff, false);
    }
}
