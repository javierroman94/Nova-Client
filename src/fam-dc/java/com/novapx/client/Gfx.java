package com.novapx.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/** Dibujo en pantalla (familia «dc»: Minecraft 1.20 o superior, con DrawContext). */
public final class Gfx {
    private final DrawContext c;
    private final TextRenderer tr = MinecraftClient.getInstance().textRenderer;

    public Gfx(DrawContext c) { this.c = c; }

    public void fill(int x1, int y1, int x2, int y2, int color) { c.fill(x1, y1, x2, y2, color); }
    public void text(String s, int x, int y, int color) { c.drawTextWithShadow(tr, s, x, y, color); }
    public void plain(String s, int x, int y, int color) { c.drawText(tr, s, x, y, color, false); }
    public void centered(String s, int cx, int y, int color) { c.drawCenteredTextWithShadow(tr, Text.literal(s), cx, y, color); }
    public int width(String s) { return tr.getWidth(s); }
    public String trim(String s, int w) { return tr.trimToWidth(s, w); }
    public void item(ItemStack s, int x, int y) { c.drawItem(s, x, y); }
    public void overlay(ItemStack s, int x, int y) { Compat.stackOverlay(c, tr, s, x, y); }
}
