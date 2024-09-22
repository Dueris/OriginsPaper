package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.HudRender;
import io.github.dueris.originspaper.data.types.Keybind;
import io.github.dueris.originspaper.event.KeybindTriggerEvent;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import io.github.dueris.originspaper.util.KeybindUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class LaunchPower extends CooldownPower {
	private final float speed;
	private final SoundEvent sound;
	private final Keybind keybind;

	public LaunchPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
					   int cooldown, float speed, SoundEvent sound, HudRender hudRender, Keybind keybind) {
		super(key, type, name, description, hidden, condition, loadingPriority, hudRender, cooldown);
		this.speed = speed;
		this.sound = sound;
		this.keybind = keybind;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("launch"), PowerType.getFactory().getSerializableData()
			.add("cooldown", SerializableDataTypes.INT, 1)
			.add("speed", SerializableDataTypes.FLOAT)
			.add("sound", SerializableDataTypes.SOUND_EVENT, null)
			.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
			.add("key", ApoliDataTypes.KEYBIND, Keybind.DEFAULT_KEYBIND));
	}

	@EventHandler
	public void onKey(@NotNull KeybindTriggerEvent e) {
		Player p = ((CraftPlayer) e.getPlayer()).getHandle();
		if (getPlayers().contains(p) && KeybindUtil.isKeyActive(keybind.key(), e.getPlayer())) {
			if (!p.level().isClientSide && isActive(p) && canUse(p)) {
				p.push(0, speed, 0);
				p.hurtMarked = true;
				if (sound != null) {
					p.level().playSound(null, p.getX(), p.getY(), p.getZ(), sound, SoundSource.NEUTRAL, 0.5F, 0.4F / (p.getRandom().nextFloat() * 0.4F + 0.8F));
				}
				for (int i = 0; i < 4; ++i) {
					((ServerLevel) p.level()).sendParticles(ParticleTypes.CLOUD, p.getX(), p.getRandomY(), p.getZ(), 8, p.getRandom().nextGaussian(), 0.0D, p.getRandom().nextGaussian(), 0.5);
				}

				if (cooldown > 1) {
					use(p);
				}
			}
		}
	}

}
