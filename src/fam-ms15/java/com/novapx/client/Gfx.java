package com.novapx.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.item.ItemStack;

/** Dibujo en pantalla (familia «ms15»: Minecraft 1.14 y 1.15, sin MatrixStack). */
public final class Gfx {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final TextRenderer tr = mc.textRenderer;

    public Gfx() {}

    public void fill(int x1, int y1, int x2, int y2, int color) { DrawableHelper.fill(x1, y1, x2, y2, color); }
    public void text(String s, int x, int y, int color) { tr.drawWithShadow(s, (float) x, (float) y, color); }
    public void plain(String s, int x, int y, int color) { tr.draw(s, (float) x, (float) y, color); }
    public void centered(String s, int cx, int y, int color) { tr.drawWithShadow(s, (float) (cx - tr.getStringWidth(s) / 2), (float) y, color); }
    public int width(String s) { return tr.getStringWidth(s); }
    public String trim(String s, int w) { return tr.trimToWidth(s, w); }
    public void item(ItemStack s, int x, int y) { mc.getItemRenderer().renderGuiItem(s, x, y); }
    public void overlay(ItemStack s, int x, int y) { mc.getItemRenderer().renderGuiItemOverlay(tr, s, x, y); }
}
