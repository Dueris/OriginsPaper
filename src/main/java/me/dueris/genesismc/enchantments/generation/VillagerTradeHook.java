package me.dueris.genesismc.enchantments.generation;

import me.dueris.genesismc.enchantments.Anvil;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.Random;

public class VillagerTradeHook implements Listener {
    @EventHandler
    public void test(VillagerAcquireTradeEvent e) {
        if (e.getEntity() instanceof WanderingTrader) return;
        Villager villager = (Villager) e.getEntity();
        if (villager.getProfession().equals(Villager.Profession.LIBRARIAN)) {
            Random chanceOfSpawnRandom = new Random();
            if (chanceOfSpawnRandom.nextInt(100) > 97 && (e.getRecipe().getResult().getType().equals(Material.ENCHANTED_BOOK))) {
                Random emeraldCountRandom = new Random();
                Random shouldAddBookRandom = new Random();
                Random chanceOfLevelRandom = new Random();
                Random maxUsesRandom = new Random();
                int maxUses = maxUsesRandom.nextInt(11);
                int chanceOfLevel = chanceOfLevelRandom.nextInt(1000);
                boolean shouldAddBook = shouldAddBookRandom.nextBoolean();
                int emeraldCount = emeraldCountRandom.nextInt(53);
                int lvl;
                if (chanceOfLevel <= 40) {
                    lvl = 1;
                } else if (chanceOfLevel <= 65 && chanceOfLevel >= 41) {
                    lvl = 2;
                } else if (chanceOfLevel <= 85 && chanceOfLevel >= 66) {
                    lvl = 3;
                } else if (chanceOfLevel <= 100 && chanceOfLevel >= 86) {
                    lvl = 4;
                } else {
                    lvl = 1;
                }
                ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
                Anvil.setWaterProtCustomEnchantLevel(lvl, item);
                MerchantRecipe recipe = new MerchantRecipe(item, maxUses);
                recipe.addIngredient(new ItemStack(Material.EMERALD, emeraldCount));
                if (shouldAddBook) {
                    recipe.addIngredient(new ItemStack(Material.BOOK, 1));
                }
                e.setRecipe(recipe);
            }
        }
    }
}
