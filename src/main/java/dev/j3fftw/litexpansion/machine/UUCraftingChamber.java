package dev.j3fftw.litexpansion.machine;

import java.util.*;

import dev.j3fftw.litexpansion.Items;
import dev.j3fftw.litexpansion.uumatter.UUMatter;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.CSCoreLibPlugin.cscorelib2.chat.ChatColors;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.events.BlockPlacerPlaceEvent;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu.AdvancedMenuClickHandler;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;

import javax.annotation.Nonnull;

public class UUCraftingChamber extends SlimefunItem implements InventoryBlock, EnergyNetComponent {

    private final int[] border = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 36, 37, 38, 39, 40, 41, 42, 43, 44 };
    private final int[] inputBorder = { 9, 10, 11, 12, 18, 21, 27, 28, 29, 30 };
    private final int[] outputBorder = { 14, 15, 15, 16, 17, 23, 26, 32, 33, 34, 35 };
    private final Map<ItemStack, Integer> recipes = new HashMap<>();
    public static final int ENERGY_CONSUMPTION = 1024;
    public static final int CAPACITY = 8192;
    public static final ItemStack Recipetype = Items.UU_CRAFTING_CHAMBER.clone();

    public UUCraftingChamber() {
        super(Items.LITEXPANSION, Items.UU_CRAFTING_CHAMBER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                Items.IRIDIUM_PLATE, SlimefunItems.REINFORCED_PLATE, Items.IRIDIUM_PLATE,
                SlimefunItems.LARGE_CAPACITOR, SlimefunItems.AUTOMATED_CRAFTING_CHAMBER, SlimefunItems.LARGE_CAPACITOR,
                Items.MAG_THOR, Items.ADVANCED_MACHINE_BLOCK, SlimefunItems.ADVANCED_CIRCUIT_BOARD
        });

        Recipetype.setLore(Arrays.asList("",
                ChatColors.color("&f將 &5通用物質 &f按照合成表加工為普通物質")));

        new BlockMenuPreset(getId(), "&5通用物質合成機") {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public void newInstance(BlockMenu menu, Block b) {
                if (!BlockStorage.hasBlockInfo(b) || BlockStorage.getLocationInfo(b.getLocation(), "enabled") == null || BlockStorage.getLocationInfo(b.getLocation(), "enabled").equals(String.valueOf(false))) {
                    menu.replaceExistingItem(31, new CustomItem(Material.GUNPOWDER, "&7啟用狀態: &4\u2718", "", "&e> 點我開啟機器"));
                    menu.addMenuClickHandler(31, (p, slot, item, action) -> {
                        BlockStorage.addBlockInfo(b, "enabled", String.valueOf(true));
                        newInstance(menu, b);
                        return false;
                    });
                }
                else {
                    menu.replaceExistingItem(31, new CustomItem(Material.REDSTONE, "&7啟用狀態: &2\u2714", "", "&e> 點我關閉機器"));
                    menu.addMenuClickHandler(31, (p, slot, item, action) -> {
                        BlockStorage.addBlockInfo(b, "enabled", String.valueOf(false));
                        newInstance(menu, b);
                        return false;
                    });
                }
            }

            @Override
            public boolean canOpen(Block b, Player p) {
                return p.hasPermission("slimefun.inventory.bypass") || SlimefunPlugin.getProtectionManager().hasPermission(p, b.getLocation(), ProtectableAction.INTERACT_BLOCK);
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
                if (flow == ItemTransportFlow.WITHDRAW) {
                    return getOutputSlots();
                }

                return getInputSlots();
            }
        };

        addItemHandler(onPlace());
        registerBlockHandler(getId(), (p, b, stack, reason) -> {
            BlockMenu inv = BlockStorage.getInventory(b);

            if (inv != null) {
                inv.dropItems(b.getLocation(), getInputSlots());
                inv.dropItems(b.getLocation(), getOutputSlots());
                inv.dropItems(b.getLocation(), 22);
            }

            return true;
        });

        loadUUrecipes();
    }

    private void loadUUrecipes() {
        for(ItemStack item : UUMatter.INSTANCE.getRecipes().keySet()){
            int i = 0;
            for(ItemStack uu : UUMatter.INSTANCE.getRecipes().get(item)){
                if(uu != null) i++;
            }
            recipes.put(item.clone(), i);
        }
    }

    private BlockPlaceHandler onPlace() {
        return new BlockPlaceHandler(true) {

            @Override
            public void onPlayerPlace(BlockPlaceEvent e) {
                BlockStorage.addBlockInfo(e.getBlock(), "enabled", String.valueOf(false));
            }

            @Override
            public void onBlockPlacerPlace(BlockPlacerPlaceEvent e) {
                BlockStorage.addBlockInfo(e.getBlock(), "enabled", String.valueOf(false));
            }
        };
    }

    private Comparator<Integer> compareSlots(DirtyChestMenu menu) {
        return Comparator.comparingInt(slot -> menu.getItemInSlot(slot).getAmount());
    }

    protected void constructMenu(BlockMenuPreset preset) {
        for (int i : border) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
        }

        for (int i : inputBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.BLUE_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
        }

        for (int i : outputBorder) {
            preset.addItem(i, new CustomItem(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE), " "), (p, slot, item, action) -> false);
        }

        for (int i : getOutputSlots()) {
            preset.addMenuClickHandler(i, new AdvancedMenuClickHandler() {

                @Override
                public boolean onClick(Player p, int slot, ItemStack cursor, ClickAction action) {
                    return false;
                }

                @Override
                public boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor, ClickAction action) {
                    return cursor == null || cursor.getType() == null || cursor.getType() == Material.AIR;
                }
            });
        }

        preset.addItem(13, new CustomItem(new ItemStack(Material.CRAFTING_TABLE), "&e合成表", "", "&6請在本格正下方放入要合成的成品", "&6並在左方輸入欄放入 &5通用物質"), (p, slot, item, action) -> false);
    }

    @Override
    public int[] getInputSlots() {
        return new int[] { 19, 20 };
    }

    @Override
    public int[] getOutputSlots() {
        return new int[] { 24, 25 };
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }

    @Nonnull
    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, Config data) {
                UUCraftingChamber.this.tick(b);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
    }

    protected void tick(Block block) {
        if (BlockStorage.getLocationInfo(block.getLocation(), "enabled").equals("false")) return;
        if (getCharge(block.getLocation()) < ENERGY_CONSUMPTION) return;

        BlockMenu menu = BlockStorage.getInventory(block);
        ItemStack target = menu.getItemInSlot(22);
        if(target == null || target.getType() == Material.AIR) return;
        boolean b1 = SlimefunUtils.isItemSimilar(menu.getItemInSlot(getInputSlots()[0]), Items.UU_MATTER, false, false),
                b2 = SlimefunUtils.isItemSimilar(menu.getItemInSlot(getInputSlots()[1]), Items.UU_MATTER, false, false);
        if(!b1 && !b2) return;
        target = target.clone();
        target.setAmount(1);

        for(ItemStack itemStack : recipes.keySet()){
            if(SlimefunUtils.isItemSimilar(target, itemStack, false, false)) {
                int min = Math.min(SlimefunPlugin.getTickerTask().getSPT(), CanFit(menu, target) / itemStack.getAmount());
                min = Math.min(min, getCharge(block.getLocation()) / ENERGY_CONSUMPTION);
                if(min == 0) return;
                int uu = 0;
                if(b1) uu += menu.getItemInSlot(getInputSlots()[0]).getAmount();
                if(b2) uu += menu.getItemInSlot(getInputSlots()[1]).getAmount();
                int amount = recipes.get(itemStack);
                min = Math.min(min, uu / amount);
                if(min == 0) return;
                amount *= min;
                if(b1) {
                    int m1 = Math.min(amount, menu.getItemInSlot(getInputSlots()[0]).getAmount());
                    menu.consumeItem(getInputSlots()[0], m1);
                    amount -= m1;
                }
                if(b2 && amount > 0){
                    menu.consumeItem(getInputSlots()[1], amount);
                }
                removeCharge(block.getLocation(), ENERGY_CONSUMPTION*min);
                ItemStack result = itemStack.clone();
                result.setAmount(result.getAmount()*min);
                menu.pushItem(result, getOutputSlots());
                return;
            }
        }
    }

    private int CanFit(BlockMenu menu, ItemStack item){
        int result = 0;
        for(int i : getOutputSlots()) {
            if (menu.getItemInSlot(i) == null) {
                result += item.getType().getMaxStackSize();
            } else if (menu.getItemInSlot(i).isSimilar(item)) {
                result += item.getType().getMaxStackSize() - menu.getItemInSlot(i).getAmount();
            }
        }
        return result;
    }

}