package com.novapx.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Menú de Nova Client (Shift derecho): un panel con cada módulo, su descripción y un interruptor ON/OFF. */
public class NovaMenuScreen extends Screen {
    private static final int PANEL_W = 340;
    private static final int ROW_H = 30;
    private static final int PAD = 14;
    private static final int HEADER_H = 46;
    private static final int FOOTER_H = 40;

    private static final int COL_PANEL = 0xEE12101C;
    private static final int COL_BORDER = 0xFF7C4DFF;
    private static final int COL_HEADER = 0xFF1B1730;
    private static final int COL_ROW = 0x1AFFFFFF;
    private static final int COL_ROW_ON = 0x337C4DFF;

    private final ModuleManager modules;
    private final String version;
    private int left, top, panelH;

    public NovaMenuScreen(ModuleManager modules) {
        super(Text.literal("Nova Client"));
        this.modules = modules;
        this.version = FabricLoader.getInstance().getModContainer(NovaClient.ID)
                .map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("");
    }

    private static Text label(Module m) {
        return Text.literal(m.isEnabled() ? "§aON" : "§cOFF");
    }

    @Override
    protected void init() {
        int rows = modules.all().size();
        panelH = HEADER_H + rows * ROW_H + FOOTER_H + 6;
        left = this.width / 2 - PANEL_W / 2;
        top = Math.max(8, this.height / 2 - panelH / 2);

        // Fondo del panel: se añade primero para que quede debajo de los botones.
        addDrawable((ctx, mouseX, mouseY, delta) -> drawPanel(ctx));

        int y = top + HEADER_H;
        for (Module m : modules.all()) {
            ButtonWidget b = ButtonWidget.builder(label(m), button -> {
                m.toggle();
                button.setMessage(label(m));
                Config.save(modules);
            }).dimensions(left + PANEL_W - PAD - 52, y + 4, 52, 20).build();
            addDrawableChild(b);
            y += ROW_H;
        }
        addDrawableChild(ButtonWidget.builder(Text.literal("Cerrar"), b -> close())
                .dimensions(this.width / 2 - 50, top + panelH - FOOTER_H + 8, 100, 20).build());

        // Textos: se añaden al final para que queden encima.
        addDrawable((ctx, mouseX, mouseY, delta) -> drawTexts(ctx));
    }

    private void drawPanel(DrawContext ctx) {
        // oscurece el juego detrás
        ctx.fill(0, 0, this.width, this.height, 0x66000000);
        // borde y fondo
        ctx.fill(left - 1, top - 1, left + PANEL_W + 1, top + panelH + 1, COL_BORDER);
        ctx.fill(left, top, left + PANEL_W, top + panelH, COL_PANEL);
        // cabecera
        ctx.fill(left, top, left + PANEL_W, top + HEADER_H - 6, COL_HEADER);
        ctx.fill(left, top + HEADER_H - 6, left + PANEL_W, top + HEADER_H - 5, COL_BORDER);
        // filas
        int y = top + HEADER_H;
        for (Module m : modules.all()) {
            ctx.fill(left + 8, y + 1, left + PANEL_W - 8, y + ROW_H - 1, m.isEnabled() ? COL_ROW_ON : COL_ROW);
            y += ROW_H;
        }
    }

    private void drawTexts(DrawContext ctx) {
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("§d§lNOVA §f§lCLIENT"), left + PAD, top + 10, 0xFFFFFFFF);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("§7Activa o desactiva cada módulo" + (version.isEmpty() ? "" : "  ·  v" + version)), left + PAD, top + 24, 0xFFAAAAAA);
        int y = top + HEADER_H;
        for (Module m : modules.all()) {
            ctx.drawTextWithShadow(this.textRenderer, Text.literal((m.isEnabled() ? "§f" : "§7") + m.name), left + PAD, y + 5, 0xFFFFFFFF);
            String d = m.description;
            int max = PANEL_W - PAD * 2 - 60;
            if (this.textRenderer.getWidth(d) > max) d = this.textRenderer.trimToWidth(d, max - 6) + "…";
            ctx.drawTextWithShadow(this.textRenderer, Text.literal("§8" + d), left + PAD, y + 16, 0xFF888888);
            y += ROW_H;
        }
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§7Shift derecho para abrir y cerrar"), this.width / 2, top + panelH - 12, 0xFF777777);
    }

    @Override
    public boolean shouldPause() { return false; }
}
