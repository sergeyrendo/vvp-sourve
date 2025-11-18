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
                                    .setCustomClientFactory(Btr4Entity::new)
                                    .fireImmune()
                                    .sized(3.9f, 3.5f)
                                    .build("btr_4"));

    public static final RegistryObject<EntityType<BradleyEntity>> BRADLEY = ENTITY_TYPES.register("bradley",
            () -> EntityType.Builder.<BradleyEntity>of(BradleyEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(BradleyEntity::new)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("bradley"));

    public static final RegistryObject<EntityType<BrmEntity>> BRM = ENTITY_TYPES.register("brm",
            () -> EntityType.Builder.<BrmEntity>of(BrmEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(BrmEntity::new)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("brm"));

    public static final RegistryObject<EntityType<Bmp3Entity>> BMP_3 = ENTITY_TYPES.register("bmp_3",
            () -> EntityType.Builder.<Bmp3Entity>of(Bmp3Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Bmp3Entity::new)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("bmp_3"));

    public static final RegistryObject<EntityType<Bmp2Entity>> BMP_2 = ENTITY_TYPES.register("bmp_2",
            () -> EntityType.Builder.<Bmp2Entity>of(Bmp2Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Bmp2Entity::new)
                    .fireImmune()
                    .sized(3.9f, 3f)
                    .build("bmp_2"));

    public static final RegistryObject<EntityType<Bmp2MEntity>> BMP_2M = ENTITY_TYPES.register("bmp_2m",
            () -> EntityType.Builder.<Bmp2MEntity>of(Bmp2MEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Bmp2MEntity::new)
                    .fireImmune()
                    .sized(3.9f, 3f)
                    .build("bmp_2m"));

    public static final RegistryObject<EntityType<Uh60ModEntity>> UH60MOD = ENTITY_TYPES.register("uh60mod",
            () -> EntityType.Builder.<Uh60ModEntity>of(Uh60ModEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Uh60ModEntity::new)
                    .fireImmune()
                    .sized(3f, 4f)
                    .build("uh60mod"));


    public static final RegistryObject<EntityType<Uh60Entity>> UH60 = ENTITY_TYPES.register("uh60",
            () -> EntityType.Builder.<Uh60Entity>of(Uh60Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Uh60Entity::new)
                    .fireImmune()
                    .sized(3f, 4f)
                    .build("uh60"));

    public static final RegistryObject<EntityType<ChryzantemaEntity>> CHRYZANTEMA = ENTITY_TYPES.register("chryzantema",
            () -> EntityType.Builder.<ChryzantemaEntity>of(ChryzantemaEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(ChryzantemaEntity::new)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("chryzantema"));

    public static final RegistryObject<EntityType<StrykerEntity>> STRYKER = ENTITY_TYPES.register("stryker",
            () -> EntityType.Builder.<StrykerEntity>of(StrykerEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(StrykerEntity::new)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("stryker"));

    public static final RegistryObject<EntityType<Stryker_M1296Entity>> STRYKER_M1296 = ENTITY_TYPES.register("stryker_m1296",
            () -> EntityType.Builder.<Stryker_M1296Entity>of(Stryker_M1296Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Stryker_M1296Entity::new)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("stryker_m1296"));

    public static final RegistryObject<EntityType<TerminatorEntity>> TERMINATOR = ENTITY_TYPES.register("terminator",
            () -> EntityType.Builder.<TerminatorEntity>of(TerminatorEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(TerminatorEntity::new)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("terminator"));

    public static final RegistryObject<EntityType<PantsirS1Entity>> PANTSIR_S1 = ENTITY_TYPES.register("pantsir_s1",
            () -> EntityType.Builder.<PantsirS1Entity>of(PantsirS1Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(PantsirS1Entity::new)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("pantsir_s1"));

    public static final RegistryObject<EntityType<SosnaEntity>> sosna = ENTITY_TYPES.register("sosna",
            () -> EntityType.Builder.<SosnaEntity>of(SosnaEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(SosnaEntity::new)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("sosna"));

    public static final RegistryObject<EntityType<T90MEntity>> T90_M = ENTITY_TYPES.register("t90_m",
            () -> EntityType.Builder.<T90MEntity>of(T90MEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(T90MEntity::new)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("t90_m"));

    public static final RegistryObject<EntityType<Su25Entity>> SU_25 = ENTITY_TYPES.register("su_25",
            () -> EntityType.Builder.<Su25Entity>of(Su25Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Su25Entity::new)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("su_25"));

    public static final RegistryObject<EntityType<T90M22Entity>> T90_M_22 = ENTITY_TYPES.register("t90_m_22",
            () -> EntityType.Builder.<T90M22Entity>of(T90M22Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(T90M22Entity::new)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("t90_m_2"));

    public static final RegistryObject<EntityType<T90AEntity>> T90_A = ENTITY_TYPES.register("t90_a",
            () -> EntityType.Builder.<T90AEntity>of(T90AEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(T90AEntity::new)
                    .fireImmune()
                    .sized(3.9f, 3.5f)
                    .build("t90_a"));

    public static final RegistryObject<EntityType<M1A2Entity>> M1A2 = ENTITY_TYPES.register("m1a2",
            () -> EntityType.Builder.<M1A2Entity>of(M1A2Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(M1A2Entity::new)
                    .fireImmune()
                    .sized(5, 5)
                    .build("m1a2"));

    public static final RegistryObject<EntityType<M1A2SepEntity>> M1A2_SEP = ENTITY_TYPES.register("m1a2_sep",
            () -> EntityType.Builder.<M1A2SepEntity>of(M1A2SepEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(M1A2SepEntity::new)
                    .fireImmune()
                    .sized(5, 5)
                    .build("m1a2_sep"));

    public static final RegistryObject<EntityType<Mi28Entity>> MI_28 = ENTITY_TYPES.register("mi_28",
            () -> EntityType.Builder.<Mi28Entity>of(Mi28Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Mi28Entity::new)
                    .fireImmune()
                    .sized(4f, 4f)
                    .build("mi_28"));

    public static final RegistryObject<EntityType<Mi28_1Entity>> MI_28_1 = ENTITY_TYPES.register("mi_28_1",
            () -> EntityType.Builder.<Mi28_1Entity>of(Mi28_1Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Mi28_1Entity::new)
                    .fireImmune()
                    .sized(4f, 4f)
                    .build("mi_28_1"));

    public static final RegistryObject<EntityType<SuperCobraEntity>> AH_1 = ENTITY_TYPES.register("ah_1",
            () -> EntityType.Builder.<SuperCobraEntity>of(SuperCobraEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(SuperCobraEntity::new)
                    .fireImmune()
                    .sized(4f, 4f)
                    .build("ah_1"));

    public static final RegistryObject<EntityType<Mi8Entity>> MI_8 = ENTITY_TYPES.register("mi_8",
            () -> EntityType.Builder.<Mi8Entity>of(Mi8Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Mi8Entity::new)
                    .fireImmune()
                    .sized(4f, 4f)
                    .build("mi_8"));

    public static final RegistryObject<EntityType<Mi8MTVEntity>> MI_8_MTV = ENTITY_TYPES.register("mi_8_mtv",
            () -> EntityType.Builder.<Mi8MTVEntity>of(Mi8MTVEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Mi8MTVEntity::new)
                    .fireImmune()
                    .sized(4f, 4f)
                    .build("mi_8_mtv"));

    public static final RegistryObject<EntityType<Mi8AMTSHEntity>> MI_8_AMTSH = ENTITY_TYPES.register("mi_8_amtsh",
            () -> EntityType.Builder.<Mi8AMTSHEntity>of(Mi8AMTSHEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Mi8AMTSHEntity::new)
                    .fireImmune()
                    .sized(4f, 4f)
                    .build("mi_8_amtsh"));

    public static final RegistryObject<EntityType<PumaEntity>> PUMA = ENTITY_TYPES.register("puma",
            () -> EntityType.Builder.<PumaEntity>of(PumaEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(PumaEntity::new)
                    .fireImmune()
                    .sized(4f, 4f)
                    .build("puma"));

    public static final RegistryObject<EntityType<BushmasterEntity>> BUSHMASTER = ENTITY_TYPES.register("bushmaster",
            () -> EntityType.Builder.<BushmasterEntity>of(BushmasterEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(BushmasterEntity::new)
                    .fireImmune()
                    .sized(4f, 4f)
                    .build("bushmaster"));

    public static final RegistryObject<EntityType<ToyotaEntity>> TOYOTA = ENTITY_TYPES.register("toyota",
            () -> EntityType.Builder.<ToyotaEntity>of(ToyotaEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(ToyotaEntity::new)
                    .fireImmune()
                    .sized(3f, 2f)
                    .build("toyota"));

    public static final RegistryObject<EntityType<TU22M3Entity>> TU_22_M3 = ENTITY_TYPES.register("tu22m3",
            () -> EntityType.Builder.<TU22M3Entity>of(TU22M3Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(TU22M3Entity::new)
                    .fireImmune()
                    .sized(8f, 8f)
                    .build("tu22m3"));

    public static final RegistryObject<EntityType<ChallengerEntity>> CHALLENGER = ENTITY_TYPES.register("challenger",
            () -> EntityType.Builder.<ChallengerEntity>of(ChallengerEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(ChallengerEntity::new)
                    .fireImmune()
                    .sized(4f, 4f)
                    .build("challenger"));

    public static final RegistryObject<EntityType<T72B3MEntity>> T72_B3M = ENTITY_TYPES.register("t72_b3m",
            () -> EntityType.Builder.<T72B3MEntity>of(T72B3MEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(T72B3MEntity::new)
                    .fireImmune()
                    .sized(4f, 4f)
                    .build("t72_b3m"));





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

    public static final RegistryObject<EntityType<tech.vvp.vvp.entity.projectile.E6_57Entity>> E6_57 = register("e6_57",
            EntityType.Builder.<tech.vvp.vvp.entity.projectile.E6_57Entity>of(tech.vvp.vvp.entity.projectile.E6_57Entity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(tech.vvp.vvp.entity.projectile.E6_57Entity::new).noSave().sized(0.8f, 0.8f));

    public static final RegistryObject<EntityType<tech.vvp.vvp.entity.projectile.M337Entity>> M337 = register("m337",
            EntityType.Builder.<tech.vvp.vvp.entity.projectile.M337Entity>of(tech.vvp.vvp.entity.projectile.M337Entity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(tech.vvp.vvp.entity.projectile.M337Entity::new).noSave().sized(0.8f, 0.8f));

    public static final RegistryObject<EntityType<Fab250Entity>> FAB_250 = register("fab_250",
            EntityType.Builder.<Fab250Entity>of(Fab250Entity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(Fab250Entity::new).noSave().sized(0.8f, 0.8f));

    public static final RegistryObject<EntityType<PantsirS1MissileEntity>> PANTSIR_S1_MISSILE = register("pantsir_s1_missile",
            EntityType.Builder.<PantsirS1MissileEntity>of(PantsirS1MissileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(PantsirS1MissileEntity::new).noSave().sized(0.5f, 0.5f));

    public static final RegistryObject<EntityType<SosnaMissileEntity>> SOSNA_MISSILE = register("sosna_missile",
            EntityType.Builder.<SosnaMissileEntity>of(SosnaMissileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(SosnaMissileEntity::new).noSave().sized(0.5f, 0.5f));

    public static final RegistryObject<EntityType<S130Entity>> S_130 = register("s_130",
            EntityType.Builder.<S130Entity>of(S130Entity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(S130Entity::new).noSave().sized(0.8f, 0.8f));

    public static final RegistryObject<EntityType<SpikeATGMEntity>> SPIKE_MISSLE = register("spike_missle",
            EntityType.Builder.<SpikeATGMEntity>of(SpikeATGMEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(SpikeATGMEntity::new).noSave().sized(0.8f, 0.8f));

    public static final RegistryObject<EntityType<TOWEntity>> TOW_MISSILE = register("tow_missle",
            EntityType.Builder.<TOWEntity>of(TOWEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(TOWEntity::new).noSave().sized(0.8f, 0.8f));

    public static final RegistryObject<EntityType<HryzantemaEntity>> HRYZANTEMA = register("hryzantema",
            EntityType.Builder.<HryzantemaEntity>of(HryzantemaEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(HryzantemaEntity::new).noSave().sized(0.8f, 0.8f));

        private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
                return ENTITY_TYPES.register(name, () -> entityTypeBuilder.build(name));
            }
        
        
            public static void register(IEventBus eventBus) {
                ENTITY_TYPES.register(eventBus);
            }
}
