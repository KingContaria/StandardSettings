package com.kingcontaria.standardsettings.mixins;

import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BakedModelManager.class)
public interface BakedModelManagerAccessor {
    @Invoker("prepare")
    ModelLoader standardSettings_prepare(ResourceManager resourceManager, Profiler profiler);

    @Invoker("apply")
    void standardSettings_apply(ModelLoader modelLoader, ResourceManager resourceManager, Profiler profiler);
}