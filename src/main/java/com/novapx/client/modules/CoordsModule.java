package com.novapx.client.modules;

import com.novapx.client.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class CoordsModule extends Module {
    public CoordsModule() { super("coords", "Coordenadas", "Posición y hacia dónde miras.", true); }

    @Override
    public String line(MinecraftClient client) {
        if (client.player == null) return null;
        BlockPos p = client.player.getBlockPos();
        return "XYZ " + p.getX() + " " + p.getY() + " " + p.getZ() + "  " + client.player.getHorizontalFacing().asString().toUpperCase();
    }
}
