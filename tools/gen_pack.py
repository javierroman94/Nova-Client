#!/usr/bin/env python3
"""
Genera el texture pack «Nova» (interfaz de Nova Client) con dibujos propios, a tamaño real de píxel.
Uso:  python3 tools/gen_pack.py        →  resourcepack/  y  dist/Nova-Pack.zip
Un solo pack sirve para todas las versiones: incluye las rutas antiguas (hasta 1.20.1) y las nuevas (1.20.2 en adelante).
"""
import json, os, zipfile
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
    d.line([x + 3, y + 1, x + w - 4, y + 1], fill=CYAN)
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


# ------------------------------ botones ------------------------------
def button(kind, w=200, h=20):
    im = new(w, h)
    d = ImageDraw.Draw(im)
    if kind == 'normal':
        top, bot, edge, hi = NAVY2, NAVY, INK, CYAN_D
    elif kind == 'hover':
        top, bot, edge, hi = (44, 74, 138), NAVY2, INK, CYAN
    else:
        top, bot, edge, hi = (44, 48, 66), (34, 37, 52), (20, 22, 32), (70, 74, 92)
    d.rectangle([0, 0, w - 1, h - 1], fill=edge)
    for y in range(1, h - 1):
        d.line([1, y, w - 2, y], fill=lerp(top, bot, (y - 1) / max(1, h - 3)))
    d.line([1, 1, w - 2, 1], fill=hi)                       # filo de luz arriba
    d.line([1, h - 2, w - 2, h - 2], fill=INK)              # sombra abajo
    if kind == 'hover':
        d.line([0, 0, w - 1, 0], fill=CYAN)                 # contorno cian al pasar el ratón
        d.line([0, h - 1, w - 1, h - 1], fill=CYAN)
        d.line([0, 0, 0, h - 1], fill=CYAN)
        d.line([w - 1, 0, w - 1, h - 1], fill=CYAN)
        d.point((2, 3), fill=AMBER)
        d.point((w - 3, h - 4), fill=AMBER)
    for cx, cy in ((0, 0), (w - 1, 0), (0, h - 1), (w - 1, h - 1)):
        d.point((cx, cy), fill=(0, 0, 0, 0))
    return im


def hotbar():
    w, h = 182, 22
    im = new(w, h)
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, w - 1, h - 1], fill=INK)
    d.rectangle([1, 1, w - 2, h - 2], fill=NAVY)
    d.line([1, 1, w - 2, 1], fill=CYAN_D)
    for i in range(9):
        x = 3 + 20 * i
        d.rectangle([x, 3, x + 15 + 1, 3 + 15 + 1], fill=SLOT_D)
        d.rectangle([x + 1, 4, x + 16, 19], fill=SLOT)
        d.line([x, 3 + 16 + 1, x + 17, 3 + 16 + 1], fill=NAVY2)
    for cx, cy in ((0, 0), (w - 1, 0), (0, h - 1), (w - 1, h - 1)):
        d.point((cx, cy), fill=(0, 0, 0, 0))
    return im


def selection(h=24):
    im = new(24, h)
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, 23, h - 1], outline=INK)
    d.rectangle([1, 1, 22, h - 2], outline=CYAN)
    d.rectangle([2, 2, 21, h - 3], outline=ICE_L)
    for cx, cy in ((0, 0), (23, 0), (0, h - 1), (23, h - 1)):
        d.point((cx, cy), fill=AMBER)
    return im


def crosshair():
    im = new(15, 15)
    d = ImageDraw.Draw(im)
    for a, b in (((7, 1), (7, 4)), ((7, 10), (7, 13)), ((1, 7), (4, 7)), ((10, 7), (13, 7))):
        d.line([a, b], fill=CYAN)
    d.point((7, 7), fill=AMBER)
    # contorno oscuro fino para que se vea en cualquier fondo
    for a, b in (((6, 1), (6, 4)), ((8, 1), (8, 4)), ((6, 10), (6, 13)), ((8, 10), (8, 13)), ((1, 6), (4, 6)), ((1, 8), (4, 8)), ((10, 6), (13, 6)), ((10, 8), (13, 8))):
        d.line([a, b], fill=(11, 16, 32, 150))
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
    d = ImageDraw.Draw(im)
    s = seed
    for y in range(size):
        for x in range(size):
            s = (s * 1103515245 + 12345) & 0x7fffffff
            r = (s >> 8) % 100
            if r < 6:
                im.putpixel((x, y), NAVY2 + (255,))
            elif r < 7:
                im.putpixel((x, y), CYAN_D + (255,))
            elif r < 8 and (x + y) % 3 == 0:
                im.putpixel((x, y), VIOLET + (255,))
    return im


