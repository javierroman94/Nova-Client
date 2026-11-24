package com.novapx.client;

import com.novapx.client.modules.ArmorModule;
import com.novapx.client.modules.CoordsModule;
import com.novapx.client.modules.CpsModule;
import com.novapx.client.modules.FpsModule;
import com.novapx.client.modules.KeystrokesModule;
import com.novapx.client.modules.SmoothAnimModule;
import com.novapx.client.modules.ZoomModule;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        modules.add(new FpsModule());
        modules.add(new CpsModule());
        modules.add(new CoordsModule());
        modules.add(new KeystrokesModule());
        modules.add(new ArmorModule());
        modules.add(new ZoomModule());
        // Las animaciones necesitan un mixin que no existe en todas las versiones de Minecraft.
        if (hasMixins()) modules.add(new SmoothAnimModule());
        // Para añadir un módulo nuevo: crea la clase en modules/ y regístrala aquí.
    }

    private static boolean hasMixins() {
        return net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer("nova_client")
                .map(c -> c.findPath("nova_client.anim.mixins.json").isPresent()).orElse(false);
    }

    public List<Module> all() { return Collections.unmodifiableList(modules); }

    public void tick(MinecraftClient client) {
        for (Module m : modules) m.tick(client);
    }

    /** Dibuja el HUD: primero el texto de los módulos simples y luego los módulos gráficos. */
    public void draw(MinecraftClient client, Gfx ctx) {
        if (client.options.hudHidden || Compat.debugOpen(client)) return;
        int x = 6, y = 6;
        for (Module m : modules) {
            if (!m.isEnabled()) continue;
            String line = m.line(client);
            if (line == null) continue;
            int w = ctx.width(line);
            ctx.fill(x - 3, y - 2, x + w + 3, y + 10, 0x88000000);
            ctx.text(line, x, y, 0xFFFFFFFF);
            y += 14;
        }
        for (Module m : modules) {
            if (!m.isEnabled()) continue;
            y += m.draw(client, ctx, x, y + 2);
        }
    }
}
