package tech.vvp.vvp.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class MuzzleSmokeParticle extends TextureSheetParticle {

    private final SpriteSet spriteSet;

    protected MuzzleSmokeParticle(ClientLevel level, double x, double y, double z, 
                                   double xSpeed, double ySpeed, double zSpeed,
                                   SpriteSet spriteSet, float scale, int baseLifetime) {
        super(level, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0.3f, 0.3f);
        this.quadSize = scale * 3.5f; // Средний размер для красоты
        this.lifetime = this.random.nextInt(20) + baseLifetime; // Случайное время жизни
        this.gravity = -0.005f; // Лёгкий подъём вверх
        this.hasPhysics = false; // Без физики - просто разлетается
        
        // Уменьшенная скорость чтобы не улетал далеко
        this.xd = xSpeed * 0.15;
        this.yd = ySpeed * 0.15;
        this.zd = zSpeed * 0.15;
        
        this.setSpriteFromAge(spriteSet);
        
        // Светло-серый прозрачный цвет
        this.rCol = 0.9f;
        this.gCol = 0.9f;
        this.bCol = 0.9f;
        this.alpha = 0.3f; // Прозрачный красивый дым
        
        // Случайный поворот для красоты
        this.roll = (float)(Math.random() * Math.PI * 2);
        this.oRoll = this.roll;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.removed) {
            // Анимация через спрайты (8 кадров) - медленнее
            this.setSprite(this.spriteSet.get(Math.min((this.age / 4) + 1, 8), 8));
        }
        
        // Увеличение размера - дым расширяется
        this.quadSize += 0.03f;
        
        // Постепенное затухание
        this.alpha *= 0.96f;
        
        // Замедление - дым теряет скорость
        this.xd *= 0.92f;
        this.yd *= 0.92f;
        this.zd *= 0.92f;
        
        // Лёгкое вращение для красоты
        this.oRoll = this.roll;
        this.roll += 0.02f;
        
        // Удаляем когда стал совсем прозрачным или время вышло
        if (this.alpha < 0.01f || this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<MuzzleSmokeParticleOption> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(MuzzleSmokeParticleOption type, ClientLevel level, 
                                       double x, double y, double z, 
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new MuzzleSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, 
                                          spriteSet, type.getScale(), type.getLifetime());
        }
    }
}
