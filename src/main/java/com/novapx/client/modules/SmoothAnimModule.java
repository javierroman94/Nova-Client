package com.novapx.client.modules;

import com.novapx.client.Module;

/**
 * Animaciones suaves del jugador: respiración al estar quieto, torso que acompaña el paso y leve inclinación al avanzar.
 * El trabajo lo hace un mixin (anim-old); este módulo solo guarda si está encendido.
 */
public class SmoothAnimModule extends Module {
    /** Lo lee el mixin en cada cuadro; por eso es estático. */
    public static volatile boolean ACTIVE = true;

    public SmoothAnimModule() {
        super("smooth_anim", "Animaciones suaves", "El jugador respira, gira el torso al caminar y se inclina al correr.", true);
        ACTIVE = true;
    }

    @Override
    public void setEnabled(boolean value) {
        super.setEnabled(value);
        ACTIVE = value;
    }
}
