package com.novapx.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Menú de Nova Client (Shift derecho): activa o desactiva cada módulo. */
public class NovaMenuScreen extends Screen {
    private final ModuleManager modules;

    public NovaMenuScreen(ModuleManager modules) {
        super(Text.literal("Nova Client"));
        this.modules = modules;
    }

    private static Text label(Module m) {
        return Text.literal(m.name + ": " + (m.isEnabled() ? "§aON" : "§cOFF"));
    }

    @Override
    protected void init() {
        int w = 200, h = 20, x = this.width / 2 - w / 2;
        int y = this.height / 2 - (modules.all().size() * 24) / 2;
        for (Module m : modules.all()) {
            ButtonWidget b = ButtonWidget.builder(label(m), button -> {
                m.toggle();
                button.setMessage(label(m));
                Config.save(modules);
            }).dimensions(x, y, w, h).build();
            addDrawableChild(b);
            y += 24;
        }
        addDrawableChild(ButtonWidget.builder(Text.literal("Cerrar"), b -> close()).dimensions(x, y + 6, w, h).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§6Nova Client"), this.width / 2, 20, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Activa o desactiva los módulos"), this.width / 2, 34, 0xAAAAAA);
    }
}
