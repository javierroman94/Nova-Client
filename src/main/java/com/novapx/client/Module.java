package com.novapx.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** Un módulo del cliente: se puede activar o desactivar desde el menú Nova y puede dibujar en el HUD. */
public abstract class Module {
    public final String id;
    public final String name;
    public final String description;
    protected boolean enabled;

    protected Module(String id, String name, String description, boolean enabledByDefault) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.enabled = enabledByDefault;
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean value) { this.enabled = value; }
    public void toggle() { setEnabled(!enabled); }

    /** Se llama en cada tick del cliente (20 veces por segundo), con el módulo activo o no. */
    public void tick(MinecraftClient client) {}

    /** Líneas de texto simples para el panel de la izquierda. Los módulos gráficos devuelven null y usan draw(). */
    public String line(MinecraftClient client) { return null; }

    /** Dibujo propio. Devuelve la altura usada en píxeles (0 si no dibuja nada). */
    public int draw(MinecraftClient client, DrawContext ctx, int x, int y) { return 0; }
}
