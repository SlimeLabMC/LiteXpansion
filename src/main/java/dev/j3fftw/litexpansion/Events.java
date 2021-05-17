package dev.j3fftw.litexpansion;

import dev.j3fftw.litexpansion.armor.ElectricChestplate;
import dev.j3fftw.litexpansion.items.FoodSynthesizer;
import dev.j3fftw.litexpansion.utils.Constants;
import dev.j3fftw.litexpansion.weapons.NanoBlade;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class Events implements Listener {

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        Player p = (Player) e.getEntity();
        if (e.getFoodLevel() < p.getFoodLevel()) {
            checkAndConsume(p, e);
        }
    }

    @EventHandler
    public void onPlayerDamageDeal(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            ItemStack itemInHand = p.getInventory().getItemInMainHand();
            final NanoBlade nanoBlade = (NanoBlade) SlimefunItem.getByID(Items.NANO_BLADE.getItemId());
            if (nanoBlade.isItem(itemInHand)) {
                int deal = 7;
                if(itemInHand.getType() == Material.NETHERITE_SWORD) deal++;
                if (itemInHand.containsEnchantment(Enchantment.getByKey(Constants.GLOW_ENCHANT))
                        && nanoBlade.removeItemCharge(itemInHand, 10) && Slimefun.hasUnlocked(p, nanoBlade, true)){
                    deal *= 3;
                }
                e.setDamage(e.getDamage() * deal);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && ((Player) e.getEntity()).getEquipment() != null && !e.isCancelled()) {
            Player p = (Player) e.getEntity();
            Optional<ItemStack> optional = Optional.ofNullable(p.getEquipment().getChestplate());
            if(optional.isPresent()){
                final ElectricChestplate electricChestplate = (ElectricChestplate) Items.ELECTRIC_CHESTPLATE.getItem();
                if (electricChestplate.isItem(optional.get())
                        && Slimefun.hasUnlocked(p, electricChestplate, true)
                ) {
                    float deal = Math.min(electricChestplate.getItemCharge(optional.get()) * 1.75F, (float) e.getDamage());
                    if(deal > 0) electricChestplate.removeItemCharge(optional.get(), deal / 1.75F);
                    if(deal >= e.getDamage()){
                        e.setCancelled(true);
                    }else {
                        e.setDamage(e.getDamage() - deal);
                    }

                }
            }

        }
    }

    @EventHandler
    public void onHungerDamage(EntityDamageEvent e) {
        if (Items.FOOD_SYNTHESIZER == null || Items.FOOD_SYNTHESIZER.getItem().isDisabled()
            || !(e.getEntity() instanceof Player)) {
            return;
        }

        if (e.getCause() == EntityDamageEvent.DamageCause.STARVATION) {
            checkAndConsume((Player) e.getEntity(), null);
        }
    }

    public void checkAndConsume(Player p, FoodLevelChangeEvent e) {
        FoodSynthesizer foodSynth = (FoodSynthesizer) Items.FOOD_SYNTHESIZER.getItem();
        for (ItemStack item : p.getInventory().getContents()) {
            if (foodSynth.isItem(item) && foodSynth.removeItemCharge(item, 3F) && Slimefun.hasUnlocked(p, foodSynth, true)) {
                p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.5F, 1F);
                e.setFoodLevel(20);
                p.setSaturation(5);
                if (e != null) {
                    e.setFoodLevel(20);
                }
                break;
            }
        }
    }
}
