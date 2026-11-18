package com.novapx.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/** Diferencias de API entre versiones de Minecraft (esta es la variante «modern2»). */
public final class Compat {
    private Compat() {}

    /** true si el menú de depuración (F3) está abierto. */
    public static boolean debugOpen(MinecraftClient c) { return c.getDebugHud().shouldShowDebugHud(); }

    /** Barra de durabilidad y cantidad sobre un objeto. */
    public static void stackOverlay(DrawContext ctx, TextRenderer tr, ItemStack s, int x, int y) { ctx.drawStackOverlay(tr, s, x, y); }

    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of("nova_client", "main"));

    /** Crea la tecla (categoría «Nova Client» en Controles). */
    public static KeyBinding key(String id, int code) {
        return new KeyBinding(id, InputUtil.Type.KEYSYM, code, CATEGORY);
    }
}
