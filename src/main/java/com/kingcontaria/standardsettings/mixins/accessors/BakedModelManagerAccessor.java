package com.kingcontaria.standardsettings.mixins.accessors;

import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BakedModelManager.class)

public interface BakedModelManagerAccessor {
    @Invoker
    ModelLoader callPrepare(ResourceManager resourceManager, Profiler profiler);

    @Invoker
    void callApply(ModelLoader modelLoader, ResourceManager resourceManager, Profiler profiler);
}
