package io.github.dueris.originspaper.client.render;

import io.github.dueris.originspaper.client.HudRenderManager;
import io.github.dueris.originspaper.client.MinecraftClient;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;

public record ClientHudRenderer(ServerPlayer player) {

	public void tick() {
		Component message = Component.empty();
		int num = 0;
		Entity vehicle = player.getBukkitEntity().getVehicle();
		if (vehicle != null) {
			if (vehicle instanceof org.bukkit.entity.LivingEntity entity) {
				AttributeInstance instance = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
				if (instance != null) {
					num += (int) (Math.floor((instance.getValue() - 1) / 10) - 1);
				}
			}
		}
		for (NamespacedKey key : MinecraftClient.HUD_RENDER.getRenders(player.getBukkitEntity())) {
			HudRenderManager.RenderInf info = MinecraftClient.HUD_RENDER.renderInf.get(key);
			if (info.getIcon() == null) continue;
			float d = MinecraftClient.HUD_RENDER.getRender(player.getBukkitEntity(), key) / (info.getRuntime() * 50f);
			if (!info.isReversed()) d = 1 - d;
			message = message.append(Component.text("\uF004")).append(MinecraftClient.HUD_RENDER.appendRender(d, info, num));
			num++;
		}
		player.getBukkitEntity().sendActionBar(Component.text("\uF003").font(Key.key("origins:resource_bar/height_0")).append(message));
	}
}
