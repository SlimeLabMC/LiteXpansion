package dev.j3fftw.litexpansion.machine;

import dev.j3fftw.litexpansion.Items;
import dev.j3fftw.litexpansion.LiteXpansion;
import dev.j3fftw.litexpansion.utils.Utils;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.blocks.BlockPosition;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems.*;

public class MassFabricator extends SlimefunItem implements InventoryBlock, EnergyNetComponent {

    public static final RecipeType RECIPE_TYPE = new RecipeType(
        new NamespacedKey(LiteXpansion.getInstance(), "mass_fabricator"), Items.MASS_FABRICATOR_MACHINE
    );

    public static final int ENERGY_CONSUMPTION = 16384;
    public static final int CAPACITY = ENERGY_CONSUMPTION * 4;

    private static final int[] INPUT_SLOTS = new int[] {10, 11};
    public static final int OUTPUT_SLOT = 15;
    public static final int PROGRESS_SLOT = 13;
    private static final int PROGRESS_AMOUNT = 30; // Seconds

    public static Map<BlockPosition, ArrayList<Integer>> progress = new HashMap<>();

    public static final CustomItem progressItem = new CustomItem(Items.UU_MATTER.getType(), "&7待機中");

    public MassFabricator() {
        super(Items.LITEXPANSION, Items.MASS_FABRICATOR_MACHINE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            REINFORCED_PLATE, ADVANCED_CIRCUIT_BOARD, REINFORCED_PLATE,
            CARBONADO_EDGED_CAPACITOR, Items.MACHINE_BLOCK, CARBONADO_EDGED_CAPACITOR,
            REINFORCED_PLATE, ADVANCED_CIRCUIT_BOARD, REINFORCED_PLATE
        });
        setupInv();
        registerBlockHandler(getId(), (p, b, stack, reason) -> {
            BlockMenu inv = BlockStorage.getInventory(b);

            if (inv != null) {
                inv.dropItems(b.getLocation(), INPUT_SLOTS);
                inv.dropItems(b.getLocation(), OUTPUT_SLOT);
            }
            BlockPosition pos = new BlockPosition(b.getWorld(), b.getX(), b.getY(), b.getZ());
            if(progress.containsKey(pos)){
                ItemStack i = Items.SCRAP.clone();
                i.setAmount(progress.get(pos).get(1));
                b.getWorld().dropItemNaturally(b.getLocation(), i);
            }
            progress.remove(pos);

            return true;
        });
    }

    private void setupInv() {
        createPreset(this, "&5物質產生器", blockMenuPreset -> {
            for (int i = 0; i < 27; i++){
                if (i == INPUT_SLOTS[0] || i == INPUT_SLOTS[1] || i == OUTPUT_SLOT) continue;
                blockMenuPreset.addItem(i, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
            }
            Utils.putOutputSlot(blockMenuPreset, OUTPUT_SLOT);

            blockMenuPreset.addItem(PROGRESS_SLOT, progressItem);
        });
    }

    @Override
    public void preRegister() {
        this.addItemHandler(new BlockTicker() {
            public void tick(Block b, SlimefunItem sf, Config data) {
                MassFabricator.this.tick(b);
            }

            public boolean isSynchronized() {
                return false;
            }
        });
    }

    private void tick(@Nonnull Block b) {
        @Nullable final BlockMenu inv = BlockStorage.getInventory(b);
        if (inv == null) return;

        // yes this is ugly shush
        @Nullable ItemStack input = inv.getItemInSlot(INPUT_SLOTS[0]);
        @Nullable ItemStack input2 = inv.getItemInSlot(INPUT_SLOTS[1]);
        @Nullable final ItemStack output = inv.getItemInSlot(OUTPUT_SLOT);
        boolean i1 = true, i2 = true;
        if (output != null && output.getAmount() == output.getMaxStackSize()) return;

        if (!SlimefunUtils.isItemSimilar(input, Items.SCRAP, false)){
            i1 = false;
        }
        if (!SlimefunUtils.isItemSimilar(input2, Items.SCRAP, false)){
            i2 = false;
        }

        if (!i1 && !i2) return;

        final BlockPosition pos = new BlockPosition(b.getWorld(), b.getX(), b.getY(), b.getZ());

        if(progress.containsKey(pos)){
            int timeleft = progress.get(pos).get(0) - SlimefunPlugin.getTickerTask().getTickstamp();
            int productleft = PROGRESS_AMOUNT - progress.get(pos).get(1);

            if(timeleft > 0 || productleft > 0) {
                int scarpAmount = 0;
                if(i1) scarpAmount += input.getAmount();
                if(i2) scarpAmount += input2.getAmount();

                int producenow = Math.min(timeleft < 0 ? productleft : productleft - timeleft, scarpAmount);
                int charge = getCharge(b.getLocation());
                if (charge < ENERGY_CONSUMPTION*producenow) producenow = charge / ENERGY_CONSUMPTION;
                removeCharge(b.getLocation(), ENERGY_CONSUMPTION*producenow);

                progress.get(pos).set(1, progress.get(pos).get(1) + producenow);
                ChestMenuUtils.updateProgressbar(inv, PROGRESS_SLOT, Math.max(timeleft, 1), PROGRESS_AMOUNT, progressItem);

                if (i1) {
                    int minus = Math.min(input.getAmount(), producenow);
                    inv.consumeItem(INPUT_SLOTS[0], minus);
                    producenow -= minus;
                }
                if(i2 && producenow > 0) {
                    inv.consumeItem(INPUT_SLOTS[1], producenow);
                }
            } else {
                inv.pushItem(Items.UU_MATTER.clone(), OUTPUT_SLOT);
                progress.remove(pos);
                inv.replaceExistingItem(PROGRESS_SLOT, progressItem);
            }
        } else {
            if(i1) inv.consumeItem(INPUT_SLOTS[0]);
            else inv.consumeItem(INPUT_SLOTS[1]);
            progress.put(pos, new ArrayList<>(Arrays.asList(SlimefunPlugin.getTickerTask().getTickstamp() + PROGRESS_AMOUNT, 0)));
        }
    }

    @Nonnull
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
        return INPUT_SLOTS;
    }

    @Override
    public int[] getOutputSlots() {
        return new int[] {OUTPUT_SLOT};
    }
}
