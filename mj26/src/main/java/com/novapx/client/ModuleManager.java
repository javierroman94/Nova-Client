package com.novapx.client;

import com.novapx.client.modules.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        modules.add(new FpsModule());
        modules.add(new CpsModule());
        modules.add(new CoordsModule());
        modules.add(new KeystrokesModule());
        modules.add(new ArmorModule());
        modules.add(new ZoomModule());
    }
    public List<Module> all() { return Collections.unmodifiableList(modules); }
    public void tick(Minecraft client) { for (Module m : modules) m.tick(client); }

    public void draw(Minecraft client, GuiGraphicsExtractor g) {
        if (Compat.guiHidden(client) || client.getDebugOverlay().showDebugScreen()) return;
        int x = 6, y = 6;
        for (Module m : modules) {
            if (!m.isEnabled()) continue;
            String line = m.line(client);
            if (line == null) continue;
            int w = client.font.width(line);
            g.fill(x - 3, y - 2, x + w + 3, y + 10, 0x88000000);
            g.text(client.font, line, x, y, 0xFFFFFFFF);
            y += 14;
        }
        for (Module m : modules) {
            if (!m.isEnabled()) continue;
            y += m.draw(client, g, x, y + 2);
        }
    }
}
