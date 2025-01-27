package dev.j3fftw.litexpansion.machine;

import dev.j3fftw.litexpansion.Items;
import dev.j3fftw.litexpansion.LiteXpansion;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.libraries.dough.inventory.InvUtils;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class RefinedSmeltery extends MultiBlockMachine {

    private final BlockFace[] faces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };

    public static final RecipeType REFINED_SMELTERY_RECIPE = new RecipeType(
        new NamespacedKey(LiteXpansion.getInstance(), "refined_smeltery"),
        Items.REFINED_SMELTERY,
        "",
        "&7用來精煉金屬"
    );

    private static final ItemStack stone_bricks = new ItemStack(Material.STONE_BRICKS);

    public RefinedSmeltery() {
        super(Items.LITEXPANSION, Items.REFINED_SMELTERY, new ItemStack[] {
            null, new ItemStack(Material.STONE_BRICK_WALL), null,
            stone_bricks, new ItemStack(Material.DISPENSER), stone_bricks,
            null, new ItemStack(Material.FLINT_AND_STEEL), null
        }, new ItemStack[0], BlockFace.DOWN);
    }

    @Nonnull
    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < recipes.size() - 1; i += 2) {
            items.add(recipes.get(i)[0]);
            items.add(recipes.get(i + 1)[0]);
        }

        return items;
    }

    @Override
    public void onInteract(Player p, Block b) {
        Block dispBlock = b.getRelative(BlockFace.DOWN);
        Dispenser disp = (Dispenser) dispBlock.getState();
        Inventory inv = disp.getInventory();
        List<ItemStack[]> inputs = RecipeType.getRecipeInputList(this);

        for (int i = 0; i < inputs.size(); i++) {
            if (canCraft(inv, inputs, i)) {
                ItemStack output = RecipeType.getRecipeOutputList(this, inputs.get(i)).clone();

                if (Slimefun.hasUnlocked(p, output, true)) {
                    Inventory outputInv = findOutputInventory(output, dispBlock, inv);

                    if (outputInv != null) {
                        craft(p, b, inv, inputs.get(i), output, outputInv, dispBlock);
                    } else
                        SlimefunPlugin.getLocalization().sendMessage(p, "machines.full-inventory", true);
                }
                return;
            }
        }

        SlimefunPlugin.getLocalization().sendMessage(p, "machines.unknown-material", true);
    }

    private void craft(Player p, Block b, Inventory inv, ItemStack[] recipe, ItemStack output, Inventory outputInv, Block disp) {
        for (ItemStack removing : recipe) {
            if (removing != null) {
                InvUtils.removeItem(inv, removing.getAmount(), true, stack ->
                    SlimefunUtils.isItemSimilar(stack, removing, false));
            }
        }

        outputInv.addItem(output);
        p.getWorld().playSound(p.getLocation(), Sound.BLOCK_LAVA_POP, 1, 1);
        p.getWorld().playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);


        Inventory chamber = findIgnitionChamber(disp);

        if (chamber != null) {
            if (chamber.contains(Material.FLINT_AND_STEEL)) {
                ItemStack item = chamber.getItem(chamber.first(Material.FLINT_AND_STEEL));
                ItemMeta meta = item.getItemMeta();
                ((Damageable) meta).setDamage(((Damageable) meta).getDamage() + 1);
                item.setItemMeta(meta);

                if (((Damageable) item.getItemMeta()).getDamage() >= item.getType().getMaxDurability()) {
                    item.setAmount(0);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                }

                p.getWorld().playSound(p.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1, 1);
                return;
            }
            else {
                SlimefunPlugin.getLocalization().sendMessage(p, "machines.ignition-chamber-no-flint", true);
            }
        }
        Block fire = b.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN);
        fire.getWorld().playEffect(fire.getLocation(), Effect.STEP_SOUND, fire.getType());
        fire.setType(Material.AIR);
    }

    private boolean canCraft(Inventory inv, List<ItemStack[]> inputs, int i) {
        for (ItemStack converting : inputs.get(i)) {
            if (converting != null) {
                for (int j = 0; j < inv.getContents().length; j++) {
                    if (j == (inv.getContents().length - 1)
                        && !SlimefunUtils.isItemSimilar(inv.getContents()[j], converting,
                         false)) {
                        return false;
                    } else if (SlimefunUtils.isItemSimilar(inv.getContents()[j], converting, false)) break;
                }
            }
        }

        return true;
    }

    private Inventory findIgnitionChamber(Block b) {
        for (BlockFace face : faces) {
            if (b.getRelative(face).getType() == Material.DROPPER && BlockStorage.check(b.getRelative(face), "IGNITION_CHAMBER")) {
                BlockState state = PaperLib.getBlockState(b.getRelative(face), false).getState();

                if (state instanceof Dropper) {
                    return ((Dropper) state).getInventory();
                }
            }
        }

        return null;
    }

}

