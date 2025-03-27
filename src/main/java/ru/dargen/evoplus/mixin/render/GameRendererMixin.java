package ru.dargen.evoplus.mixin.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.event.EventBus;
import ru.dargen.evoplus.event.render.ScreenRenderEvent;
import ru.dargen.evoplus.features.misc.RenderFeature;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onTiltViewWhenHurt(MatrixStack matrixStack, float f, CallbackInfo ci) {
        if (RenderFeature.INSTANCE.getNoDamageShake()) ci.cancel();
    }

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderWithTooltip(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
    private void render(Screen instance, DrawContext context, int mouseX, int mouseY, float delta) {
        if (EventBus.INSTANCE.fireResult(new ScreenRenderEvent.Pre(instance, context.getMatrices(), delta))) {
            instance.renderWithTooltip(context, mouseX, mouseY, delta);
            EventBus.INSTANCE.fire(new ScreenRenderEvent.Post(instance, context.getMatrices(), delta));
        }
    }

}
