package io.github.dueris.originspaper.client.resource;

import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ResourceManager implements Listener {
	private static final String url = "https://github.com/Dueris/OriginsPaper/raw/refs/heads/origin/resource/originspaper-1.3.0.zip";
	private static final UUID packUUID = UUID.nameUUIDFromBytes(url.getBytes(StandardCharsets.UTF_8));

	public static void applyPlayer(@NotNull ServerPlayer player) {
		CraftPlayer craftPlayer = player.getBukkitEntity();
		craftPlayer.addResourcePack(
			packUUID, url, null, "This server requires the OriginsPaper client rendering pack to continue", true
		);
	}

	@EventHandler
	public void clientResponseCallback(@NotNull PlayerResourcePackStatusEvent event) {
		if (packUUID.equals(event.getID())) {
			switch (event.getStatus()) {
				case DECLINED, DISCARDED, FAILED_DOWNLOAD, FAILED_RELOAD -> {
					event.getPlayer().kick(Component.text("OriginsPaper requires the client rendering pack to join. Failed for reason: " + event.getStatus()));
				}
			}
		}
	}
}
