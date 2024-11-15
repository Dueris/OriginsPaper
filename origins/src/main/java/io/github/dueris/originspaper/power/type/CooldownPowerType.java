package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.client.MinecraftClient;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.HudRender;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CooldownPowerType extends PowerType implements HudRendered {

	public static final TypedDataObjectFactory<CooldownPowerType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("hud_render", HudRender.DATA_TYPE, HudRender.DONT_RENDER)
			.add("cooldown", SerializableDataTypes.INT),
		data -> new CooldownPowerType(
			data.get("cooldown"),
			data.get("hud_render")
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("hud_render", powerType.hudRender)
			.set("cooldown", powerType.cooldown)
	);

	private final HudRender hudRender;
	private final int cooldown;

	protected long lastUseTime;

	public CooldownPowerType(int cooldown, HudRender hudRender) {
		this(cooldown, hudRender, Optional.empty());
	}

	public CooldownPowerType(int cooldown, HudRender hudRender, Optional<EntityCondition> condition) {
		super(condition);
		this.cooldown = cooldown;
		this.hudRender = hudRender;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.COOLDOWN;
	}

	@Override
	public boolean isActive() {
		return canUse();
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
		return (getHolder().getCommandSenderWorld().getGameTime() - lastUseTime) <= cooldown;
	}

	@Override
	public int getRuntimeMax() {
		return cooldown;
	}

	public boolean canUse() {
		return isInitialized()
			&& getHolder().level().getGameTime() >= lastUseTime + cooldown
			&& super.isActive();
	}

	public void use() {
		this.lastUseTime = getHolder().level().getGameTime();
		PowerHolderComponent.syncPower(getHolder(), getPower());
		if (getHolder() instanceof ServerPlayer player) {
			MinecraftClient.HUD_RENDER.setRender(player.getBukkitEntity(), CraftNamespacedKey.fromMinecraft(getPower().getId()));
		}
	}

	public float getProgress() {
		float time = getHolder().getCommandSenderWorld().getGameTime() - lastUseTime;
		return Math.min(1F, Math.max(time / (float) cooldown, 0F));
	}

	public int getRemainingTicks() {
		return (int) Math.max(0, cooldown - (getHolder().level().getGameTime() - lastUseTime));
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldownInTicks) {
		long currentTime = getHolder().level().getGameTime();
		this.lastUseTime = currentTime - Math.min(cooldownInTicks, cooldown);
		if (getHolder() instanceof ServerPlayer player) {
			MinecraftClient.HUD_RENDER.setRender(player.getBukkitEntity(), CraftNamespacedKey.fromMinecraft(getPower().getId()), cooldownInTicks);
		}
	}

	public void modify(int changeInTicks) {

		this.lastUseTime += changeInTicks;
		long currentTime = getHolder().level().getGameTime();
		if (getHolder() instanceof ServerPlayer player) {
			MinecraftClient.HUD_RENDER.setRender(player.getBukkitEntity(), CraftNamespacedKey.fromMinecraft(getPower().getId()), Math.toIntExact(currentTime - lastUseTime));
		}

		if (this.lastUseTime > currentTime) {
			lastUseTime = currentTime;
		}

	}

}