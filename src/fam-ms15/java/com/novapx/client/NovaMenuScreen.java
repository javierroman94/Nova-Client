package com.novapx.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

/** Menú de Nova Client (Shift derecho): panel con cada módulo y un interruptor ON/OFF. */
public class NovaMenuScreen extends Screen {
    private final ModuleManager modules;
    private final String version;
    private int left, top, panelH;

    public NovaMenuScreen(ModuleManager modules) {
        super(new LiteralText("Nova Client"));
        this.modules = modules;
        this.version = FabricLoader.getInstance().getModContainer(NovaClient.ID)
                .map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("");
    }

    private static String label(Module m) { return m.isEnabled() ? "\u00a7aON" : "\u00a7cOFF"; }

    @Override
    protected void init() {
        panelH = MenuPainter.panelHeight(modules);
        left = this.width / 2 - MenuPainter.PANEL_W / 2;
        top = Math.max(8, this.height / 2 - panelH / 2);
        int y = top + MenuPainter.HEADER_H;
        for (Module m : modules.all()) {
            addButton(new ButtonWidget(left + MenuPainter.PANEL_W - MenuPainter.PAD - 52, y + 4, 52, 20, label(m), button -> {
                m.toggle();
                button.setMessage(label(m));
                Config.save(modules);
            }));
            y += MenuPainter.ROW_H;
        }
        addButton(new ButtonWidget(this.width / 2 - 50, top + panelH - MenuPainter.FOOTER_H + 8, 100, 20, "Cerrar", b -> onClose()));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        Gfx g = new Gfx();
        MenuPainter.panel(g, modules, width, height, left, top, panelH);
        super.render(mouseX, mouseY, delta);
        MenuPainter.texts(g, modules, version, width, left, top, panelH);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
