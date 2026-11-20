package com.novapx.client.modules;

import com.novapx.client.Gfx;
import com.novapx.client.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

/** Armadura y objeto en mano con su durabilidad. */
public class ArmorModule extends Module {
    private static final EquipmentSlot[] SLOTS = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND };

    public ArmorModule() { super("armor", "Armadura", "Muestra tu armadura y objeto en mano con su durabilidad.", true); }

    @Override
    public int draw(MinecraftClient c, Gfx ctx, int x, int y) {
        if (c.player == null) return 0;
        int used = 0;
        for (EquipmentSlot slot : SLOTS) {
            ItemStack s = c.player.getEquippedStack(slot);
            if (s.isEmpty()) continue;
            ctx.item(s, x, y + used);
            ctx.overlay(s, x, y + used);
            if (s.isDamageable()) {
                int left = s.getMaxDamage() - s.getDamage();
                ctx.text(String.valueOf(left), x + 20, y + used + 5, 0xFFFFFFFF);
            }
            used += 18;
        }
        return used;
    }
}
