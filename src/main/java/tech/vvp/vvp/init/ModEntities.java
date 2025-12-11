package tech.vvp.vvp.init;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.BallisticMissileEntity;
import tech.vvp.vvp.entity.projectile.Fab250Entity;
import tech.vvp.vvp.entity.projectile.Fab500Entity;
import tech.vvp.vvp.entity.projectile.R73Entity;
import tech.vvp.vvp.entity.vehicle.*;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, VVP.MOD_ID);

    public static final RegistryObject<EntityType<Btr4Entity>> BTR_4 = register("btr_4",
            EntityType.Builder.of(Btr4Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<BradleyEntity>> BRADLEY = register("bradley",
            EntityType.Builder.of(BradleyEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<BrmEntity>> BRM = register("brm",
            EntityType.Builder.of(BrmEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<Bmp3Entity>> BMP_3 = register("bmp_3",
            EntityType.Builder.of(Bmp3Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<C3MEntity>> C3M = register("2c3m",
            EntityType.Builder.of(C3MEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4.5f, 4.0f));

    public static final RegistryObject<EntityType<Bmp2Entity>> BMP_2 = register("bmp_2",
            EntityType.Builder.of(Bmp2Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3f));

    public static final RegistryObject<EntityType<Bmp2MEntity>> BMP_2M = register("bmp_2m",
            EntityType.Builder.of(Bmp2MEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3f));

    public static final RegistryObject<EntityType<ChryzantemaEntity>> CHRYZANTEMA = register("chryzantema",
            EntityType.Builder.of(ChryzantemaEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<StrykerEntity>> STRYKER = register("stryker",
            EntityType.Builder.of(StrykerEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<Stryker_M1296Entity>> STRYKER_M1296 = register("stryker_m1296",
            EntityType.Builder.of(Stryker_M1296Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<TerminatorEntity>> TERMINATOR = register("terminator",
            EntityType.Builder.of(TerminatorEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<T90MEntity>> T90_M = register("t90_m",
            EntityType.Builder.of(T90MEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<T90AEntity>> T90_A = register("t90_a",
            EntityType.Builder.of(T90AEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<M1A2Entity>> M1A2 = register("m1a2",
            EntityType.Builder.of(M1A2Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(5f, 5f));

    public static final RegistryObject<EntityType<M1A2SepEntity>> M1A2_SEP = register("m1a2_sep",
            EntityType.Builder.of(M1A2SepEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(5f, 5f));

    public static final RegistryObject<EntityType<PumaEntity>> PUMA = register("puma",
            EntityType.Builder.of(PumaEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<BushmasterEntity>> BUSHMASTER = register("bushmaster",
            EntityType.Builder.of(BushmasterEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<FMTVEntity>> FMTV = register("fmtv",
            EntityType.Builder.of(FMTVEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<Mi28Entity>> MI_28 = register("mi_28",
            EntityType.Builder.of(Mi28Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<Mi24Entity>> MI_24 = register("mi_24",
            EntityType.Builder.of(Mi24Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<Leopard2A7VEntity>> LEOPARD_2A7V = register("leopard_2a7v",
            EntityType.Builder.of(Leopard2A7VEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<Ah64Entity>> AH_64 = register("ah_64",
            EntityType.Builder.of(Ah64Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<GazTigrEntity>> GAZ_TIGR = register("gaz_tigr",
            EntityType.Builder.of(GazTigrEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f, 2f));

    public static final RegistryObject<EntityType<ChallengerEntity>> CHALLENGER = register("challenger",
            EntityType.Builder.of(ChallengerEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<T72B3MEntity>> T72_B3M = register("t72_b3m",
            EntityType.Builder.of(T72B3MEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<UralEntity>> URAL = register("ural",
            EntityType.Builder.of(UralEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 5f));

    public static final RegistryObject<EntityType<VartaEntity>> VARTA = register("varta",
            EntityType.Builder.of(VartaEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f, 3.5f));

    public static final RegistryObject<EntityType<PantsirS1Entity>> PANTSIR_S1 = register("pantsir_s1",
            EntityType.Builder.of(PantsirS1Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<Su25Entity>> SU_25 = register("su_25",
            EntityType.Builder.of(Su25Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<Fab500Entity>> FAB_500 = register("fab_500",
            EntityType.Builder.<Fab500Entity>of(Fab500Entity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).noSave().sized(0.8f, 0.8f));

    public static final RegistryObject<EntityType<Fab250Entity>> FAB_250 = register("fab_250",
            EntityType.Builder.<Fab250Entity>of(Fab250Entity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).noSave().sized(0.8f, 0.8f));

    public static final RegistryObject<EntityType<R73Entity>> R73 = register("r73",
            EntityType.Builder.<R73Entity>of(R73Entity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).fireImmune().sized(0.5f, 0.5f));

    public static final RegistryObject<EntityType<BallisticMissileEntity>> BALLISTIC_MISSILE = register("ballistic_missile",
            EntityType.Builder.<BallisticMissileEntity>of(BallisticMissileEntity::new, MobCategory.MISC).setTrackingRange(256).setUpdateInterval(1).fireImmune().sized(0.5f, 0.5f));

    public static final RegistryObject<EntityType<Ags30Entity>> AGS_30 = register("ags_30",
            EntityType.Builder.of(Ags30Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(1f, 1.5f));

    public static final RegistryObject<EntityType<KornetEntity>> KORNET = register("kornet",
            EntityType.Builder.of(KornetEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(1f, 2f));

    public static final RegistryObject<EntityType<F16Entity>> F_16 = register("f_16",
            EntityType.Builder.of(F16Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<CobraEntity>> COBRA = register("cobra",
            EntityType.Builder.of(CobraEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));


    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
        return ENTITY_TYPES.register(name, () -> entityTypeBuilder.build(name));
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
