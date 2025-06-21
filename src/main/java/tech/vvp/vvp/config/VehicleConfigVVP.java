package tech.vvp.vvp.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class VehicleConfigVVP {
        public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        public static final ForgeConfigSpec SPEC;
    
        public static final ForgeConfigSpec.IntValue TYPHOON_MAX_ENERGY;
        public static final ForgeConfigSpec.IntValue TYPHOON_HP;
        public static final ForgeConfigSpec.IntValue TYPHOON_ENERGY_COST;
        public static final ForgeConfigSpec.DoubleValue TYPHOON_ENERGY_MULTIPLIER;
        public static final ForgeConfigSpec.IntValue TYPHOON_SHOOT_COST;

        public static ForgeConfigSpec.IntValue MI_24_MAX_ENERGY;
        public static ForgeConfigSpec.IntValue MI_24_MAX_ENERGY_COST;
        public static ForgeConfigSpec.IntValue MI_24_MIN_ENERGY_COST;
        public static ForgeConfigSpec.IntValue MI_24_HP;

        public static ForgeConfigSpec.IntValue MI_24_CANNON_DAMAGE;
        public static ForgeConfigSpec.IntValue MI_24_CANNON_EXPLOSION_DAMAGE;
        public static ForgeConfigSpec.DoubleValue MI_24_CANNON_EXPLOSION_RADIUS;
        public static ForgeConfigSpec.IntValue MI_24_ROCKET_DAMAGE;
        public static ForgeConfigSpec.IntValue MI_24_ROCKET_EXPLOSION_DAMAGE;
        public static ForgeConfigSpec.IntValue MI_24_ROCKET_EXPLOSION_RADIUS;
        public static ForgeConfigSpec.BooleanValue MI_24_CANNON_DESTROY;

        public static ForgeConfigSpec.IntValue JAGUAR_MAX_ENERGY;
        public static ForgeConfigSpec.IntValue JAGUAR_HP;
        public static ForgeConfigSpec.IntValue JAGUAR_MAX_ENERGY_COST;
        public static ForgeConfigSpec.IntValue JAGUAR_MIN_ENERGY_COST;

        public static ForgeConfigSpec.IntValue STRYKER_M1128_CANNON_DAMAGE;
        public static ForgeConfigSpec.IntValue STRYKER_M1128_CANNON_EXPLOSION_DAMAGE;
        public static ForgeConfigSpec.DoubleValue STRYKER_M1128_CANNON_EXPLOSION_RADIUS;

        static {
            BUILDER.push("Vehicle Configs for VVP");
    
            TYPHOON_MAX_ENERGY = BUILDER
                    .comment("Maximum energy for Typhoon vehicle")
                    .defineInRange("typhoon_max_energy", 6500, 1, 10000000);
    
            TYPHOON_HP = BUILDER
                    .comment("Health points for Typhoon vehicle")
                    .defineInRange("typhoon_hp", 250, 1, 10000000);
    
            TYPHOON_ENERGY_COST = BUILDER
                    .comment("Energy cost for Typhoon vehicle")
                    .defineInRange("typhoon_energy_cost", 64, 0, 2147483647);
    
            TYPHOON_ENERGY_MULTIPLIER = BUILDER
                    .comment("Energy multiplier for Typhoon vehicle")
                    .defineInRange("typhoon_energy_multiplier", 0.1, 0.01, 1.0);
    
            TYPHOON_SHOOT_COST = BUILDER
                    .comment("Shooting cost for Typhoon vehicle")
                    .defineInRange("typhoon_shoot_cost", 10, 1, 1000);            

            BUILDER.comment("Deprecated, use datapack to change this value instead");
            MI_24_HP = BUILDER.defineInRange("mi_24_hp", 250, 1, 10000000);
            
            BUILDER.comment("The max energy cost of MI-24 per tick");
            MI_24_MAX_ENERGY_COST = BUILDER.defineInRange("mi_24_max_energy_cost", 228, 0, 2147483647);
            
            BUILDER.comment("Deprecated, use datapack to change this value instead");
            MI_24_MAX_ENERGY = BUILDER.defineInRange("mi_24_max_energy", 5000000, 0, 2147483647);

            BUILDER.comment("The min energy cost of MI-24 per tick");
            MI_24_MIN_ENERGY_COST = BUILDER.defineInRange("mi_24_min_energy_cost", 64, 0, 2147483647);

            BUILDER.comment("The cannon damage of MI-24");
            MI_24_CANNON_DAMAGE = BUILDER.defineInRange("mi_24_cannon_damage", 25, 1, 10000000);
    
            BUILDER.comment("The cannon explosion damage of MI-24");
            MI_24_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("mi_24_cannon_explosion_damage", 13, 1, 10000000);
    
            BUILDER.comment("The cannon explosion damage of MI-24");
            MI_24_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("mi_24_cannon_explosion_damage", 4d, 1, 10000000);
    
            BUILDER.comment("The rocket damage of MI-24");
            MI_24_ROCKET_DAMAGE = BUILDER.defineInRange("mi_24_rocket_damage", 80, 1, 10000000);
    
            BUILDER.comment("The rocket explosion damage of MI-24");
            MI_24_ROCKET_EXPLOSION_DAMAGE = BUILDER.defineInRange("mi_24_rocket_explosion_damage", 40, 1, 10000000);
    
            BUILDER.comment("The rocket explosion radius of MI-24");
            MI_24_ROCKET_EXPLOSION_RADIUS = BUILDER.defineInRange("mi_24_rocket_explosion_radius", 5, 1, 10000000);

            BUILDER.comment("The cannon damage of Stryker M1128");
            STRYKER_M1128_CANNON_DAMAGE = BUILDER.defineInRange("stryker_m1128_cannon_damage", 550, 1, 10000000);
    
            BUILDER.comment("The cannon explosion damage of Stryker M1128");
            STRYKER_M1128_CANNON_EXPLOSION_DAMAGE = BUILDER.defineInRange("stryker_m1128_cannon_explosion_damage", 128, 1, 10000000);
    
            BUILDER.comment("The cannon explosion radius of Stryker M1128");
            STRYKER_M1128_CANNON_EXPLOSION_RADIUS = BUILDER.defineInRange("stryker_m1128_cannon_explosion_radius", 7d, 1d, 10000000d);

            BUILDER.comment("Whether to destroy the block when cannon of MI-24 hits a block");
            MI_24_CANNON_DESTROY = BUILDER.define("mi_24_cannon_destroy", true);
    
            BUILDER.pop();
            SPEC = BUILDER.build();
        }
    }