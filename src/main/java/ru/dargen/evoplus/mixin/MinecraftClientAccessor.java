package ru.dargen.evoplus.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Queue;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {

    @Accessor("attackCooldown")
    int getAttackCooldown();

    @Accessor("attackCooldown")
    void setAttackCooldown(int attackCooldown);

    @Invoker("doAttack")
    boolean leftClick();

    @Accessor("renderTaskQueue")
    Queue<Runnable> getRenderTaskQueue();

    @Invoker("openChatScreen")
    void openChat(String text);

    @Accessor("renderTickCounter")
    RenderTickCounter.Dynamic getTimer();

}
