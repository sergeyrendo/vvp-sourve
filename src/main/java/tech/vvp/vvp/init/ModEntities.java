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
import tech.vvp.vvp.entity.vehicle.vazikEntity;
import tech.vvp.vvp.entity.vehicle.bikeredEntity;
import tech.vvp.vvp.entity.vehicle.bikegreenEntity;
import tech.vvp.vvp.entity.vehicle.mi24Entity;
import tech.vvp.vvp.entity.vehicle.mi24polEntity;
import tech.vvp.vvp.entity.vehicle.mi24ukrEntity;
import tech.vvp.vvp.entity.vehicle.m997Entity;
import tech.vvp.vvp.entity.vehicle.cobraEntity;
import tech.vvp.vvp.entity.vehicle.cobrasharkEntity;
import tech.vvp.vvp.entity.vehicle.f35Entity;
import tech.vvp.vvp.entity.vehicle.btr80aEntity;
import tech.vvp.vvp.entity.vehicle.m997_greenEntity;
import tech.vvp.vvp.entity.vehicle.btr80a_1Entity;
import tech.vvp.vvp.entity.vehicle.strykerEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, VVP.MOD_ID);
                    
    public static final RegistryObject<EntityType<vazikEntity>> VAZIK = ENTITY_TYPES.register("vazik",
                    () -> EntityType.Builder.<vazikEntity>of(vazikEntity::new, MobCategory.MISC)
                        .setTrackingRange(64)
                        .setUpdateInterval(1)
                        .setCustomClientFactory(vazikEntity::clientSpawn)
                        .fireImmune()
                        .sized(2.7f, 2.3f)
                        .build("vazik"));

    public static final RegistryObject<EntityType<m997Entity>> M997 = ENTITY_TYPES.register("m997",
                        () -> EntityType.Builder.<m997Entity>of(m997Entity::new, MobCategory.MISC)
                            .setTrackingRange(64)
                            .setUpdateInterval(1)
                            .setCustomClientFactory(m997Entity::clientSpawn)
                            .fireImmune()
                            .sized(4.2f, 3.2f)
                            .build("m997"));
                            
    public static final RegistryObject<EntityType<m997_greenEntity>> M997_GREEN = ENTITY_TYPES.register("m997_green",
                        () -> EntityType.Builder.<m997_greenEntity>of(m997_greenEntity::new, MobCategory.MISC)
                            .setTrackingRange(64)
                            .setUpdateInterval(1)
                            .setCustomClientFactory(m997_greenEntity::clientSpawn)
                            .fireImmune()
                            .sized(4.2f, 3.2f)
                            .build("m997_green"));

    public static final RegistryObject<EntityType<btr80aEntity>> BTR80A = ENTITY_TYPES.register("btr_80a",
                            () -> EntityType.Builder.<btr80aEntity>of(btr80aEntity::new, MobCategory.MISC)
                                .setTrackingRange(64)
                                .setUpdateInterval(1)
                                // .setCustomClientFactory(btr80aEntity::clientSpawn)
                                .fireImmune()
                                .sized(3.9f, 3.2f)
                                .build("btr_80a"));

    public static final RegistryObject<EntityType<btr80a_1Entity>> BTR_80A_1 = ENTITY_TYPES.register("btr_80a_1",
                            () -> EntityType.Builder.<btr80a_1Entity>of(btr80a_1Entity::new, MobCategory.MISC)
                                .setTrackingRange(64)
                                .setUpdateInterval(1)
                                // .setCustomClientFactory(btr80a_1Entity::clientSpawn)
                                .fireImmune()
                                .sized(3.9f, 3.2f)
                                .build("btr_80a_1"));

    public static final RegistryObject<EntityType<strykerEntity>> STRYKER = ENTITY_TYPES.register("stryker",
                            () -> EntityType.Builder.<strykerEntity>of(strykerEntity::new, MobCategory.MISC)
                                .setTrackingRange(64)
                                .setUpdateInterval(1)
                                // .setCustomClientFactory(strykerEntity::clientSpawn)
                                .fireImmune()
                                .sized(3.9f, 3.2f)
                                .build("stryker"));

    public static final RegistryObject<EntityType<bikegreenEntity>> BIKEGREEN = ENTITY_TYPES.register("bikegreen",
                        () -> EntityType.Builder.<bikegreenEntity>of(bikegreenEntity::new, MobCategory.MISC)
                        .setTrackingRange(64)
                        .setUpdateInterval(1)
                        .setCustomClientFactory(bikegreenEntity::clientSpawn)
                        .fireImmune()
                        .sized(0.9f, 1.2f)
                        .build("bikegreen"));

    public static final RegistryObject<EntityType<bikeredEntity>> BIKERED = ENTITY_TYPES.register("bikered",
                            () -> EntityType.Builder.<bikeredEntity>of(bikeredEntity::new, MobCategory.MISC)
                        .setTrackingRange(64)
                        .setUpdateInterval(1)
                        .setCustomClientFactory(bikeredEntity::clientSpawn)
                        .fireImmune()
                        .sized(0.9f, 1.2f)
                        .build("bikered"));

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
        
    public static final RegistryObject<EntityType<HeliRocketEntity>> HELI_ROCKET = ENTITY_TYPES.register("heli_rocket",
                    () -> EntityType.Builder.<HeliRocketEntity>of(HeliRocketEntity::new, MobCategory.MISC)
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


    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
        return ENTITY_TYPES.register(name, () -> entityTypeBuilder.build(name));
    }

    public static final RegistryObject<EntityType<mi24Entity>> MI24 = register("mi24",
            EntityType.Builder.<mi24Entity>of(mi24Entity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(mi24Entity::new).fireImmune().sized(4.5f, 4.8f));

    public static final RegistryObject<EntityType<mi24ukrEntity>> MI24UKR = register("mi24ukr",
            EntityType.Builder.<mi24ukrEntity>of(mi24ukrEntity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(mi24ukrEntity::new).fireImmune().sized(4.5f, 4.8f));

    public static final RegistryObject<EntityType<mi24polEntity>> MI24POL = register("mi24polsha",
            EntityType.Builder.<mi24polEntity>of(mi24polEntity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(mi24polEntity::new).fireImmune().sized(4.5f, 4.8f));
    
    public static final RegistryObject<EntityType<cobraEntity>> COBRA = register("cobra",
            EntityType.Builder.<cobraEntity>of(cobraEntity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(cobraEntity::new).fireImmune().sized(4.5f, 4.8f));

    public static final RegistryObject<EntityType<cobrasharkEntity>> COBRASHARK = register("cobrashark",
            EntityType.Builder.<cobrasharkEntity>of(cobrasharkEntity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(cobrasharkEntity::new).fireImmune().sized(4.5f, 4.8f));

    public static final RegistryObject<EntityType<f35Entity>> F35 = register("f35",
            EntityType.Builder.<f35Entity>of(f35Entity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(f35Entity::new).fireImmune().sized(4.5f, 4.8f));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
