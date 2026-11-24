package com.novapx.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Modos de Nova Client: «PvP» (lo máximo de FPS y HUD para pelear) y «Clásico» (construir y jugar tranquilo).
 * Encender uno apaga el otro, cambia los módulos y ajusta los gráficos al momento.
 * Los gráficos se cambian por options.txt y se recargan, así funciona igual en todas las versiones.
 */
public final class NovaMode {
    private NovaMode() {}

    // Cada versión de Minecraft entiende unas claves u otras: las que no conoce las ignora.
    // renderClouds va dos veces: el formato viejo (sin comillas) y el nuevo (con comillas); gana el que entienda.
    private static final String[] PVP = {
        "renderDistance:8", "simulationDistance:6", "maxFps:260", "particles:2", "biomeBlendRadius:0",
        "enableVsync:false", "entityShadows:false", "bobView:false", "graphicsMode:0", "fancyGraphics:false",
        "entityDistanceScaling:0.5", "renderClouds:false", "renderClouds:\"false\""
    };
    private static final String[] CLASSIC = {
        "renderDistance:12", "simulationDistance:8", "maxFps:144", "particles:0", "biomeBlendRadius:2",
        "entityShadows:true", "bobView:true", "graphicsMode:1", "fancyGraphics:true",
        "entityDistanceScaling:1.0", "renderClouds:\"true\"", "renderClouds:true"
    };
    private static final String[] PVP_ON = {"cps", "keystrokes", "fps", "armor", "zoom"};
    private static final String[] PVP_OFF = {"coords", "smooth_anim"};
    private static final String[] CLASSIC_ON = {"fps", "coords", "armor", "zoom", "smooth_anim"};
    private static final String[] CLASSIC_OFF = {"cps", "keystrokes"};

    /** Enciende un modo (pvp = true para PvP). Se llama desde el menú. */
    public static void apply(ModuleManager modules, boolean pvp) {
        for (Module m : modules.all()) {
            if (m.id.equals(pvp ? "mode_classic" : "mode_pvp")) m.setEnabled(false);
            if (has(pvp ? PVP_ON : CLASSIC_ON, m.id)) m.setEnabled(true);
            if (has(pvp ? PVP_OFF : CLASSIC_OFF, m.id)) m.setEnabled(false);
        }
        Config.save(modules);
        MinecraftClient c = MinecraftClient.getInstance();
        try {
            c.options.write();
            Path file = FabricLoader.getInstance().getGameDir().resolve("options.txt");
            String text = Files.exists(file) ? new String(Files.readAllBytes(file), StandardCharsets.UTF_8) : "";
            Files.write(file, merge(text, pvp ? PVP : CLASSIC).getBytes(StandardCharsets.UTF_8));
            c.options.load();
            c.options.write();
            if (c.worldRenderer != null) c.worldRenderer.reload();
        } catch (Exception e) {
            Env.warn("No se pudieron cambiar los gráficos del modo: " + e);
        }
        // El menú se vuelve a abrir para que todos los interruptores muestren su estado nuevo.
        Env.open(c, new NovaMenuScreen(modules));
    }

    private static boolean has(String[] list, String id) {
        for (String s : list) if (s.equals(id)) return true;
        return false;
    }

    /** Cambia las claves dadas y deja el resto de options.txt como estaba. */
    static String merge(String text, String[] lines) {
        Map<String, List<String>> set = new LinkedHashMap<>();
        for (String l : lines) set.computeIfAbsent(l.substring(0, l.indexOf(':')), k -> new ArrayList<>()).add(l);
        StringBuilder out = new StringBuilder();
        for (String raw : text.split("\r?\n")) {
            if (raw.isEmpty()) continue;
            int i = raw.indexOf(':');
            if (i > 0 && set.containsKey(raw.substring(0, i))) continue;
            out.append(raw).append('\n');
        }
        for (List<String> ls : set.values()) for (String l : ls) out.append(l).append('\n');
        return out.toString();
    }
}
