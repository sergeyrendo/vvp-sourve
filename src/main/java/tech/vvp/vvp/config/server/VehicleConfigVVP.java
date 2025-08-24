package tech.vvp.vvp.config.server;

import net.minecraftforge.common.ForgeConfigSpec;

public class VehicleConfigVVP {
        public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        public static final ForgeConfigSpec SPEC;


        public static ForgeConfigSpec.IntValue MI_24_MAX_ENERGY_COST;
        public static ForgeConfigSpec.IntValue MI_24_MIN_ENERGY_COST;
        public static ForgeConfigSpec.IntValue MI_24_CANNON_DAMAGE;
        public static ForgeConfigSpec.IntValue MI_24_CANNON_EXPLOSION_DAMAGE;
        public static ForgeConfigSpec.DoubleValue MI_24_CANNON_EXPLOSION_RADIUS;
        public static ForgeConfigSpec.IntValue MI_24_ROCKET_DAMAGE;
        public static ForgeConfigSpec.IntValue MI_24_ROCKET_EXPLOSION_DAMAGE;
        public static ForgeConfigSpec.IntValue MI_24_ROCKET_EXPLOSION_RADIUS;
        public static ForgeConfigSpec.BooleanValue MI_24_CANNON_DESTROY;


        public static ForgeConfigSpec.IntValue STRYKER_M1128_CANNON_DAMAGE;
        public static ForgeConfigSpec.IntValue STRYKER_M1128_CANNON_EXPLOSION_DAMAGE;
        public static ForgeConfigSpec.DoubleValue STRYKER_M1128_CANNON_EXPLOSION_RADIUS;

        public static ForgeConfigSpec.IntValue BRADLEY_ENERGY_COST;
        public static ForgeConfigSpec.IntValue BRADLEY_CANNON_DAMAGE;
        public static ForgeConfigSpec.IntValue BRADLEY_CANNON_EXPLOSION_DAMAGE;
        public static ForgeConfigSpec.DoubleValue BRADLEY_CANNON_EXPLOSION_RADIUS;

        public static ForgeConfigSpec.IntValue WHEEL_ENERGY_COST;
        public static ForgeConfigSpec.IntValue A72_CANNON_DAMAGE;
        public static ForgeConfigSpec.IntValue A72_CANNON_EXPLOSION_DAMAGE;
        public static ForgeConfigSpec.DoubleValue A72_CANNON_EXPLOSION_RADIUS;


        public static ForgeConfigSpec.IntValue COBRA_CANNON_DAMAGE;
        public static ForgeConfigSpec.IntValue COBRA_CANNON_EXPLOSION_DAMAGE;
        public static ForgeConfigSpec.DoubleValue COBRA_CANNON_EXPLOSION_RADIUS;
        public static ForgeConfigSpec.IntValue COBRA_ROCKET_DAMAGE;
        public static ForgeConfigSpec.IntValue COBRA_ROCKET_EXPLOSION_DAMAGE;
        public static ForgeConfigSpec.IntValue COBRA_ROCKET_EXPLOSION_RADIUS;
        public static ForgeConfigSpec.BooleanValue COBRA_CANNON_DESTROY;

        public static ForgeConfigSpec.IntValue M1A2_ENERGY_COST;
        public static ForgeConfigSpec.IntValue M1A2_CANNON_DAMAGE;
        public static ForgeConfigSpec.IntValue M1A2_CANNON_EXPLOSION_DAMAGE;
        public static ForgeConfigSpec.DoubleValue M1A2_CANNON_EXPLOSION_RADIUS;
        public static ForgeConfigSpec.BooleanValue M1A2_CANNON_DESTROY;

        public static ForgeConfigSpec.IntValue TERMINATOR_ENERGY_COST;
        public static ForgeConfigSpec.IntValue TERMINATOR_CANNON_DAMAGE;
        public static ForgeConfigSpec.IntValue TERMINATOR_CANNON_EXPLOSION_DAMAGE;
        public static ForgeConfigSpec.DoubleValue TERMINATOR_CANNON_EXPLOSION_RADIUS;
        public static ForgeConfigSpec.BooleanValue TERMINATOR_CANNON_DESTROY;

        public static ForgeConfigSpec.IntValue STRYKER_M1296_CANNON_DAMAGE;
        public static ForgeConfigSpec.IntValue STRYKER_M1296_ENERGY_COST;
        public static ForgeConfigSpec.IntValue STRYKER_M1296_CANNON_EXPLOSION_DAMAGE;
        public static ForgeConfigSpec.DoubleValue STRYKER_M1296_CANNON_EXPLOSION_RADIUS;

