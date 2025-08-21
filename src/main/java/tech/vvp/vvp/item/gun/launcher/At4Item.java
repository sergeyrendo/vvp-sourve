package tech.vvp.vvp.item.gun.launcher;

import tech.vvp.vvp.VVP;
import tech.vvp.vvp.client.renderer.gun.At4ItemRenderer;
import com.atsuishio.superbwarfare.client.tooltip.component.LauncherImageComponent;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
// import com.atsuishio.superbwarfare.init.ModSounds; // Звук перезарядки больше не нужен
import com.atsuishio.superbwarfare.item.gun.GunItem;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player; // Импортируем Player
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class At4Item extends GunItem {

    public At4Item() {
        super(new Item.Properties().rarity(Rarity.RARE).stacksTo(16)); // Можно указать, сколько их стакается
    }

    @Override
    public Supplier<? extends GeoItemRenderer<? extends Item>> getRenderer() {
        return At4ItemRenderer::new;
    }

    // ЛОГИКА АНИМАЦИИ УПРОЩЕНА
    private PlayState idlePredicate(AnimationState<At4Item> event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return PlayState.STOP;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof GunItem)) return PlayState.STOP;

        // Убраны все проверки на isHolstered и перезарядку
        if (event.getData(DataTickets.ITEM_RENDER_PERSPECTIVE) != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.at4.idle"));

        if (ClientEventHandler.isEditing) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.at4.edit"));
        }

        // Логика анимации бега остается
        if (player.isSprinting() && player.onGround() && ClientEventHandler.cantSprint == 0 && ClientEventHandler.drawTime < 0.01) {
            if (ClientEventHandler.tacticalSprint) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.at4.run_fast"));
            } else {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.at4.run"));
            }
        }

        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.at4.idle"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        var idleController = new AnimationController<>(this, "idleController", 4, this::idlePredicate);
        data.add(idleController);
    }

    // УДАЛЕНО: Метод getReloadSound() больше не нужен, так как перезарядки нет.
    // @Override public Set<SoundEvent> getReloadSound() { ... }

    @Override
    public ResourceLocation getGunIcon(ItemStack stack) {
        // Логика иконки остается прежней
        int i = GunData.from(stack).selectedAmmoType.get();
        if (i == 0) {
            return VVP.loc("textures/gun_icon/at4_tbg_icon.png");
        }
        return VVP.loc("textures/gun_icon/at4_standard_icon.png");
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack pStack) {
        return Optional.of(new LauncherImageComponent(pStack));
    }

    @Override
    public boolean shootBullet(
            @Nullable Entity shooter,
            @NotNull ServerLevel level,
            @NotNull Vec3 shootPosition,
            @NotNull Vec3 shootDirection,
            @NotNull GunData data,
            double spread,
            boolean zoom,
            @Nullable UUID uuid
    ) {
        if (!super.shootBullet(shooter, level, shootPosition, shootDirection, data, spread, zoom, uuid)) return false;

        // Частицы после выстрела остаются
        if (shooter != null) {
            ParticleTool.sendParticle(level, ParticleTypes.CLOUD, shooter.getX() + 1.8 * shooter.getLookAngle().x,
                    shooter.getY() + shooter.getBbHeight() - 0.1 + 1.8 * shooter.getLookAngle().y,
                    shooter.getZ() + 1.8 * shooter.getLookAngle().z,
                    30, 0.4, 0.4, 0.4, 0.005, true);
        }

        // УДАЛЕНО: Управление состоянием isEmpty и isHolstered больше не требуется.
        // data.isEmpty.set(true);
        // data.isHolstered.set(true);

        // ГЛАВНОЕ ИЗМЕНЕНИЕ: Потребляем один предмет из стака после выстрела.
        // Эта проверка нужна, чтобы убедиться, что стреляет игрок, а не кто-то еще.
        if (shooter instanceof Player player) {
            // Если игрок не в творческом режиме, уменьшаем количество предметов в руке.
            if (!player.getAbilities().instabuild) {
                ItemStack mainHandItem = player.getMainHandItem();
                if (mainHandItem.is(this)) {
                    mainHandItem.shrink(1); // Уменьшает стак на 1. Если был 1, предмет исчезнет.
                }
            }
        }

        return true;
    }

    // УДАЛЕНО: Метод addReloadTimeBehavior() больше не нужен, так как перезарядки нет.
    // @Override public void addReloadTimeBehavior(...) { ... }

    @Override
    public boolean canEditAttachments(ItemStack stack) {
        return true;
    }
}
