package ru.dargen.evoplus.mixin.network;

import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.event.EventBus;
import ru.dargen.evoplus.event.network.ChangeServerEvent;
import ru.dargen.evoplus.event.network.CustomPayloadEvent;
import ru.dargen.evoplus.extension.UnknownCustomPayloadExtension;

@Mixin(ClientCommonNetworkHandler.class)
public abstract class ClientCommonNetworkHandlerMixin {


    @Inject(method = "onCustomPayload(Lnet/minecraft/network/packet/s2c/common/CustomPayloadS2CPacket;)V", at = @At("HEAD"), cancellable = true)
    public void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {

        if (packet.payload() instanceof BrandCustomPayload){
            EventBus.INSTANCE.fire(ChangeServerEvent.INSTANCE);
        }

        if (packet.payload() instanceof UnknownCustomPayloadExtension payload){
            if (!EventBus.INSTANCE.fireResult(new CustomPayloadEvent(packet.payload().getId().id().toString(), payload.payload()))) {
                ci.cancel();
            }
        }
    }

}
