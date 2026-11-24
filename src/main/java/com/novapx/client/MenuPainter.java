package com.novapx.client;

/** Dibujo del menú de Nova Client (igual en todas las versiones; solo cambia la pantalla que lo contiene). */
public final class MenuPainter {
    public static final int PANEL_W = 340, PAD = 14, HEADER_H = 46, FOOTER_H = 40;
    /** Alto de cada fila: 30, o menos (hasta 24) si la pantalla es baja, para que el menú quepa entero. */
    public static int ROW_H = 30;
    private static final int COL_PANEL = 0xEE12101C, COL_BORDER = 0xFF7C4DFF, COL_HEADER = 0xFF1B1730, COL_ROW = 0x1AFFFFFF, COL_ROW_ON = 0x337C4DFF;

    private MenuPainter() {}

    public static int panelHeight(ModuleManager modules) {
        int n = Math.max(1, modules.all().size());
        int sh = 0;
        try { sh = net.minecraft.client.MinecraftClient.getInstance().getWindow().getScaledHeight(); } catch (Throwable ignored) {}
        ROW_H = sh <= 0 ? 30 : Math.max(24, Math.min(30, (sh - 16 - HEADER_H - FOOTER_H - 6) / n));
        return HEADER_H + n * ROW_H + FOOTER_H + 6;
    }

    public static void panel(Gfx g, ModuleManager modules, int sw, int sh, int left, int top, int panelH) {
        g.fill(0, 0, sw, sh, 0x66000000);
        g.fill(left - 1, top - 1, left + PANEL_W + 1, top + panelH + 1, COL_BORDER);
        g.fill(left, top, left + PANEL_W, top + panelH, COL_PANEL);
        g.fill(left, top, left + PANEL_W, top + HEADER_H - 6, COL_HEADER);
        g.fill(left, top + HEADER_H - 6, left + PANEL_W, top + HEADER_H - 5, COL_BORDER);
        int y = top + HEADER_H;
        for (Module m : modules.all()) {
            g.fill(left + 8, y + 1, left + PANEL_W - 8, y + ROW_H - 1, m.isEnabled() ? COL_ROW_ON : COL_ROW);
            y += ROW_H;
        }
    }

    public static void texts(Gfx g, ModuleManager modules, String version, int sw, int left, int top, int panelH) {
        g.text("\u00a7d\u00a7lNOVA \u00a7f\u00a7lCLIENT", left + PAD, top + 10, 0xFFFFFFFF);
        g.text("\u00a77Activa o desactiva cada m\u00f3dulo" + (version.isEmpty() ? "" : "  \u00b7  v" + version), left + PAD, top + 24, 0xFFAAAAAA);
        int y = top + HEADER_H;
        for (Module m : modules.all()) {
            g.text((m.isEnabled() ? "\u00a7f" : "\u00a77") + m.name, left + PAD, y + (ROW_H >= 27 ? 5 : 8), 0xFFFFFFFF);
            String d = m.description;
            int max = PANEL_W - PAD * 2 - 60;
            if (g.width(d) > max) d = g.trim(d, max - 6) + "\u2026";
            if (ROW_H >= 27) g.text("\u00a78" + d, left + PAD, y + 16, 0xFF888888);
            y += ROW_H;
        }
        g.centered("\u00a77Shift derecho para abrir y cerrar", sw / 2, top + panelH - 12, 0xFF777777);
    }
}
