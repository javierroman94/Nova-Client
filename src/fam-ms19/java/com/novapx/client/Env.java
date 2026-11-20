package com.novapx.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Cosas pequeñas que cambian entre versiones (familia «ms19»). */
public final class Env {
    private static final Logger LOG = LogManager.getLogger("Nova Client");
    private Env() {}
    public static void info(String m) { LOG.info(m); }
    public static void warn(String m) { LOG.warn(m); }
    public static int getFov(MinecraftClient c) { return c.options.getFov().getValue(); }
    public static void setFov(MinecraftClient c, int v) { c.options.getFov().setValue(v); }
    public static int fps(MinecraftClient c) {
        try { return Integer.parseInt(c.fpsDebugString.split(" ")[0]); } catch (Exception e) { return 0; }
    }
    public static net.minecraft.client.option.KeyBinding[] moveKeys(MinecraftClient c) { return new net.minecraft.client.option.KeyBinding[]{ c.options.forwardKey, c.options.leftKey, c.options.backKey, c.options.rightKey, c.options.jumpKey }; }
    public static void open(MinecraftClient c, Screen s) { c.setScreen(s); }
}
