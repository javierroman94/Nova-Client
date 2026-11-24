package com.novapx.client.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** La ventana se llama «Nova PX · Minecraft 1.21.1 …». */
@Mixin(Minecraft.class)
public abstract class TitleMixin {
    @Inject(method = "createTitle", at = @At("RETURN"), cancellable = true)
    private void nova$title(CallbackInfoReturnable<String> cir) {
        String t = cir.getReturnValue();
        if (t != null && !t.startsWith("Nova PX")) cir.setReturnValue("Nova PX · " + t);
    }
}
