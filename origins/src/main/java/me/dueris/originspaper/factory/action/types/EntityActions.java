package me.dueris.originspaper.factory.action.types;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.factory.action.ActionFactory;
import me.dueris.originspaper.factory.action.Actions;
import me.dueris.originspaper.factory.action.meta.*;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.registry.registries.OriginLayer;
import me.dueris.originspaper.registry.registries.PowerType;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import me.dueris.originspaper.util.entity.PowerUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EntityActions {

	public static void register(ActionFactory<Entity> factory) {
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

		register(new ActionFactory<>(OriginsPaper.apoliIdentifier("grant_power"),
			InstanceDefiner.instanceDefiner()
				.add("power", SerializableDataTypes.IDENTIFIER)
				.add("source", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> {
				if (entity.getBukkitEntity() instanceof Player p) {
					PowerType powerContainer = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(data.getId("power"));
					if (powerContainer == null) {
						OriginsPaper.getPlugin().getLogger().severe("Searched PowerType was null when attempting to revoke a power: {}".replace("{}", data.getId("power").toString()));
						return;
					}
					OriginLayer layer = CraftApoli.getLayer(data.getId("source"));
					try {
						PowerUtils.removePower(Bukkit.getConsoleSender(), powerContainer, p, layer, false);
					} catch (InstantiationException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}));
		register(new ActionFactory<>(OriginsPaper.apoliIdentifier("remove_power"),
			InstanceDefiner.instanceDefiner()
				.add("power", SerializableDataTypes.IDENTIFIER)
				.add("source", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> {
				if (entity.getBukkitEntity() instanceof Player p) {
					PowerType powerContainer = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(data.getId("power"));
					if (powerContainer == null) {
						OriginsPaper.getPlugin().getLogger().severe("Searched PowerType was null when attempting to apply a new power: {}".replace("{}", data.getId("power").toString()));
						return;
					}
					OriginLayer layer = CraftApoli.getLayer(data.getId("source"));
					try {
						PowerUtils.grantPower(Bukkit.getConsoleSender(), powerContainer, p, layer, false);
					} catch (InstantiationException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}));
		register(new ActionFactory<>(OriginsPaper.apoliIdentifier("revoke_all_powers"),
			InstanceDefiner.instanceDefiner()
				.add("source", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> {
				if (entity instanceof Player p) {
					for (ResourceLocation powerKey : PowerHolderComponent.getPowers(p).stream().map(PowerType::key).toList()) {
						PowerType powerContainer = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(powerKey);
						if (powerContainer == null) {
							OriginsPaper.getPlugin().getLogger().severe("Searched PowerType was null when attempting to revoke a power: {}".replace("{}", powerKey.toString()));
							return;
						}
						OriginLayer layer = CraftApoli.getLayer(data.getId("source"));
						try {
							PowerUtils.removePower(Bukkit.getConsoleSender(), powerContainer, p, layer, false);
						} catch (InstantiationException | IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					}
				}
			}));
		Actions.registerPackage(EntityActions::register, "me.dueris.originspaper.factory.action.types.entity");
	}

}
