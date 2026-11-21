package com.novapx.client.modules;

import com.novapx.client.Gfx;
import com.novapx.client.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;

/** Teclas W A S D y espacio en pantalla; se iluminan al pulsarlas. */
public class KeystrokesModule extends Module {
    private static final int SIZE = 20, GAP = 2;

    public KeystrokesModule() { super("keystrokes", "Teclas", "Muestra W A S D y espacio al pulsarlos.", true); }

    private void key(MinecraftClient c, Gfx ctx, int x, int y, int w, String label, KeyBinding k) {
        boolean on = k.isPressed();
        ctx.fill(x, y, x + w, y + SIZE, on ? 0xCCFFFFFF : 0x88000000);
        int tw = ctx.width(label);
        ctx.plain(label, x + (w - tw) / 2, y + 6, on ? 0xFF000000 : 0xFFFFFFFF);
    }

    @Override
    public int draw(MinecraftClient c, Gfx ctx, int x, int y) {
        net.minecraft.client.options.KeyBinding[] o = com.novapx.client.Env.moveKeys(c);
        int step = SIZE + GAP;
        key(c, ctx, x + step, y, SIZE, "W", o[0]);
        key(c, ctx, x, y + step, SIZE, "A", o[1]);
        key(c, ctx, x + step, y + step, SIZE, "S", o[2]);
        key(c, ctx, x + step * 2, y + step, SIZE, "D", o[3]);
        key(c, ctx, x, y + step * 2, step * 3 - GAP, "ESPACIO", o[4]);
        return step * 3 + 4;
    }
}
