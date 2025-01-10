package ru.dargen.evoplus.mixin.resource;

import com.google.common.collect.ImmutableSet;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.extension.ResourcePackManagerExtension;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.resourcepack.ResourceProvidersInitializeEvent;

import java.util.HashSet;
import java.util.Set;

@Mixin(ResourcePackManager.class)
public abstract class ResourcePackManagerMixin implements ResourcePackManagerExtension {

    @Mutable
    @Shadow @Final private Set<ResourcePackProvider> providers;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(ResourcePackProvider[] providers, CallbackInfo ci) {
        var event = new ResourceProvidersInitializeEvent(new HashSet<>(this.providers));
        EventBus.INSTANCE.fire(event);

        this.providers = ImmutableSet.copyOf(event.getProviders());
    }

    @Override
    public void appendProviders(Set<ResourcePackProvider> providers) {
        this.providers = ImmutableSet.<ResourcePackProvider>builder().addAll(this.providers).addAll(providers).build();
    }

}
