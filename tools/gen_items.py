#!/usr/bin/env python3
"""Ores con marco brillante e ítems con dibujo sombreado para el pack Nova (dibujos propios, 16x16)."""
import math, os
from PIL import Image

T = (0, 0, 0, 0)


def mixc(a, b, t):
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))


def px(im, x, y, c):
    if 0 <= x < im.width and 0 <= y < im.height:
        im.putpixel((x, y), tuple(c[:3]) + (255,))


def tones(base):
    """Cuatro tonos (oscuro, base, claro, brillo) a partir de un color."""
    return [mixc(base, (0, 0, 20), .45), base, mixc(base, (255, 255, 255), .3), mixc(base, (255, 255, 255), .7)]


def blob(im, cx, cy, rx, ry, tn, lump=0, seed=1):
    s = seed
    for y in range(16):
        for x in range(16):
            nx, ny = (x + .5 - cx) / rx, (y + .5 - cy) / ry
            r = nx * nx + ny * ny
            if lump:
                s = (s * 1103515245 + 12345) & 0x7fffffff
                r += ((s >> 10) % 100 - 50) / 50 * lump
            if r <= 1:
                nz = math.sqrt(max(0, 1 - r))
                dot = -.45 * nx - .55 * ny + .7 * nz
                lvl = 0 if dot < .12 else 1 if dot < .5 else 2 if dot < .82 else 3
                px(im, x, y, tn[lvl])


def poly(im, pts, fn):
    """Rellena un polígono; fn(x,y) devuelve el color."""
    for y in range(16):
        for x in range(16):
            inside, n = False, len(pts)
            j = n - 1
            for i in range(n):
                xi, yi = pts[i]; xj, yj = pts[j]
                if (yi > y + .5) != (yj > y + .5) and (x + .5) < (xj - xi) * (y + .5 - yi) / (yj - yi) + xi:
                    inside = not inside
                j = i
            if inside:
                px(im, x, y, fn(x, y))


def finish(im, outline):
    out = im.copy()
    for y in range(16):
        for x in range(16):
            if im.getpixel((x, y))[3] == 0:
                for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    xx, yy = x + dx, y + dy
                    if 0 <= xx < 16 and 0 <= yy < 16 and im.getpixel((xx, yy))[3]:
                        out.putpixel((x, y), tuple(outline) + (255,))
                        break
    # sombra suave abajo a la derecha
    for y in range(15):
        for x in range(15):
            if out.getpixel((x, y))[3] == 0 and out.getpixel((x - 1, y - 1))[3] and x and y:
                out.putpixel((x, y), (0, 0, 0, 55))
    return out


def new():
    return Image.new('RGBA', (16, 16), T)


# ------------------------------ ores ------------------------------
def ore(base, ore_col, frame, seed, hi=None, accent=None, blobs=8):
    im = Image.new('RGBA', (16, 16))
    s = seed
    for y in range(16):
        for x in range(16):
            s = (s * 1103515245 + 12345) & 0x7fffffff
            r = (s >> 9) % 100
            c = base if r < 70 else mixc(base, (255, 255, 255), .08) if r < 85 else mixc(base, (0, 0, 0), .25)
            im.putpixel((x, y), c + (255,))
    shapes = [[(0, 0), (1, 0), (0, 1), (1, 1)], [(0, 0), (1, 0), (2, 0), (1, 1)], [(0, 0), (0, 1), (1, 1)], [(0, 0), (1, 0), (1, 1), (2, 1)], [(0, 0)]]
    placed = []
    tries = 0
    while len(placed) < blobs and tries < 200:
        tries += 1
        s = (s * 1103515245 + 12345) & 0x7fffffff
        cx = 2 + (s >> 8) % 11
        s = (s * 1103515245 + 12345) & 0x7fffffff
        cy = 2 + (s >> 8) % 11
        if any(abs(cx - a) < 4 and abs(cy - b) < 4 for a, b in placed):
            continue
        placed.append((cx, cy))
        shp = shapes[(cx * 7 + cy) % len(shapes)]
        for dx, dy in shp:                                   # halo de color alrededor
            for ax in (-1, 0, 1):
                for ay in (-1, 0, 1):
                    xx, yy = cx + dx + ax, cy + dy + ay
                    if 1 <= xx <= 14 and 1 <= yy <= 14:
                        im.putpixel((xx, yy), mixc(im.getpixel((xx, yy))[:3], ore_col, .22) + (255,))
        for dx, dy in shp:
            xx, yy = cx + dx, cy + dy
            px(im, xx, yy, ore_col)
        px(im, cx + shp[0][0], cy + shp[0][1], hi or mixc(ore_col, (255, 255, 255), .6))
        px(im, cx + shp[-1][0], cy + shp[-1][1], mixc(ore_col, (0, 0, 0), .35))
        if accent and (cx + cy) % 3 == 0:
            px(im, cx + 2, cy + 1, accent)
    for i in range(16):                                      # marco brillante (como cubo contorneado)
        for x, y in ((i, 0), (i, 15), (0, i), (15, i)):
            im.putpixel((x, y), frame + (255,))
        for x, y in ((i, 1), (i, 14), (1, i), (14, i)):
            if 1 <= i <= 14:
                im.putpixel((x, y), mixc(im.getpixel((x, y))[:3], frame, .45) + (255,))
    return im