        public static ForgeConfigSpec.IntValue M60_ENERGY_COST;
        public static ForgeConfigSpec.IntValue M60_CANNON_DAMAGE;
        public static ForgeConfigSpec.IntValue M60_CANNON_EXPLOSION_DAMAGE;
        public static ForgeConfigSpec.DoubleValue M60_CANNON_EXPLOSION_RADIUS;
        public static ForgeConfigSpec.BooleanValue M60_CANNON_DESTROY;

        public static ForgeConfigSpec.IntValue BLACKHAWK_MAX_ENERGY_COST;
        public static ForgeConfigSpec.IntValue BLACKHAWK_MIN_ENERGY_COST;
        public static ForgeConfigSpec.IntValue BLACKHAWK_ROCKET_DAMAGE;
        public static ForgeConfigSpec.IntValue BLACKHAWK_ROCKET_EXPLOSION_DAMAGE;
        public static ForgeConfigSpec.IntValue BLACKHAWK_ROCKET_EXPLOSION_RADIUS;
        public static ForgeConfigSpec.BooleanValue BLACKHAWK_CANNON_DESTROY;

        public static ForgeConfigSpec.IntValue HUMVEE_ENERGY_COST;

        public static ForgeConfigSpec.IntValue FMTV_ENERGY_COST;

