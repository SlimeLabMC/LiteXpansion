package dev.j3fftw.litexpansion;

import io.github.thebusybiscuit.slimefun4.core.attributes.MachineTier;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineType;
import dev.j3fftw.litexpansion.machine.AdvancedSolarPanel;
import dev.j3fftw.litexpansion.machine.RubberSynthesizer;
import dev.j3fftw.litexpansion.utils.Constants;
import dev.j3fftw.litexpansion.utils.LoreBuilderDynamic;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactivity;
import io.github.thebusybiscuit.slimefun4.utils.LoreBuilder;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.skull.SkullItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

public final class Items {

    // Category
    public static final Category LITEXPANSION = new Category(
        new NamespacedKey(LiteXpansion.getInstance(), "litexpansion"),
        new CustomItem(SkullItem.fromHash("3f87fc5cbb233743a82fb0fa51fe739487f29bcc01c9026621ecefad197f4fb1"),
            "&7物理科技")
    );
    public static final SlimefunItemStack ELECTRIC_CHESTPLATE = new SlimefunItemStack(
        "ELECTRIC_CHESTPLATE",
        Material.LEATHER_CHESTPLATE, Color.TEAL,
        "&9電磁胸甲",
        "",
        "&8\u21E8 &7吸收所有傷害",
        "",
        "&c&o&8\u21E8 &e\u26A1 &70 / 250 J"
    );

    // Armor
    public static final SlimefunItemStack NANO_BLADE = new SlimefunItemStack(
        "NANO_BLADE",
        Material.DIAMOND_SWORD,
        "&2光劍",
        "",
        "&f輕鬆切開有機物質的高科技武器",
        "",
        "&a右鍵 &f啟動",
        "",
        "&8\u21E8 &7每次攻擊消耗 &e10J",
        "",
        "&c&o&8\u21E8 &e\u26A1 &70 / 500 J"
    );

    // Weapon
    // Tools
    public static final SlimefunItemStack GLASS_CUTTER = new SlimefunItemStack(
        "GLASS_CUTTER",
        Material.GHAST_TEAR,
        "&b玻璃切割刀",
        "",
        "&7快速切下玻璃",
        "",
        "&c&o&8\u21E8 &e\u26A1 &70 / 300 J"
    );
    public static final SlimefunItemStack TREETAP = new SlimefunItemStack(
        "TREETAP",
        Material.WOODEN_HOE,
        "&7Treetap"
    );
    public static final SlimefunItemStack CARGO_CONFIGURATOR = new SlimefunItemStack(
        "CARGO_CONFIGURATOR",
        Material.COMPASS,
        "&7物流配置器",
        "",
        "&7快速調整物流節點設定",
        "",
        "&7> &e右鍵 &7- 複製節點設定",
        "&7> &e左鍵  &7- 設置節點設定",
        "&7> &eShift+右鍵 &7- 清除節點設定"
    );

    // Machines
    public static final SlimefunItemStack SCRAP_MACHINE = new SlimefunItemStack(
        "SCRAP_MACHINE",
            new CustomItem(SkullItem.fromHash("6872d0710cce9e11f6b4393d8215788ad8f99e5dc1378606f775fd064287d7dc"),
                    "物質回收裝置"),
        "&8物質回收裝置",
        "",
            "&f處理各種物質轉變為 &8廢料",
            "",
            LoreBuilder.machine(MachineTier.END_GAME, MachineType.MACHINE),
            LoreBuilder.powerPerSecond(450)
    );

