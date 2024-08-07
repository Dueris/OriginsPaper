package me.dueris.originspaper.factory.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.ActionFactory;
import me.dueris.originspaper.factory.powers.CooldownInterface;
import me.dueris.originspaper.factory.powers.CooldownPower;
import me.dueris.originspaper.registry.registries.PowerType;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class TriggerCooldownAction {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("trigger_cooldown"),
			InstanceDefiner.instanceDefiner()
				.add("power", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> {
				if (entity instanceof Player player) {
					Arrays.stream(new String[]{"apoli:action_on_hit", "apoli:action_when_damage_taken", "apoli:action_when_hit",
						"apoli:action_self", "apoli:attacker_action_when_hit", "apoli:self_action_on_hit",
						"apoli:self_action_on_kill", "apoli:self_action_when_hit", "apoli:target_action_on_hit", "apoli:cooldown"}).forEach(type -> {
						for (PowerType powerContainer : PowerHolderComponent.getPowers((org.bukkit.entity.Player) player.getBukkitEntity(), type)) {
							if (powerContainer instanceof CooldownInterface cooldownInterface) {
								CooldownPower.addCooldown((org.bukkit.entity.Player) player.getBukkitEntity(), cooldownInterface.getCooldown(), cooldownInterface);
							}
						}
					});
				}
			}
		);
	}
}
