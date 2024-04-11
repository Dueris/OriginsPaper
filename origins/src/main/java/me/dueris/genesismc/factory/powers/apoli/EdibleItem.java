package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.util.MiscUtils;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodProperties;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static net.minecraft.world.level.GameType.CREATIVE;

public class EdibleItem extends CraftPower implements Listener {
    public static HashMap<Power, FoodProperties> cachedFoodProperties = new HashMap<>();

    public static ItemStack runResultStack(Power power, boolean runActionUpon, InventoryHolder holder) {
        JSONObject stack = power.get("result_stack");
        int amt;
        if (stack.get("amount") != null) {
            amt = Integer.parseInt(stack.get("amount").toString());
        } else {
            amt = 1;
        }
        ItemStack itemStack = new ItemStack(MiscUtils.getBukkitMaterial(stack.get("item").toString()), amt);
        holder.getInventory().addItem(itemStack);
        if (runActionUpon) Actions.executeItem(itemStack, power.get("result_item_action"));
        return itemStack;
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void tryConsume(PlayerInteractEvent e) {
        if (e.getItem() == null) return;
        if (e.getAction().isLeftClick() || !e.getHand().isHand() || !e.getHand().equals(EquipmentSlot.HAND))
            return; // Offhand causes lots of issues
        if (e.getItem().getItemMeta() == null) return;

        if (this.getPowerArray().contains(e.getPlayer())) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getPowerFile())) {
                if (ConditionExecutor.testItem(power.get("item_condition"), e.getItem())) {
                    if (consume(power, e.getPlayer(), e.getItem())) {
                        e.setCancelled(true);
                    }
                    Actions.executeItem(e.getItem(), power.get("item_action"));
                    Actions.executeEntity(e.getPlayer(), power.get("entity_action"));
                }
            }
        }
    }

    private boolean consume(Power power, Player player, ItemStack itemStack) {
        if (!cachedFoodProperties.containsKey(power)) {
            cachedFoodProperties.put(power, Utils.parseProperties(power.get("food_component")));
        }
        FoodProperties properties = cachedFoodProperties.get(power);
        int hunger = properties.getNutrition();
        float saturation = properties.getSaturationModifier();
        boolean alwaysEdible = properties.canAlwaysEat();

        if (player.getFoodLevel() >= 20 && !alwaysEdible) return false;

        player.setSaturation(player.getSaturation() + (hunger * saturation * 2));
        player.setFoodLevel(player.getFoodLevel() + hunger >= 20 ? 20 : player.getFoodLevel() + hunger);
        if (!player.getGameMode().equals(CREATIVE)) Utils.consumeItem(itemStack);
        player.playSound(player.getEyeLocation(), MiscUtils.parseSound(power.getStringOrDefault("consume_sound", "minecraft:entity.generic.eat")), 1, 1);

        ServerPlayer p = ((CraftPlayer) player).getHandle();
        properties.getEffects().forEach(effectPair -> {
            p.addEffect(effectPair.getFirst());
        });

        runResultStack(power, true, player);
        return true;
    }

    @Override
    public String getPowerFile() {
        return "apoli:edible_item";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return edible_item;
    }
}