    public static final SlimefunItemStack MASS_FABRICATOR_MACHINE = new SlimefunItemStack(
        "MASS_FABRICATOR_MACHINE",
            new CustomItem(SkullItem.fromHash("215cd248e3fb7b038013a1d1487168df093a7bbedce4224b5c823d3ef681a784"),
                    "物質產生器"),
        "&5物質產生器",
            "",
            "&f將 &8廢料 &f強制轉換為 &5通用物質",
            "",
            LoreBuilder.machine(MachineTier.END_GAME, MachineType.MACHINE),
            LoreBuilder.powerPerSecond(16666)
    );
    // Items
    public static final SlimefunItemStack FOOD_SYNTHESIZER = new SlimefunItemStack(
        "FOOD_SYNTHESIZER",
        new CustomItem(SkullItem.fromHash("a11a2df7d37af40ed5ce442fd2d78cd8ebcdcdc029d2ae691a2b64395cdf"),
            "食品合成器"),
        "&d食品合成器",
        "",
        "&f持續產生你愛吃的人工食品!",
        "&f放在物品欄內自動補充飽食度",
        "",
        "&c&o&8\u21E8 &e\u26A1 &70 / 100 J"
    );
    public static final SlimefunItemStack MAG_THOR = new SlimefunItemStack(
        "MAG_THOR",
        Material.IRON_INGOT,
        "&b&l釷鎂合金",
        "",
        "&7&o極度耐用的合金"
    );
    public static final SlimefunItemStack THORIUM = new SlimefunItemStack(
        "THORIUM",
        new CustomItem(SkullItem.fromHash("427d1a6184c62d4c4a67f862b8e19ec001abe4c7d889f23349e8dafe6d033"),
            "釷"),
        "&8釷",
        "",
        LoreBuilder.radioactive(Radioactivity.HIGH),
            "&8\u21E8 &4需要生化套裝!"
    );
    public static final SlimefunItemStack SCRAP = new SlimefunItemStack(
        "SCRAP",
        Material.DEAD_BUSH,
        "&8廢料",
        "",
        "&7可用來製造 &5通用物質"
    );
    public static final SlimefunItemStack UU_MATTER = new SlimefunItemStack(
        "UU_MATTER",
        Material.PURPLE_DYE,
        "&5通用物質",
        "",
        "&7可用來創造資源"
    );
    public static final SlimefunItemStack IRIDIUM = new SlimefunItemStack(
        "IRIDIUM",
        Material.WHITE_DYE,
        "&f銥"
    );
    public static final SlimefunItemStack IRIDIUM_PLATE = new SlimefunItemStack(
        "IRIDIUM_PLATE",
        Material.PAPER,
        "&f銥板",
        "",
        "&7用來製作 銥盔甲"
    );
    public static final SlimefunItemStack THORIUM_DUST = new SlimefunItemStack(
        "THORIUM_DUST",
        Material.BLACK_DYE,
        "&8釷粉"
    );
    public static final SlimefunItemStack REFINED_IRON = new SlimefunItemStack(
        "REFINED_IRON",
        Material.IRON_INGOT,
        "&7精煉鐵"
    );
    public static final SlimefunItemStack MACHINE_BLOCK = new SlimefunItemStack(
        "MACHINE_BLOCK",
        Material.IRON_BLOCK,
        "&7機器單元"
    );
    public static final SlimefunItemStack UNINSULATED_COPPER_CABLE = new SlimefunItemStack(
        "UNINSULATED_COPPER_CABLE",
        Material.STRING,
        "&7銅纜線"
    );
    public static final SlimefunItemStack COPPER_CABLE = new SlimefunItemStack(
        "COPPER_CABLE",
        Material.STRING,
        "&7絕緣銅纜線"
    );
    public static final SlimefunItemStack RUBBER = new SlimefunItemStack(
        "RUBBER",
        Material.INK_SAC,
        "&7橡膠"
    );
    public static final SlimefunItemStack ELECTRONIC_CIRCUIT = new SlimefunItemStack(
        "ELECTRONIC_CIRCUIT",
        Material.COBWEB,
        "&7電子晶片"
    );
    public static final SlimefunItemStack ADVANCED_CIRCUIT = new SlimefunItemStack(
        "ADVANCED_CIRCUIT",
        Material.COBWEB,
        "&7高級電子晶片"
    );
    ////////////////
    // CARBON CRAP
    public static final SlimefunItemStack COAL_DUST = new SlimefunItemStack(
        "COAL_DUST",
        Material.BLACK_DYE,
        "&7煤灰"
    );
    public static final SlimefunItemStack RAW_CARBON_FIBRE = new SlimefunItemStack(
        "RAW_CARBON_FIBRE",
        Material.BLACK_DYE,
        "&7初等碳纖維"
    );
    public static final SlimefunItemStack RAW_CARBON_MESH = new SlimefunItemStack(
        "RAW_CARBON_MESH",
        Material.BLACK_DYE,
        "&7初等碳纖維片"
    );
    public static final SlimefunItemStack CARBON_PLATE = new SlimefunItemStack(
        "CARBON_PLATE",
        Material.BLACK_CARPET,
        "&7碳板"
    );
    public static final SlimefunItemStack ADVANCED_ALLOY = new SlimefunItemStack(
        "ADVANCED_ALLOY",
        Material.PAPER,
        "&7高級合金"
    );
    /////////
    public static final SlimefunItemStack ADVANCED_MACHINE_BLOCK = new SlimefunItemStack(
        "ADVANCED_MACHINE_BLOCK",
        Material.DIAMOND_BLOCK,
        "&7高級機器單元"
    );
    public static final SlimefunItemStack ENERGY_CRYSTAl = new SlimefunItemStack(
        "ENERGY_CRYSTAL",
        Material.DIAMOND,
        "&7能量晶體"
    );
    //todo make it enchanted
    public static final SlimefunItemStack LAPOTRON_CRYSTAL = new SlimefunItemStack(
        "LAPOTRON_CRYSTAL",
        Material.DIAMOND,
        "&7藍晶"
    );
    public static final SlimefunItemStack REINFORCED_STONE = new SlimefunItemStack(
        "REINFORCED_STONE",
        Material.STONE,
        "&7強化石"
    );
    public static final SlimefunItemStack REINFORCED_DOOR = new SlimefunItemStack(
        "REINFORCED_DOOR",
        Material.IRON_DOOR,
        "&7強化門"
    );
    public static final SlimefunItemStack REINFORCED_GLASS = new SlimefunItemStack(
        "REINFORCED_GLASS",
        Material.GRAY_STAINED_GLASS,
        "&7強化玻璃"
    );
    public static final SlimefunItemStack MIXED_METAL_INGOT = new SlimefunItemStack(
        "MIXED_METAL_INGOT",
        Material.IRON_INGOT,
        "&7混合金屬錠"
    );
    public static final SlimefunItemStack RUBBER_SYNTHESIZER_MACHINE = new SlimefunItemStack(
        "RUBBER_SYNTHESIZER_MACHINE",
        Material.ORANGE_CONCRETE,
        "&6橡膠合成機",
        "",
        "&f將燃料桶轉換為 &7橡膠",
        "",
        LoreBuilderDynamic.powerBuffer(RubberSynthesizer.CAPACITY),
        LoreBuilderDynamic.powerPerTick(RubberSynthesizer.ENERGY_CONSUMPTION)
    );
    //// Solar panels
    public static final SlimefunItemStack ADVANCED_SOLAR_PANEL = new SlimefunItemStack(
        "ADVANCED_SOLAR_PANEL",
        "afdd9e588d2461d2d3d058cb3e0af2b3a3367607aa14d124ed92a833f25fb112",
        "&7&l太陽能電池",
        "",
        "&9可以在夜間工作",
        "",
        LoreBuilderDynamic.powerBuffer(AdvancedSolarPanel.ADVANCED_STORAGE),
        LoreBuilderDynamic.powerPerTick(AdvancedSolarPanel.ADVANCED_DAY_RATE) + " (日間)",
        LoreBuilderDynamic.powerPerTick(AdvancedSolarPanel.ADVANCED_NIGHT_RATE) + " (夜間)"
    );
    public static final SlimefunItemStack HYBRID_SOLAR_PANEL = new SlimefunItemStack(
        "HYBRID_SOLAR_PANEL",
        "240775c3ad75763613f32f04986881bbe4eee4366d0c57f17f7c7514e2d0a77d",
        "&b&l單晶矽太陽能電池",
            "",
            "&9可以在夜間工作",
        "",
        LoreBuilderDynamic.powerBuffer(AdvancedSolarPanel.HYBRID_STORAGE),
        LoreBuilderDynamic.powerPerTick(AdvancedSolarPanel.HYBRID_DAY_RATE) + " (日間 + 地獄)",
        LoreBuilderDynamic.powerPerTick(AdvancedSolarPanel.HYBRID_NIGHT_RATE) + " (夜間 + 終界)"
    );
    public static final SlimefunItemStack ULTIMATE_SOLAR_PANEL = new SlimefunItemStack(
        "ULTIMATE_SOLAR_PANEL",
        "c4fe135c311f7086edcc5e6dbc4ef4b23f819fddaa42f827dac46e3574de2287",
        "&5&l混合式太陽能電池",
            "",
            "&9可以在夜間工作",
        "",
        LoreBuilderDynamic.powerBuffer(AdvancedSolarPanel.ULTIMATE_STORAGE),
        LoreBuilderDynamic.powerPerTick(AdvancedSolarPanel.ULTIMATE_DAY_RATE) + " (日間)",
        LoreBuilderDynamic.powerPerTick(AdvancedSolarPanel.ULTIMATE_NIGHT_RATE) + " (夜間)"
    );
    //Basic Machines
    public static final SlimefunItemStack REFINED_SMELTERY = new SlimefunItemStack(
        "REFINED_SMELTERY",
        Material.BLAST_FURNACE,
        "&7精煉爐",
            "",
            "&a&o用來精煉金屬"
    );
    private static final Enchantment glowEnchant = Enchantment.getByKey(Constants.GLOW_ENCHANT);

    static {
        ADVANCED_CIRCUIT.addEnchantment(glowEnchant, 1);
        GLASS_CUTTER.addEnchantment(glowEnchant, 1);
    }

    private Items() {}
}
