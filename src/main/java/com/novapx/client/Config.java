package com.novapx.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

/** Guarda qué módulos están activados en config/nova-client.json. */
public final class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private Config() {}

    private static Path file() { return FabricLoader.getInstance().getConfigDir().resolve("nova-client.json"); }

    public static void load(ModuleManager modules) {
        try {
            Path f = file();
            if (!Files.exists(f)) return;
            JsonObject root = new JsonParser().parse(new String(Files.readAllBytes(f), java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
            JsonObject mods = root.has("modules") ? root.getAsJsonObject("modules") : new JsonObject();
            for (Module m : modules.all()) {
                if (mods.has(m.id)) m.setEnabled(mods.get(m.id).getAsBoolean());
            }
        } catch (Exception e) {
            Env.warn("No se pudo leer la configuración de Nova Client: " + e);
        }
    }

    public static void save(ModuleManager modules) {
        try {
            JsonObject mods = new JsonObject();
            for (Module m : modules.all()) mods.addProperty(m.id, m.isEnabled());
            JsonObject root = new JsonObject();
            root.add("modules", mods);
            Files.write(file(), GSON.toJson(root).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            Env.warn("No se pudo guardar la configuración de Nova Client: " + e);
        }
    }
}
