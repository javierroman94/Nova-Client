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
            JsonObject root = JsonParser.parseString(Files.readString(f)).getAsJsonObject();
            JsonObject mods = root.has("modules") ? root.getAsJsonObject("modules") : new JsonObject();
            for (Module m : modules.all()) {
                if (mods.has(m.id)) m.setEnabled(mods.get(m.id).getAsBoolean());
            }
        } catch (Exception e) {
            NovaClient.LOGGER.warn("No se pudo leer la configuración de Nova Client: {}", e.toString());
        }
    }

    public static void save(ModuleManager modules) {
        try {
            JsonObject mods = new JsonObject();
            for (Module m : modules.all()) mods.addProperty(m.id, m.isEnabled());
            JsonObject root = new JsonObject();
            root.add("modules", mods);
            Files.writeString(file(), GSON.toJson(root));
        } catch (Exception e) {
            NovaClient.LOGGER.warn("No se pudo guardar la configuración de Nova Client: {}", e.toString());
        }
    }
}
