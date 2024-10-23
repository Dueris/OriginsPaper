package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.client.MinecraftClient;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.util.HudRender;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

public class HudRenderedVariableIntPowerType extends VariableIntPowerType implements HudRendered {

	private final HudRender hudRender;

	public HudRenderedVariableIntPowerType(Power power, LivingEntity entity, HudRender hudRender, int startValue, int min, int max) {
		super(power, entity, startValue, min, max);
		this.hudRender = hudRender;
	}

	@Override
	public HudRender getRenderSettings() {
		return hudRender;
	}

	@Override
	public float getFill() {
		return (this.getValue() - this.getMin()) / (float) (this.getMax() - this.getMin());
	}

	@Override
	public boolean shouldRender() {
		return true;
	}

	@Override
	public int getRuntimeMax() {
		return max;
	}

	@Override
	public int setValue(int newValue) {
		int newVal = super.setValue(newValue);
		if (entity instanceof ServerPlayer player) {
			MinecraftClient.HUD_RENDER.setRender(player.getBukkitEntity(), CraftNamespacedKey.fromMinecraft(getPowerId()), newVal);
		}
		return newVal;
	}
}

