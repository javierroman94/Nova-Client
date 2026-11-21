package com.novapx.client.modules;

import com.novapx.client.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public class CoordsModule extends Module {
    public CoordsModule() { super("coords", "Coordenadas", "Posición y hacia dónde miras.", true); }
    @Override
    public String line(Minecraft client) {
        if (client.player == null) return null;
        BlockPos p = client.player.blockPosition();
        return "XYZ " + p.getX() + " " + p.getY() + " " + p.getZ() + "  " + client.player.getDirection().getName().toUpperCase(java.util.Locale.ROOT);
    }
}
