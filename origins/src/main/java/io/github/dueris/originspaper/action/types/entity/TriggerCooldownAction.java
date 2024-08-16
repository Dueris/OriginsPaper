package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.power.CooldownInterface;
import io.github.dueris.originspaper.power.CooldownPower;
import io.github.dueris.originspaper.power.PowerType;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class TriggerCooldownAction {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("trigger_cooldown"),
				SerializableData.serializableData()
						.add("power", SerializableDataTypes.IDENTIFIER),
				(data, entity) -> {
					if (entity instanceof Player player) {
						Arrays.stream(new String[]{"apoli:action_on_hit", "apoli:action_when_damage_taken", "apoli:action_when_hit",
								"apoli:action_self", "apoli:attacker_action_when_hit", "apoli:self_action_on_hit",
								"apoli:self_action_on_kill", "apoli:self_action_when_hit", "apoli:target_action_on_hit", "apoli:cooldown"}).forEach(type -> {
							for (PowerType powerContainer : PowerHolderComponent.getPowers((org.bukkit.entity.Player) player.getBukkitEntity(), type)) {
								if (powerContainer instanceof CooldownInterface cooldownInterface) {
									CooldownPower.addCooldown(player.getBukkitEntity(), cooldownInterface);
								}
							}
						});
					}
				}
		);
	}
}
