package com.maor.roboticarm.client.screen;

import com.maor.roboticarm.blockentity.RoboticArmBlockEntity;
import com.maor.roboticarm.menu.RoboticArmMenu;
import com.maor.roboticarm.network.ArmControlPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class RoboticArmScreen extends AbstractContainerScreen<RoboticArmMenu> {
    public RoboticArmScreen(RoboticArmMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); imageWidth=176; imageHeight=240; }
    @Override protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.literal("Start"), b -> PacketDistributor.sendToServer(new ArmControlPayload(menu.pos(), 0))).bounds(leftPos+8, topPos+142, 52, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Stop"), b -> PacketDistributor.sendToServer(new ArmControlPayload(menu.pos(), 1))).bounds(leftPos+64, topPos+142, 52, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Task >"), b -> PacketDistributor.sendToServer(new ArmControlPayload(menu.pos(), 2))).bounds(leftPos+120, topPos+142, 52, 20).build());
    }
    @Override protected void renderBg(GuiGraphics g, float partial, int mouseX, int mouseY) {
        g.fill(leftPos, topPos, leftPos+imageWidth, topPos+imageHeight, 0xff202b30);
        g.fill(leftPos+8, topPos+56, leftPos+168, topPos+60, 0xff38484d);
        g.fill(leftPos+8, topPos+56, leftPos+8+(int)(160.0*menu.energy()/Math.max(1,menu.maxEnergy())), topPos+60, 0xff48c6a6);
        for (int i = 0; i < menu.slots.size(); i++) {
            int x = leftPos + menu.getSlot(i).x, y = topPos + menu.getSlot(i).y;
            g.fill(x - 1, y - 1, x + 17, y + 17, 0xff71858a);
        }
    }
    @Override protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(font, "ROBOTIC ARM", 8, 6, 0xffffff, false);
        g.drawString(font, "Energy: %d/%d".formatted(menu.energy(), menu.maxEnergy()), 8, 16, 0xffffff, false);
        g.drawString(font, "Task: "+capitalized(menu.task().name()), 8, 26, 0xffffff, false);
        g.drawString(font, "Target: "+menu.targetName(), 8, 36, 0xffffff, false);
        g.drawString(font, "Status: "+capitalized(menu.status().name()), 8, 46, 0xffffff, false);
        g.drawString(font, "Tool", 26, 69, 0xffffff, false);
        g.drawString(font, "Fuel", 26, 91, 0xffffff, false);
    }
    private static String capitalized(String value) { return value.charAt(0) + value.substring(1).toLowerCase(); }
}
