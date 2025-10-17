package tech.vvp.vvp.config.server;

import net.minecraftforge.common.ForgeConfigSpec;

public class VehicleConfigVVP {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.IntValue WHEEL_ENERGY_COST;
    public static ForgeConfigSpec.IntValue A72_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue A72_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue A72_CANNON_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue BLACKHAWK_MAX_ENERGY_COST;
    public static ForgeConfigSpec.IntValue BLACKHAWK_MIN_ENERGY_COST;
    public static ForgeConfigSpec.IntValue BLACKHAWK_ROCKET_DAMAGE;
    public static ForgeConfigSpec.IntValue BLACKHAWK_ROCKET_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue BLACKHAWK_ROCKET_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.BooleanValue BLACKHAWK_CANNON_DESTROY;

    public static ForgeConfigSpec.IntValue BRADLEY_ENERGY_COST;
    public static ForgeConfigSpec.IntValue BRADLEY_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue BRADLEY_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue BRADLEY_CANNON_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue COBRA_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue COBRA_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue COBRA_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.IntValue COBRA_ROCKET_DAMAGE;
    public static ForgeConfigSpec.IntValue COBRA_ROCKET_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue COBRA_ROCKET_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.BooleanValue COBRA_CANNON_DESTROY;

    public static ForgeConfigSpec.IntValue FMTV_ENERGY_COST;
    public static ForgeConfigSpec.IntValue HUMVEE_ENERGY_COST;

    public static ForgeConfigSpec.IntValue M1A2_ENERGY_COST;
    public static ForgeConfigSpec.IntValue M1A2_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue M1A2_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue M1A2_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.BooleanValue M1A2_CANNON_DESTROY;

    public static ForgeConfigSpec.IntValue M2_GUN_DAMAGE;

    public static ForgeConfigSpec.IntValue M60_ENERGY_COST;
    public static ForgeConfigSpec.IntValue M60_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue M60_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue M60_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.BooleanValue M60_CANNON_DESTROY;

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

    public static ForgeConfigSpec.IntValue T90_ENERGY_COST;
    public static ForgeConfigSpec.IntValue T90_AP_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue T90_AP_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue T90_AP_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.BooleanValue T90_CANNON_DESTROY;

    public static ForgeConfigSpec.IntValue T90_HE_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue T90_HE_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue T90_HE_CANNON_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue TERMINATOR_ENERGY_COST;
    public static ForgeConfigSpec.IntValue TERMINATOR_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue TERMINATOR_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue TERMINATOR_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.BooleanValue TERMINATOR_CANNON_DESTROY;

