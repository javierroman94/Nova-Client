package com.novapx.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

public final class Compat {
    private Compat() {}
    public static boolean debugOpen(MinecraftClient c) { return c.options.debugEnabled; }
    public static KeyBinding key(String id, int code) { return new KeyBinding(id, InputUtil.Type.KEYSYM, code, "key.categories.nova_client"); }
}
