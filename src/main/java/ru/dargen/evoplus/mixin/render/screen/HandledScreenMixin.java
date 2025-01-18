package ru.dargen.evoplus.mixin.render.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.features.misc.RenderFeature;
import ru.dargen.evoplus.render.Colors;
import ru.dargen.evoplus.util.minecraft.ItemsKt;
import ru.dargen.evoplus.util.render.ColorKt;
import ru.dargen.evoplus.util.render.DrawKt;

import java.util.List;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"))
    private void drawItem(MatrixStack matrices, Slot slot, CallbackInfo ci) {
        if (RenderFeature.INSTANCE.getHighlightAvailableItems() && slot.hasStack() && isHighlightedItem(slot.getStack())) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            DrawKt.drawRectangle(matrices, slot.x, slot.y, slot.x + 16f, slot.y + 16f, 101f, ColorKt.alpha(Colors.Green.INSTANCE, 100));
        }
    }

    @Unique
    private static final List<String> HIGHLIGHT_DESCRIPTION = List.of(
            "Нажмите, чтобы получить награду",
            "Нажмите, чтобы забрать награду"
    );

    @Unique
    private static boolean isHighlightedItem(ItemStack stack) {
        var lore = ItemsKt.getLore(stack);
        if (lore.isEmpty()) {
            return false;
        }

        return HIGHLIGHT_DESCRIPTION.contains(lore.get(lore.size() - 1).getString());
    }

}
