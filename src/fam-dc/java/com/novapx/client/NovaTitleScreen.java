package com.novapx.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Pantalla de inicio de Nova Client: fondo nocturno, logo de bloques y los botones principales. Se dibuja solo con rectángulos y texto. */
public class NovaTitleScreen extends Screen {
    private final ModuleManager modules;
    private final String version;

    // Letras 5x7 del logo (1 = bloque).
    private static final String[][] LETTERS = {
        { "10001", "11001", "10101", "10011", "10001", "10001", "10001" }, // N
        { "01110", "10001", "10001", "10001", "10001", "10001", "01110" }, // O
        { "10001", "10001", "10001", "10001", "01010", "01010", "00100" }, // V
        { "01110", "10001", "10001", "11111", "10001", "10001", "10001" }  // A
    };
    private static final String[] STAR = {
        "000010000", "000010000", "000111000", "001111100", "111111111", "001111100", "000111000", "000010000", "000010000"
    };

    public NovaTitleScreen(ModuleManager modules) {
        super(Text.literal("Nova Client"));
        this.modules = modules;
        this.version = FabricLoader.getInstance().getModContainer(NovaClient.ID)
                .map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("");
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = this.height / 2 + 4;
        addDrawableChild(ButtonWidget.builder(Text.literal("Un jugador"), b -> this.client.setScreen(new SelectWorldScreen(this)))
                .dimensions(cx - 100, y, 200, 24).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Multijugador"), b -> this.client.setScreen(
                this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this)))
                .dimensions(cx - 100, y + 28, 200, 24).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Opciones"), b -> this.client.setScreen(new OptionsScreen(this, this.client.options)))
                .dimensions(cx - 100, y + 56, 98, 24).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("§dNova Client"), b -> this.client.setScreen(new NovaMenuScreen(modules)))
                .dimensions(cx + 2, y + 56, 98, 24).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Salir del juego"), b -> this.client.scheduleStop())
                .dimensions(cx - 100, y + 84, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Menú de Minecraft"), b -> {
            TitleScreen vanilla = new TitleScreen();
            NovaTitle.allowedVanilla = vanilla;
            this.client.setScreen(vanilla);
        }).dimensions(8, this.height - 28, 110, 20).build());
    }

    // El mismo fondo para cualquier versión: algunas pintan su propio fondo desde render() y otras desde renderBackground().
    public void renderBackground(DrawContext ctx) { paintBackground(new Gfx(ctx)); }
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) { paintBackground(new Gfx(ctx)); }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        try {
            draw(ctx, mouseX, mouseY, delta);
        } catch (Throwable t) {
            NovaTitle.fallback(this.client, t);
        }
    }

    private void draw(DrawContext ctx, int mouseX, int mouseY, float delta) {
        Gfx g = new Gfx(ctx);
        paintBackground(g);
        super.render(ctx, mouseX, mouseY, delta);
        paintLogo(g);
        String mc = this.client != null ? this.client.getGameVersion() : "";
        g.text("§7Nova Client v" + version + (mc.isEmpty() ? "" : "  ·  Minecraft " + mc), 8, this.height - 42, 0xFFAAAAAA);
        String legal = "No afiliado con Mojang ni Microsoft.";
        g.text("§8" + legal, this.width - g.width(legal) - 8, this.height - 14, 0xFF777777);
    }

    private void paintBackground(Gfx g) {
        int w = this.width, h = this.height;
        int step = 4;
        for (int y = 0; y < h; y += step) {
            float t = h <= 0 ? 0 : (float) y / h;
            // Azul noche arriba, violeta abajo.
            int r = (int) (6 + t * t * 74), gr = (int) (8 + t * 14), b = (int) (28 + t * 70);
            g.fill(0, y, w, Math.min(h, y + step), 0xFF000000 | (r << 16) | (gr << 8) | b);
        }
        // Estrellas fijas (siempre las mismas) con un parpadeo suave.
        long seed = 1234567L;
        long tick = System.currentTimeMillis() / 400;
        for (int i = 0; i < 90; i++) {
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            int x = (int) ((seed >>> 33) % Math.max(1, w));
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            int y = (int) ((seed >>> 33) % Math.max(1, (long) (h * 0.7)));
            int size = (i % 7 == 0) ? 2 : 1;
            int a = 90 + (int) ((tick + i * 3) % 5) * 28;
            g.fill(x, y, x + size, y + size, (a << 24) | 0xDDEBFF);
        }
        // Suelo de bloques abajo.
        int ground = h - 34;
        g.fill(0, ground, w, h, 0xFF14102A);
        for (int x = 0; x < w; x += 16) g.fill(x, ground, x + 16, ground + 4, ((x / 16) % 3 == 0) ? 0xFF37D6E8 : (((x / 16) % 3 == 1) ? 0xFF6A5CFF : 0xFF4F7BFF));
    }

    private void paintLogo(Gfx g) {
        int cx = this.width / 2;
        int px = Math.max(3, Math.min(7, this.height / 70)); // tamaño del bloque según la pantalla
        int letterW = 5 * px, gap = px * 2;
        int totalW = 4 * letterW + 3 * gap;
        int top = Math.max(14, this.height / 2 - 7 * px - 62);
        int left = cx - totalW / 2;
        // Estrella encima.
        int sp = Math.max(2, px - 2);
        int sx = cx - (9 * sp) / 2, sy = top - 9 * sp - 6;
        long pulse = (System.currentTimeMillis() / 300) % 2;
        for (int r = 0; r < STAR.length; r++) for (int c = 0; c < STAR[r].length(); c++) {
            if (STAR[r].charAt(c) != '1') continue;
            boolean core = r >= 3 && r <= 5 && c >= 3 && c <= 5;
            g.fill(sx + c * sp, sy + r * sp, sx + (c + 1) * sp, sy + (r + 1) * sp, core ? 0xFFFFFFFF : (pulse == 0 ? 0xFF9FF3FF : 0xFF6FE6FF));
        }
        for (int i = 0; i < 4; i++) {
            int lx = left + i * (letterW + gap);
            String[] rows = LETTERS[i];
            for (int r = 0; r < 7; r++) for (int c = 0; c < 5; c++) {
                if (rows[r].charAt(c) != '1') continue;
                int x = lx + c * px, y = top + r * px;
                g.fill(x + 2, y + 2, x + px + 2, y + px + 2, 0xFF5B3CC4); // sombra violeta
                g.fill(x, y, x + px, y + px, 0xFFFFFFFF);
                g.fill(x, y, x + px, y + 1, 0xFFB9F3FF); // brillo
            }
        }
        g.centered("§b§lC L I E N T", cx, top + 7 * px + 8, 0xFF9FF3FF);
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}
