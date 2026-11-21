package com.novapx.client.modules;

import com.novapx.client.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/** Armadura y objeto en mano con su durabilidad. */
public class ArmorModule extends Module {
    private static final EquipmentSlot[] SLOTS = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND };
    public ArmorModule() { super("armor", "Armadura", "Muestra tu armadura y objeto en mano con su durabilidad.", true); }

    @Override
    public int draw(Minecraft c, GuiGraphicsExtractor g, int x, int y) {
        if (c.player == null) return 0;
        int used = 0;
        for (EquipmentSlot slot : SLOTS) {
            ItemStack s = c.player.getItemBySlot(slot);
            if (s.isEmpty()) continue;
            g.item(s, x, y + used);
            g.itemDecorations(c.font, s, x, y + used);
            if (s.isDamageableItem()) g.text(c.font, String.valueOf(s.getMaxDamage() - s.getDamageValue()), x + 20, y + used + 5, 0xFFFFFFFF);
            used += 18;
        }
        return used;
    }
}