STONE, DEEP, NETH = (104, 106, 112), (48, 50, 58), (104, 42, 44)
ORES = {
    'coal': ((30, 30, 36), (236, 236, 244), (255, 255, 255)),
    'iron': ((240, 196, 150), (236, 190, 140), None),
    'copper': ((255, 128, 64), (240, 120, 70), (70, 215, 175)),
    'gold': ((255, 214, 40), (255, 220, 60), None),
    'redstone': ((255, 40, 40), (255, 70, 70), None),
    'lapis': ((50, 100, 255), (90, 140, 255), None),
    'diamond': ((80, 255, 240), (120, 255, 245), None),
    'emerald': ((50, 255, 110), (110, 255, 150), None),
}


def build_blocks(put):
    n = 1
    for name, (col, frame, acc) in ORES.items():
        for pre, base in (('', STONE), ('deepslate_', DEEP)):
            if name == 'coal' and not pre:
                base = (170, 172, 178)
            if name == 'coal' and pre:
                base = (96, 98, 106)
            put('block', pre + name + '_ore', ore(base, col, frame, n * 977, accent=acc if name == 'copper' else None)); n += 1
    put('block', 'nether_gold_ore', ore(NETH, (255, 214, 40), (255, 220, 60), 31337))
    put('block', 'nether_quartz_ore', ore(NETH, (245, 240, 235), (250, 245, 240), 4242))


# ------------------------------ ítems ------------------------------
def ingot(col, outline):
    im = new(); tn = tones(col)
    poly(im, [(4, 5), (14, 5), (12, 8), (2, 8)], lambda x, y: tn[3] if y == 5 else tn[2])
    poly(im, [(2, 8), (12, 8), (12, 12), (2, 12)], lambda x, y: tn[1] if y < 11 else tn[0])
    poly(im, [(12, 8), (14, 5), (14, 9), (12, 12)], lambda x, y: tn[0])
    px(im, 3, 9, tn[3]); px(im, 4, 9, tn[2])
    return finish(im, outline)


def gem(pts, col, outline, split=8):
    im = new(); tn = tones(col)
    poly(im, pts, lambda x, y: tn[3] if (y < 6 and x < split) else tn[2] if y < 6 else tn[1] if x < split else tn[0])
    return finish(im, outline)


def raw(col, outline, seed):
    im = new(); tn = tones(col); st = tones((120, 120, 128))
    blob(im, 8, 8.5, 6, 5, st, lump=.35, seed=seed)
    blob(im, 6.5, 7, 3.2, 2.6, tn, lump=.3, seed=seed + 5)
    blob(im, 10.5, 10, 2.6, 2.2, tn, lump=.3, seed=seed + 9)
    return finish(im, outline)


