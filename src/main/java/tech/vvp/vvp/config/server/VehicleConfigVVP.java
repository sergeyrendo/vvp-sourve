package tech.vvp.vvp.config.server;

import net.minecraftforge.common.ForgeConfigSpec;

public class VehicleConfigVVP {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // --- Наземная техника ---
    public static ForgeConfigSpec.IntValue FMTV_ENERGY_COST;
    public static ForgeConfigSpec.IntValue HUMVEE_ENERGY_COST;

    public static ForgeConfigSpec.IntValue BRADLEY_ENERGY_COST;
    public static ForgeConfigSpec.IntValue BRADLEY_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue BRADLEY_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue BRADLEY_CANNON_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue BMP_2_ENERGY_COST;
    public static ForgeConfigSpec.IntValue BMP_2_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue BMP_2_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue BMP_2_CANNON_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue PUMA_ENERGY_COST;
    public static ForgeConfigSpec.IntValue PUMA_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue PUMA_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue PUMA_CANNON_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue STRYKER_M1128_AP_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue STRYKER_M1128_AP_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue STRYKER_M1128_AP_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.IntValue STRYKER_M1128_HE_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue STRYKER_M1128_HE_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue STRYKER_M1128_HE_CANNON_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue STRYKER_M1296_ENERGY_COST;
    public static ForgeConfigSpec.IntValue STRYKER_M1296_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue STRYKER_M1296_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue STRYKER_M1296_CANNON_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue TERMINATOR_ENERGY_COST;
    public static ForgeConfigSpec.IntValue TERMINATOR_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue TERMINATOR_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue TERMINATOR_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.BooleanValue TERMINATOR_CANNON_DESTROY;

    public static ForgeConfigSpec.IntValue PANTSIR_S1_ENERGY_COST;
    public static ForgeConfigSpec.IntValue PANTSIR_S1_CANNON_DAMAGE;
    
    public static ForgeConfigSpec.IntValue SOSNA_ENERGY_COST;
    public static ForgeConfigSpec.IntValue PANTSIR_S1_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue PANTSIR_S1_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.BooleanValue PANTSIR_S1_CANNON_DESTROY;

    public static ForgeConfigSpec.IntValue T90_ENERGY_COST;
    public static ForgeConfigSpec.IntValue T90_AP_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue T90_AP_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue T90_AP_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.BooleanValue T90_CANNON_DESTROY;
    public static ForgeConfigSpec.IntValue T90_HE_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue T90_HE_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue T90_HE_CANNON_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue T72_ENERGY_COST;
    public static ForgeConfigSpec.IntValue T72_AP_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue T72_AP_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue T72_AP_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.BooleanValue T72_CANNON_DESTROY;
    public static ForgeConfigSpec.IntValue T72_HE_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue T72_HE_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue T72_HE_CANNON_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue M1A2_ENERGY_COST;
    public static ForgeConfigSpec.IntValue M1A2_AP_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue M1A2_AP_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue M1A2_AP_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.BooleanValue M1A2_CANNON_DESTROY;
    public static ForgeConfigSpec.IntValue M1A2_HE_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue M1A2_HE_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue M1A2_HE_CANNON_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue CHALLENGER_ENERGY_COST;
    public static ForgeConfigSpec.IntValue CHALLENGER_AP_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue CHALLENGER_AP_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue CHALLENGER_AP_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.BooleanValue CHALLENGER_CANNON_DESTROY;
    public static ForgeConfigSpec.IntValue CHALLENGER_HE_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue CHALLENGER_HE_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue CHALLENGER_HE_CANNON_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue WHEEL_ENERGY_COST;
    public static ForgeConfigSpec.IntValue A72_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue A72_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue A72_CANNON_EXPLOSION_RADIUS;

    // --- Воздушная техника ---
    public static ForgeConfigSpec.IntValue COBRA_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue COBRA_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue COBRA_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.IntValue COBRA_ROCKET_DAMAGE;
    public static ForgeConfigSpec.IntValue COBRA_ROCKET_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue COBRA_ROCKET_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.BooleanValue COBRA_CANNON_DESTROY;

