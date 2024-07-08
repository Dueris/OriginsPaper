package me.dueris.originspaper.factory.powers.apoli;

import com.mojang.datafixers.util.Pair;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.AttributeExecuteEvent;
import me.dueris.originspaper.event.PowerUpdateEvent;
import me.dueris.originspaper.factory.data.types.Modifier;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.screen.OriginPage;
import me.dueris.originspaper.util.DataConverter;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AttributeHandler extends PowerType {
	public static ConcurrentHashMap<Player, List<Pair<Attribute, AttributeModifier>>> playerModifiers = new ConcurrentHashMap<>();
	private final Modifier[] modifiers;
	private final boolean updateHealth;
	private final @Nullable String attribute;

	public AttributeHandler(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, boolean updateHealth, FactoryJsonObject modifier, FactoryJsonArray modifiers, String attribute) {
		super(name, description, hidden, condition, loading_priority);
		this.updateHealth = updateHealth;
		this.attribute = attribute;
		this.modifiers = Modifier.getModifiers(modifier, modifiers);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("attribute"))
			.add("update_health", boolean.class, true)
			.add("modifier", FactoryJsonObject.class, new OptionalInstance())
			.add("modifiers", FactoryJsonArray.class, new OptionalInstance())
			.add("attribute", String.class, new OptionalInstance());
	}

	@EventHandler
	public void powerUpdate(PowerUpdateEvent e) {
		if (!e.getPower().getTag().equalsIgnoreCase(getTag())) return;
		Player p = e.getPlayer();
		OriginPage.setAttributesToDefault(p);
		if (getPlayers().contains(p)) {
			runAttributeModifyPower(e);
		}
	}

	@Override
	public void bootstrapUnapply(Player player) {
		if (playerModifiers.containsKey(player)) {
			playerModifiers.get(player).forEach(pair -> {
				if (player.getAttribute(pair.getFirst()) == null) return;
				player.getAttribute(pair.getFirst()).removeModifier(pair.getSecond());
			});
		}
	}

	protected void runAttributeModifyPower(PlayerEvent e) {
		Player p = e.getPlayer();
		if (!getPlayers().contains(p)) return;
		playerModifiers.putIfAbsent(p, new ArrayList<>());
		for (Modifier modifier : modifiers) {
			try {
				String attrName = modifier.handle.getString("attribute");
				double nmsValue;
				Attribute attributeModifier;

				switch (attrName.toLowerCase()) {
					case "reach-entity-attributes:reach":
						nmsValue = DataConverter.attributeToBlockReach(modifier.value());
						attributeModifier = Attribute.PLAYER_BLOCK_INTERACTION_RANGE;
						break;
					case "reach-entity-attributes:attack_range":
						nmsValue = DataConverter.attributeToEntityReach(modifier.value());
						attributeModifier = Attribute.PLAYER_ENTITY_INTERACTION_RANGE;
						break;
					default:
						attributeModifier = attribute == null ?
							DataConverter.resolveAttribute(attrName) :
							DataConverter.resolveAttribute(attribute);
						nmsValue = modifier.value();
				}

				AttributeModifier attrModifier = new AttributeModifier("unnamed", nmsValue, DataConverter.convertToOperation(modifier));

				if (p.getAttribute(attributeModifier) != null) {
					p.getAttribute(attributeModifier).addTransientModifier(attrModifier);
					playerModifiers.get(p).add(new Pair<>(attributeModifier, attrModifier));
				}

				AttributeExecuteEvent attributeExecuteEvent = new AttributeExecuteEvent(p, attributeModifier, this, e.isAsynchronous());
				Bukkit.getServer().getPluginManager().callEvent(attributeExecuteEvent);
			} catch (Exception ev) {
				ev.printStackTrace();
			}
		}
		if (updateHealth) {
			p.sendHealthUpdate();
		}
	}

	public Modifier[] getModifiers() {
		return modifiers;
	}

	public boolean updateHealth() {
		return updateHealth;
	}

	@Nullable
	public String getAttribute() {
		return attribute;
	}
}
