package com.novapx.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Nova Client para Minecraft 26.x (nombres oficiales de Mojang, sin ofuscar). */
public class NovaClient implements ClientModInitializer {
    public static final String ID = "nova_client";
    public static final Logger LOG = LoggerFactory.getLogger("Nova Client");
    public static ModuleManager modules;
    public static KeyMapping menuKey;
    public static KeyMapping zoomKey;

    public static boolean screenOpen(Minecraft c) { return Compat.screen(c) != null; }

    @Override
    public void onInitializeClient() {
        modules = new ModuleManager();
        Config.load(modules);
        KeyMapping.Category cat = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(ID, "main"));
        menuKey = KeyMappingHelper.registerKeyMapping(Compat.key("key.nova_client.menu", InputConstants.KEY_RSHIFT, cat));
        zoomKey = KeyMappingHelper.registerKeyMapping(Compat.key("key.nova_client.zoom", InputConstants.KEY_C, cat));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.consumeClick()) {
                if (Compat.screen(client) == null) Compat.open(client, new NovaMenuScreen(modules));
            }
            modules.tick(client);
        });
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(ID, "hud"), (g, delta) -> {
            Minecraft client = Minecraft.getInstance();
            if (client.player != null) modules.draw(client, g);
        });
        LOG.info("Nova Client cargado con {} módulos.", modules.all().size());
    }
}
