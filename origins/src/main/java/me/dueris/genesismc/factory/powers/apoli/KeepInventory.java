package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.factory.FactoryNumber;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class KeepInventory extends PowerType {

	private final Integer[] slots;
	private final FactoryJsonObject itemCondition;

	public KeepInventory(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonArray slots, FactoryJsonObject itemCondition) {
		super(name, description, hidden, condition, loading_priority);
		this.slots = slots == null ? null : slots.asList().stream().map(FactoryElement::getNumber).map(FactoryNumber::getInt).toList().toArray(new Integer[0]);
		this.itemCondition = itemCondition;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("keep_inventory"))
			.add("slots", FactoryJsonArray.class, new OptionalInstance())
			.add("item_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void keepinv(PlayerDeathEvent e) {
		Player player = e.getEntity();
		if (!getPlayers().contains(player)) return;
		if (isActive(player)) {
			e.setKeepInventory(true);
			ItemStack[] stackedClone = player.getInventory().getContents();
			ItemStack[] toDrop = new ItemStack[40];
			if (slots != null) {
				int[] a = Utils.missingNumbers(slots, 0, 40);
				int b = 0;
				for (int i : a) {
					toDrop[b++] = stackedClone[i];
					stackedClone[i] = new ItemStack(Material.AIR);
				}
			}

			int v = 0;
			for (ItemStack stack : Arrays.stream(stackedClone).toList()) {
				if (!ConditionExecutor.testItem(itemCondition, stack)) {
					stackedClone[v] = new ItemStack(Material.AIR);
					toDrop[v] = stack;
				}
			}

			e.getDrops().clear();
			e.getDrops().addAll(Arrays.stream(toDrop).filter(Objects::nonNull).toList());
			player.getInventory().setContents(stackedClone);
		}
	}

}
