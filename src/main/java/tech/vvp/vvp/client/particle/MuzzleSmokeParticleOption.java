package tech.vvp.vvp.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

public class MuzzleSmokeParticleOption implements ParticleOptions {

    public static final Codec<MuzzleSmokeParticleOption> CODEC = RecordCodecBuilder.create(builder ->
        builder.group(
            Codec.FLOAT.fieldOf("scale").forGetter(option -> option.scale),
            Codec.INT.fieldOf("lifetime").forGetter(option -> option.lifetime)
        ).apply(builder, MuzzleSmokeParticleOption::new));

    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<MuzzleSmokeParticleOption> DESERIALIZER = 
        new ParticleOptions.Deserializer<>() {
            @Override
            public MuzzleSmokeParticleOption fromCommand(ParticleType<MuzzleSmokeParticleOption> type, 
                                                         com.mojang.brigadier.StringReader reader) {
                return new MuzzleSmokeParticleOption(0.5f, 20);
            }

            @Override
            public MuzzleSmokeParticleOption fromNetwork(ParticleType<MuzzleSmokeParticleOption> type, 
                                                         FriendlyByteBuf buffer) {
                return new MuzzleSmokeParticleOption(buffer.readFloat(), buffer.readInt());
            }
        };

    private final float scale;
    private final int lifetime;

    public MuzzleSmokeParticleOption(float scale, int lifetime) {
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
        return tech.vvp.vvp.init.ModParticles.MUZZLE_SMOKE.get();
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