    public static ForgeConfigSpec.IntValue BLACKHAWK_MAX_ENERGY_COST;
    public static ForgeConfigSpec.IntValue BLACKHAWK_MIN_ENERGY_COST;
    public static ForgeConfigSpec.IntValue BLACKHAWK_ROCKET_DAMAGE;
    public static ForgeConfigSpec.IntValue BLACKHAWK_ROCKET_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue BLACKHAWK_ROCKET_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.BooleanValue BLACKHAWK_CANNON_DESTROY;

    public static ForgeConfigSpec.IntValue MI_24_MAX_ENERGY_COST;
    public static ForgeConfigSpec.IntValue MI_24_MIN_ENERGY_COST;
    public static ForgeConfigSpec.IntValue MI_24_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue MI_24_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue MI_24_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.IntValue MI_24_ROCKET_DAMAGE;
    public static ForgeConfigSpec.IntValue MI_24_ROCKET_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue MI_24_ROCKET_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.BooleanValue MI_24_CANNON_DESTROY;

    public static ForgeConfigSpec.IntValue MI_28_MAX_ENERGY_COST;
    public static ForgeConfigSpec.IntValue MI_28_MIN_ENERGY_COST;
    public static ForgeConfigSpec.IntValue MI_28_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue MI_28_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue MI_28_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.IntValue MI_28_ROCKET_DAMAGE;
    public static ForgeConfigSpec.IntValue MI_28_ROCKET_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue MI_28_ROCKET_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.IntValue MI_28_MEDIUM_ROCKET_DAMAGE;
    public static ForgeConfigSpec.IntValue MI_28_MEDIUM_ROCKET_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue MI_28_MEDIUM_ROCKET_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.BooleanValue MI_28_CANNON_DESTROY;

    // --- Орудия ---
    public static ForgeConfigSpec.IntValue M2_GUN_DAMAGE;

