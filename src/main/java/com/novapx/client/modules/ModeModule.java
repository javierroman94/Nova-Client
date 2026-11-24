package com.novapx.client.modules;

import com.novapx.client.Module;
import com.novapx.client.NovaClient;
import com.novapx.client.NovaMode;

/** Interruptor de modo (PvP o Clásico). Al encenderlo apaga el otro y ajusta módulos y gráficos. */
public class ModeModule extends Module {
    private final boolean pvp;

    public ModeModule(boolean pvp) {
        super(pvp ? "mode_pvp" : "mode_classic",
              pvp ? "⚔ Modo PvP" : "⌂ Modo Clásico",
              pvp ? "Máximo de FPS: CPS y teclas, sin nubes ni sombras." : "Para construir: gráficos bonitos y animaciones.",
              false);
        this.pvp = pvp;
    }

    @Override
    public void toggle() {
        if (enabled) { setEnabled(false); return; }
        setEnabled(true);
        NovaMode.apply(NovaClient.modules, pvp);
    }
}
