package com.novapx.client.mixin;

import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** F3 y el servidor ven «nova» como cliente en lugar de «fabric». */
@Mixin(ClientBrandRetriever.class)
public abstract class BrandMixin {
    @Inject(method = "getClientModName", at = @At("RETURN"), cancellable = true, remap = false)
    private static void nova$brand(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("nova");
    }
}