    static {
        // A72
        BUILDER.push("a72");
        WHEEL_ENERGY_COST = BUILDER.defineInRange("wheel_energy_cost", 72, 0, 2147483647);
        A72_CANNON_DAMAGE = BUILDER.defineInRange("a72_cannon_damage", 45, 1, 10000000);
        A72_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("a72_cannon_explosion_damage", 6, 1, 10000000);
        A72_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("a72_cannon_explosion_radius", 2d, 1d, 10000000d);
        BUILDER.pop();

        // BLACKHAWK
        BUILDER.push("blackhawk");
        BLACKHAWK_MAX_ENERGY_COST = BUILDER.defineInRange("blackhawk_max_energy_cost", 128, 0, 2147483647);
        BLACKHAWK_MIN_ENERGY_COST = BUILDER.defineInRange("blackhawk_min_energy_cost", 64, 0, 2147483647);
        BLACKHAWK_ROCKET_DAMAGE = BUILDER.defineInRange("blackhawk_rocket_damage", 40, 1, 10000000);
        BLACKHAWK_ROCKET_EXPLOSION_DAMAGE = BUILDER.defineInRange("blackhawk_rocket_explosion_damage", 25, 1, 10000000);
        BLACKHAWK_ROCKET_EXPLOSION_RADIUS = BUILDER.defineInRange("blackhawk_rocket_explosion_radius", 3, 1, 10000000);
        BLACKHAWK_CANNON_DESTROY = BUILDER.define("blackhawk_cannon_destroy", true);
        BUILDER.pop();

        // BRADLEY
        BUILDER.push("bradley");
        BRADLEY_ENERGY_COST = BUILDER.defineInRange("bradley_energy_cost", 75, 0, 2147483647);
        BRADLEY_CANNON_DAMAGE = BUILDER.defineInRange("bradley_cannon_damage", 47, 1, 10000000);
        BRADLEY_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("bradley_cannon_explosion_damage", 8, 1, 10000000);
        BRADLEY_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("bradley_cannon_explosion_radius", 2d, 1d, 10000000d);
        BUILDER.pop();

        // COBRA
        BUILDER.push("cobra");
        COBRA_CANNON_DAMAGE = BUILDER.defineInRange("cobra_cannon_damage", 22, 1, 10000000);
        COBRA_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("cobra_cannon_explosion_damage", 12, 1, 10000000);
        COBRA_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("cobra_cannon_explosion_radius", 6d, 1, 10000000);
        COBRA_ROCKET_DAMAGE = BUILDER.defineInRange("cobra_rocket_damage", 35, 1, 10000000);
        COBRA_ROCKET_EXPLOSION_DAMAGE = BUILDER.defineInRange("cobra_rocket_explosion_damage", 22, 1, 10000000);
        COBRA_ROCKET_EXPLOSION_RADIUS = BUILDER.defineInRange("cobra_rocket_explosion_radius", 3, 1, 10000000);
        COBRA_CANNON_DESTROY = BUILDER.define("cobra_cannon_destroy", true);
        BUILDER.pop();

        // FMTV
        BUILDER.push("fmtv");
        FMTV_ENERGY_COST = BUILDER.defineInRange("fmtv_energy_cost", 70, 0, 2147483647);
        BUILDER.pop();

        // HUMVEE
        BUILDER.push("humvee");
        HUMVEE_ENERGY_COST = BUILDER.defineInRange("humvee_energy_cost", 68, 0, 2147483647);
        BUILDER.pop();

        // M1A2
        BUILDER.push("m1a2");
        M1A2_ENERGY_COST = BUILDER.defineInRange("m1a2_energy_cost", 96, 0, 2147483647);
        M1A2_CANNON_DAMAGE = BUILDER.defineInRange("m1a2_cannon_damage", 415, 1, 10000000);
        M1A2_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("m1a2_cannon_explosion_damage", 70, 1, 10000000);
        M1A2_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("m1a2_cannon_explosion_radius", 5d, 1d, 10000000d);
        M1A2_CANNON_DESTROY = BUILDER.define("m1a2_cannon_destroy", true);
        BUILDER.pop();

        // M2 Gun
        BUILDER.push("m2_gun");
        M2_GUN_DAMAGE = BUILDER.defineInRange("m2_gun_damage", 33, 1, 10000000);
        BUILDER.pop();

        // M60
        BUILDER.push("m60");
        M60_ENERGY_COST = BUILDER.defineInRange("m60_energy_cost", 88, 0, 2147483647);
        M60_CANNON_DAMAGE = BUILDER.defineInRange("m60_cannon_damage", 325, 1, 10000000);
        M60_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("m60_cannon_explosion_damage", 42, 1, 10000000);
        M60_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("m60_cannon_explosion_radius", 5d, 1d, 10000000d);
        M60_CANNON_DESTROY = BUILDER.define("m60_cannon_destroy", true);
        BUILDER.pop();

        // MI-24
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

        // MI-28
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

        // STRYKER M1128 AP
        BUILDER.push("stryker_m1128_ap");
        STRYKER_M1128_AP_CANNON_DAMAGE = BUILDER.defineInRange("stryker_m1128_ap_cannon_damage", 750, 1, 10000000);
        STRYKER_M1128_AP_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("stryker_m1128_ap_cannon_explosion_damage", 32, 1, 10000000);
        STRYKER_M1128_AP_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("stryker_m1128_ap_cannon_explosion_radius", 4d, 1d, 10000000d);
        BUILDER.pop();

        // STRYKER M1128 HE
        BUILDER.push("stryker_m1128_he");
        STRYKER_M1128_HE_CANNON_DAMAGE = BUILDER.defineInRange("stryker_m1128_he_cannon_damage", 100, 1, 10000000);
        STRYKER_M1128_HE_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("stryker_m1128_he_cannon_explosion_damage", 175, 1, 10000000);
        STRYKER_M1128_HE_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("stryker_m1128_he_cannon_explosion_radius", 13d, 1d, 10000000d);
        BUILDER.pop();

        // STRYKER M1296
        BUILDER.push("stryker_m1296");
        STRYKER_M1296_ENERGY_COST = BUILDER.defineInRange("stryker_m1296_energy_cost", 66, 0, 2147483647);
        STRYKER_M1296_CANNON_DAMAGE = BUILDER.defineInRange("stryker_m1296_cannon_damage", 39, 1, 10000000);
        STRYKER_M1296_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("stryker_m1296_cannon_explosion_damage", 8, 1, 10000000);
        STRYKER_M1296_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("stryker_m1296_cannon_explosion_radius", 2.5d, 1d, 10000000d);
        BUILDER.pop();

        // T90 AP
        BUILDER.push("t90_ap");
        T90_ENERGY_COST = BUILDER.defineInRange("t90_ap_energy_cost", 105, 0, 2147483647);
        T90_AP_CANNON_DAMAGE = BUILDER.defineInRange("t90_ap_cannon_damage", 450, 1, 10000000);
        T90_AP_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("t90_ap_cannon_explosion_damage", 75, 1, 10000000);
        T90_AP_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("t90_ap_cannon_explosion_radius", 6d, 1d, 10000000d);
        T90_CANNON_DESTROY = BUILDER.define("t90_ap_cannon_destroy", true);
        BUILDER.pop();

        // T90 HE
        BUILDER.push("t90_he");
        T90_HE_CANNON_DAMAGE = BUILDER.defineInRange("t90_he_cannon_damage", 110, 1, 10000000);
        T90_HE_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("t90_he_cannon_explosion_damage", 185, 1, 10000000);
        T90_HE_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("t90_he_cannon_explosion_radius", 17d, 1d, 10000000d);
        BUILDER.pop();

        // TERMINATOR
        BUILDER.push("terminator");
        TERMINATOR_ENERGY_COST = BUILDER.defineInRange("terminator_energy_cost", 80, 0, 2147483647);
        TERMINATOR_CANNON_DAMAGE = BUILDER.defineInRange("terminator_cannon_damage", 38, 1, 10000000);
        TERMINATOR_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("terminator_cannon_explosion_damage", 8, 1, 10000000);
        TERMINATOR_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("terminator_cannon_explosion_radius", 2.5d, 1d, 10000000d);
        TERMINATOR_CANNON_DESTROY = BUILDER.define("terminator_cannon_destroy", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}