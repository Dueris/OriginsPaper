package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.client.MinecraftClient;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.HudRender;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

public class CooldownPowerType extends PowerType implements HudRendered {

	public final int cooldownDuration;
	private final HudRender hudRender;
	protected long lastUseTime;

	public CooldownPowerType(Power power, LivingEntity entity, int cooldownDuration, HudRender hudRender) {
		super(power, entity);
		this.cooldownDuration = cooldownDuration;
		this.hudRender = hudRender;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("cooldown"),
			new SerializableData()
				.add("cooldown", SerializableDataTypes.INT)
				.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER),
			data -> (power, entity) -> new CooldownPowerType(power, entity,
				data.get("cooldown"),
				data.get("hud_render")
			)
		).allowCondition();
	}

	public boolean canUse() {
		return entity.getCommandSenderWorld().getGameTime() >= lastUseTime + cooldownDuration && isActive();
	}

	public void use() {
		lastUseTime = entity.getCommandSenderWorld().getGameTime();
		PowerHolderComponent.syncPower(entity, this.power);
		if (entity instanceof ServerPlayer player) {
			MinecraftClient.HUD_RENDER.setRender(player.getBukkitEntity(), CraftNamespacedKey.fromMinecraft(getPowerId()));
		}
	}

	@Override
	public int getRuntimeMax() {
		return cooldownDuration;
	}

	@Override
	public boolean shouldTick() {
		return true;
	}

	public float getProgress() {
		float time = entity.getCommandSenderWorld().getGameTime() - lastUseTime;
		return Math.min(1F, Math.max(time / (float) cooldownDuration, 0F));
	}

	public int getRemainingTicks() {
		return (int) Math.max(0, cooldownDuration - (entity.getCommandSenderWorld().getGameTime() - lastUseTime));
	}

	public void modify(int changeInTicks) {
		this.lastUseTime += changeInTicks;
		long currentTime = entity.getCommandSenderWorld().getGameTime();
		if (entity instanceof ServerPlayer player) {
			MinecraftClient.HUD_RENDER.setRender(player.getBukkitEntity(), CraftNamespacedKey.fromMinecraft(getPowerId()), Math.toIntExact(currentTime - lastUseTime));
		}
		if (this.lastUseTime > currentTime) {
			lastUseTime = currentTime;
		}
	}

	public void setCooldown(int cooldownInTicks) {
		long currentTime = entity.getCommandSenderWorld().getGameTime();
		this.lastUseTime = currentTime - Math.min(cooldownInTicks, cooldownDuration);
		if (entity instanceof ServerPlayer player) {
			MinecraftClient.HUD_RENDER.setRender(player.getBukkitEntity(), CraftNamespacedKey.fromMinecraft(getPowerId()), cooldownInTicks);
		}
	}

	@Override
	public Tag toTag() {
		return LongTag.valueOf(lastUseTime);
	}

	@Override
	public void fromTag(Tag tag) {
		lastUseTime = ((LongTag) tag).getAsLong();
	}

	@Override
	public HudRender getRenderSettings() {
		return hudRender;
	}

	@Override
	public float getFill() {
		return getProgress();
	}

	@Override
	public boolean shouldRender() {
		return (entity.getCommandSenderWorld().getGameTime() - lastUseTime) <= cooldownDuration;
	}

}
