package tech.vvp.vvp.config.server;

import net.minecraftforge.common.ForgeConfigSpec;

public class ExplosionConfigVVP {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.IntValue TOW_MISSILE_DAMAGE;
    public static ForgeConfigSpec.IntValue TOW_MISSILE_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue TOW_MISSILE_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue SPIKE_MISSILE_DAMAGE;
    public static ForgeConfigSpec.IntValue SPIKE_MISSILE_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue SPIKE_MISSILE_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue BMP_2_MISSILE_DAMAGE;
    public static ForgeConfigSpec.IntValue BMP_2_MISSILE_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue BMP_2_MISSILE_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue BTR_4_MISSILE_DAMAGE;
    public static ForgeConfigSpec.IntValue BTR_4_MISSILE_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue BTR_4_MISSILE_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue TERMINATOR_MISSILE_DAMAGE;
    public static ForgeConfigSpec.IntValue TERMINATOR_MISSILE_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue TERMINATOR_MISSILE_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue PANTSIR_S1_MISSILE_DAMAGE;
    public static ForgeConfigSpec.IntValue PANTSIR_S1_MISSILE_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue PANTSIR_S1_MISSILE_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue sosna_MISSILE_DAMAGE;
    public static ForgeConfigSpec.IntValue sosna_MISSILE_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue sosna_MISSILE_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue FAB_500_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue FAB_500_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue FAB_250_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue FAB_250_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue LMUR_DAMAGE;
    public static ForgeConfigSpec.IntValue LMUR_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue LMUR_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue HRYZANTEMA_DAMAGE;
    public static ForgeConfigSpec.IntValue HRYZANTEMA_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue HRYZANTEMA_EXPLOSION_RADIUS;

    static {
        BUILDER.push("tow_missile");

        BUILDER.comment("The damage of TOW missile");
        TOW_MISSILE_DAMAGE = BUILDER.defineInRange("tow_missile_damage", 800, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion damage of TOW missile");
        TOW_MISSILE_EXPLOSION_DAMAGE = BUILDER.defineInRange("tow_missile_explosion_damage", 50, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion radius of TOW missile");
        TOW_MISSILE_EXPLOSION_RADIUS = BUILDER.defineInRange("tow_missile_explosion_radius", 7, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("spike_missile");

        BUILDER.comment("The damage of spike missile");
        SPIKE_MISSILE_DAMAGE = BUILDER.defineInRange("spike_missile_damage", 775, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion damage of spike missile");
        SPIKE_MISSILE_EXPLOSION_DAMAGE = BUILDER.defineInRange("spike_missile_explosion_damage", 47, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion radius of spike missile");
        SPIKE_MISSILE_EXPLOSION_RADIUS = BUILDER.defineInRange("spike_missile_explosion_radius", 6, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("bmp_2_missile");

        BUILDER.comment("The damage of BMP-2 missile");
        BMP_2_MISSILE_DAMAGE = BUILDER.defineInRange("bmp_2_missile_damage", 600, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion damage of BMP-2 missile");
        BMP_2_MISSILE_EXPLOSION_DAMAGE = BUILDER.defineInRange("bmp_2_missile_explosion_damage", 47, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion radius of BMP-2 missile");
        BMP_2_MISSILE_EXPLOSION_RADIUS = BUILDER.defineInRange("bmp_2_missile_explosion_radius", 4, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("btr_4_missile");

        BUILDER.comment("The damage of BTR-4 missile");
        BTR_4_MISSILE_DAMAGE = BUILDER.defineInRange("btr_4_missile_damage", 615, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion damage of BTR-4 missile");
        BTR_4_MISSILE_EXPLOSION_DAMAGE = BUILDER.defineInRange("btr_4_missile_explosion_damage", 50, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion radius of BTR-4 missile");
        BTR_4_MISSILE_EXPLOSION_RADIUS = BUILDER.defineInRange("btr_4_missile_explosion_radius", 5, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("terminator_missile");

        BUILDER.comment("The damage of BMPT Terminator missile");
        TERMINATOR_MISSILE_DAMAGE = BUILDER.defineInRange("terminatir_missile_damage", 450, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion damage of BMPT Terminator missile");
        TERMINATOR_MISSILE_EXPLOSION_DAMAGE = BUILDER.defineInRange("terminatir_missile_explosion_damage", 19, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion radius of BMPT Terminator missile");
        TERMINATOR_MISSILE_EXPLOSION_RADIUS = BUILDER.defineInRange("terminatir_missile_explosion_radius", 3, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("pantsir_s1_missile");

        BUILDER.comment("The damage of Pantsir-S1 57E6 missile");
        PANTSIR_S1_MISSILE_DAMAGE = BUILDER.defineInRange("pantsir_s1_missile_damage", 1200, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion damage of Pantsir-S1 57E6 missile");
        PANTSIR_S1_MISSILE_EXPLOSION_DAMAGE = BUILDER.defineInRange("pantsir_s1_missile_explosion_damage", 350, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion radius of Pantsir-S1 57E6 missile");
        PANTSIR_S1_MISSILE_EXPLOSION_RADIUS = BUILDER.defineInRange("pantsir_s1_missile_explosion_radius", 12, 1, Integer.MAX_VALUE);

        BUILDER.comment("The damage of Sosna-R 9M340 missile");
        sosna_MISSILE_DAMAGE = BUILDER.defineInRange("sosna_missile_damage", 1200, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion damage of Sosna-R 9M340 missile");
        sosna_MISSILE_EXPLOSION_DAMAGE = BUILDER.defineInRange("sosna_missile_explosion_damage", 350, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion radius of Sosna-R 9M340 missile");
        sosna_MISSILE_EXPLOSION_RADIUS = BUILDER.defineInRange("sosna_missile_explosion_radius", 12, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("FAB-500");

        BUILDER.comment("The explosion damage of FAB-500");
        FAB_500_EXPLOSION_DAMAGE = BUILDER.defineInRange("fab_500_explosion_damage", 625, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion radius of FAB-500");
        FAB_500_EXPLOSION_RADIUS = BUILDER.defineInRange("fab_500_explosion_radius", 25d, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("FAB-250");

        BUILDER.comment("The explosion damage of FAB-250");
        FAB_250_EXPLOSION_DAMAGE = BUILDER.defineInRange("fab_250_explosion_damage", 350, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion radius of FAB-250");
        FAB_250_EXPLOSION_RADIUS = BUILDER.defineInRange("fab_250_explosion_radius", 19d, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("LMUR");

        BUILDER.comment("The damage of LMUR");
        LMUR_DAMAGE = BUILDER.defineInRange("lmur_damage", 1600, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion damage of LMUR");
        LMUR_EXPLOSION_DAMAGE = BUILDER.defineInRange("lmur_explosion_damage", 225, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion radius of LMUR");
        LMUR_EXPLOSION_RADIUS = BUILDER.defineInRange("lmur_explosion_radius", 13d, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("9M123");

        BUILDER.comment("The damage of 9M123");
        HRYZANTEMA_DAMAGE = BUILDER.defineInRange("hryzantema_damage", 1100, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion damage of 9M123");
        HRYZANTEMA_EXPLOSION_DAMAGE = BUILDER.defineInRange("hryzantema_explosion_damage", 175, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion radius of 9M123");
        HRYZANTEMA_EXPLOSION_RADIUS = BUILDER.defineInRange("hryzantema_explosion_radius", 10d, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}

