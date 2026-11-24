#!/usr/bin/env python3
"""
Genera el texture pack «Nova» (interfaz de Nova Client) con dibujos propios, a tamaño real de píxel.
Uso:  python3 tools/gen_pack.py        →  resourcepack/  y  dist/Nova-Pack.zip
Un solo pack sirve para todas las versiones: incluye las rutas antiguas (hasta 1.20.1) y las nuevas (1.20.2 en adelante).
"""
import json, os, sys, zipfile
sys.path.insert(0, os.path.dirname(__file__))
from PIL import Image, ImageDraw

OUT = os.path.join(os.path.dirname(__file__), '..', 'resourcepack')
DIST = os.path.join(os.path.dirname(__file__), '..', 'dist')

# Paleta Nova: azul noche, cian (acento) y ámbar (detalle).
INK = (11, 16, 32)         # contorno
NAVY = (18, 26, 51)        # fondo oscuro
NAVY2 = (28, 40, 82)       # relieve
CYAN = (76, 201, 240)      # acento
CYAN_D = (38, 120, 168)
AMBER = (242, 184, 75)
ICE = (200, 211, 234)      # panel claro (el texto de Minecraft es gris oscuro: tiene que leerse)
ICE_L = (232, 240, 255)
ICE_D = (142, 156, 192)
SLOT = (26, 35, 71)
SLOT_D = (9, 13, 28)
VIOLET = (139, 92, 246)


def path(*p):
    f = os.path.join(OUT, *p)
    os.makedirs(os.path.dirname(f), exist_ok=True)
    return f


def save(im, *p):
    im.save(path(*p))


def new(w, h, c=(0, 0, 0, 0)):
    return Image.new('RGBA', (w, h), c)


def lerp(a, b, t):
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))


import colorsys


def rgb(h, sat=0.85, val=1.0):
    """Color arcoíris a partir de un tono 0..1."""
    r, g, b = colorsys.hsv_to_rgb(h % 1.0, sat, val)
    return (int(r * 255), int(g * 255), int(b * 255))


def mix(a, b, t):
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))


def strip(frames):
    """Une los cuadros en una tira vertical (formato de textura animada)."""
    w, h = frames[0].size
    out = Image.new('RGBA', (w, h * len(frames)))
    for i, f in enumerate(frames):
        out.paste(f, (0, h * i))
    return out


def anim_meta(w, h, frametime, scaling=None):
    m = {'animation': {'frametime': frametime, 'interpolate': True, 'width': w, 'height': h}}
    if scaling:
        m.update(scaling)
    return m


def write_json(obj, *p):
    with open(path(*p), 'w') as f:
        json.dump(obj, f)


def panel(d, x, y, w, h):
    """Panel claro con marco azul noche, brillo cian arriba y esquinas cortadas."""
    d.rectangle([x, y, x + w - 1, y + h - 1], fill=INK)
    d.rectangle([x + 1, y + 1, x + w - 2, y + h - 2], fill=NAVY2)
    d.rectangle([x + 2, y + 2, x + w - 3, y + h - 3], fill=ICE)
    # degradado suave hacia abajo
    for i in range(h - 4):
        d.line([x + 2, y + 2 + i, x + w - 3, y + 2 + i], fill=lerp(ICE_L, ICE, min(1, i / max(1, (h - 4) * 0.5))))
    d.line([x + 2, y + 2, x + w - 3, y + 2], fill=(255, 255, 255))
    d.line([x + 2, y + h - 3, x + w - 3, y + h - 3], fill=ICE_D)
    d.line([x + w - 3, y + 2, x + w - 3, y + h - 3], fill=ICE_D)
    # acento: filo cian arriba y puntas ámbar en las esquinas
    for i in range(x + 3, x + w - 3):
        d.point((i, y + 1), fill=rgb((i - x) / max(1, w) * 1.5))
        d.point((i, y + h - 2), fill=rgb((i - x) / max(1, w) * 1.5 + 0.5))
    for cx, cy in ((x + 1, y + 1), (x + w - 2, y + 1), (x + 1, y + h - 2), (x + w - 2, y + h - 2)):
        d.point((cx, cy), fill=AMBER)
    for cx, cy in ((x, y), (x + w - 1, y), (x, y + h - 1), (x + w - 1, y + h - 1)):
        d.point((cx, cy), fill=(0, 0, 0, 0))


