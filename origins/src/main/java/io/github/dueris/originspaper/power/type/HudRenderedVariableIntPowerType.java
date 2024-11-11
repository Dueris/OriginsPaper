package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.client.MinecraftClient;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.util.HudRender;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

public abstract class HudRenderedVariableIntPowerType extends VariableIntPowerType implements HudRendered {

	private final HudRender hudRender;

	public HudRenderedVariableIntPowerType(HudRender hudRender, int min, int max, int startValue) {
		super(startValue, min, max);
		this.hudRender = hudRender;
	}

	@Override
	public HudRender getRenderSettings() {
		return hudRender;
	}

	@Override
	public float getFill() {
		return (this.getValue() - this.getMin()) / (float)(this.getMax() - this.getMin());
	}

	@Override
	public boolean shouldRender() {
		return true;
	}

	@Override
	public int getRuntimeMax() {
		return super.getMax();
	}

	@Override
	public int setValue(int newValue) {
		int newVal = super.setValue(newValue);
		if (getHolder() instanceof ServerPlayer player) {
			MinecraftClient.HUD_RENDER.setRender(player.getBukkitEntity(), CraftNamespacedKey.fromMinecraft(getPower().getId()), newVal);
		}
		return newVal;
	}
}
