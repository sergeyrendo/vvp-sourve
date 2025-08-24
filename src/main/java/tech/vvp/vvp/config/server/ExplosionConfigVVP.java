package tech.vvp.vvp.config.server;

import net.minecraftforge.common.ForgeConfigSpec;

public class ExplosionConfigVVP {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.IntValue TOW_MISSILE_DAMAGE;
    public static ForgeConfigSpec.IntValue TOW_MISSILE_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue TOW_MISSILE_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue BTR_4_MISSILE_DAMAGE;
    public static ForgeConfigSpec.IntValue BTR_4_MISSILE_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue BTR_4_MISSILE_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue TERMINATOR_MISSILE_DAMAGE;
    public static ForgeConfigSpec.IntValue TERMINATOR_MISSILE_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue TERMINATOR_MISSILE_EXPLOSION_RADIUS;

    static {
        BUILDER.push("tow_missile");

        BUILDER.comment("The damage of TOW missile");
        TOW_MISSILE_DAMAGE = BUILDER.defineInRange("tow_missile_damage", 325, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion damage of TOW missile");
        TOW_MISSILE_EXPLOSION_DAMAGE = BUILDER.defineInRange("tow_missile_explosion_damage", 30, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion radius of TOW missile");
        TOW_MISSILE_EXPLOSION_RADIUS = BUILDER.defineInRange("tow_missile_explosion_radius", 4, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("btr_4_missile");

        BUILDER.comment("The damage of BTR-4 missile");
        BTR_4_MISSILE_DAMAGE = BUILDER.defineInRange("btr_4_missile_damage", 235, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion damage of BTR-4 missile");
        BTR_4_MISSILE_EXPLOSION_DAMAGE = BUILDER.defineInRange("btr_4_missile_explosion_damage", 17, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion radius of BTR-4 missile");
        BTR_4_MISSILE_EXPLOSION_RADIUS = BUILDER.defineInRange("btr_4_missile_explosion_radius", 3, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("terminator_missile");

        BUILDER.comment("The damage of BMPT Terminator missile");
        TERMINATOR_MISSILE_DAMAGE = BUILDER.defineInRange("terminatir_missile_damage", 160, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion damage of BMPT Terminator missile");
        TERMINATOR_MISSILE_EXPLOSION_DAMAGE = BUILDER.defineInRange("terminatir_missile_explosion_damage", 19, 1, Integer.MAX_VALUE);

        BUILDER.comment("The explosion radius of BMPT Terminator missile");
        TERMINATOR_MISSILE_EXPLOSION_RADIUS = BUILDER.defineInRange("terminatir_missile_explosion_radius", 3, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}

