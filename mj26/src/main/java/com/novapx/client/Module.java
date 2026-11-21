package com.novapx.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Un módulo del cliente: se activa o desactiva desde el menú Nova y puede dibujar en el HUD. */
public abstract class Module {
    public final String id, name, description;
    protected boolean enabled;

    protected Module(String id, String name, String description, boolean enabledByDefault) {
        this.id = id; this.name = name; this.description = description; this.enabled = enabledByDefault;
    }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean v) { this.enabled = v; }
    public void toggle() { setEnabled(!enabled); }
    public void tick(Minecraft client) {}
    public String line(Minecraft client) { return null; }
    public int draw(Minecraft client, GuiGraphicsExtractor g, int x, int y) { return 0; }
}
