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
import ru.dargen.evoplus.resource.builtin.EvoPlusPackProvider;

import java.util.Set;

@Mixin(ResourcePackManager.class)
public abstract class ResourcePackManagerMixin {

    @Mutable
    @Shadow
    @Final
    private Set<ResourcePackProvider> providers;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(ResourcePackProvider[] providers, CallbackInfo ci) {
        this.providers = ImmutableSet.<ResourcePackProvider>builder()
                .addAll(this.providers)
                .add(new EvoPlusPackProvider())
                .build();
    }

}
