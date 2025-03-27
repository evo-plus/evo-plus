package ru.dargen.evoplus.mixin.world;

import lombok.val;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.event.EventBus;
import ru.dargen.evoplus.event.world.ChunkUnloadEvent;

@Mixin(ClientChunkManager.class)
public class ClientChunkManagerMixin {

    @Shadow @Final
    ClientWorld world;

    @Inject(at = @At(value = "HEAD"), method = "unload")
    private void unloadChunk(ChunkPos pos, CallbackInfo ci) {
        val chunk = world.getChunk(pos.x, pos.z);
        EventBus.INSTANCE.fire(new ChunkUnloadEvent(chunk));
    }
}
