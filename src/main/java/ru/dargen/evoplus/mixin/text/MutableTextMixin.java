package ru.dargen.evoplus.mixin.text;

import net.minecraft.text.MutableText;
import net.minecraft.text.TextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import ru.dargen.evoplus.extension.MutableTextExtension;

@Mixin(MutableText.class)
public class MutableTextMixin implements MutableTextExtension {


    @Mutable
    @Shadow
    @Final
    private TextContent content;

    @Override
    public void setTextContent(TextContent content) {
        this.content = content;
    }

}
