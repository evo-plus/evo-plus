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
import ru.dargen.evoplus.event.EventBus;
import ru.dargen.evoplus.event.resourcepack.ResourcePackProvidersEvent;
import ru.dargen.evoplus.extension.ResourcePackManagerExtension;

import java.util.HashSet;
import java.util.Set;

@Mixin(ResourcePackManager.class)
public abstract class ResourcePackManagerMixin implements ResourcePackManagerExtension {

    @Mutable
    @Shadow
    @Final
    private Set<ResourcePackProvider> providers;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(ResourcePackProvider[] providers, CallbackInfo ci) {
        var event = EventBus.INSTANCE.fire(new ResourcePackProvidersEvent(new HashSet<>(Set.of(providers))));
        this.providers = ImmutableSet.copyOf(event.getProviders());
    }

    @Override
    public Set<ResourcePackProvider> getProviders() {
        return providers;
    }

    @Override
    public void addProvider(ResourcePackProvider provider) {
        var list = new HashSet<>(providers);
        list.add(provider);
        providers = ImmutableSet.copyOf(list);
    }

    @Override
    public void removeProvider(ResourcePackProvider provider) {
        var list = new HashSet<>(providers);
        list.remove(provider);
        providers = ImmutableSet.copyOf(list);
    }

}
