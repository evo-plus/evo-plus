package ru.dargen.evoplus.mixin.render.hud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
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
import ru.dargen.evoplus.features.text.TextFeature;
import ru.dargen.evoplus.features.misc.RenderFeature;
import ru.dargen.evoplus.util.mixin.HeartType;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    private Random random;

    @Shadow protected abstract int getHeartCount(LivingEntity entity);

    @Inject(method = "render", at = @At("TAIL"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;render(Lnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V")), cancellable = true)
    private void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        matrices.push();
        EventBus.INSTANCE.fire(new OverlayRenderEvent(matrices, tickDelta));
        matrices.pop();
    }

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I"))
    private int renderStatusBars_getHeartCount(InGameHud instance, LivingEntity entity) {
        return RenderFeature.INSTANCE.getNoExcessHud() ? -1 : getHeartCount(entity);
    }

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getArmor()I"))
    private int renderStatusBars_getArmor(PlayerEntity instance) {
        return RenderFeature.INSTANCE.getNoExcessHud() ? 0 : instance.getArmor();
    }

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAir()I"))
    private int renderStatusBars_getAir(PlayerEntity instance) {
        return RenderFeature.INSTANCE.getNoExcessHud() ? 0 : instance.getAir();
    }

    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    private int renderScoreboardSidebar(TextRenderer instance, MatrixStack matrices, String text, float x, float y, int color) {
        return RenderFeature.INSTANCE.getNoScoreboardNumbers() ? 0 : instance.draw(matrices, text, x, y, color);
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void renderExperienceBar(MatrixStack matrices, int x, CallbackInfo ci) {
        if (RenderFeature.INSTANCE.getNoExpHud()) {
            ci.cancel();
        }
    }

    //tmp mb bc idk how to edit locals
    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
    private void renderHealthBar(MatrixStack matrices, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
        ci.cancel();

        RenderFeature.HealthRenderMode mode = RenderFeature.INSTANCE.getHealthRender();
        if (!mode.isDefaultHearts()) return;

        int lineWidth = mode == RenderFeature.HealthRenderMode.LONG ? 23 : 10;
        int xOffset = mode == RenderFeature.HealthRenderMode.LONG ? -2 : 0;
        int yOffset = RenderFeature.INSTANCE.getNoExpHud() ? 6 : 0;

        HeartType heartType = HeartType.fromPlayerState(player);
        int i = 9 * (player.world.getLevelProperties().isHardcore() ? 5 : 0);
        int j = MathHelper.ceil((double) maxHealth / 2.0);
        int k = MathHelper.ceil((double) absorption / 2.0);
        int l = j * 2;
        for (int m = j + k - 1; m >= 0; --m) {
            boolean bl3;
            int s;
            boolean bl;
            int n = m / lineWidth;
            int o = m % lineWidth;
            int p = x + o * 8 + xOffset;
            int q = y - n * lines + yOffset;
            if (lastHealth + absorption <= 4) {
                q += random.nextInt(2);
            }
            if (m < j && m == regeneratingHeartIndex) {
                q -= 2;
            }
            this.drawHeart(matrices, HeartType.CONTAINER, p, q, i, blinking, false);
            int r = m * 2;
            boolean bl2 = bl = m >= j;
            if (bl && (s = r - l) < absorption) {
                boolean bl22 = s + 1 == absorption;
                this.drawHeart(matrices, heartType == HeartType.WITHERED ? heartType : HeartType.ABSORBING, p, q, i, false, bl22);
            }
            if (blinking && r < health) {
                bl3 = r + 1 == health;
                this.drawHeart(matrices, heartType, p, q, i, true, bl3);
            }
            if (r >= lastHealth) continue;
            bl3 = r + 1 == lastHealth;
            this.drawHeart(matrices, heartType, p, q, i, false, bl3);
        }
    }

    private void drawHeart(MatrixStack matrices, HeartType type, int x, int y, int v, boolean blinking, boolean halfHeart) {
        InGameHud.drawTexture(matrices, x, y, type.getU(halfHeart, blinking), v, 9, 9);
    }

    @Inject(method = "clear", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;clear(Z)V"), cancellable = true)
    private void onClear(CallbackInfo info) {
        if (TextFeature.INSTANCE.getKeepHistory()) info.cancel();
    }

}