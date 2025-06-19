package tech.vvp.vvp.tools;

import com.atsuishio.superbwarfare.init.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InventoryTool {

    /**
     * 计算物品列表内指定物品的数量
     *
     * @param itemList 物品列表
     * @param item     物品类型
     */
    public static int countItem(NonNullList<ItemStack> itemList, @NotNull Item item) {
        return itemList.stream()
                .filter(stack -> stack.is(item))
                .mapToInt(ItemStack::getCount)
                .sum();
    }

    /**
     * 计算玩家背包内指定物品的数量
     *
     * @param player 玩家
     * @param item   物品类型
     */
    public static int countItem(Player player, @NotNull Item item) {
        return countItem(player.getInventory().items, item);
    }

    /**
     * 判断玩家背包内是否有指定物品
     *
     * @param player 玩家
     * @param item   物品类型
     */
    public static boolean hasItem(Player player, @NotNull Item item) {
        return countItem(player, item) > 0;
    }

    /**
     * 判断物品列表内是否有指定物品
     *
     * @param itemList 物品列表
     * @param item     物品类型
     */
    public static boolean hasItem(NonNullList<ItemStack> itemList, @NotNull Item item) {
        return countItem(itemList, item) > 0;
    }

    /**
     * 判断物品列表内是否有创造模式弹药盒
     *
     * @param itemList 物品列表
     */
    public static boolean hasCreativeAmmoBox(NonNullList<ItemStack> itemList) {
        return countItem(itemList, ModItems.CREATIVE_AMMO_BOX.get()) > 0;
    }

    /**
     * 判断玩家背包内是否有创造模式弹药盒
     *
     * @param player 玩家
     */
    public static boolean hasCreativeAmmoBox(Player player) {
        return hasItem(player, ModItems.CREATIVE_AMMO_BOX.get());
    }
} 