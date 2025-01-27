package dev.j3fftw.litexpansion.machine;

import dev.j3fftw.litexpansion.Items;
import dev.j3fftw.litexpansion.LiteXpansion;
import dev.j3fftw.litexpansion.utils.Utils;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ScrapMachine extends SlimefunItem implements InventoryBlock, EnergyNetComponent {

    public static final RecipeType RECIPE_TYPE = new RecipeType(
        new NamespacedKey(LiteXpansion.getInstance(), "scrap_machine"), Items.SCRAP_MACHINE
    );

    public static final int ENERGY_CONSUMPTION = 100;
    public static final int CAPACITY = 450;

    private static final int INPUT_SLOT = 11;
    public static final int OUTPUT_SLOT = 15;
    public static final int PROGRESS_SLOT = 13;
    private static final int PROGRESS_AMOUNT = 5; // Seconds

    public static Map<BlockPosition, ArrayList<Integer>> progress = new HashMap<>();

    public static final CustomItemStack progressItem = new CustomItemStack(Material.DEAD_BUSH, "&7待機中");

    public ScrapMachine() {
        super(Items.LITEXPANSION, Items.SCRAP_MACHINE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.REINFORCED_PLATE, SlimefunItems.ADVANCED_CIRCUIT_BOARD,
            SlimefunItems.REINFORCED_PLATE, Items.MACHINE_BLOCK, SlimefunItems.REINFORCED_PLATE,
            SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.REINFORCED_PLATE, SlimefunItems.ADVANCED_CIRCUIT_BOARD
        });
        setupInv();
        registerBlockHandler(getId(), (p, b, stack, reason) -> {
            BlockMenu inv = BlockStorage.getInventory(b);

            if (inv != null) {
                inv.dropItems(b.getLocation(), INPUT_SLOT);
                inv.dropItems(b.getLocation(), OUTPUT_SLOT);
            }
            progress.remove(new BlockPosition(b));

            return true;
        });
    }

    private void setupInv() {
        createPreset(this, "&8物質回收裝置", blockMenuPreset -> {
            for (int i = 0; i < 27; i++) {
                if (i == INPUT_SLOT || i == OUTPUT_SLOT) continue;
                blockMenuPreset.addItem(i, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
            }

            Utils.putOutputSlot(blockMenuPreset, OUTPUT_SLOT);

            blockMenuPreset.addItem(PROGRESS_SLOT, new CustomItemStack(Material.DEAD_BUSH, "&7進度"));
        });
    }

    @Override
    public void preRegister() {
        this.addItemHandler(new BlockTicker() {
            public void tick(Block b, SlimefunItem sf, Config data) {
                ScrapMachine.this.tick(b);
            }

            public boolean isSynchronized() {
                return false;
            }
        });
    }

    private void tick(@Nonnull Block b) {
        @Nullable final BlockMenu inv = BlockStorage.getInventory(b);
        if (inv == null) return;

        @Nullable final ItemStack input = inv.getItemInSlot(INPUT_SLOT);

        final BlockPosition pos = new BlockPosition(b.getWorld(), b.getX(), b.getY(), b.getZ());

        if(progress.containsKey(pos)) {
            int timeleft = progress.get(pos).get(0) - SlimefunPlugin.getTickerTask().getTickstamp();
            int productleft = PROGRESS_AMOUNT - progress.get(pos).get(1);

            if(timeleft > 0 || productleft > 0){
                int producenow = timeleft < 0 ? productleft : productleft - timeleft;
                int charge = getCharge(b.getLocation());
                if (charge < ENERGY_CONSUMPTION*producenow) producenow = charge / ENERGY_CONSUMPTION;
                removeCharge(b.getLocation(), ENERGY_CONSUMPTION*producenow);

                progress.get(pos).set(1, progress.get(pos).get(1) + producenow);
                ChestMenuUtils.updateProgressbar(inv, PROGRESS_SLOT, Math.max(timeleft, 1), PROGRESS_AMOUNT,
                        progressItem);
            } else {
                inv.pushItem(Items.SCRAP.clone(), OUTPUT_SLOT);
                progress.remove(pos);
                inv.replaceExistingItem(PROGRESS_SLOT, progressItem);
            }
        } else if(input != null && input.getType() != Material.AIR && inv.fits(Items.SCRAP, OUTPUT_SLOT)){
            inv.consumeItem(INPUT_SLOT);
            progress.put(pos, new ArrayList<>(Arrays.asList(SlimefunPlugin.getTickerTask().getTickstamp() + PROGRESS_AMOUNT, 0)));
        }
    }

    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }

    @Override
    public int[] getInputSlots() {
        return new int[] {INPUT_SLOT};
    }

    @Override
    public int[] getOutputSlots() {
        return new int[] {OUTPUT_SLOT};
    }
}
