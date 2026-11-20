package com.novapx.client.modules;

import com.novapx.client.Module;
import net.minecraft.client.MinecraftClient;

public class FpsModule extends Module {
    public FpsModule() { super("fps", "FPS", "Muestra los fotogramas por segundo.", true); }

    @Override
    public String line(MinecraftClient client) { return com.novapx.client.Env.fps(client) + " FPS"; }
}