    static {
        // --- Наземная техника ---
        BUILDER.push("fmtv");
        FMTV_ENERGY_COST = BUILDER.defineInRange("fmtv_energy_cost", 70, 0, 2147483647);
        BUILDER.pop();

        BUILDER.push("humvee");
        HUMVEE_ENERGY_COST = BUILDER.defineInRange("humvee_energy_cost", 68, 0, 2147483647);
        BUILDER.pop();

        BUILDER.push("bradley");
        BRADLEY_ENERGY_COST = BUILDER.defineInRange("bradley_energy_cost", 75, 0, 2147483647);
        BRADLEY_CANNON_DAMAGE = BUILDER.defineInRange("bradley_cannon_damage", 60, 1, 10000000);
        BRADLEY_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("bradley_cannon_explosion_damage", 18, 1, 10000000);
        BRADLEY_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("bradley_cannon_explosion_radius", 6d, 1d, 10000000d);
        BUILDER.pop();

        BUILDER.push("bmp2");
        BMP_2_ENERGY_COST = BUILDER.defineInRange("bmp2_energy_cost", 90, 0, 2147483647);
        BMP_2_CANNON_DAMAGE = BUILDER.defineInRange("bmp2_cannon_damage", 52, 1, 10000000);
        BMP_2_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("bmp2_cannon_explosion_damage", 19, 1, 10000000);
        BMP_2_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("bmp2_cannon_explosion_radius", 7d, 1d, 10000000d);
        BUILDER.pop();

        BUILDER.push("puma");
        PUMA_ENERGY_COST = BUILDER.defineInRange("puma_energy_cost", 89, 0, 2147483647);
        PUMA_CANNON_DAMAGE = BUILDER.defineInRange("puma_cannon_damage", 58, 1, 10000000);
        PUMA_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("puma_cannon_explosion_damage", 19, 1, 10000000);
        PUMA_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("puma_cannon_explosion_radius", 7d, 1d, 10000000d);
        BUILDER.pop();

        BUILDER.push("stryker_m1128_ap");
        STRYKER_M1128_AP_CANNON_DAMAGE = BUILDER.defineInRange("stryker_m1128_ap_cannon_damage", 750, 1, 10000000);
        STRYKER_M1128_AP_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("stryker_m1128_ap_cannon_explosion_damage", 32, 1, 10000000);
        STRYKER_M1128_AP_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("stryker_m1128_ap_cannon_explosion_radius", 4d, 1d, 10000000d);
        BUILDER.pop();

        BUILDER.push("stryker_m1128_he");
        STRYKER_M1128_HE_CANNON_DAMAGE = BUILDER.defineInRange("stryker_m1128_he_cannon_damage", 100, 1, 10000000);
        STRYKER_M1128_HE_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("stryker_m1128_he_cannon_explosion_damage", 175, 1, 10000000);
        STRYKER_M1128_HE_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("stryker_m1128_he_cannon_explosion_radius", 13d, 1d, 10000000d);
        BUILDER.pop();

        BUILDER.push("stryker_m1296");
        STRYKER_M1296_ENERGY_COST = BUILDER.defineInRange("stryker_m1296_energy_cost", 66, 0, 2147483647);
        STRYKER_M1296_CANNON_DAMAGE = BUILDER.defineInRange("stryker_m1296_cannon_damage", 39, 1, 10000000);
        STRYKER_M1296_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("stryker_m1296_cannon_explosion_damage", 8, 1, 10000000);
        STRYKER_M1296_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("stryker_m1296_cannon_explosion_radius", 2.5d, 1d, 10000000d);
        BUILDER.pop();

        BUILDER.push("terminator");
        TERMINATOR_ENERGY_COST = BUILDER.defineInRange("terminator_energy_cost", 80, 0, 2147483647);
        TERMINATOR_CANNON_DAMAGE = BUILDER.defineInRange("terminator_cannon_damage", 38, 1, 10000000);
        TERMINATOR_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("terminator_cannon_explosion_damage", 15, 1, 10000000);
        TERMINATOR_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("terminator_cannon_explosion_radius", 6d, 1d, 10000000d);
        TERMINATOR_CANNON_DESTROY = BUILDER.define("terminator_cannon_destroy", true);
        BUILDER.pop();

        BUILDER.push("pantsir_s1");
        PANTSIR_S1_ENERGY_COST = BUILDER.defineInRange("pantsir_s1_energy_cost", 80, 0, 2147483647);
        PANTSIR_S1_CANNON_DAMAGE = BUILDER.defineInRange("pantsir_s1_cannon_damage", 38, 1, 10000000);
        PANTSIR_S1_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("pantsir_s1_cannon_explosion_damage", 15, 1, 10000000);
        PANTSIR_S1_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("pantsir_s1_cannon_explosion_radius", 6d, 1d, 10000000d);
        PANTSIR_S1_CANNON_DESTROY = BUILDER.define("pantsir_s1_cannon_destroy", true);
        BUILDER.pop();
        
        BUILDER.push("sosna");
        SOSNA_ENERGY_COST = BUILDER.defineInRange("sosna_energy_cost", 70, 0, 2147483647);
        BUILDER.pop();

        BUILDER.push("t72");
        T72_ENERGY_COST = BUILDER.defineInRange("t72_energy_cost", 115, 0, 2147483647);
        T72_AP_CANNON_DAMAGE = BUILDER.defineInRange("t72_ap_cannon_damage", 800, 1, 10000000);
        T72_AP_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("t72_ap_cannon_explosion_damage", 75, 1, 10000000);
        T72_AP_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("t72_ap_cannon_explosion_radius", 6d, 1d, 10000000d);
        T72_CANNON_DESTROY = BUILDER.define("t72_cannon_destroy", true);
        BUILDER.pop();

        BUILDER.push("t72_he");
        T72_HE_CANNON_DAMAGE = BUILDER.defineInRange("t72_he_cannon_damage", 115, 1, 10000000);
        T72_HE_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("t72_he_cannon_explosion_damage", 185, 1, 10000000);
        T72_HE_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("t72_he_cannon_explosion_radius", 17d, 1d, 10000000d);
        BUILDER.pop();

        BUILDER.push("t90");
        T90_ENERGY_COST = BUILDER.defineInRange("t90_energy_cost", 105, 0, 2147483647);
        T90_AP_CANNON_DAMAGE = BUILDER.defineInRange("t90_ap_cannon_damage", 820, 1, 10000000);
        T90_AP_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("t90_ap_cannon_explosion_damage", 75, 1, 10000000);
        T90_AP_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("t90_ap_cannon_explosion_radius", 6d, 1d, 10000000d);
        T90_CANNON_DESTROY = BUILDER.define("t90_cannon_destroy", true);
        BUILDER.pop();

        BUILDER.push("t90_he");
        T90_HE_CANNON_DAMAGE = BUILDER.defineInRange("t90_he_cannon_damage", 125, 1, 10000000);
        T90_HE_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("t90_he_cannon_explosion_damage", 185, 1, 10000000);
        T90_HE_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("t90_he_cannon_explosion_radius", 17d, 1d, 10000000d);
        BUILDER.pop();

        BUILDER.push("m1a2");
        M1A2_ENERGY_COST = BUILDER.defineInRange("m1a2_energy_cost", 105, 0, 2147483647);
        M1A2_AP_CANNON_DAMAGE = BUILDER.defineInRange("m1a2_ap_cannon_damage", 825, 1, 10000000);
        M1A2_AP_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("m1a2_ap_cannon_explosion_damage", 70, 1, 10000000);
        M1A2_AP_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("m1a2_ap_cannon_explosion_radius", 5d, 1d, 10000000d);
        M1A2_CANNON_DESTROY = BUILDER.define("m1a2_cannon_destroy", true);
        BUILDER.pop();

        BUILDER.push("m1a2_he");
        M1A2_HE_CANNON_DAMAGE = BUILDER.defineInRange("m1a2_he_cannon_damage", 150, 1, 10000000);
        M1A2_HE_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("m1a2_he_cannon_explosion_damage", 190, 1, 10000000);
        M1A2_HE_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("m1a2_he_cannon_explosion_radius", 17d, 1d, 10000000d);;
        BUILDER.pop();

        BUILDER.push("challenger");
        CHALLENGER_ENERGY_COST = BUILDER.defineInRange("challenger_energy_cost", 115, 0, 2147483647);
        CHALLENGER_AP_CANNON_DAMAGE = BUILDER.defineInRange("challenger_ap_cannon_damage", 800, 1, 10000000);
        CHALLENGER_AP_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("challenger_ap_cannon_explosion_damage", 69, 1, 10000000);
        CHALLENGER_AP_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("challenger_ap_cannon_explosion_radius", 3d, 1d, 10000000d);
        CHALLENGER_CANNON_DESTROY = BUILDER.define("challenger_cannon_destroy", true);
        BUILDER.pop();

        BUILDER.push("challenger_he");
        CHALLENGER_HE_CANNON_DAMAGE = BUILDER.defineInRange("challenger_he_cannon_damage", 140, 1, 10000000);
        CHALLENGER_HE_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("challenger_he_cannon_explosion_damage", 185, 1, 10000000);
        CHALLENGER_HE_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("challenger_he_cannon_explosion_radius", 17d, 1d, 10000000d);
        BUILDER.pop();

        BUILDER.push("a72");
        WHEEL_ENERGY_COST = BUILDER.defineInRange("wheel_energy_cost", 72, 0, 2147483647);
        A72_CANNON_DAMAGE = BUILDER.defineInRange("a72_cannon_damage", 45, 1, 10000000);
        A72_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("a72_cannon_explosion_damage", 6, 1, 10000000);
        A72_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("a72_cannon_explosion_radius", 2d, 1d, 10000000d);
        BUILDER.pop();

        // --- Воздушная техника ---
        BUILDER.push("cobra");
        COBRA_CANNON_DAMAGE = BUILDER.defineInRange("cobra_cannon_damage", 22, 1, 10000000);
        COBRA_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("cobra_cannon_explosion_damage", 12, 1, 10000000);
        COBRA_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("cobra_cannon_explosion_radius", 6d, 1, 10000000);
        COBRA_ROCKET_DAMAGE = BUILDER.defineInRange("cobra_rocket_damage", 35, 1, 10000000);
        COBRA_ROCKET_EXPLOSION_DAMAGE = BUILDER.defineInRange("cobra_rocket_explosion_damage", 22, 1, 10000000);
        COBRA_ROCKET_EXPLOSION_RADIUS = BUILDER.defineInRange("cobra_rocket_explosion_radius", 3, 1, 10000000);
        COBRA_CANNON_DESTROY = BUILDER.define("cobra_cannon_destroy", true);
        BUILDER.pop();

        BUILDER.push("blackhawk");
        BLACKHAWK_MAX_ENERGY_COST = BUILDER.defineInRange("blackhawk_max_energy_cost", 128, 0, 2147483647);
        BLACKHAWK_MIN_ENERGY_COST = BUILDER.defineInRange("blackhawk_min_energy_cost", 64, 0, 2147483647);
        BLACKHAWK_ROCKET_DAMAGE = BUILDER.defineInRange("blackhawk_rocket_damage", 40, 1, 10000000);
        BLACKHAWK_ROCKET_EXPLOSION_DAMAGE = BUILDER.defineInRange("blackhawk_rocket_explosion_damage", 25, 1, 10000000);
        BLACKHAWK_ROCKET_EXPLOSION_RADIUS = BUILDER.defineInRange("blackhawk_rocket_explosion_radius", 3, 1, 10000000);
        BLACKHAWK_CANNON_DESTROY = BUILDER.define("blackhawk_cannon_destroy", true);
        BUILDER.pop();

        BUILDER.push("mi_24");
        MI_24_MAX_ENERGY_COST = BUILDER.defineInRange("mi_24_max_energy_cost", 128, 0, 2147483647);
        MI_24_MIN_ENERGY_COST = BUILDER.defineInRange("mi_24_min_energy_cost", 64, 0, 2147483647);
        MI_24_CANNON_DAMAGE = BUILDER.defineInRange("mi_24_cannon_damage", 15, 1, 10000000);
        MI_24_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("mi_24_cannon_explosion_damage", 13, 1, 10000000);
        MI_24_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("mi_24_cannon_explosion_radius", 4d, 1, 10000000);
        MI_24_ROCKET_DAMAGE = BUILDER.defineInRange("mi_24_rocket_damage", 40, 1, 10000000);
        MI_24_ROCKET_EXPLOSION_DAMAGE = BUILDER.defineInRange("mi_24_rocket_explosion_damage", 25, 1, 10000000);
        MI_24_ROCKET_EXPLOSION_RADIUS = BUILDER.defineInRange("mi_24_rocket_explosion_radius", 3, 1, 10000000);
        MI_24_CANNON_DESTROY = BUILDER.define("mi_24_cannon_destroy", true);
        BUILDER.pop();

        BUILDER.push("mi_28");
        MI_28_MAX_ENERGY_COST = BUILDER.defineInRange("mi_28_max_energy_cost", 138, 0, 2147483647);
        MI_28_MIN_ENERGY_COST = BUILDER.defineInRange("mi_28_min_energy_cost", 64, 0, 2147483647);
        MI_28_CANNON_DAMAGE = BUILDER.defineInRange("mi_28_cannon_damage", 25, 1, 10000000);
        MI_28_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("mi_28_cannon_explosion_damage", 15, 1, 10000000);
        MI_28_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("mi_28_cannon_explosion_radius", 5d, 1, 10000000);
        MI_28_ROCKET_DAMAGE = BUILDER.defineInRange("mi_28_rocket_damage", 35, 1, 10000000);
        MI_28_ROCKET_EXPLOSION_DAMAGE = BUILDER.defineInRange("mi_28_rocket_explosion_damage", 33, 1, 10000000);
        MI_28_ROCKET_EXPLOSION_RADIUS = BUILDER.defineInRange("mi_28_rocket_explosion_radius", 5, 1, 10000000);
        MI_28_MEDIUM_ROCKET_DAMAGE = BUILDER.defineInRange("mi_28_medium_rocket_damage", 55, 1, 10000000);
        MI_28_MEDIUM_ROCKET_EXPLOSION_DAMAGE = BUILDER.defineInRange("mi_28_medium_rocket_explosion_damage", 40, 1, 10000000);
        MI_28_MEDIUM_ROCKET_EXPLOSION_RADIUS = BUILDER.defineInRange("mi_28_medium_rocket_explosion_radius", 8, 1, 10000000);
        MI_28_CANNON_DESTROY = BUILDER.define("mi_28_cannon_destroy", true);
        BUILDER.pop();

        // --- Орудие ---
        BUILDER.push("m2_gun");
        M2_GUN_DAMAGE = BUILDER.defineInRange("m2_gun_damage", 33, 1, 10000000);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
