package com.novapx.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Menú de Nova Client (Shift derecho): panel con cada módulo y un interruptor ON/OFF. */
public class NovaMenuScreen extends Screen {
    private static final int PANEL_W = 340, ROW_H = 30, PAD = 14, HEADER_H = 46, FOOTER_H = 40;
    private final ModuleManager modules;
    private final String version;
    private int left, top, panelH;

    public NovaMenuScreen(ModuleManager modules) {
        super(Component.literal("Nova Client"));
        this.modules = modules;
        this.version = FabricLoader.getInstance().getModContainer(NovaClient.ID).map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("");
    }

    private static Component label(Module m) { return Component.literal(m.isEnabled() ? "§aON" : "§cOFF"); }

    @Override
    protected void init() {
        panelH = HEADER_H + modules.all().size() * ROW_H + FOOTER_H + 6;
        left = this.width / 2 - PANEL_W / 2;
        top = Math.max(8, this.height / 2 - panelH / 2);
        int y = top + HEADER_H;
        for (Module m : modules.all()) {
            addRenderableWidget(Button.builder(label(m), b -> { m.toggle(); b.setMessage(label(m)); Config.save(modules); })
                    .bounds(left + PANEL_W - PAD - 52, y + 4, 52, 20).build());
            y += ROW_H;
        }
        addRenderableWidget(Button.builder(Component.literal("Cerrar"), b -> onClose()).bounds(this.width / 2 - 50, top + panelH - FOOTER_H + 8, 100, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        g.fill(0, 0, width, height, 0x66000000);
        g.fill(left - 1, top - 1, left + PANEL_W + 1, top + panelH + 1, 0xFF7C4DFF);
        g.fill(left, top, left + PANEL_W, top + panelH, 0xEE12101C);
        g.fill(left, top, left + PANEL_W, top + HEADER_H - 6, 0xFF1B1730);
        g.fill(left, top + HEADER_H - 6, left + PANEL_W, top + HEADER_H - 5, 0xFF7C4DFF);
        int y = top + HEADER_H;
        for (Module m : modules.all()) {
            g.fill(left + 8, y + 1, left + PANEL_W - 8, y + ROW_H - 1, m.isEnabled() ? 0x337C4DFF : 0x1AFFFFFF);
            y += ROW_H;
        }
        super.extractRenderState(g, mx, my, delta);
        g.text(font, "§d§lNOVA §f§lCLIENT", left + PAD, top + 10, 0xFFFFFFFF);
        g.text(font, "§7Activa o desactiva cada módulo" + (version.isEmpty() ? "" : "  ·  v" + version), left + PAD, top + 24, 0xFFAAAAAA);
        y = top + HEADER_H;
        for (Module m : modules.all()) {
            g.text(font, (m.isEnabled() ? "§f" : "§7") + m.name, left + PAD, y + 5, 0xFFFFFFFF);
            String d = m.description;
            int max = PANEL_W - PAD * 2 - 60;
            if (font.width(d) > max) d = font.plainSubstrByWidth(d, max - 6) + "…";
            g.text(font, "§8" + d, left + PAD, y + 16, 0xFF888888);
            y += ROW_H;
        }
        g.centeredText(font, "§7Shift derecho para abrir y cerrar", width / 2, top + panelH - 12, 0xFF777777);
    }

    @Override public boolean isPauseScreen() { return false; }
}
