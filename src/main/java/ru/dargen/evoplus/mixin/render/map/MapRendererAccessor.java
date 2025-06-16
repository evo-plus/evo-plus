package ru.dargen.evoplus.mixin.render.map;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.render.MapRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MapRenderer.class)
public interface MapRendererAccessor {

//    @Accessor("texturesManager")
//    Int2ObjectMap<MapTextureAccessor> getTextures();

}
