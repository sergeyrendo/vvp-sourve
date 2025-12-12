package tech.vvp.vvp.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

public class MuzzleFlashParticleOption implements ParticleOptions {

    public static final Codec<MuzzleFlashParticleOption> CODEC = RecordCodecBuilder.create(builder ->
        builder.group(
            Codec.FLOAT.fieldOf("scale").forGetter(option -> option.scale),
            Codec.INT.fieldOf("lifetime").forGetter(option -> option.lifetime)
        ).apply(builder, MuzzleFlashParticleOption::new));

    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<MuzzleFlashParticleOption> DESERIALIZER = 
        new ParticleOptions.Deserializer<>() {
            @Override
            public MuzzleFlashParticleOption fromCommand(ParticleType<MuzzleFlashParticleOption> type, 
                                                         com.mojang.brigadier.StringReader reader) {
                return new MuzzleFlashParticleOption(0.3f, 4);
            }

            @Override
            public MuzzleFlashParticleOption fromNetwork(ParticleType<MuzzleFlashParticleOption> type, 
                                                         FriendlyByteBuf buffer) {
                return new MuzzleFlashParticleOption(buffer.readFloat(), buffer.readInt());
            }
        };

    private final float scale;
    private final int lifetime;

    public MuzzleFlashParticleOption(float scale, int lifetime) {
        this.scale = scale;
        this.lifetime = lifetime;
    }

    public float getScale() {
        return scale;
    }

    public int getLifetime() {
        return lifetime;
    }

    @Override
    public ParticleType<?> getType() {
        return tech.vvp.vvp.init.ModParticles.MUZZLE_FLASH.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(scale);
        buffer.writeInt(lifetime);
    }

    @Override
    public String writeToString() {
        return String.format("%s %.2f %d", getType(), scale, lifetime);
    }
}
