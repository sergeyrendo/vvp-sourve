package tech.vvp.vvp.init;

import com.atsuishio.superbwarfare.entity.projectile.*;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.Fab250Entity;
import tech.vvp.vvp.entity.projectile.Fab500Entity;
import tech.vvp.vvp.entity.vehicle.*;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, VVP.MOD_ID);

    public static final RegistryObject<EntityType<SmallCannonShellEntity>> SMALL_CANNON_SHELL = ENTITY_TYPES.register("small_cannon_shell",
                    () -> EntityType.Builder.<SmallCannonShellEntity>of(SmallCannonShellEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .build("small_cannon_shell"));
        
    public static final RegistryObject<EntityType<CannonShellEntity>> CANNON_SHELL = ENTITY_TYPES.register("cannon_shell",
                    () -> EntityType.Builder.<CannonShellEntity>of(CannonShellEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .build("cannon_shell"));
        
    public static final RegistryObject<EntityType<ProjectileEntity>> PROJECTILE = ENTITY_TYPES.register("projectile",
                    () -> EntityType.Builder.<ProjectileEntity>of(ProjectileEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .build("projectile"));
        
    public static final RegistryObject<EntityType<SmallRocketEntity>> HELI_ROCKET = ENTITY_TYPES.register("heli_rocket",
                    () -> EntityType.Builder.<SmallRocketEntity>of(SmallRocketEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .build("heli_rocket"));
        
    public static final RegistryObject<EntityType<Mk82Entity>> MK82 = ENTITY_TYPES.register("mk82",
                    () -> EntityType.Builder.<Mk82Entity>of(Mk82Entity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .build("mk82"));
        
    public static final RegistryObject<EntityType<Agm65Entity>> AGM65 = ENTITY_TYPES.register("agm65",
                    () -> EntityType.Builder.<Agm65Entity>of(Agm65Entity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .build("agm65"));
        
    public static final RegistryObject<EntityType<SwarmDroneEntity>> SWARM_DRONE = ENTITY_TYPES.register("swarm_drone",
                    () -> EntityType.Builder.<SwarmDroneEntity>of(SwarmDroneEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .build("swarm_drone"));

    public static final RegistryObject<EntityType<WgMissileEntity>> WGL_MISSILE = ENTITY_TYPES.register("wg_missile",
                            () -> EntityType.Builder.<WgMissileEntity>of(WgMissileEntity::new, MobCategory.MISC)
                                    .sized(0.5f, 0.5f)
                                    .build("wg_missile"));

    public static final RegistryObject<EntityType<Btr4Entity>> BTR_4 = ENTITY_TYPES.register("btr_4",
                                () -> EntityType.Builder.<Btr4Entity>of(Btr4Entity::new, MobCategory.MISC)
                                    .setTrackingRange(64)
                                    .setUpdateInterval(1)
                                    .setCustomClientFactory(Btr4Entity::clientSpawn)
                                    .fireImmune()
                                    .sized(3.9f, 3.5f)
                                    .build("stryker_haki"));

    public static final RegistryObject<EntityType<BradleyUkrEntity>> BRADLEY_UKR = ENTITY_TYPES.register("bradley_ukr",
            () -> EntityType.Builder.<BradleyUkrEntity>of(BradleyUkrEntity::new, MobCategory.MISC)
                                    .setTrackingRange(64)
                                    .setUpdateInterval(1)
                                    .setCustomClientFactory(BradleyUkrEntity::clientSpawn)
                                    .fireImmune()
                                    .sized(3.9f, 3.5f)
                                    .build("bradley_ukr"));

    public static final RegistryObject<EntityType<BradleyEntity>> BRADLEY = ENTITY_TYPES.register("bradley",
            () -> EntityType.Builder.<BradleyEntity>of(BradleyEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(BradleyEntity::clientSpawn)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("bradley"));

    public static final RegistryObject<EntityType<StrykerEntity>> STRYKER = ENTITY_TYPES.register("stryker",
            () -> EntityType.Builder.<StrykerEntity>of(StrykerEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(StrykerEntity::clientSpawn)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("stryker"));

    public static final RegistryObject<EntityType<Stryker_M1296Entity>> STRYKER_M1296 = ENTITY_TYPES.register("stryker_m1296",
            () -> EntityType.Builder.<Stryker_M1296Entity>of(Stryker_M1296Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Stryker_M1296Entity::clientSpawn)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("stryker_m1296"));

    public static final RegistryObject<EntityType<TerminatorEntity>> TERMINATOR = ENTITY_TYPES.register("terminator",
            () -> EntityType.Builder.<TerminatorEntity>of(TerminatorEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(TerminatorEntity::clientSpawn)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("terminator"));


    public static final RegistryObject<EntityType<M224Entity>> M224 = ENTITY_TYPES.register("m224",
            () -> EntityType.Builder.<M224Entity>of(M224Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(M224Entity::clientSpawn)
                    .fireImmune()
                    .sized(0.5f, 0.5f)
                    .build("m224"));

    public static final RegistryObject<EntityType<KornetEntity>> KORNET = ENTITY_TYPES.register("kornet",
            () -> EntityType.Builder.<KornetEntity>of(KornetEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(KornetEntity::clientSpawn)
                    .fireImmune()
                    .sized(1f, 3f)
                    .build("kornet"));

    public static final RegistryObject<EntityType<tech.vvp.vvp.entity.projectile.CannonAtgmShellEntity>> CANNON_ATGM_SHELL = register("cannon_atgm_shell",
            EntityType.Builder.<tech.vvp.vvp.entity.projectile.CannonAtgmShellEntity>of(tech.vvp.vvp.entity.projectile.CannonAtgmShellEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(tech.vvp.vvp.entity.projectile.CannonAtgmShellEntity::new).noSave().sized(0.75f, 0.75f));

    public static final RegistryObject<EntityType<Fab500Entity>> FAB_500 = register("fab_500",
            EntityType.Builder.<Fab500Entity>of(Fab500Entity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(Fab500Entity::new).noSave().sized(0.8f, 0.8f));

    public static final RegistryObject<EntityType<Fab250Entity>> FAB_250 = register("fab_250",
            EntityType.Builder.<Fab250Entity>of(Fab250Entity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(Fab250Entity::new).noSave().sized(0.8f, 0.8f));

        private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
                return ENTITY_TYPES.register(name, () -> entityTypeBuilder.build(name));
            }
        
        
            public static void register(IEventBus eventBus) {
                ENTITY_TYPES.register(eventBus);
            }
}
