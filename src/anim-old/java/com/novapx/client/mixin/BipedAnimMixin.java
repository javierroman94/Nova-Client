package com.novapx.client.mixin;

import com.novapx.client.modules.SmoothAnimModule;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Añade movimiento suave al modelo del jugador después de que Minecraft calcula la pose normal. */
@Mixin(BipedEntityModel.class)
public abstract class BipedAnimMixin {
    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void nova$smooth(LivingEntity entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, CallbackInfo ci) {
        if (!SmoothAnimModule.ACTIVE || !(entity instanceof PlayerEntity)) return;
        BipedEntityModel<?> m = (BipedEntityModel<?>) (Object) this;
        float walk = Math.min(1f, limbDistance);
        float idle = 1f - walk;
        float swing = (float) Math.sin(limbAngle * 0.6662f);
        float breath = (float) Math.sin(age * 0.09f);

        // Quieto: los brazos se mecen un poco, como al respirar.
        m.rightArm.roll += 0.04f * breath * idle + 0.02f;
        m.leftArm.roll -= 0.04f * breath * idle + 0.02f;
        m.rightArm.pitch += 0.015f * (float) Math.sin(age * 0.067f) * idle;
        m.leftArm.pitch -= 0.015f * (float) Math.sin(age * 0.067f) * idle;
        m.head.pitch += 0.012f * breath * idle;

        // Andando: el torso gira contra el paso y se inclina hacia delante; los brazos acompañan.
        m.body.yaw += swing * 0.14f * walk;
        m.body.pitch += 0.09f * walk;
        m.rightArm.yaw += swing * 0.10f * walk;
        m.leftArm.yaw += swing * 0.10f * walk;
        m.head.yaw -= swing * 0.05f * walk;
    }
}
