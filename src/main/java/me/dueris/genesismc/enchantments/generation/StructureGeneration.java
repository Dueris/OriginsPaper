package me.dueris.genesismc.enchantments.generation;

import me.dueris.genesismc.enchantments.Anvil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class StructureGeneration implements Listener {
    protected static ArrayList<String> validLootTableKeys = new ArrayList<>();
    static {
        validLootTableKeys.add("minecraft:chests/desert_pyramid");
        validLootTableKeys.add("minecraft:chests/shipwreck_treasure");
        validLootTableKeys.add("minecraft:chests/underwater_ruin_big");
        validLootTableKeys.add("minecraft:chests/abandoned_mineshaft");
        validLootTableKeys.add("minecraft:chests/jungle_temple");
        validLootTableKeys.add("minecraft:chests/stronghold_library");
        validLootTableKeys.add("minecraft:chests/simple_dungeon");
        validLootTableKeys.add("minecraft:chests/ancient_city");
        validLootTableKeys.add("minecraft:chests/woodland_mansion");
    }

    @EventHandler
    public void lootGen(LootGenerateEvent e){
        if(validLootTableKeys.contains(e.getLootTable().key().asString())){
            Random r = new Random();
            if(r.nextInt(1000) > 954){
                ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK, 1);
                Anvil.setWaterProtCustomEnchantLevel(r.nextInt(4), itemStack);
                e.getLoot().add(itemStack);
            }
        }
    }
}
