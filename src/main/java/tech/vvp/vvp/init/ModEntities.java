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
import tech.vvp.vvp.entity.vehicle.*;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, VVP.MOD_ID);
                    
//     public static final RegistryObject<EntityType<VazikEntity>> VAZIK = ENTITY_TYPES.register("vazik",
//                     () -> EntityType.Builder.<VazikEntity>of(VazikEntity::new, MobCategory.MISC)
//                         .setTrackingRange(64)
//                         .setUpdateInterval(1)
//                         .setCustomClientFactory(VazikEntity::clientSpawn)
//                         .fireImmune()
//                         .sized(2.7f, 2.3f)
//                         .build("vazik"));


                            
    public static final RegistryObject<EntityType<M997_greenEntity>> M997_GREEN = ENTITY_TYPES.register("m997_green",
                        () -> EntityType.Builder.<M997_greenEntity>of(M997_greenEntity::new, MobCategory.MISC)
                            .setTrackingRange(64)
                            .setUpdateInterval(1)
                            .setCustomClientFactory(M997_greenEntity::clientSpawn)
                            .fireImmune()
                            .sized(4.2f, 3.2f)
                            .build("m997_green"));

    public static final RegistryObject<EntityType<Btr80aEntity>> BTR80A = ENTITY_TYPES.register("btr_80a",
                            () -> EntityType.Builder.<Btr80aEntity>of(Btr80aEntity::new, MobCategory.MISC)
                                .setTrackingRange(64)
                                .setUpdateInterval(1)
                                .setCustomClientFactory(Btr80aEntity::clientSpawn)
                                .fireImmune()
                                .sized(3.9f, 3.2f)
                                .build("btr_80a"));

    public static final RegistryObject<EntityType<StrykerEntity>> STRYKER = ENTITY_TYPES.register("stryker",
                            () -> EntityType.Builder.<StrykerEntity>of(StrykerEntity::new, MobCategory.MISC)
                                .setTrackingRange(64)
                                .setUpdateInterval(1)
                                .setCustomClientFactory(StrykerEntity::clientSpawn)
                                .fireImmune()
                                .sized(3.9f, 3.5f)
                                .build("stryker"));
                        
    public static final RegistryObject<EntityType<Stryker_1Entity>> STRYKER_1 = ENTITY_TYPES.register("stryker_m1296",
                            () -> EntityType.Builder.<Stryker_1Entity>of(Stryker_1Entity::new, MobCategory.MISC)
                                .setTrackingRange(64)
                                .setUpdateInterval(1)
                                .setCustomClientFactory(Stryker_1Entity::clientSpawn)
                                .fireImmune()
                                .sized(3.9f, 3.5f)
                                .build("stryker_1"));


    public static final RegistryObject<EntityType<HumveeEntity>> HUMVEE = ENTITY_TYPES.register("humvee",
            () -> EntityType.Builder.<HumveeEntity>of(HumveeEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(HumveeEntity::clientSpawn)
                    .fireImmune()
                    .sized(3f, 2.5f)
                    .build("humvee"));

//     public static final RegistryObject<EntityType<BikegreenEntity>> BIKEGREEN = ENTITY_TYPES.register("bikegreen",
//                         () -> EntityType.Builder.<BikegreenEntity>of(BikegreenEntity::new, MobCategory.MISC)
//                         .setTrackingRange(64)
//                         .setUpdateInterval(1)
//                         .setCustomClientFactory(BikegreenEntity::clientSpawn)
//                         .fireImmune()
//                         .sized(0.9f, 1.2f)
//                         .build("bikegreen"));

//     public static final RegistryObject<EntityType<BikeredEntity>> BIKERED = ENTITY_TYPES.register("bikered",
//                             () -> EntityType.Builder.<BikeredEntity>of(BikeredEntity::new, MobCategory.MISC)
//                         .setTrackingRange(64)
//                         .setUpdateInterval(1)
//                         .setCustomClientFactory(BikeredEntity::clientSpawn)
//                         .fireImmune()
//                         .sized(0.9f, 1.2f)
//                         .build("bikered"));

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

    public static final RegistryObject<EntityType<TerminatorEntity>> TERMINATOR = ENTITY_TYPES.register("terminator",
                                () -> EntityType.Builder.<TerminatorEntity>of(TerminatorEntity::new, MobCategory.MISC)
                                    .setTrackingRange(64)
                                    .setUpdateInterval(1)
                                    .setCustomClientFactory(TerminatorEntity::clientSpawn)
                                    .fireImmune()
                                    .sized(3.9f, 3.5f)
                                    .build("terminator"));

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

    public static final RegistryObject<EntityType<Uh60ModEntity>> UH60MOD = ENTITY_TYPES.register("uh60mod",
                                () -> EntityType.Builder.<Uh60ModEntity>of(Uh60ModEntity::new, MobCategory.MISC)
                                    .setTrackingRange(64)
                                    .setUpdateInterval(1)
                                    .setCustomClientFactory(Uh60ModEntity::clientSpawn)
                                    .fireImmune()
                                    .sized(3f, 4f)
                                    .build("uh60mod"));

    public static final RegistryObject<EntityType<Uh60Entity>> UH60 = ENTITY_TYPES.register("uh60",
            () -> EntityType.Builder.<Uh60Entity>of(Uh60Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Uh60Entity::clientSpawn)
                    .fireImmune()
                    .sized(3f, 4f)
                    .build("uh60"));

    public static final RegistryObject<EntityType<FMTVEntity>> FMTV = ENTITY_TYPES.register("fmtv",
            () -> EntityType.Builder.<FMTVEntity>of(FMTVEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(FMTVEntity::clientSpawn)
                    .fireImmune()
                    .sized(3.1f, 3.9f)
                    .build("fmtv"));

    public static final RegistryObject<EntityType<M60Entity>> M60 = ENTITY_TYPES.register("m60",
            () -> EntityType.Builder.<M60Entity>of(M60Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(M60Entity::clientSpawn)
                    .fireImmune()
                    .sized(4f, 3f)
                    .build("m60"));

    public static final RegistryObject<EntityType<M1A2Entity>> M1A2 = ENTITY_TYPES.register("m1a2",
            () -> EntityType.Builder.<M1A2Entity>of(M1A2Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(M1A2Entity::clientSpawn)
                    .fireImmune()
                    .sized(4f, 3f)
                    .build("m1a2"));

    public static final RegistryObject<EntityType<M1A2SepEntity>> M1A2_SEP = ENTITY_TYPES.register("m1a2_sep",
            () -> EntityType.Builder.<M1A2SepEntity>of(M1A2SepEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(M1A2SepEntity::clientSpawn)
                    .fireImmune()
                    .sized(4f, 3f)
                    .build("m1a2_sep"));

    public static final RegistryObject<EntityType<Mi24Entity>> MI24 = register("mi24",
            EntityType.Builder.<Mi24Entity>of(Mi24Entity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(Mi24Entity::new).fireImmune().sized(4.5f, 4.8f));

    
    public static final RegistryObject<EntityType<CobraEntity>> COBRA = register("cobra",
            EntityType.Builder.<CobraEntity>of(CobraEntity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(CobraEntity::new).fireImmune().sized(4.5f, 4.8f));


    public static final RegistryObject<EntityType<F35Entity>> F35 = register("f35",
            EntityType.Builder.<F35Entity>of(F35Entity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(F35Entity::new).fireImmune().sized(4.5f, 4.8f));

        private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
                return ENTITY_TYPES.register(name, () -> entityTypeBuilder.build(name));
            }
        
        
            public static void register(IEventBus eventBus) {
                ENTITY_TYPES.register(eventBus);
            }
}
