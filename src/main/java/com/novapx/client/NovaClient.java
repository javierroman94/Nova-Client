package com.novapx.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class NovaClient implements ClientModInitializer {
    public static final String ID = "nova_client";

    public static ModuleManager modules;
    public static KeyBinding menuKey;
    public static KeyBinding zoomKey;

    @Override
    public void onInitializeClient() {
        modules = new ModuleManager();
        Config.load(modules);

        menuKey = KeyBindingHelper.registerKeyBinding(Compat.key("key.nova_client.menu", GLFW.GLFW_KEY_RIGHT_SHIFT));
        zoomKey = KeyBindingHelper.registerKeyBinding(Compat.key("key.nova_client.zoom", GLFW.GLFW_KEY_C));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.wasPressed()) {
                if (client.currentScreen == null) Env.open(client, new NovaMenuScreen(modules));
            }
            modules.tick(client);
        });
        HudRenderCallback.EVENT.register((ctx, tickCounter) -> {
            net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
            if (client.player != null) modules.draw(client, new Gfx(ctx));
        });
        Env.initTitle();
        Env.info("Nova Client cargado con " + modules.all().size() + " módulos.");
    }
}
