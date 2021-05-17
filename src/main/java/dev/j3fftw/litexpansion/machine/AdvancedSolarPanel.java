package dev.j3fftw.litexpansion.machine;

import dev.j3fftw.litexpansion.Items;
import dev.j3fftw.litexpansion.utils.Utils;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetProvider;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AdvancedSolarPanel extends SlimefunItem implements InventoryBlock, EnergyNetProvider {

    private static final int PROGRESS_SLOT = 4;
    private static final CustomItem generatingItem = new CustomItem(Material.ORANGE_STAINED_GLASS_PANE,
            "&c未在生產狀態"
    );
    public static int ADVANCED_DAY_RATE = 80;
    public static int ADVANCED_NIGHT_RATE = 10;
    public static int ADVANCED_OUTPUT = 320;
    public static int ADVANCED_STORAGE = 262144;
    public static int HYBRID_DAY_RATE = 640;
    public static int HYBRID_NIGHT_RATE = 80;
    public static int HYBRID_OUTPUT = 1200;
    public static int HYBRID_STORAGE = 1048576;
    public static int ULTIMATE_DAY_RATE = 5120;
    public static int ULTIMATE_NIGHT_RATE = 640;
    public static int ULTIMATE_OUTPUT = 5120;
    public static int ULTIMATE_STORAGE = 8388608;
    private final Type type;

    public AdvancedSolarPanel(Type type) {
        super(Items.LITEXPANSION, type.item, RecipeType.ENHANCED_CRAFTING_TABLE, type.recipe);
        this.type = type;

        createPreset(this, type.item.getImmutableMeta().getDisplayName().orElse("&7"),
                blockMenuPreset -> {
                    for (int i = 0; i < 9; i++)
                        blockMenuPreset.addItem(i, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());

                    blockMenuPreset.addItem(PROGRESS_SLOT, generatingItem);
                });
    }

    @Override
    public int getGeneratedOutput(@Nonnull Location l, @Nonnull Config data) {
        @Nullable final BlockMenu inv = BlockStorage.getInventory(l);
        if (inv == null) return 0;

        final int stored = getCharge(l);
        final boolean canGenerate = stored < getCapacity();
        final int rate = canGenerate ? getGeneratingAmount(inv.getBlock(), l.getWorld()) : 0;

        String generationType = "&4未知";

        if (l.getWorld().getEnvironment() == World.Environment.NETHER) {
            generationType = "&c地獄 &e(日間)";
        } else if (l.getWorld().getEnvironment() == World.Environment.THE_END) {
            generationType = "&5終界 &8(夜間)";
        } else if (rate == this.type.dayGenerationRate) {
            generationType = "&e日間";
        } else if (rate == this.type.nightGenerationRate) {
            generationType = "&8夜間";
        }

        if (inv.toInventory() != null && !inv.toInventory().getViewers().isEmpty()) {
            inv.replaceExistingItem(PROGRESS_SLOT,
                    canGenerate ? new CustomItem(Material.GREEN_STAINED_GLASS_PANE, "&a發電中",
                            "", "&b效率: " + generationType
                            + "&7( &6" + Utils.powerFormatAndFadeDecimals(rate) + " J/s &7)",
                                    "", "&7已儲存: &6" + Utils.powerFormatAndFadeDecimals(stored + rate) + " J"
                    )
                            : new CustomItem(Material.ORANGE_STAINED_GLASS_PANE, "&c待機中",
                            "", "&7已達到最大儲存量",
                            "", "&7已儲存: &6" + Utils.powerFormatAndFadeDecimals(stored) + " J")
            );
        }

        return rate*SlimefunPlugin.getTickerTask().getSPT();
    }

    @Override
    public boolean willExplode(@Nonnull Location l, @Nonnull Config data) {
        return false;
    }

    private int getGeneratingAmount(@Nonnull Block b, @Nonnull World world) {
        if (world.getEnvironment() == World.Environment.NETHER) return this.type.dayGenerationRate;
        if (world.getEnvironment() == World.Environment.THE_END) return this.type.nightGenerationRate;

        // Note: You need to get the block above for the light check, the block itself is always 0
        long time = world.getTime();
        if (world.isThundering() || world.hasStorm() || (time >= 12300 && time <= 23850)
                || b.getLocation().add(0, 1, 0).getBlock().getLightFromSky() < 15
        ) {
            return this.type.nightGenerationRate;
        } else {
            return this.type.dayGenerationRate;
        }
    }

    @Nonnull
    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.GENERATOR;
    }

    @Override
    public int getCapacity() {
        return this.type.storage;
    }

    @Override
    public int[] getInputSlots() {
        return new int[0];
    }

    @Override
    public int[] getOutputSlots() {
        return new int[0];
    }

    @Getter
    public enum Type {

        ADVANCED(Items.ADVANCED_SOLAR_PANEL, ADVANCED_DAY_RATE, ADVANCED_NIGHT_RATE, ADVANCED_OUTPUT,
                ADVANCED_STORAGE, new ItemStack[] {
                Items.REINFORCED_GLASS, Items.REINFORCED_GLASS, Items.REINFORCED_GLASS,
                Items.ADVANCED_ALLOY, SlimefunItems.SOLAR_GENERATOR_4, Items.ADVANCED_ALLOY,
                SlimefunItems.CARBONADO_EDGED_CAPACITOR, Items.ADVANCED_MACHINE_BLOCK, SlimefunItems.ADVANCED_CIRCUIT_BOARD
        }),

        HYBRID(Items.HYBRID_SOLAR_PANEL, HYBRID_DAY_RATE, HYBRID_NIGHT_RATE, HYBRID_OUTPUT, HYBRID_STORAGE,
            new ItemStack[] {
                Items.CARBON_PLATE, SlimefunItems.SILICON, Items.CARBON_PLATE,
                Items.ADVANCED_SOLAR_PANEL, Items.ADVANCED_SOLAR_PANEL, Items.ADVANCED_SOLAR_PANEL,
                Items.IRIDIUM_PLATE, Items.ADVANCED_MACHINE_BLOCK, SlimefunItems.ADVANCED_CIRCUIT_BOARD
            }),

        ULTIMATE(Items.ULTIMATE_SOLAR_PANEL, ULTIMATE_DAY_RATE, ULTIMATE_NIGHT_RATE, ULTIMATE_OUTPUT,
                ULTIMATE_STORAGE, new ItemStack[] {
                Items.HYBRID_SOLAR_PANEL, Items.HYBRID_SOLAR_PANEL, Items.HYBRID_SOLAR_PANEL,
                Items.ADVANCED_SOLAR_PANEL, SlimefunItems.ADVANCED_CIRCUIT_BOARD, Items.ADVANCED_SOLAR_PANEL,
                Items.HYBRID_SOLAR_PANEL, SlimefunItems.SOLAR_GENERATOR_4, Items.HYBRID_SOLAR_PANEL,
        });

        @Nonnull
        private final SlimefunItemStack item;
        private final int dayGenerationRate;
        private final int nightGenerationRate;
        private final int output;
        private final int storage;

        @Nonnull
        private final ItemStack[] recipe;

        Type(SlimefunItemStack advancedSolarPanel, int advancedDayRate, int advancedNightRate, int advancedOutput, int advancedStorage, ItemStack[] itemStacks) {
            this.item = advancedSolarPanel;
            this.dayGenerationRate = advancedDayRate;
            this.nightGenerationRate = advancedNightRate;
            this.output = advancedOutput;
            this.storage = advancedStorage;
            this.recipe = itemStacks;
        }
    }
}
