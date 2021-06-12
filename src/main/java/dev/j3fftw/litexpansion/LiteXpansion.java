package dev.j3fftw.litexpansion;

import dev.j3fftw.litexpansion.armor.ElectricChestplate;
import dev.j3fftw.litexpansion.resources.ThoriumResource;
import dev.j3fftw.litexpansion.utils.Constants;
import dev.j3fftw.litexpansion.uumatter.UUMatter;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.researching.Research;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.File;
import java.lang.reflect.Field;

public class LiteXpansion extends JavaPlugin implements SlimefunAddon {

    private static LiteXpansion instance;

    @Override
    public void onEnable() {
        instance = this;

        if (!new File(getDataFolder(), "config.yml").exists())
            saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new Events(), this);

        // Enchantment
        try {
            if (!Enchantment.isAcceptingRegistrations()) {
                Field accepting = Enchantment.class.getDeclaredField("acceptingNew");
                accepting.setAccessible(true);
                accepting.set(null, true);
            }
        } catch (IllegalAccessException | NoSuchFieldException ignored) {
            getLogger().warning("Failed to register enchantment. Seems the 'acceptingNew' field changed monkaS");
        }
        Enchantment.registerEnchantment(new GlowEnchant(Constants.GLOW_ENCHANT));

        UUMatter.INSTANCE.register();

        // Category
        Items.LITEXPANSION.register();

        ItemSetup.INSTANCE.init();

        /*
        registerItem(Items.ENERGY_CRYSTAl, RecipeType.ENHANCED_CRAFTING_TABLE,
            new ItemStack(Material.REDSTONE), new ItemStack(Material.REDSTONE), new ItemStack(Material.REDSTONE),
            new ItemStack(Material.REDSTONE), new ItemStack(Material.DIAMOND), new ItemStack(Material.REDSTONE),
            new ItemStack(Material.REDSTONE), new ItemStack(Material.REDSTONE), new ItemStack(Material.REDSTONE)
        );

        registerItem(Items.LAPOTRON_CRYSTAL, RecipeType.ENHANCED_CRAFTING_TABLE,
            new ItemStack(Material.LAPIS_LAZULI), Items.ELECTRONIC_CIRCUIT, new ItemStack(Material.LAPIS_LAZULI),
            new ItemStack(Material.LAPIS_LAZULI), Items.ENERGY_CRYSTAl, new ItemStack(Material.LAPIS_LAZULI),
            new ItemStack(Material.LAPIS_LAZULI), Items.ELECTRONIC_CIRCUIT, new ItemStack(Material.LAPIS_LAZULI)
        );

        registerItem(Items.REINFORCED_STONE, RecipeType.ENHANCED_CRAFTING_TABLE,
            new ItemStack(Material.STONE), new ItemStack(Material.STONE), new ItemStack(Material.STONE),
            new ItemStack(Material.STONE), Items.ADVANCED_ALLOY, new ItemStack(Material.STONE),
            new ItemStack(Material.STONE), new ItemStack(Material.STONE), new ItemStack(Material.STONE)
        );

        registerItem(Items.REINFORCED_DOOR, RecipeType.ENHANCED_CRAFTING_TABLE,
            Items.REINFORCED_STONE, Items.REINFORCED_STONE, null,
            Items.REINFORCED_STONE, Items.REINFORCED_STONE, null,
            Items.REINFORCED_STONE, Items.REINFORCED_STONE, null
        );
        */

        // Tools
        /*

        registerItem(Items.TREETAP, RecipeType.ENHANCED_CRAFTING_TABLE,
            null, new ItemStack(Material.OAK_PLANKS), null,
            new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.OAK_PLANKS),
            new ItemStack(Material.OAK_PLANKS), null, null
        );
        */

        // Armor
        new ElectricChestplate().register(this);

        setupResearches();
        new ThoriumResource().register();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private void setupResearches() {
        new Research(new NamespacedKey(this, "sanitizing_foots"),
            696969, "消失的吃飯時間", 100)
            .addItems(Items.FOOD_SYNTHESIZER)
            .register();

        new Research(new NamespacedKey(this, "superalloys"),
            696970, "超級金屬", 150)
            .addItems(Items.THORIUM, Items.MAG_THOR)
            .register();

        new Research(new NamespacedKey(this, "super_hot_fire"),
            696971, "絕地武士的覺醒", 200)
            .addItems(Items.NANO_BLADE)
            .register();
    }

    @Nonnull
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    public String getBugTrackerURL() {
        return "https://github.com/J3fftw1/LiteXpansion/issues";
    }

    public static LiteXpansion getInstance() {
        return instance;
    }
}