def slot(d, x, y):
    """Hueco de objeto (18x18; el objeto se dibuja en x+1..x+16)."""
    d.rectangle([x, y, x + 17, y + 17], fill=SLOT)
    d.line([x, y, x + 17, y], fill=SLOT_D)
    d.line([x, y, x, y + 17], fill=SLOT_D)
    d.line([x, y + 17, x + 17, y + 17], fill=ICE_L)
    d.line([x + 17, y, x + 17, y + 17], fill=ICE_L)
    d.point((x + 1, y + 1), fill=NAVY2)


def slots(d, x0, y0, cols, rows):
    for r in range(rows):
        for c in range(cols):
            slot(d, x0 + 18 * c - 1, y0 + 18 * r - 1)


def arrow(d, x, y, body=16):
    for i in range(body):
        d.line([x + i, y + 6, x + i, y + 9], fill=CYAN_D)
    for i in range(8):
        d.line([x + body + i, y + 2 + i, x + body + i, y + 13 - i], fill=CYAN_D)
    d.line([x, y + 6, x + body - 1, y + 6], fill=CYAN)


# ------------------------------ botones (RGB animados) ------------------------------
N = 16  # cuadros por ciclo


def perim_hue(x, y, w, h, phase, cycles=2.0):
    """Tono según la posición a lo largo del perímetro, para que el color «corra» alrededor."""
    if y == 0:
        d_ = x
    elif x == w - 1:
        d_ = w + y
    elif y == h - 1:
        d_ = w + h + (w - 1 - x)
    else:
        d_ = 2 * w + h + (h - 1 - y)
    return (d_ / (2 * (w + h)) * cycles + phase) % 1.0


def button(kind, phase=0.0, w=200, h=20):
    im = new(w, h)
    d = ImageDraw.Draw(im)
    if kind == 'off':
        top, bot, edge = (44, 48, 66), (34, 37, 52), (20, 22, 32)
        d.rectangle([0, 0, w - 1, h - 1], fill=edge)
        for y in range(1, h - 1):
            d.line([1, y, w - 2, y], fill=lerp(top, bot, (y - 1) / max(1, h - 3)))
        d.line([1, 1, w - 2, 1], fill=(70, 74, 92))
    else:
        hov = kind == 'hover'
        d.rectangle([0, 0, w - 1, h - 1], fill=INK)
        for y in range(1, h - 1):
            for x in range(1, w - 1):
                base = lerp((34, 50, 104) if hov else NAVY2, NAVY, (y - 1) / max(1, h - 3))
                tint = rgb((x / w) * 1.5 + phase, 0.8, 1.0)
                im.putpixel((x, y), mix(base, tint, 0.30 if hov else 0.10) + (255,))
        # borde arcoíris que corre: completo al pasar el ratón, solo arriba/abajo en reposo
        for x in range(w):
            for y in (0, h - 1):
                if hov:
                    im.putpixel((x, y), rgb(perim_hue(x, y, w, h, phase)) + (255,))
                else:
                    im.putpixel((x, y), mix(INK, rgb(perim_hue(x, y, w, h, phase)), 0.85) + (255,))
        if hov:
            for y in range(h):
                for x in (0, w - 1):
                    im.putpixel((x, y), rgb(perim_hue(x, y, w, h, phase)) + (255,))
            d.line([1, 1, w - 2, 1], fill=(255, 255, 255, 90))
        else:
            d.line([1, 1, w - 2, 1], fill=(255, 255, 255, 40))
    for cx, cy in ((0, 0), (w - 1, 0), (0, h - 1), (w - 1, h - 1)):
        im.putpixel((cx, cy), (0, 0, 0, 0))
    return im


