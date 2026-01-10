package tech.vvp.vvp.item.armor;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import tech.vvp.vvp.VVP;

import java.util.function.Supplier;

public enum ModArmorMaterials implements ArmorMaterial {
    // В параметрах: сапоги, поножи, нагрудник, шлем
    USA_HELMET("usahelmet", 25, new int[]{3, 6, 8, 3}, 19, SoundEvents.ARMOR_EQUIP_NETHERITE,
            2.0f, 0.1f, () -> Ingredient.of(Items.IRON_INGOT));

    private final String name;
    private final int durabilityMultiplier;
    private final int[] protectionAmounts;
    private final int enchantmentValue;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;

    // Базовая прочность ванильной брони (шлем, нагрудник, поножи, ботинки)
    private static final int[] BASE_DURABILITY = {13, 15, 16, 11};

    ModArmorMaterials(String name, int durabilityMultiplier, int[] protectionAmounts, int enchantmentValue, SoundEvent equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmounts = protectionAmounts;
        this.enchantmentValue = enchantmentValue;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        // Безопасный расчет прочности через switch
        return switch (type) {
            case HELMET -> BASE_DURABILITY[0] * this.durabilityMultiplier;
            case CHESTPLATE -> BASE_DURABILITY[1] * this.durabilityMultiplier;
            case LEGGINGS -> BASE_DURABILITY[2] * this.durabilityMultiplier;
            case BOOTS -> BASE_DURABILITY[3] * this.durabilityMultiplier;
            default -> 0;
        };
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        // Безопасный расчет защиты через switch. 
        // Порядок в массиве {3, 6, 8, 3} соответствует: [ботинки, поножи, нагрудник, шлем]
        return switch (type) {
            case BOOTS -> this.protectionAmounts[0];
            case LEGGINGS -> this.protectionAmounts[1];
            case CHESTPLATE -> this.protectionAmounts[2];
            case HELMET -> this.protectionAmounts[3];
            default -> 0;
        };
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @Override
    public String getName() {
        return VVP.MOD_ID + ":" + this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}