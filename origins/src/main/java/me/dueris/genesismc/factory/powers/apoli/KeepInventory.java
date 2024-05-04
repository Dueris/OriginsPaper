package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.builder.inst.factory.FactoryElement;
import me.dueris.calio.builder.inst.factory.FactoryNumber;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class KeepInventory extends CraftPower implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void keepinv(PlayerDeathEvent e) {
        Player player = e.getEntity();
        if (!keep_inventory.contains(player)) return;
        for (Power power : OriginPlayerAccessor.getPowers(player, getType())) {
            if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) player)) {
                setActive(player, power.getTag(), true);
                e.setKeepInventory(true);
                ItemStack[] stackedClone = player.getInventory().getContents();
                ItemStack[] toDrop = new ItemStack[40];
                if (power.isPresent("slots")) {
                    Integer[] slots = power.getJsonArray("slots").asList().stream()
                        .filter(FactoryElement::isGsonPrimative)
                        .map(FactoryElement::getNumber)
                        .map(FactoryNumber::getInt).toList().toArray(new Integer[0]);
                    int[] a = Utils.missingNumbers(slots, 0, 40);
                    int b = 0;
                    for (int i : a) {
                        toDrop[b++] = stackedClone[i];
                        stackedClone[i] = new ItemStack(Material.AIR);
                    }
                }

                int v = 0;
                for (ItemStack stack : Arrays.stream(stackedClone).toList()) {
                    if (!ConditionExecutor.testItem(power.getJsonObject("item_condition"), stack)) {
                        stackedClone[v] = new ItemStack(Material.AIR);
                        toDrop[v] = stack;
                    }
                }

                e.getDrops().clear();
                e.getDrops().addAll(Arrays.stream(toDrop).filter(Objects::nonNull).toList());
                player.getInventory().setContents(stackedClone);
            } else {
                setActive(player, power.getTag(), false);
            }
        }
    }

    @Override
    public String getType() {
        return "apoli:keep_inventory";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return keep_inventory;
    }
}
