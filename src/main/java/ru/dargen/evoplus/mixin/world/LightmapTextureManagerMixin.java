package ru.dargen.evoplus.mixin.world;

import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.features.misc.RenderFeature;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {

    @Shadow
    @Final
    private SimpleFramebuffer lightmapFramebuffer;

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/SimpleFramebuffer;endWrite()V", shift = At.Shift.BEFORE))
    private void onUpdate(CallbackInfo info) {
        if (RenderFeature.INSTANCE.getFullBright())
            this.lightmapFramebuffer.clear();
    }

}
