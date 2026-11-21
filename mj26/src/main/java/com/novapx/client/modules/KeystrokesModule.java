package com.novapx.client.modules;

import com.novapx.client.Module;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Teclas W A S D y espacio en pantalla; se iluminan al pulsarlas. */
public class KeystrokesModule extends Module {
    private static final int SIZE = 20, GAP = 2;
    public KeystrokesModule() { super("keystrokes", "Teclas", "Muestra W A S D y espacio al pulsarlos.", true); }

    private void key(Minecraft c, GuiGraphicsExtractor g, int x, int y, int w, String label, KeyMapping k) {
        boolean on = k.isDown();
        g.fill(x, y, x + w, y + SIZE, on ? 0xCCFFFFFF : 0x88000000);
        int tw = c.font.width(label);
        g.text(c.font, label, x + (w - tw) / 2, y + 6, on ? 0xFF000000 : 0xFFFFFFFF, false);
    }
    @Override
    public int draw(Minecraft c, GuiGraphicsExtractor g, int x, int y) {
        int step = SIZE + GAP;
        key(c, g, x + step, y, SIZE, "W", c.options.keyUp);
        key(c, g, x, y + step, SIZE, "A", c.options.keyLeft);
        key(c, g, x + step, y + step, SIZE, "S", c.options.keyDown);
        key(c, g, x + step * 2, y + step, SIZE, "D", c.options.keyRight);
        key(c, g, x, y + step * 2, step * 3 - GAP, "ESPACIO", c.options.keyJump);
        return step * 3 + 4;
    }
}
