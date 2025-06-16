package ru.dargen.evoplus.mixin.render;

import lombok.val;
import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ru.dargen.evoplus.event.EventBus;
import ru.dargen.evoplus.event.render.StringRenderEvent;

@Mixin(TextVisitFactory.class)
public abstract class TextVisitFactoryMixin {

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z", ordinal = 0), method = {"visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"}, index = 0)
    private static String adjustText(String text) {
        val newText = EventBus.INSTANCE.fire(new StringRenderEvent(text)).getText();

        if (newText != null) return newText;

        else return text;
    }
}
