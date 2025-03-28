package ru.dargen.evoplus.mixin.network.payload;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.UnknownCustomPayload;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.extension.UnknownCustomPayloadExtension;

@Mixin(UnknownCustomPayload.class)
public class UnknownCustomPayloadMixin implements UnknownCustomPayloadExtension {

    @Unique
    protected ByteBuf payload;

    @Override
    public void setPayload(ByteBuf buf) {
        this.payload = buf;
    }

    @Override
    public ByteBuf payload() {
        return payload;
    }

    @Inject(method = "createCodec", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketByteBuf> void createCodec(Identifier id, int maxBytes, CallbackInfoReturnable<PacketCodec<T, UnknownCustomPayload>> cir) {
        cir.setReturnValue(CustomPayload.codecOf((value, buf) -> {
        }, (buf) -> {
            var payload = new UnknownCustomPayload(id);
            try {
                var buffer = PooledByteBufAllocator.DEFAULT.buffer();
                buffer.writeBytes(buf, buf.readableBytes());
                ((UnknownCustomPayloadExtension) (Object) payload).setPayload(buffer);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return payload;
        }));
    }

}
