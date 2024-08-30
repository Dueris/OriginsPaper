package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.data.types.modifier.ModifierUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModifyExperiencePower extends ModifierPower {
	public ModifyExperiencePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority, @Nullable Modifier modifier, @Nullable List<Modifier> modifiers) {
		super(key, type, name, description, hidden, condition, loadingPriority, modifier, modifiers);
	}

	public static SerializableData getFactory() {
		return ModifierPower.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("modify_xp_gain"));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onXpGain(@NotNull PlayerExpChangeEvent e) {
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (getPlayers().contains(player) && isActive(player)) {
			int original = e.getAmount();
			long modified = Math.round(ModifierUtil.applyModifiers(player, getModifiers(), original));
			e.setAmount(Math.toIntExact(modified));
		}
	}
}
