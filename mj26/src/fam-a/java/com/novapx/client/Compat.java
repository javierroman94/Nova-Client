package com.novapx.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/** Diferencias de la API entre 26.1/26.2 (familia a) y 26.3+ (familia b). */
final class Compat {
    private Compat() {}
    static Screen screen(Minecraft c) { return c.screen; }
    static void open(Minecraft c, Screen s) { c.setScreen(s); }
    static KeyMapping key(String id, int code, KeyMapping.Category cat) { return new KeyMapping(id, InputConstants.Type.KEYSYM, code, cat); }
    static boolean guiHidden(Minecraft c) { return c.options.hideGui; }
}
