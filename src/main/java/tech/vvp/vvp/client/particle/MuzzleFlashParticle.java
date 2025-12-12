package tech.vvp.vvp.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MuzzleFlashParticle extends TextureSheetParticle {

    private final SpriteSet spriteSet;

    protected MuzzleFlashParticle(ClientLevel level, double x, double y, double z, 
                                   double xSpeed, double ySpeed, double zSpeed,
                                   SpriteSet spriteSet, float scale, int lifetime) {
        super(level, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0.35f, 0.35f);
        this.quadSize = scale * 4.1f; // Размер эффекта выстрела
        this.lifetime = 1; // Моментальная вспышка - 1 тик
        this.gravity = 0.0f;
        this.hasPhysics = false;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        
        // Используем единственную текстуру
        this.pickSprite(spriteSet);
        
        // Белый цвет с полной яркостью - огонь
        this.rCol = 1.0f;
        this.gCol = 1.0f;
        this.bCol = 1.0f;
        this.alpha = 1.0f;
        
        // Случайный поворот для разнообразия
        this.roll = (float)(Math.random() * Math.PI * 2);
        this.oRoll = this.roll;
    }

    @Override
    public int getLightColor(float partialTick) {
        // Максимальная яркость как в SBW
        return 15728880;
    }

    @Override
    public void tick() {
        super.tick();
        // Мгновенно исчезает - это огонь, не эффект монтажный
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<MuzzleFlashParticleOption> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(MuzzleFlashParticleOption type, ClientLevel level, 
                                       double x, double y, double z, 
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new MuzzleFlashParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, 
                                          spriteSet, type.getScale(), type.getLifetime());
        }
    }
}
