package ru.dargen.evoplus.mixin.render.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
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

import java.util.List;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void drawItem(DrawContext context, Slot slot, CallbackInfo ci) {
        if (RenderFeature.INSTANCE.getHighlightAvailableItems() && slot.hasStack() && isHighlightedItem(slot.getStack())) {
            int color = ColorKt.alpha(Colors.Green.INSTANCE, 150).getRGB();
            context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 101, color);
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

        if (lore.isEmpty())
            return false;

        return HIGHLIGHT_DESCRIPTION.contains(lore.getLast().getString());
    }

}
