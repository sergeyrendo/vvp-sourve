package tech.vvp.vvp.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.item.armor.*;
import tech.vvp.vvp.item.varies.*;


public class ModItems {
    public static final DeferredRegister<Item> REGISTRY = 
            DeferredRegister.create(ForgeRegistries.ITEMS, VVP.MOD_ID);

    public static final RegistryObject<Item> NATO_TAB_ICON = REGISTRY.register("nato_tab_icon",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RU_TAB_ICON = REGISTRY.register("ru_tab_icon",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ARMOR_ICON = REGISTRY.register("armor_icon",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MULTICAM_HELMET = REGISTRY.register("multicamhelmet",
            () -> new multicamhelmet());

    public static final RegistryObject<Item> MULTICAM_CHEST = REGISTRY.register("multicamchest",
            () -> new multicamchest());

    public static final RegistryObject<Item> MI_28_HELMET = REGISTRY.register("mi28helmet",
            () -> new mi28helmet());

    public static final RegistryObject<Item> MI_28_CHEST = REGISTRY.register("mi28chest",
            () -> new mi28chest());


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

    public static final RegistryObject<Item> LMUR_ITEM = REGISTRY.register("lmur_item", LmurItem::new);
    public static final RegistryObject<Item> X25_ITEM = REGISTRY.register("x25_item", X25Item::new);
    public static final RegistryObject<Item> HFIRE_ITEM = REGISTRY.register("hfire_item", HFireItem::new);
    public static final RegistryObject<Item> HRYZANTEMA_ITEM = REGISTRY.register("hryzantema_item", HFireItem::new);

    public static final RegistryObject<Item> CACTUS_TURRET_ITEM = REGISTRY.register("cactus_turret", CactusTurretItem::new);
    public static final RegistryObject<Item> BMP3M_BODY = REGISTRY.register("bmp3m_body", Bmp3MBodyItem::new);

    public static final RegistryObject<Item> CREW_HELMET = REGISTRY.register("crewhelmet",
            () -> new crewhelmet());
    public static final RegistryObject<Item> PANAMA = REGISTRY.register("panama",
            () -> new panama());
    public static final RegistryObject<Item> KEPKA = REGISTRY.register("kepka",
            () -> new kepka());
    public static final RegistryObject<Item> BERETA = REGISTRY.register("bereta",
            () -> new bereta());



    public static final RegistryObject<Item> RADIOHEAD = REGISTRY.register("music_disc_radiohead",
                        () -> new RecordItem(15, ModSounds.RADIOHEAD, // 15 = comparator signal strength
                                new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 4800)); // 4800 = длительность в тиках


    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}