def hotbar(phase=0.0):
    w, h = 182, 22
    im = new(w, h)
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, w - 1, h - 1], fill=INK)
    d.rectangle([1, 1, w - 2, h - 2], fill=NAVY)
    for x in range(1, w - 1):
        c = rgb(x / w * 2 + phase)
        im.putpixel((x, 0), c + (255,))
        im.putpixel((x, h - 1), mix(INK, c, 0.8) + (255,))
    for i in range(9):
        x = 3 + 20 * i
        d.rectangle([x, 3, x + 16, 19], fill=SLOT_D)
        d.rectangle([x + 1, 4, x + 16, 19], fill=SLOT)
        d.line([x, 20, x + 17, 20], fill=mix(NAVY2, rgb(i / 9 + phase), 0.55))
    for cx, cy in ((0, 0), (w - 1, 0), (0, h - 1), (w - 1, h - 1)):
        im.putpixel((cx, cy), (0, 0, 0, 0))
    return im


def selection(h=24, phase=0.0):
    im = new(24, h)
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, 23, h - 1], outline=INK)
    for r in (1, 2):
        for x in range(r, 24 - r):
            for y in (r, h - 1 - r):
                im.putpixel((x, y), rgb(perim_hue(x, y, 24, h, phase, 1.0)) + (255,))
        for y in range(r, h - r):
            for x in (r, 23 - r):
                im.putpixel((x, y), rgb(perim_hue(x, y, 24, h, phase, 1.0)) + (255,))
    d.rectangle([3, 3, 20, h - 4], outline=(255, 255, 255, 160))
    return im


def xp_back():
    im = new(182, 5)
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, 181, 4], fill=INK)
    for x in range(1, 181):
        for y in (1, 2, 3):
            im.putpixel((x, y), mix((20, 26, 52), rgb(x / 182 * 2, 0.8, 1.0), 0.16) + (255,))
    return im


def xp_progress(phase=0.0):
    im = new(182, 5)
    for x in range(182):
        c = rgb(x / 182 * 2 + phase, 0.9, 1.0)
        im.putpixel((x, 0), mix(c, (255, 255, 255), 0.45) + (255,))
        im.putpixel((x, 1), c + (255,))
        im.putpixel((x, 2), c + (255,))
        im.putpixel((x, 3), mix(c, (0, 0, 0), 0.25) + (255,))
        im.putpixel((x, 4), mix(c, (0, 0, 0), 0.55) + (255,))
    return im


def slot_back(phase=0.0):
    im = new(24, 24)
    for y in range(24):
        for x in range(24):
            edge = min(x, y, 23 - x, 23 - y)
            if edge < 2:
                im.putpixel((x, y), rgb(perim_hue(x, y, 24, 24, phase, 1.0)) + (220,))
            else:
                im.putpixel((x, y), rgb(perim_hue(x, y, 24, 24, phase, 1.0), 0.6) + (70,))
    for cx, cy in ((0, 0), (23, 0), (0, 23), (23, 23)):
        im.putpixel((cx, cy), (0, 0, 0, 0))
    return im


def slot_front():
    im = new(24, 24)
    for y in range(24):
        for x in range(24):
            if min(x, y, 23 - x, 23 - y) == 0:
                im.putpixel((x, y), (255, 255, 255, 110))
    return im


def crosshair():
    im = new(15, 15)
    d = ImageDraw.Draw(im)
    arms = {'up': ((7, 1), (7, 4), (255, 60, 60)), 'down': ((7, 10), (7, 13), (70, 130, 255)),
            'left': ((1, 7), (4, 7), (255, 220, 60)), 'right': ((10, 7), (13, 7), (60, 255, 120))}
    for a, b, c in arms.values():
        for off in (-1, 1):
            if a[0] == b[0]:
                d.line([(a[0] + off, a[1]), (b[0] + off, b[1])], fill=(11, 16, 32, 160))
            else:
                d.line([(a[0], a[1] + off), (b[0], b[1] + off)], fill=(11, 16, 32, 160))
        d.line([a, b], fill=c)
    d.point((7, 7), fill=(255, 255, 255))
    return im


# ------------------------------ contenedores ------------------------------
def inv_rows(d, y_main, y_hot):
    slots(d, 8, y_main, 9, 3)
    slots(d, 8, y_hot, 9, 1)


