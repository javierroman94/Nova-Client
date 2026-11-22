package com.novapx.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;

/** Cambia la pantalla de inicio de Minecraft por la de Nova Client (solo la pantalla de Minecraft «pura»: no toca la de otros mods). */
public final class NovaTitle {
    /** Pantalla de Minecraft que el jugador pidió ver con el botón «Menú de Minecraft». */
    static Screen allowedVanilla;

    /** Si la pantalla de Nova falla por cualquier motivo, se vuelve a la de Minecraft y no se intenta más en esta sesión. */
    static boolean failed;

    private NovaTitle() {}

    static void fallback(net.minecraft.client.MinecraftClient c, Throwable t) {
        failed = true;
        Env.warn("La pantalla de inicio de Nova falló y se usa la de Minecraft: " + t);
        TitleScreen vanilla = new TitleScreen();
        allowedVanilla = vanilla;
        c.setScreen(vanilla);
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(c -> {
            Screen s = c.currentScreen;
            if (s == null) { allowedVanilla = null; return; }
            if (!failed && s.getClass() == TitleScreen.class && s != allowedVanilla && NovaClient.modules != null) {
                try {
                    c.setScreen(new NovaTitleScreen(NovaClient.modules));
                } catch (Throwable t) {
                    fallback(c, t);
                }
            }
        });
    }
}
