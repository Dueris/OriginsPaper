package io.github.dueris.originspaper.action.types;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import io.github.dueris.originspaper.action.ActionTypes;
import io.github.dueris.originspaper.action.meta.*;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.PowerType;
import io.github.dueris.originspaper.registry.Registries;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.github.dueris.originspaper.util.entity.PowerUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EntityActions {

	public static void register(ActionTypeFactory<Entity> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.ENTITY_ACTION).register(factory, factory.getSerializerId());
	}

	public static void registerAll() {
		register(AndAction.getFactory(SerializableDataTypes.list(ApoliDataTypes.ENTITY_ACTION)));
		register(ChanceAction.getFactory(ApoliDataTypes.ENTITY_ACTION));
		register(IfElseAction.getFactory(ApoliDataTypes.ENTITY_ACTION, ApoliDataTypes.ENTITY_CONDITION));
		register(ChoiceAction.getFactory(ApoliDataTypes.ENTITY_ACTION));
		register(IfElseListAction.getFactory(ApoliDataTypes.ENTITY_ACTION, ApoliDataTypes.ENTITY_CONDITION));
		register(DelayAction.getFactory(ApoliDataTypes.ENTITY_ACTION));
		register(NothingAction.getFactory());
		register(SideAction.getFactory(ApoliDataTypes.ENTITY_ACTION, entity -> !entity.level().isClientSide));

		register(new ActionTypeFactory<>(OriginsPaper.apoliIdentifier("grant_power"),
			SerializableData.serializableData()
				.add("power", SerializableDataTypes.IDENTIFIER)
				.add("source", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> {
				if (entity.getBukkitEntity() instanceof Player p) {
					PowerType powerContainer = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(data.getId("power"));
					if (powerContainer == null) {
						OriginsPaper.getPlugin().getLogger().severe("Searched PowerType was null when attempting to revoke a power: {}".replace("{}", data.getId("power").toString()));
						return;
					}
					OriginLayer layer = OriginsPaper.getLayer(data.getId("source"));
					PowerUtils.grantPower(Bukkit.getConsoleSender(), powerContainer, p, layer, false);
				}
			}));
		register(new ActionTypeFactory<>(OriginsPaper.apoliIdentifier("remove_power"),
			SerializableData.serializableData()
				.add("power", SerializableDataTypes.IDENTIFIER)
				.add("source", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> {
				if (entity.getBukkitEntity() instanceof Player p) {
					PowerType powerContainer = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(data.getId("power"));
					if (powerContainer == null) {
						OriginsPaper.getPlugin().getLogger().severe("Searched PowerType was null when attempting to apply a new power: {}".replace("{}", data.getId("power").toString()));
						return;
					}
					OriginLayer layer = OriginsPaper.getLayer(data.getId("source"));
					PowerUtils.removePower(Bukkit.getConsoleSender(), powerContainer, p, layer, false);
				}
			}));
		register(new ActionTypeFactory<>(OriginsPaper.apoliIdentifier("revoke_all_powers"),
			SerializableData.serializableData()
				.add("source", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> {
				if (entity instanceof Player p) {
					for (ResourceLocation powerKey : PowerHolderComponent.getPowers(p).stream().map(PowerType::key).toList()) {
						PowerType powerContainer = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(powerKey);
						if (powerContainer == null) {
							OriginsPaper.getPlugin().getLogger().severe("Searched PowerType was null when attempting to revoke a power: {}".replace("{}", powerKey.toString()));
							return;
						}
						OriginLayer layer = OriginsPaper.getLayer(data.getId("source"));
						PowerUtils.removePower(Bukkit.getConsoleSender(), powerContainer, p, layer, false);
					}
				}
			}));
		ActionTypes.registerPackage(EntityActions::register, "io.github.dueris.originspaper.action.types.entity");
	}

}
