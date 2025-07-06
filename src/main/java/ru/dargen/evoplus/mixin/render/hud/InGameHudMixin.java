package ru.dargen.evoplus.mixin.render.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.event.EventBus;
import ru.dargen.evoplus.event.render.OverlayRenderEvent;
import ru.dargen.evoplus.features.chat.TextFeature;
import ru.dargen.evoplus.features.misc.RenderFeature;
import ru.dargen.evoplus.util.mixin.HeartType;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    private Random random;

    @Shadow
    protected abstract int getHeartCount(LivingEntity entity);

    @Inject(method = "render", at = @At("TAIL"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LayeredDrawer;render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V")), cancellable = true)
    private void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        context.getMatrices().push();
        EventBus.INSTANCE.fire(new OverlayRenderEvent(context, tickCounter.getTickDelta(true)));
        context.getMatrices().pop();
    }

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I"))
    private int renderStatusBars_getHeartCount(InGameHud instance, LivingEntity entity) {
        return RenderFeature.INSTANCE.getNoExcessHud() ? -1 : getHeartCount(entity);
    }

    @Inject(method = "renderAirBubbles", at = @At("HEAD"), cancellable = true)
    private void renderStatusBars_getAir(DrawContext context, PlayerEntity player, int heartCount, int top, int left, CallbackInfo ci) {
        if (RenderFeature.INSTANCE.getNoExcessHud()) ci.cancel();
    }

    //tmp mb bc idk how to edit locals
    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
    private void renderHealthBar(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
        ci.cancel();

        RenderFeature.HealthRenderMode mode = RenderFeature.INSTANCE.getHealthRender();
        if (!mode.isDefaultHearts()) return;


        int lineWidth = mode == RenderFeature.HealthRenderMode.LONG ? 23 : 10;
        int xOffset = mode == RenderFeature.HealthRenderMode.LONG ? -2 : 0;
//        int yOffset = RenderFeature.INSTANCE.getNoExpHud() ? 6 : 0;

        HeartType heartType = HeartType.fromPlayerState(player);
        boolean bl = player.getWorld().getLevelProperties().isHardcore();
        int i = MathHelper.ceil((double) maxHealth / 2.0);
        int j = MathHelper.ceil((double) absorption / 2.0);
        int k = i * 2;
        for(int l = i + j - 1; l >= 0; --l) {
            int m = l / lineWidth;
            int n = l % lineWidth;
            int o = x + n * 8 + xOffset;
//            int p = y - m * lines + yOffset;
            int p = y - m * lines;
            if (lastHealth + absorption <= 4) {
                p += this.random.nextInt(2);
            }

            if (l < i && l == regeneratingHeartIndex) {
                p -= 2;
            }

            this.drawHeart(context, HeartType.CONTAINER, o, p, bl, blinking, false);
            int q = l * 2;
            boolean bl2 = l >= i;
            if (bl2) {
                int r = q - k;
                if (r < absorption) {
                    boolean bl3 = r + 1 == absorption;
                    this.drawHeart(context, heartType == HeartType.WITHERED ? heartType : HeartType.ABSORBING, o, p, bl, false, bl3);
                }
            }

            if (blinking && q < health) {
                boolean bl4 = q + 1 == health;
                this.drawHeart(context, heartType, o, p, bl, true, bl4);
            }

            if (q < lastHealth) {
                boolean bl4 = q + 1 == lastHealth;
                this.drawHeart(context, heartType, o, p, bl, false, bl4);
            }
        }
    }

    private void drawHeart(DrawContext context, HeartType type, int x, int y, boolean hardcore, boolean blinking, boolean half) {
        context.drawGuiTexture(RenderLayer::getGuiTextured, type.getTexture(hardcore, half, blinking), x, y, 9, 9);
    }

    @Inject(method = "clear", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;clear(Z)V"), cancellable = true)
    private void onClear(CallbackInfo info) {
        if (TextFeature.INSTANCE.getKeepHistory()) info.cancel();
    }

}