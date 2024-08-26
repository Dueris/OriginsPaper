package io.github.dueris.originspaper.power;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.data.types.modifier.ModifierUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModifyHealingPower extends ModifierPower {
	public ModifyHealingPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
							  @Nullable Modifier modifier, @Nullable List<Modifier> modifiers) {
		super(key, type, name, description, hidden, condition, loadingPriority, modifier, modifiers);
	}

	public static SerializableData buildFactory() {
		return ModifierPower.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("modify_healing"));
	}

	@EventHandler
	public void onHeal(EntityRegainHealthEvent e) {
		if (e.getEntity() instanceof org.bukkit.entity.Player p) {
			Player player = ((CraftPlayer) p).getHandle();
			double original = e.getAmount();
			if (getPlayers().contains(player) && isActive(player)) {
				double modified = ModifierUtil.applyModifiers(player, getModifiers(), original);
				e.setAmount(modified);
			}
		}
	}
}