def separator():
    im = new(32, 2)
    d = ImageDraw.Draw(im)
    d.line([0, 0, 31, 0], fill=CYAN)
    d.line([0, 1, 31, 1], fill=INK)
    return im


def pack_icon():
    im = Image.new('RGBA', (128, 128), NAVY + (255,))
    d = ImageDraw.Draw(im)
    for y in range(128):
        d.line([0, y, 127, y], fill=lerp((11, 16, 32), (40, 28, 96), y / 127))
    d.rectangle([2, 2, 125, 125], outline=CYAN)
    d.rectangle([5, 5, 122, 122], outline=NAVY2)
    # «N» de bloques con brillo
    def blk(x, y, c):
        d.rectangle([x, y, x + 13, y + 13], fill=c)
        d.rectangle([x + 1, y + 1, x + 12, y + 12], fill=lerp(c, (255, 255, 255), .12))
    cells = [(0, 0), (0, 1), (0, 2), (0, 3), (0, 4), (1, 1), (2, 2), (3, 3), (4, 0), (4, 1), (4, 2), (4, 3), (4, 4)]
    ox, oy = 24, 28
    for cx, cy in cells:
        t = (cx + cy) / 8
        blk(ox + cx * 16, oy + cy * 16, lerp(CYAN, VIOLET, t))
    d.rectangle([ox + 4 * 16 + 4, oy - 8, ox + 4 * 16 + 9, oy - 3], fill=AMBER)
    return im


def mcmeta_nine(w=200, h=20, border=3):
    return {'gui': {'scaling': {'type': 'nine_slice', 'width': w, 'height': h, 'border': border}}}


def main():
    import shutil
    shutil.rmtree(OUT, ignore_errors=True)
    # --- rutas de 1.20.2 en adelante (sprites) ---
    for name, kind in (('button', 'normal'), ('button_highlighted', 'hover'), ('button_disabled', 'off')):
        save(button(kind), 'assets', 'minecraft', 'textures', 'gui', 'sprites', 'widget', name + '.png')
        json.dump(mcmeta_nine(), open(path('assets', 'minecraft', 'textures', 'gui', 'sprites', 'widget', name + '.png.mcmeta'), 'w'))
    save(hotbar(), 'assets', 'minecraft', 'textures', 'gui', 'sprites', 'hud', 'hotbar.png')
    save(selection(23), 'assets', 'minecraft', 'textures', 'gui', 'sprites', 'hud', 'hotbar_selection.png')
    save(crosshair(), 'assets', 'minecraft', 'textures', 'gui', 'sprites', 'hud', 'crosshair.png')
    save(separator(), 'assets', 'minecraft', 'textures', 'gui', 'header_separator.png')
    save(separator(), 'assets', 'minecraft', 'textures', 'gui', 'footer_separator.png')
    save(tile(), 'assets', 'minecraft', 'textures', 'gui', 'menu_background.png')
    # --- rutas de 1.20.1 y anteriores (widgets.png) ---
    w = new(256, 256)
    w.paste(hotbar(), (0, 0))
    w.paste(selection(24), (0, 22))
    w.paste(button('off'), (0, 46))
    w.paste(button('normal'), (0, 66))
    w.paste(button('hover'), (0, 86))
    save(w, 'assets', 'minecraft', 'textures', 'gui', 'widgets.png')
    save(tile(), 'assets', 'minecraft', 'textures', 'gui', 'options_background.png')
    # --- contenedores (iguales en todas las versiones) ---
    for name, im in (('inventory', inventory()), ('crafting_table', crafting()), ('generic_54', generic54()), ('shulker_box', shulker()), ('dispenser', dispenser())):
        save(im, 'assets', 'minecraft', 'textures', 'gui', 'container', name + '.png')
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
