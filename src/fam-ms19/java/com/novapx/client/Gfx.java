package com.novapx.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

/** Dibujo en pantalla (familia «ms19»: Minecraft 1.19 a 1.19.4, con MatrixStack). */
public final class Gfx {
    private final MatrixStack m;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final TextRenderer tr = mc.textRenderer;

    public Gfx(MatrixStack m) { this.m = m; }

    public void fill(int x1, int y1, int x2, int y2, int color) { DrawableHelper.fill(m, x1, y1, x2, y2, color); }
    public void text(String s, int x, int y, int color) { tr.drawWithShadow(m, s, (float) x, (float) y, color); }
    public void plain(String s, int x, int y, int color) { tr.draw(m, s, (float) x, (float) y, color); }
    public void centered(String s, int cx, int y, int color) { tr.drawWithShadow(m, s, (float) (cx - tr.getWidth(s) / 2), (float) y, color); }
    public int width(String s) { return tr.getWidth(s); }
    public String trim(String s, int w) { return tr.trimToWidth(s, w); }
    public void item(ItemStack s, int x, int y) { mc.getItemRenderer().renderGuiItemIcon(s, x, y); }
    public void overlay(ItemStack s, int x, int y) { mc.getItemRenderer().renderGuiItemOverlay(tr, s, x, y); }
}
