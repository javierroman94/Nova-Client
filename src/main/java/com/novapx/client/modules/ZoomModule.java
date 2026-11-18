package com.novapx.client.modules;

import com.novapx.client.Module;
import com.novapx.client.NovaClient;
import net.minecraft.client.MinecraftClient;

/** Zoom mientras mantienes pulsada la tecla C: baja el FOV y lo devuelve al soltar. */
public class ZoomModule extends Module {
    private static final int ZOOM_FOV = 30;
    private int savedFov = -1;

    public ZoomModule() { super("zoom", "Zoom (tecla C)", "Mantén C para acercar la vista.", true); }

    @Override
    public void tick(MinecraftClient client) {
        var fov = client.options.getFov();
        boolean zooming = enabled && client.currentScreen == null && NovaClient.zoomKey.isPressed();
        if (zooming && savedFov < 0) {
            savedFov = fov.getValue();
            fov.setValue(ZOOM_FOV);
        } else if (!zooming && savedFov >= 0) {
            fov.setValue(savedFov);
            savedFov = -1;
        }
    }
}
