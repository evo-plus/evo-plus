package ru.dargen.evoplus.mixin.entity;

import net.minecraft.component.ComponentMap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.extension.ArmorStandEntityExtension;
import ru.dargen.evoplus.util.minecraft.MinecraftKt;

@Mixin(ArmorStandEntity.class)
public class ArmorStandEntityMixin implements ArmorStandEntityExtension {

    @Unique
    protected boolean selfAccessory;

    @Inject(method = "equipStack", at = @At("TAIL"))
    private void equipStack(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
        if (slot != EquipmentSlot.HEAD) return;

        var tag = stack.getComponents();
        if (tag == null) return;

        var name = MinecraftKt.getPlayerName();
        if (isAccessory(tag, name) || isOldAccessory(tag, name)) {
            selfAccessory = true;
        }

    }

    @Unique
    private static boolean isAccessory(ComponentMap map, String name) {
//        map = map.get("PublicBukkitValues");

//        return map.contains("diamondworld:accessory_owner") && map.getString("diamondworld:accessory_owner").equalsIgnoreCase(name);
        return false;
    }

    @Unique
    private static boolean isOldAccessory(ComponentMap map, String name) {
//        return map.contains("accessory_owner") && map.getString("accessory_owner").equalsIgnoreCase(name);
        return false;
    }

    @Override
    public boolean evo_plus$isSelfAccessory() {
        return selfAccessory;
    }

}
