package com.novapx.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/** Diferencias de la API entre 26.1/26.2 (familia a) y 26.3+ (familia b). */
final class Compat {
    private Compat() {}
    static Screen screen(Minecraft c) { return c.gui.screen(); }
    static void open(Minecraft c, Screen s) { c.setScreenAndShow(s); }
    static KeyMapping key(String id, int code, KeyMapping.Category cat) { return new KeyMapping(id, code, cat); }
    static boolean guiHidden(Minecraft c) { return c.gui.hud.isHidden(); }
}