        static {

            BUILDER.push("mi_24");
            
            BUILDER.comment("The max energy cost of MI-24 per tick");
            MI_24_MAX_ENERGY_COST = BUILDER.defineInRange("mi_24_max_energy_cost", 128, 0, 2147483647);

            BUILDER.comment("The min energy cost of MI-24 per tick");
            MI_24_MIN_ENERGY_COST = BUILDER.defineInRange("mi_24_min_energy_cost", 64, 0, 2147483647);

            BUILDER.comment("The cannon damage of MI-24");
            MI_24_CANNON_DAMAGE = BUILDER.defineInRange("mi_24_cannon_damage", 15, 1, 10000000);
    
            BUILDER.comment("The cannon explosion damage of MI-24");
            MI_24_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("mi_24_cannon_explosion_damage", 13, 1, 10000000);
    
            BUILDER.comment("The cannon explosion damage of MI-24");
            MI_24_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("mi_24_cannon_explosion_damage", 4d, 1, 10000000);
    
            BUILDER.comment("The rocket damage of MI-24");
            MI_24_ROCKET_DAMAGE = BUILDER.defineInRange("mi_24_rocket_damage", 40, 1, 10000000);
    
            BUILDER.comment("The rocket explosion damage of MI-24");
            MI_24_ROCKET_EXPLOSION_DAMAGE = BUILDER.defineInRange("mi_24_rocket_explosion_damage", 25, 1, 10000000);
    
            BUILDER.comment("The rocket explosion radius of MI-24");
            MI_24_ROCKET_EXPLOSION_RADIUS = BUILDER.defineInRange("mi_24_rocket_explosion_radius", 3, 1, 10000000);

            BUILDER.comment("Whether to destroy the block when cannon of MI-24 hits a block");
            MI_24_CANNON_DESTROY = BUILDER.define("mi_24_cannon_destroy", true);

            BUILDER.pop();

            BUILDER.push("stryker_m1128");

            BUILDER.comment("The cannon damage of Stryker M1128");
            STRYKER_M1128_CANNON_DAMAGE = BUILDER.defineInRange("stryker_m1128_cannon_damage", 225, 1, 10000000);
    
            BUILDER.comment("The cannon explosion damage of Stryker M1128");
            STRYKER_M1128_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("stryker_m1128_cannon_explosion_damage", 32, 1, 10000000);
    
            BUILDER.comment("The cannon explosion radius of Stryker M1128");
            STRYKER_M1128_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("stryker_m1128_cannon_explosion_radius", 4d, 1d, 10000000d);

            BUILDER.pop();

            BUILDER.push("bradley");

            BUILDER.comment("The energy cost of Bradley per tick");
            BRADLEY_ENERGY_COST = BUILDER.defineInRange("bradley_energy_cost", 75, 0, 2147483647);

            BUILDER.comment("The cannon damage of Bradley");
            BRADLEY_CANNON_DAMAGE = BUILDER.defineInRange("bradley_cannon_damage", 47, 1, 10000000);

            BUILDER.comment("The cannon explosion damage of Bradley");
            BRADLEY_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("bradley_cannon_explosion_damage", 8, 1, 10000000);

            BUILDER.comment("The cannon explosion radius of Bradley");
            BRADLEY_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("bradley_cannon_explosion_radius", 2d, 1d, 10000000d);

            BUILDER.pop();

            BUILDER.push("a72");


            BUILDER.comment("The energy cost of BTR-4 and BTR-80A per tick");
            WHEEL_ENERGY_COST = BUILDER.defineInRange("wheel_energy_cost", 72, 0, 2147483647);

            BUILDER.pop();

            BUILDER.push("a72");

            BUILDER.comment("The cannon damage of 2A72 weapon (BTR-4, BTR-80A)");
            A72_CANNON_DAMAGE = BUILDER.defineInRange("a47_cannon_damage", 45, 1, 10000000);

            BUILDER.comment("The cannon explosion damage of 2A72 weapon (BTR-4, BTR-80A)");
            A72_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("a47_cannon_explosion_damage", 6, 1, 10000000);

            BUILDER.comment("The cannon explosion radius of 2A72 weapon (BTR-4, BTR-80A)");
            A72_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("a47_cannon_explosion_radius", 2d, 1d, 10000000d);

            BUILDER.pop();


            BUILDER.push("cobra");


            BUILDER.comment("The cannon damage of Cobra");
            COBRA_CANNON_DAMAGE = BUILDER.defineInRange("cobra_cannon_damage", 22, 1, 10000000);

            BUILDER.comment("The cannon explosion damage of Cobra");
            COBRA_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("cobra_cannon_explosion_damage", 12, 1, 10000000);

            BUILDER.comment("The cannon explosion damage of Cobra");
            COBRA_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("cobra_cannon_explosion_damage", 6d, 1, 10000000);

            BUILDER.comment("The rocket damage of Cobra");
            COBRA_ROCKET_DAMAGE = BUILDER.defineInRange("cobra_rocket_damage", 35, 1, 10000000);

            BUILDER.comment("The rocket explosion damage of Cobra");
            COBRA_ROCKET_EXPLOSION_DAMAGE = BUILDER.defineInRange("cobra_rocket_explosion_damage", 22, 1, 10000000);

            BUILDER.comment("The rocket explosion radius of Cobra");
            COBRA_ROCKET_EXPLOSION_RADIUS = BUILDER.defineInRange("cobra_rocket_explosion_radius", 3, 1, 10000000);

            BUILDER.comment("Whether to destroy the block when cannon of Cobra hits a block");
            COBRA_CANNON_DESTROY = BUILDER.define("cobra_cannon_destroy", true);

            BUILDER.pop();

            // ⬇️ В СТАТИЧЕСКОМ БЛОКЕ (перед SPEC = BUILDER.build();) ДОБАВЬ НОВЫЕ СЕКЦИИ

            BUILDER.push("m1a2");

            BUILDER.comment("The energy cost of M1A2 per tick");
            M1A2_ENERGY_COST = BUILDER.defineInRange("m1a2_energy_cost", 96, 0, 2147483647);

            BUILDER.comment("The cannon damage of M1A2");
            M1A2_CANNON_DAMAGE = BUILDER.defineInRange("m1a2_cannon_damage", 395, 1, 10000000);

            BUILDER.comment("The cannon explosion damage of M1A2");
            M1A2_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("m1a2_cannon_explosion_damage", 70, 1, 10000000);

            BUILDER.comment("The cannon explosion radius of M1A2");
            M1A2_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("m1a2_cannon_explosion_radius", 5d, 1d, 10000000d);

            BUILDER.comment("Whether to destroy the block when cannon of M1A2 hits a block");
            M1A2_CANNON_DESTROY = BUILDER.define("m1a2_cannon_destroy", true);

            BUILDER.pop();

            // ⬇️ В СТАТИЧЕСКОМ БЛОКЕ (перед SPEC = BUILDER.build();) ДОБАВЬ НОВЫЕ СЕКЦИИ

// -------- TERMINATOR --------
            BUILDER.push("terminator");

            BUILDER.comment("The energy cost of Terminator per tick");
            TERMINATOR_ENERGY_COST = BUILDER.defineInRange("terminator_energy_cost", 80, 0, 2147483647);

            BUILDER.comment("The cannon damage of Terminator");
            TERMINATOR_CANNON_DAMAGE = BUILDER.defineInRange("terminator_cannon_damage", 38, 1, 10000000);

            BUILDER.comment("The cannon explosion damage of Terminator");
            TERMINATOR_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("terminator_cannon_explosion_damage", 8, 1, 10000000);

            BUILDER.comment("The cannon explosion radius of Terminator");
            TERMINATOR_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("terminator_cannon_explosion_radius", 2.5d, 1d, 10000000d);

            BUILDER.comment("Whether to destroy the block when Terminator's cannon hits a block");
            TERMINATOR_CANNON_DESTROY = BUILDER.define("terminator_cannon_destroy", true);

            BUILDER.pop();


// -------- STRYKER M1296 --------
            BUILDER.push("stryker_m1296");

            BUILDER.comment("The energy cost of Stryler M1296 per tick");
            STRYKER_M1296_ENERGY_COST = BUILDER.defineInRange("stryker_m1296_energy_cost", 66, 0, 2147483647);

            BUILDER.comment("The cannon damage of Stryker M1296");
            STRYKER_M1296_CANNON_DAMAGE = BUILDER.defineInRange("stryker_m1296_cannon_damage", 39, 1, 10000000);

            BUILDER.comment("The cannon explosion damage of Stryker M1296");
            STRYKER_M1296_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("stryker_m1296_cannon_explosion_damage", 8, 1, 10000000);

            BUILDER.comment("The cannon explosion radius of Stryker M1296");
            STRYKER_M1296_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("stryker_m1296_cannon_explosion_radius", 2.5d, 1d, 10000000d);

            BUILDER.pop();



            BUILDER.push("m60");

            BUILDER.comment("The energy cost of M60 per tick");
            M60_ENERGY_COST = BUILDER.defineInRange("m60_energy_cost", 88, 0, 2147483647);

            BUILDER.comment("The cannon damage of M60");
            M60_CANNON_DAMAGE = BUILDER.defineInRange("m60_cannon_damage", 256, 1, 10000000);

            BUILDER.comment("The cannon explosion damage of M60");
            M60_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("m60_cannon_explosion_damage", 42, 1, 10000000);

            BUILDER.comment("The cannon explosion radius of M60");
            M60_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("m60_cannon_explosion_radius", 5d, 1d, 10000000d);

            BUILDER.comment("Whether to destroy the block when M60's cannon hits a block");
            M60_CANNON_DESTROY = BUILDER.define("m60_cannon_destroy", true);

            BUILDER.pop();

            BUILDER.push("blackhawk");


            BUILDER.comment("The max energy cost of Blackhawk per tick");
            BLACKHAWK_MAX_ENERGY_COST = BUILDER.defineInRange("blackhawk_max_energy_cost", 128, 0, 2147483647);

            BUILDER.comment("The min energy cost of Blackhawk per tick");
            BLACKHAWK_MIN_ENERGY_COST = BUILDER.defineInRange("blackhawk_min_energy_cost", 64, 0, 2147483647);

            BUILDER.comment("The rocket damage of Blackhawk");
            BLACKHAWK_ROCKET_DAMAGE = BUILDER.defineInRange("blackhawk_rocket_damage", 40, 1, 10000000);

            BUILDER.comment("The rocket explosion damage of Blackhawk");
            BLACKHAWK_ROCKET_EXPLOSION_DAMAGE = BUILDER.defineInRange("blackhawk_rocket_explosion_damage", 25, 1, 10000000);

            BUILDER.comment("The rocket explosion radius of Blackhawk");
            BLACKHAWK_ROCKET_EXPLOSION_RADIUS = BUILDER.defineInRange("blackhawk_rocket_explosion_radius", 3, 1, 10000000);

            BUILDER.comment("Whether to destroy the block when cannon of Blackhawk hits a block");
            BLACKHAWK_CANNON_DESTROY = BUILDER.define("blackhawk_cannon_destroy", true);

            BUILDER.pop();

            BUILDER.push("humvee");

            BUILDER.comment("The energy cost of Humvee M997 and Humvee MK19 per tick");
            HUMVEE_ENERGY_COST = BUILDER.defineInRange("humvee_energy_cost", 60, 0, 2147483647);

            BUILDER.pop();

            BUILDER.push("fmtv");

            BUILDER.comment("The energy cost of FMTV per tick");
            FMTV_ENERGY_COST = BUILDER.defineInRange("fmtv_energy_cost", 69, 0, 2147483647);

            BUILDER.pop();

            SPEC = BUILDER.build();
        }
    }