def apple(col, leaf=(70, 190, 70), shine=False):
    im = new(); tn = tones(col)
    blob(im, 5.6, 9, 4.6, 4.8, tn); blob(im, 10.4, 9, 4.6, 4.8, tn)
    blob(im, 8, 9.5, 4.2, 4.6, tn)
    for y in range(2, 6): px(im, 8, y, (110, 70, 40))
    blob(im, 10.5, 3.5, 2.6, 1.6, tones(leaf))
    if shine:
        px(im, 4, 6, (255, 255, 230)); px(im, 3, 8, (255, 255, 230))
    return finish(im, mixc(col, (0, 0, 0), .7))


def potato(col, outline, baked=False):
    im = new(); tn = tones(col)
    blob(im, 8, 9, 6, 4.4, tn, lump=.25, seed=3)
    if baked:
        for x in range(5, 12): px(im, x, 8, (255, 232, 130))
        for x in range(6, 11): px(im, x, 7, (255, 244, 180))
    else:
        for x, y in ((5, 9), (9, 11), (10, 8)): px(im, x, y, mixc(col, (0, 0, 0), .4))
    return finish(im, outline)


def carrot():
    im = new(); tn = tones((255, 150, 40))
    poly(im, [(4, 6), (12, 8), (3, 14)], lambda x, y: tn[3] if (x + y) % 7 == 0 else tn[2] if y < 8 else tn[1] if x < 8 else tn[0])
    gt = tones((70, 190, 70))
    poly(im, [(10, 6), (14, 2), (13, 7)], lambda x, y: gt[2]); poly(im, [(9, 7), (11, 1), (12, 7)], lambda x, y: gt[1])
    return finish(im, (110, 50, 10))


def cookie():
    im = new(); tn = tones((214, 160, 90))
    blob(im, 8, 8, 6, 5.6, tn, lump=.1, seed=2)
    for x, y in ((6, 6), (10, 7), (7, 10), (11, 11), (5, 9)): px(im, x, y, (80, 45, 30)); px(im, x + 1, y, (60, 32, 22))
    return finish(im, (84, 52, 30))


def bread():
    im = new(); tn = tones((222, 160, 70))
    poly(im, [(2, 7), (14, 7), (14, 12), (2, 12)], lambda x, y: tn[2] if y < 9 else tn[1] if y < 11 else tn[0])
    blob(im, 8, 7.4, 6.6, 3.4, tn)
    for x in (5, 8, 11): px(im, x, 6, tn[3]); px(im, x + 1, 7, tn[0])
    return finish(im, (96, 58, 24))


def melon():
    im = new()
    for y in range(16):
        for x in range(16):
            d = math.hypot(x + .5 - 8, y + .5 - 2)
            if y >= 3 and d < 11:
                c = (46, 150, 60) if d > 9.4 else (220, 245, 200) if d > 8.5 else (240, 70, 80) if d < 7.6 or True else 0
                if d <= 8.5:
                    c = mixc((240, 70, 80), (255, 140, 140), max(0, 1 - y / 12) * .5)
                px(im, x, y, c)
    for x, y in ((6, 7), (9, 9), (11, 7), (8, 11)): px(im, x, y, (30, 20, 25))
    return finish(im, (30, 90, 40))


def meat(col, edge, bone=False, outline=(70, 30, 20)):
    im = new(); tn = tones(col); et = tones(edge)
    blob(im, 8, 8.5, 6.4, 4.8, et, lump=.15, seed=7)
    blob(im, 8, 8.5, 5.2, 3.6, tn, lump=.2, seed=11)
    if bone:
        px(im, 12, 6, (240, 236, 220)); px(im, 13, 5, (240, 236, 220)); px(im, 13, 7, (240, 236, 220))
    return finish(im, outline)


def drumstick(col, outline):
    im = new(); tn = tones(col)
    blob(im, 6.2, 6.2, 4.6, 4.2, tn, lump=.15, seed=4)
    poly(im, [(8, 9), (10, 8), (13, 12), (11, 13)], lambda x, y: (238, 232, 214))
    blob(im, 13, 12.6, 1.8, 1.8, [(210, 204, 186), (238, 232, 214), (250, 246, 236), (255, 255, 255)])
    return finish(im, outline)


