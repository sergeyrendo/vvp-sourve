package tech.vvp.vvp.init;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.SuperCobraEntity;
import tech.vvp.vvp.entity.projectile.*;
import tech.vvp.vvp.entity.vehicle.*;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, VVP.MOD_ID);

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

    public static final RegistryObject<EntityType<BrmEntity>> BRM = ENTITY_TYPES.register("brm",
            () -> EntityType.Builder.<BrmEntity>of(BrmEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(BrmEntity::clientSpawn)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("brm"));

    public static final RegistryObject<EntityType<Bmp3Entity>> BMP_3 = ENTITY_TYPES.register("bmp_3",
            () -> EntityType.Builder.<Bmp3Entity>of(Bmp3Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Bmp3Entity::clientSpawn)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("bmp_3"));

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

    public static final RegistryObject<EntityType<ChryzantemaEntity>> CHRYZANTEMA = ENTITY_TYPES.register("chryzantema",
            () -> EntityType.Builder.<ChryzantemaEntity>of(ChryzantemaEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(ChryzantemaEntity::clientSpawn)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("chryzantema"));

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

    public static final RegistryObject<EntityType<T90MEntity>> T90_M = ENTITY_TYPES.register("t90_m",
            () -> EntityType.Builder.<T90MEntity>of(T90MEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(T90MEntity::clientSpawn)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("t90_m"));

    public static final RegistryObject<EntityType<Su25Entity>> SU_25 = ENTITY_TYPES.register("su_25",
            () -> EntityType.Builder.<Su25Entity>of(Su25Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Su25Entity::clientSpawn)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("su_25"));

    public static final RegistryObject<EntityType<T90M22Entity>> T90_M_22 = ENTITY_TYPES.register("t90_m_22",
            () -> EntityType.Builder.<T90M22Entity>of(T90M22Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(T90M22Entity::clientSpawn)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("t90_m_2"));

    public static final RegistryObject<EntityType<T90AEntity>> T90_A = ENTITY_TYPES.register("t90_a",
            () -> EntityType.Builder.<T90AEntity>of(T90AEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(T90AEntity::clientSpawn)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("t90_a"));

    public static final RegistryObject<EntityType<M1A2Entity>> M1A2 = ENTITY_TYPES.register("m1a2",
            () -> EntityType.Builder.<M1A2Entity>of(M1A2Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(M1A2Entity::clientSpawn)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("m1a2"));

    public static final RegistryObject<EntityType<Mi28Entity>> MI_28 = ENTITY_TYPES.register("mi_28",
            () -> EntityType.Builder.<Mi28Entity>of(Mi28Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Mi28Entity::clientSpawn)
                    .fireImmune()
                    .sized(4f, 4f)
                    .build("mi_28"));

    public static final RegistryObject<EntityType<SuperCobraEntity>> AH_1 = ENTITY_TYPES.register("ah_1",
            () -> EntityType.Builder.<SuperCobraEntity>of(SuperCobraEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(SuperCobraEntity::clientSpawn)
                    .fireImmune()
                    .sized(4f, 4f)
                    .build("ah_1"));

    public static final RegistryObject<EntityType<Mi8Entity>> MI_8 = ENTITY_TYPES.register("mi_8",
            () -> EntityType.Builder.<Mi8Entity>of(Mi8Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Mi8Entity::clientSpawn)
                    .fireImmune()
                    .sized(4f, 4f)
                    .build("mi_8"));

    public static final RegistryObject<EntityType<Mi8MTVEntity>> MI_8_MTV = ENTITY_TYPES.register("mi_8_mtv",
            () -> EntityType.Builder.<Mi8MTVEntity>of(Mi8MTVEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Mi8MTVEntity::clientSpawn)
                    .fireImmune()
                    .sized(4f, 4f)
                    .build("mi_8_mtv"));

    public static final RegistryObject<EntityType<Mi8AMTSHEntity>> MI_8_AMTSH = ENTITY_TYPES.register("mi_8_amtsh",
            () -> EntityType.Builder.<Mi8AMTSHEntity>of(Mi8AMTSHEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Mi8AMTSHEntity::clientSpawn)
                    .fireImmune()
                    .sized(4f, 4f)
                    .build("mi_8_amtsh"));



    public static final RegistryObject<EntityType<tech.vvp.vvp.entity.projectile.CannonAtgmShellEntity>> CANNON_ATGM_SHELL = register("cannon_atgm_shell",
            EntityType.Builder.<tech.vvp.vvp.entity.projectile.CannonAtgmShellEntity>of(tech.vvp.vvp.entity.projectile.CannonAtgmShellEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(tech.vvp.vvp.entity.projectile.CannonAtgmShellEntity::new).noSave().sized(0.75f, 0.75f));

    public static final RegistryObject<EntityType<Fab500Entity>> FAB_500 = register("fab_500",
            EntityType.Builder.<Fab500Entity>of(Fab500Entity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(Fab500Entity::new).noSave().sized(0.8f, 0.8f));

    public static final RegistryObject<EntityType<LmurEntity>> LMUR = register("lmur",
            EntityType.Builder.<LmurEntity>of(LmurEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(LmurEntity::new).noSave().sized(0.8f, 0.8f));

    public static final RegistryObject<EntityType<HFireEntity>> H_FIRE = register("h_fire",
            EntityType.Builder.<HFireEntity>of(HFireEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(HFireEntity::new).noSave().sized(0.8f, 0.8f));

    public static final RegistryObject<EntityType<X25Entity>> X25 = register("x25",
            EntityType.Builder.<X25Entity>of(X25Entity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(X25Entity::new).noSave().sized(0.8f, 0.8f));

    public static final RegistryObject<EntityType<Fab250Entity>> FAB_250 = register("fab_250",
            EntityType.Builder.<Fab250Entity>of(Fab250Entity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(Fab250Entity::new).noSave().sized(0.8f, 0.8f));

    public static final RegistryObject<EntityType<S130Entity>> S_130 = register("s_130",
            EntityType.Builder.<S130Entity>of(S130Entity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(S130Entity::new).noSave().sized(0.8f, 0.8f));

        private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
                return ENTITY_TYPES.register(name, () -> entityTypeBuilder.build(name));
            }
        
        
            public static void register(IEventBus eventBus) {
                ENTITY_TYPES.register(eventBus);
            }
}
