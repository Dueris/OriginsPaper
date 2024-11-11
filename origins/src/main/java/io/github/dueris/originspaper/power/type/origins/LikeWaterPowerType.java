package io.github.dueris.originspaper.power.type.origins;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.power.type.PowerType;
import net.minecraft.world.entity.player.Player;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class LikeWaterPowerType extends PowerType {
	private static final AttributeModifier modifier = new AttributeModifier(CraftNamespacedKey.fromMinecraft(OriginsPaper.identifier("likewater")), -1, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

	public LikeWaterPowerType() {
		super(Optional.empty());
	}

	public static void tick(@NotNull Player player) {
		CraftPlayer p = (CraftPlayer) player.getBukkitEntity();

		if (!PowerHolderComponent.hasPowerType(player, LikeWaterPowerType.class)) {
			if (p.getAttribute(Attribute.GENERIC_GRAVITY).getModifiers().contains(modifier)) {
				p.getAttribute(Attribute.GENERIC_GRAVITY).removeModifier(modifier);
			}
			return;
		}
		if (p.isInWaterOrBubbleColumn() && !p.isSneaking() && !p.isSwimming()) {
			if (!p.getAttribute(Attribute.GENERIC_GRAVITY).getModifiers().contains(modifier)) {
				p.getAttribute(Attribute.GENERIC_GRAVITY).addTransientModifier(modifier);
			}
		} else {
			if (p.getAttribute(Attribute.GENERIC_GRAVITY).getModifiers().contains(modifier)) {
				p.getAttribute(Attribute.GENERIC_GRAVITY).removeModifier(modifier);
			}
		}
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return OriginsPowerTypes.LIKE_WATER;
	}
}
