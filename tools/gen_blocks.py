#!/usr/bin/env python3
"""Bloques base con más detalle para el pack Nova (dibujos propios, 16x16, con ruido agrupado y bordes de luz)."""
import os
from PIL import Image


def rng(seed):
    s = [seed & 0x7fffffff]
    def r():
        s[0] = (s[0] * 1103515245 + 12345) & 0x7fffffff
        return (s[0] >> 8) / (0x7fffffff >> 8)
    return r


def noise(tones, seed, clump=.35, weights=None):
    """Ruido por píxel que a veces copia el tono del vecino para formar manchas."""
    r = rng(seed); im = Image.new('RGBA', (16, 16)); grid = [[0] * 16 for _ in range(16)]
    w = weights or [1] * len(tones); tot = sum(w)
    for y in range(16):
        for x in range(16):
            if x and r() < clump:
                k = grid[y][x - 1]
            elif y and r() < clump:
                k = grid[y - 1][x]
            else:
                v = r() * tot; k = 0
                while v > w[k]:
                    v -= w[k]; k += 1
            grid[y][x] = k
            im.putpixel((x, y), tuple(tones[k]) + (255,))
    return im


def shade(c, f):
    return tuple(max(0, min(255, int(v * f))) for v in c)


def stone(base, seed):
    tones = [shade(base, .82), shade(base, .92), base, shade(base, 1.1), shade(base, 1.2)]
    im = noise(tones, seed, .4, [2, 3, 4, 2, 1])
    r = rng(seed + 9)
    for _ in range(3):                                   # grietas finas
        x, y = int(r() * 14) + 1, int(r() * 14) + 1
        for _ in range(4):
            im.putpixel((x % 16, y % 16), shade(base, .6) + (255,))
            x += 1 if r() < .6 else 0; y += 1 if r() < .6 else 0
    return im


def cobble(base, seed):
    r = rng(seed); pts = [(r() * 16, r() * 16) for _ in range(7)]
    im = Image.new('RGBA', (16, 16)); own = [[0] * 16 for _ in range(16)]
    for y in range(16):
        for x in range(16):
            best = []
            for i, (px_, py_) in enumerate(pts):
                dx = min(abs(x - px_), 16 - abs(x - px_)); dy = min(abs(y - py_), 16 - abs(y - py_))
                best.append((dx * dx + dy * dy, i))
            best.sort(); own[y][x] = (best[0][1], best[1][0] - best[0][0])
    for y in range(16):
        for x in range(16):
            i, gap = own[y][x]
            tone = shade(base, .86 + (i * 37 % 20) / 100)
            if gap < 7:
                tone = shade(base, .48)
            elif gap < 14:
                tone = shade(tone, .85)
            elif own[(y - 1) % 16][x][0] != i or own[y][(x - 1) % 16][0] != i:
                tone = shade(tone, 1.18)
            im.putpixel((x, y), tone + (255,))
    return im


def dirt(seed):
    base = (121, 85, 58)
    im = noise([shade(base, .8), shade(base, .92), base, shade(base, 1.12), (150, 112, 80)], seed, .38, [2, 3, 4, 2, 1])
    r = rng(seed + 3)
    for _ in range(4):
        x, y = int(r() * 15), int(r() * 15)
        im.putpixel((x, y), (92, 92, 96, 255)); im.putpixel((x + 1, y), (126, 126, 130, 255))
    return im


def grass_top(seed):
    base = (150, 150, 150)
    im = noise([shade(base, .8), shade(base, .92), base, shade(base, 1.1), shade(base, 1.2)], seed, .45, [2, 3, 4, 2, 1])
    r = rng(seed + 1)
    for _ in range(9):                                   # briznas más oscuras
        x, y = int(r() * 16), int(r() * 14)
        im.putpixel((x, y), shade(base, .62) + (255,)); im.putpixel((x, y + 1), shade(base, .72) + (255,))
    return im


def grass_overlay(seed):
    r = rng(seed); im = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    for x in range(16):
        depth = 3 + int(r() * 3)
        for y in range(depth):
            tone = 150 if y < depth - 1 else 118
            im.putpixel((x, y), (tone, tone, tone, 255))
    return im


def planks(seed):
    base = (176, 140, 82)
    im = noise([shade(base, .9), base, shade(base, 1.08)], seed, .6, [3, 4, 2])
    for gy in (3, 7, 11, 15):                              # juntas entre tablas
        for x in range(16):
            im.putpixel((x, gy), shade(base, .55) + (255,))
            if gy > 0:
                im.putpixel((x, gy - 1), shade(base, 1.14) + (255,)) if x % 5 else None
    for gy, gx in ((1, 3), (5, 11), (9, 6), (13, 13)):     # clavos
        im.putpixel((gx, gy), shade(base, .65) + (255,))
    return im


def log_side(seed):
    base = (104, 82, 50)
    im = noise([shade(base, .7), shade(base, .85), base, shade(base, 1.15)], seed, .15, [2, 3, 3, 1])
    for x in range(16):
        for y in range(16):
            if x % 4 == 0:
                im.putpixel((x, y), shade(im.getpixel((x, y))[:3], .78) + (255,))
    return im


def log_top():
    base = (178, 144, 88); im = Image.new('RGBA', (16, 16))
    for y in range(16):
        for x in range(16):
            d = max(abs(x - 7.5), abs(y - 7.5))
            ring = int(d) % 2
            c = shade(base, 1.1 if ring else .92)
            if d > 6.5: c = (104, 82, 50)
            im.putpixel((x, y), c + (255,))
    return im


def sand(seed):
    base = (222, 207, 160)
    return noise([shade(base, .88), shade(base, .95), base, shade(base, 1.05), (240, 228, 188)], seed, .3, [2, 3, 4, 3, 1])


def leaves(seed):
    base = (140, 140, 140); r = rng(seed)
    im = noise([shade(base, .6), shade(base, .8), base, shade(base, 1.15)], seed, .4, [3, 4, 3, 2])
    for _ in range(16):
        im.putpixel((int(r() * 16), int(r() * 16)), (0, 0, 0, 0))
    return im


def deepslate(seed):
    base = (58, 58, 64); im = noise([shade(base, .8), base, shade(base, 1.2), shade(base, 1.4)], seed, .1, [3, 4, 2, 1])
    for x in range(16):
        for y in range(16):
            if (x + (y // 4)) % 5 == 0:
                im.putpixel((x, y), shade(im.getpixel((x, y))[:3], .78) + (255,))
    return im


def netherrack(seed):
    base = (112, 46, 48); im = noise([shade(base, .7), shade(base, .88), base, shade(base, 1.2), (160, 70, 70)], seed, .4, [2, 3, 4, 2, 1])
    return im


def build(root):
    d = os.path.join(root, 'assets', 'minecraft', 'textures', 'block'); os.makedirs(d, exist_ok=True)
    out = {'stone': stone((110, 112, 118), 11), 'cobblestone': cobble((118, 120, 126), 5), 'dirt': dirt(21),
           'grass_block_top': grass_top(31), 'grass_block_side': dirt(22), 'grass_block_side_overlay': grass_overlay(41),
           'oak_planks': planks(51), 'oak_log': log_side(61), 'oak_log_top': log_top(), 'sand': sand(71),
           'oak_leaves': leaves(81), 'deepslate': deepslate(91), 'netherrack': netherrack(101)}
    for k, v in out.items():
        v.save(os.path.join(d, k + '.png'))