def coal(col, outline):
    im = new(); tn = tones(col)
    blob(im, 6, 8, 4.6, 4.2, tn, lump=.3, seed=5); blob(im, 10.5, 9.5, 3.8, 3.4, tn, lump=.3, seed=8)
    return finish(im, outline)


def redstone():
    im = new(); tn = tones((230, 30, 30))
    for cx, cy, r in ((5.5, 9, 2.8), (10, 7.5, 3), (9, 11.5, 2.5)): blob(im, cx, cy, r, r * .85, tn, lump=.2, seed=int(cx * 3))
    return finish(im, (90, 8, 8))


def lapis():
    im = new(); tn = tones((40, 80, 230))
    for cx, cy, r in ((5.5, 6.5, 2.8), (10.5, 8, 3), (7, 11.5, 2.8)): blob(im, cx, cy, r, r * .9, tn, lump=.35, seed=int(cx * 5))
    return finish(im, (12, 24, 90))


def build_items(put):
    put('item', 'iron_ingot', ingot((225, 228, 236), (60, 64, 80)))
    put('item', 'gold_ingot', ingot((255, 214, 60), (110, 70, 0)))
    put('item', 'copper_ingot', ingot((232, 120, 82), (100, 40, 20)))
    put('item', 'netherite_ingot', ingot((86, 72, 78), (24, 18, 22)))
    put('item', 'diamond', gem([(8, 1), (14, 6), (8, 14), (2, 6)], (70, 240, 235), (10, 90, 110)))
    put('item', 'emerald', gem([(5, 2), (11, 2), (14, 5), (14, 10), (11, 14), (5, 14), (2, 10), (2, 5)], (40, 220, 100), (6, 80, 40)))
    put('item', 'amethyst_shard', gem([(8, 1), (12, 5), (11, 14), (5, 14), (4, 5)], (170, 110, 240), (60, 30, 110)))
    put('item', 'quartz', gem([(8, 2), (13, 8), (8, 14), (3, 8)], (240, 236, 232), (110, 100, 100)))
    put('item', 'raw_iron', raw((230, 170, 120), (70, 50, 40), 3))
    put('item', 'raw_gold', raw((255, 205, 50), (90, 60, 10), 13))
    put('item', 'raw_copper', raw((235, 120, 70), (90, 40, 20), 23))
    put('item', 'coal', coal((50, 52, 60), (14, 14, 18)))
    put('item', 'charcoal', coal((70, 58, 52), (20, 14, 12)))
    put('item', 'redstone', redstone())
    put('item', 'lapis_lazuli', lapis())
    put('item', 'apple', apple((230, 50, 50)))
    put('item', 'golden_apple', apple((255, 205, 50), shine=True))
    put('item', 'potato', potato((196, 150, 90), (84, 56, 28)))
    put('item', 'baked_potato', potato((200, 130, 60), (84, 44, 16), baked=True))
    put('item', 'carrot', carrot())
    put('item', 'cookie', cookie())
    put('item', 'bread', bread())
    put('item', 'melon_slice', melon())
    put('item', 'beef', meat((220, 80, 90), (250, 190, 190)))
    put('item', 'cooked_beef', meat((150, 80, 50), (110, 56, 36), outline=(50, 22, 14)))
    put('item', 'porkchop', meat((240, 140, 150), (255, 210, 210), bone=True))
    put('item', 'cooked_porkchop', meat((200, 120, 70), (240, 190, 120), bone=True, outline=(70, 36, 20)))
    put('item', 'chicken', drumstick((245, 190, 180), (110, 60, 50)))
    put('item', 'cooked_chicken', drumstick((205, 125, 60), (80, 42, 18)))


def build(root):
    def put(kind, name, im):
        p = os.path.join(root, 'assets', 'minecraft', 'textures', kind, name + '.png')
        os.makedirs(os.path.dirname(p), exist_ok=True)
        im.save(p)
    build_blocks(put)
    build_items(put)
