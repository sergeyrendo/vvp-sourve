package tech.vvp.vvp.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.item.armor.multicamchest;
import tech.vvp.vvp.item.armor.multicamhelmet;
import tech.vvp.vvp.item.armor.usahelmet;
import tech.vvp.vvp.item.armor.usachest;
import tech.vvp.vvp.item.varies.*;
import tech.vvp.vvp.item.gun.launcher.At4Item;


public class ModItems {
    public static final DeferredRegister<Item> REGISTRY = 
            DeferredRegister.create(ForgeRegistries.ITEMS, VVP.MOD_ID);

    public static final RegistryObject<Item> ICON_SPAWN_ITEM = REGISTRY.register("icon_spawn_item",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ICON_CIVILIAN = REGISTRY.register("icon_civilian",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ARMOR_ICON = REGISTRY.register("armor_icon",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> USA_HELMET = REGISTRY.register("usahelmet",
            () -> new usahelmet());
            
    public static final RegistryObject<Item> USA_CHEST = REGISTRY.register("usachest",
            () -> new usachest());

    public static final RegistryObject<Item> MULTICAM_HELMET = REGISTRY.register("multicamhelmet",
            () -> new multicamhelmet());

    public static final RegistryObject<Item> MULTICAM_CHEST = REGISTRY.register("multicamchest",
            () -> new multicamchest());


    public static final RegistryObject<Item> MANGAL_BODY = REGISTRY.register("mangal_body", MangalBodyItem::new);
    public static final RegistryObject<Item> MANGAL_TURRET = REGISTRY.register("mangal_turret", MangalTurretItem::new);
    public static final RegistryObject<Item> SETKA_BODY = REGISTRY.register("setka_body", SetkaBodyItem::new);
    public static final RegistryObject<Item> SETKA_TURRET = REGISTRY.register("setka_turret", SetkaTurretItem::new);
    public static final RegistryObject<Item> WRENCH = REGISTRY.register("wrench", WrenchItem::new);
    public static final RegistryObject<Item> KOROBKI = REGISTRY.register("korobki", KorobkiItem::new);
    public static final RegistryObject<Item> SPRAY = REGISTRY.register("spray", SprayItem::new);
    public static final RegistryObject<Item> TENT = REGISTRY.register("tent", TentItem::new);
    public static final RegistryObject<Item> FAB_500_ITEM = REGISTRY.register("fab_500_item", Fab500Item::new);
    public static final RegistryObject<Item> S_13 = REGISTRY.register("s_13", S13Item::new);

    public static final RegistryObject<Item> AT4 = REGISTRY.register("at4", At4Item::new);



    public static final RegistryObject<Item> RADIOHEAD = REGISTRY.register("music_disc_radiohead",
                        () -> new RecordItem(15, ModSounds.RADIOHEAD, // 15 = comparator signal strength
                                new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 4800)); // 4800 = длительность в тиках


    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}