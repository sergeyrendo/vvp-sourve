package tech.vvp.vvp.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tech.vvp.vvp.client.particle.MuzzleFlashParticleOption;
import tech.vvp.vvp.client.particle.MuzzleSmokeParticleOption;

@SuppressWarnings("deprecation")
public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTRY = 
        DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "vvp");

    public static final RegistryObject<ParticleType<MuzzleFlashParticleOption>> MUZZLE_FLASH = 
        REGISTRY.register("muzzle_flash", () -> new ParticleType<MuzzleFlashParticleOption>(false, MuzzleFlashParticleOption.DESERIALIZER) {
            @Override
            public com.mojang.serialization.Codec<MuzzleFlashParticleOption> codec() {
                return MuzzleFlashParticleOption.CODEC;
            }
        });

    public static final RegistryObject<ParticleType<MuzzleSmokeParticleOption>> MUZZLE_SMOKE = 
        REGISTRY.register("muzzle_smoke", () -> new ParticleType<MuzzleSmokeParticleOption>(false, MuzzleSmokeParticleOption.DESERIALIZER) {
            @Override
            public com.mojang.serialization.Codec<MuzzleSmokeParticleOption> codec() {
                return MuzzleSmokeParticleOption.CODEC;
            }
        });
}