def inventory():
    im = new(256, 256)
    d = ImageDraw.Draw(im)
    panel(d, 0, 0, 176, 166)
    for i in range(4):
        slot(d, 7, 7 + 18 * i)
    # vitrina del jugador
    d.rectangle([25, 7, 75, 77], fill=INK)
    d.rectangle([26, 8, 74, 76], fill=NAVY)
    for y in range(9, 76):
        d.line([27, y, 73, y], fill=lerp(NAVY2, NAVY, (y - 9) / 66))
    d.line([26, 8, 74, 8], fill=CYAN_D)
    d.point((26, 8), fill=AMBER); d.point((74, 8), fill=AMBER)
    slots(d, 98, 18, 2, 2)
    slot(d, 153, 27)
    slot(d, 76, 61)
    arrow(d, 135, 28, 9)
    inv_rows(d, 84, 142)
    return im


def crafting():
    im = new(256, 256)
    d = ImageDraw.Draw(im)
    panel(d, 0, 0, 176, 166)
    slots(d, 30, 17, 3, 3)
    slot(d, 123, 34)
    arrow(d, 92, 35)
    inv_rows(d, 84, 142)
    return im


def generic54():
    im = new(256, 256)
    d = ImageDraw.Draw(im)
    panel(d, 0, 0, 176, 222)
    slots(d, 8, 18, 9, 6)
    inv_rows(d, 140, 198)
    return im


def shulker():
    im = new(256, 256)
    d = ImageDraw.Draw(im)
    panel(d, 0, 0, 176, 166)
    slots(d, 8, 18, 9, 3)
    inv_rows(d, 84, 142)
    return im


def dispenser():
    im = new(256, 256)
    d = ImageDraw.Draw(im)
    panel(d, 0, 0, 176, 166)
    slots(d, 62, 17, 3, 3)
    inv_rows(d, 84, 142)
    return im


# ------------------------------ fondos y detalles ------------------------------
def tile(size=16, seed=7):
    im = Image.new('RGBA', (size, size), NAVY + (255,))
    s_ = seed
    for y in range(size):
        for x in range(size):
            s_ = (s_ * 1103515245 + 12345) & 0x7fffffff
            r = (s_ >> 8) % 100
            if r < 5:
                im.putpixel((x, y), NAVY2 + (255,))
            elif r < 11:
                hue = ((s_ >> 16) % 360) / 360
                im.putpixel((x, y), mix(NAVY, rgb(hue, 0.9, 1.0), 0.55) + (255,))
    return im


def separator():
    im = new(32, 2)
    for x in range(32):
        im.putpixel((x, 0), rgb(x / 32) + (255,))
        im.putpixel((x, 1), INK + (255,))
    return im


def pack_icon():
    im = Image.new('RGBA', (128, 128), NAVY + (255,))
    d = ImageDraw.Draw(im)
    for y in range(128):
        d.line([0, y, 127, y], fill=lerp((11, 16, 32), (40, 28, 96), y / 127))
    for i in range(2, 6):
        for k in range(i, 128 - i):
            for (px, py) in ((k, i), (k, 127 - i), (i, k), (127 - i, k)):
                im.putpixel((px, py), rgb(perim_hue(px, py, 128, 128, 0, 1.0)) + (255,))
    def blk(x, y, c):
        d.rectangle([x, y, x + 13, y + 13], fill=c)
        d.rectangle([x + 1, y + 1, x + 12, y + 12], fill=lerp(c, (255, 255, 255), .12))
    cells = [(0, 0), (0, 1), (0, 2), (0, 3), (0, 4), (1, 1), (2, 2), (3, 3), (4, 0), (4, 1), (4, 2), (4, 3), (4, 4)]
    ox, oy = 24, 28
    for cx, cy in cells:
        blk(ox + cx * 16, oy + cy * 16, rgb((cx + cy) / 8 * 0.85, 0.85, 1.0))
    d.rectangle([ox + 4 * 16 + 4, oy - 8, ox + 4 * 16 + 9, oy - 3], fill=AMBER)
    return im


def mcmeta_nine(w=200, h=20, border=3):
    return {'gui': {'scaling': {'type': 'nine_slice', 'width': w, 'height': h, 'border': border}}}


