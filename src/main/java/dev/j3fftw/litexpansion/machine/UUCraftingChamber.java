package dev.j3fftw.litexpansion.machine;

import java.util.*;

import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.cscorelib2.item.CustomItem;
import io.github.thebusybiscuit.cscorelib2.protection.ProtectableAction;
import io.github.thebusybiscuit.slimefun4.api.events.BlockPlacerPlaceEvent;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu.AdvancedMenuClickHandler;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public abstract class UUCraftingChamber extends SlimefunItem implements InventoryBlock, EnergyNetComponent {

    private final int[] border = { 0, 1, 3, 4, 5, 7, 8, 13, 14, 15, 16, 17, 50, 51, 52, 53 };
    private final int[] inputBorder = { 9, 10, 12, 13, 18, 22, 27, 31, 36, 40, 45, 46, 47, 48, 49 };
    private final int[] outputBorder = { 23, 24, 25, 26, 32, 35, 41, 42, 43, 44 };

    public UUCraftingChamber(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        new BlockMenuPreset(getID(), "&5UU物質合成機") {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public void newInstance(BlockMenu menu, Block b) {
                if (!BlockStorage.hasBlockInfo(b) || BlockStorage.getLocationInfo(b.getLocation(), "enabled") == null || BlockStorage.getLocationInfo(b.getLocation(), "enabled").equals(String.valueOf(false))) {
                    menu.replaceExistingItem(6, new CustomItem(Material.GUNPOWDER, "&7啟用狀態: &4\u2718", "", "&e> 點我開啟機器"));
                    menu.addMenuClickHandler(6, (p, slot, item, action) -> {
                        BlockStorage.addBlockInfo(b, "enabled", String.valueOf(true));
                        newInstance(menu, b);
                        return false;
                    });
                }
                else {
                    menu.replaceExistingItem(6, new CustomItem(Material.REDSTONE, "&7啟用狀態: &2\u2714", "", "&e> 點我關閉機器"));
                    menu.addMenuClickHandler(6, (p, slot, item, action) -> {
                        BlockStorage.addBlockInfo(b, "enabled", String.valueOf(false));
                        newInstance(menu, b);
                        return false;
                    });
                }
            }

            @Override
            public boolean canOpen(Block b, Player p) {
                return p.hasPermission("slimefun.inventory.bypass") || SlimefunPlugin.getProtectionManager().hasPermission(p, b.getLocation(), ProtectableAction.ACCESS_INVENTORIES);
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

                List<Integer> slots = new ArrayList<>();
                for (int slot : getInputSlots()) {
                    if (menu.getItemInSlot(slot) != null) {
                        slots.add(slot);
                    }
                }

                slots.sort(compareSlots(menu));

                int[] array = new int[slots.size()];

                for (int i = 0; i < slots.size(); i++) {
                    array[i] = slots.get(i);
                }

                return array;
            }
        };

        addItemHandler(onPlace());
        registerBlockHandler(getID(), (p, b, stack, reason) -> {
            BlockMenu inv = BlockStorage.getInventory(b);

            if (inv != null) {
                inv.dropItems(b.getLocation(), getInputSlots());
                inv.dropItems(b.getLocation(), getOutputSlots());
                inv.dropItems(b.getLocation(), 11);
            }

            return true;
        });
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
        return (slot1, slot2) -> menu.getItemInSlot(slot1).getAmount() - menu.getItemInSlot(slot2).getAmount();
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

        preset.addItem(2, new CustomItem(new ItemStack(Material.CRAFTING_TABLE), "&e合成表", "", "&b請在本格正下方放入一個成品 (原版/進階合成台物品皆可)", "&6並在九宮格內放入該物品的合成表", "&6使用進階合成台的合成表需要按照位置擺放", "&6而原版物品則只需要放入材料即可"), (p, slot, item, action) -> false);
    }

    public abstract int getEnergyConsumption();

    @Override
    public int[] getInputSlots() {
        return new int[] { 19, 20, 21, 28, 29, 30, 37, 38, 39 };
    }

    @Override
    public int[] getOutputSlots() {
        return new int[] { 33, 34 };
    }

    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, Config data) {
                AutomatedCraftingChamber.this.tick(b, false);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
    }

    protected void tick(Block block, boolean craftLast) {
        if (!craftLast && BlockStorage.getLocationInfo(block.getLocation(), "enabled").equals("false")) return;
        if (getCharge(block.getLocation()) < getEnergyConsumption()) return;

        BlockMenu menu = BlockStorage.getInventory(block);
        ItemStack target = menu.getItemInSlot(11);
        if(target == null || target.getType() == Material.AIR) return;
        target = target.clone();
        target.setAmount(1);

        if(SlimefunItem.getByItem(target) != null){
            SlimefunItem sfitem = SlimefunItem.getByItem(target);
            if(sfitem != null && sfitem.getRecipeType() == RecipeType.ENHANCED_CRAFTING_TABLE){
                ItemStack[] recipe = sfitem.getRecipe();
                int min = Math.min(SlimefunPlugin.getTickerTask().getSPT(), CanFit(menu, sfitem.getItem()) / ((sfitem.getRecipeOutput() == null) ? 1 : sfitem.getRecipeOutput().getAmount()));
                if(min == 0) return;
                for(int i =0; i < 9; i++){
                    ItemStack item = menu.getItemInSlot(getInputSlots()[i]);
                    if(!isSimilar(recipe[i], item)) return;
                    if(item != null){
                        if(item.getAmount() == 1 && !craftLast) return;
                        min = Math.min(item.getAmount()-1, min);
                    }
                }
                min = Math.min(min, getCharge(block.getLocation()) / getEnergyConsumption());
                for(int i =0; i < 9; i++){
                    if(menu.getItemInSlot(getInputSlots()[i] ) != null){
                        menu.consumeItem(getInputSlots()[i], min);
                    }
                }
                removeCharge(block.getLocation(), getEnergyConsumption()*min);
                ItemStack result;
                if(sfitem.getRecipeOutput() != null){
                    result = sfitem.getRecipeOutput().clone();
                }else{
                    result = sfitem.getItem().clone();
                }
                result.setAmount(result.getAmount()*min);
                menu.pushItem(result, getOutputSlots());
            }
        }else{
            boolean isWooden = false;
            for(Tag<Material> tag : Wooden){
                if(tag.isTagged(target.getType())){
                    isWooden = true;
                    break;
                }
            }
            List<Recipe> rs = Bukkit.getRecipesFor(target);
            for(Recipe r : rs){
                if(r == null) continue;
                int min = Math.min(SlimefunPlugin.getTickerTask().getSPT()*4, CanFit(menu, r.getResult()) / r.getResult().getAmount());
                if(min == 0) continue;
                List<ItemStack> ings = null;
                if(r instanceof ShapedRecipe){
                    ShapedRecipe sr = (ShapedRecipe)r;
                    //SlimefunPlugin.instance.getLogger().log(Level.INFO, sr.getChoiceMap().toString() + " " + sr.getIngredientMap().toString()+ " " + sr.getIngredientMap().toString());
                    ings = new ArrayList<>(sr.getIngredientMap().values());
                }else if(r instanceof ShapelessRecipe){
                    ShapelessRecipe slr = (ShapelessRecipe)r;
                    //SlimefunPlugin.instance.getLogger().log(Level.INFO, slr.getChoiceList().toString() + " "+ slr.getKey().toString());
                    ings = slr.getIngredientList();
                }
                if(ings == null) continue;
                HashMap<Material, Integer> require = new HashMap<>();
                for(ItemStack i : ings){
                    if(i == null) continue;
                    if(require.containsKey(i.getType())){
                        require.replace(i.getType(), require.get(i.getType())+1);
                    }else{
                        require.put(i.getType(), 1);
                    }
                }
                HashMap<Material, Integer> inv = new HashMap<>();
                for(int i=0; i < 9; i++){
                    ItemStack item = menu.getItemInSlot(getInputSlots()[i]);
                    if (item != null) {
                        item = item.clone();
                        if(SlimefunItem.getByItem(item) != null) continue;
                        if(!isWooden && isWood(item)){
                            item.setType(Material.BIRCH_PLANKS);
                        }
                        if(inv.containsKey(item.getType())){
                            inv.replace(item.getType(), inv.get(item.getType())+item.getAmount());
                        }else{
                            inv.put(item.getType(), item.getAmount());
                        }
                    }
                }

                boolean cont = false;
                for(Material ma : require.keySet()){
                    if(!inv.containsKey(ma)) {
                        cont = true;
                        break;
                    }
                    min = Math.min(min, (inv.get(ma)-1) / require.get(ma));
                }
                if(cont) continue;

                for(Material m : require.keySet()){
                    require.replace(m, require.get(m)*min);
                }

                for(int i=0; i < 9; i++){
                    ItemStack item = menu.getItemInSlot(getInputSlots()[i]);
                    if (item == null) continue;
                    item = item.clone();
                    if(SlimefunItem.getByItem(item) != null) continue;
                    if(!isWooden && isWood(item)) item.setType(Material.BIRCH_PLANKS);
                    Material m = item.getType();
                    if(require.containsKey(m)){
                        int cost = require.get(m);
                        int ia = item.getAmount();
                        if(ia >= cost){
                            menu.consumeItem(getInputSlots()[i], cost);
                            require.remove(m);
                        }else{
                            menu.consumeItem(getInputSlots()[i], ia);
                            require.replace(m, require.get(m) - ia);
                        }
                    }
                }
                removeCharge(block.getLocation(), getEnergyConsumption()*min);
                ItemStack result = r.getResult().clone();
                result.setAmount(min*result.getAmount());
                menu.pushItem(result, getOutputSlots());
                return;
            }
        }
    }

    private boolean isSimilar(ItemStack i1, ItemStack i2){
        if(i1 == null && i2 == null) return true;
        if(i1 == null || i2 == null) return false;
        if(i1.getType() != i2.getType()) return false;
        if(i1.hasItemMeta() && i2.hasItemMeta()){
            if(i1.getItemMeta().hasDisplayName() && i2.getItemMeta().hasDisplayName()){
                return i1.getItemMeta().getDisplayName().equals(i2.getItemMeta().getDisplayName());
            }
        }
        return !i1.hasItemMeta() && !i2.hasItemMeta();
    }

    private int CanFit(BlockMenu menu, ItemStack item){
        int result = 0;
        for(int i : getOutputSlots()){
            if(menu.getItemInSlot(i) == null){
                result += item.getType().getMaxStackSize();
            }
            else if(menu.getItemInSlot(i).isSimilar(item)){
                result += item.getType().getMaxStackSize() - menu.getItemInSlot(i).getAmount();
            }
        }
        return result;
    }

    private boolean isWood(ItemStack i){
        return Tag.PLANKS.isTagged(i.getType());
    }
}