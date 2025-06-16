package ru.dargen.evoplus.mixin.render.map;

import net.minecraft.client.render.MapRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MapRenderer.class)
public interface MapRendererAccessor {

//    @Accessor("texturesManager")
//    Int2ObjectMap<MapTextureAccessor> getTextures();

}