def main():
    import shutil
    shutil.rmtree(OUT, ignore_errors=True)
    # --- rutas de 1.20.2 en adelante (sprites) ---
    G = ('assets', 'minecraft', 'textures', 'gui')
    nine = {'gui': {'scaling': {'type': 'nine_slice', 'width': 200, 'height': 20, 'border': 3}}}
    for name, kind, ft in (('button', 'normal', 3), ('button_highlighted', 'hover', 2)):
        save(strip([button(kind, i / N) for i in range(N)]), *G, 'sprites', 'widget', name + '.png')
        write_json(anim_meta(200, 20, ft, nine), *G, 'sprites', 'widget', name + '.png.mcmeta')
    save(button('off'), *G, 'sprites', 'widget', 'button_disabled.png')
    write_json(nine, *G, 'sprites', 'widget', 'button_disabled.png.mcmeta')
    save(strip([hotbar(i / N) for i in range(N)]), *G, 'sprites', 'hud', 'hotbar.png')
    write_json(anim_meta(182, 22, 3), *G, 'sprites', 'hud', 'hotbar.png.mcmeta')
    save(strip([selection(23, i / N) for i in range(N)]), *G, 'sprites', 'hud', 'hotbar_selection.png')
    write_json(anim_meta(24, 23, 2), *G, 'sprites', 'hud', 'hotbar_selection.png.mcmeta')
    save(xp_back(), *G, 'sprites', 'hud', 'experience_bar_background.png')
    save(strip([xp_progress(i / 24) for i in range(24)]), *G, 'sprites', 'hud', 'experience_bar_progress.png')
    write_json(anim_meta(182, 5, 2), *G, 'sprites', 'hud', 'experience_bar_progress.png.mcmeta')
    save(strip([slot_back(i / N) for i in range(N)]), *G, 'sprites', 'container', 'slot_highlight_back.png')
    write_json(anim_meta(24, 24, 2), *G, 'sprites', 'container', 'slot_highlight_back.png.mcmeta')
    save(slot_front(), *G, 'sprites', 'container', 'slot_highlight_front.png')
    save(crosshair(), *G, 'sprites', 'hud', 'crosshair.png')
    save(separator(), 'assets', 'minecraft', 'textures', 'gui', 'header_separator.png')
    save(separator(), 'assets', 'minecraft', 'textures', 'gui', 'footer_separator.png')
    save(tile(), 'assets', 'minecraft', 'textures', 'gui', 'menu_background.png')
    # --- rutas de 1.20.1 y anteriores (widgets.png) ---
    w = new(256, 256)
    w.paste(hotbar(0.0), (0, 0))
    w.paste(selection(24), (0, 22))
    w.paste(button('off'), (0, 46))
    w.paste(button('normal', 0.0), (0, 66))
    w.paste(button('hover', 0.0), (0, 86))
    save(w, 'assets', 'minecraft', 'textures', 'gui', 'widgets.png')
    save(tile(), 'assets', 'minecraft', 'textures', 'gui', 'options_background.png')
    # --- contenedores (iguales en todas las versiones) ---
    for name, im in (('inventory', inventory()), ('crafting_table', crafting()), ('generic_54', generic54()), ('shulker_box', shulker()), ('dispenser', dispenser())):
        save(im, 'assets', 'minecraft', 'textures', 'gui', 'container', name + '.png')
    import gen_items, gen_blocks
    gen_items.build(OUT)
    gen_blocks.build(OUT)
    save(pack_icon(), 'pack.png')
    meta = {'pack': {
        'pack_format': 34,
        'supported_formats': {'min_inclusive': 5, 'max_inclusive': 120},
        'min_format': 5,
        'max_format': [120, 0],
        'description': '§bNova§r §7· interfaz de Nova Client\n§8Menús azul noche, acento cian'}}
    json.dump(meta, open(path('pack.mcmeta'), 'w'), ensure_ascii=False, indent=2)
    # --- zip ---
    os.makedirs(DIST, exist_ok=True)
    z = os.path.join(DIST, 'Nova-Pack.zip')
    with zipfile.ZipFile(z, 'w', zipfile.ZIP_DEFLATED) as zf:
        for root, _, files in os.walk(OUT):
            for f in sorted(files):
                full = os.path.join(root, f)
                zf.write(full, os.path.relpath(full, OUT).replace(os.sep, '/'))
    print('Listo:', os.path.abspath(z))


if __name__ == '__main__':
    main()
