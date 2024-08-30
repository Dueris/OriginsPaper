package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.power.factory.PowerType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class InvisibilityPower extends PowerType {
	public InvisibilityPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("invisibility"));
	}

	@Override
	public void tick(Player p) {
		boolean shouldSetInvisible = isActive(p);

		p.getBukkitEntity().setInvisible(shouldSetInvisible || p.getBukkitEntity().getActivePotionEffects().stream().map(PotionEffect::getType).toList().contains(PotionEffectType.INVISIBILITY));
	}

	@Override
	public void onRemoved(Player player) {
		player.getBukkitEntity().setInvisible(false);
	}
}
