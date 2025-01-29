package ru.dargen.evoplus.mixin.render.map;

import net.minecraft.client.texture.NativeImageBackedTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.render.MapRenderer$MapTexture")
public interface MapTextureAccessor {

    @Accessor("texture")
    NativeImageBackedTexture getTexture();

}
