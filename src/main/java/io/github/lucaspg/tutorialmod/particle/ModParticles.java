package io.github.lucaspg.tutorialmod.particle;

import io.github.lucaspg.tutorialmod.TutorialMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, TutorialMod.MOD_ID);

    public static final Supplier<SimpleParticleType> BISMUTH_PARTICLES =
            PARTICLES_TYPES.register("bismuth_particles", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLES_TYPES.register(eventBus);
    }
}
