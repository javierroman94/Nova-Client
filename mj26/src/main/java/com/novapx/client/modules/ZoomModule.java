package com.novapx.client.modules;

import com.novapx.client.Module;
import com.novapx.client.NovaClient;
import net.minecraft.client.Minecraft;

/** Zoom mientras mantienes pulsada la tecla C: baja el FOV y lo devuelve al soltar. */
public class ZoomModule extends Module {
    private static final int ZOOM_FOV = 30;
    private int savedFov = -1;
    public ZoomModule() { super("zoom", "Zoom (tecla C)", "Mantén C para acercar la vista.", true); }

    @Override
    public void tick(Minecraft client) {
        boolean zooming = enabled && com.novapx.client.NovaClient.screenOpen(client) == false && NovaClient.zoomKey.isDown();
        if (zooming && savedFov < 0) {
            savedFov = client.options.fov().get();
            client.options.fov().set(ZOOM_FOV);
        } else if (!zooming && savedFov >= 0) {
            client.options.fov().set(savedFov);
            savedFov = -1;
        }
    }
}
