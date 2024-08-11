package me.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.action.ActionFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class ShowToastAction {

	public static void action(DeserializedFactoryJson data, @NotNull Entity entity) {

		if (!entity.level().isClientSide) {
			String title = data.getString("title");
			String description = data.getString("description");
			ItemStack icon = data.get("icon");

			if (entity.getBukkitEntity() instanceof CraftPlayer player) {
				String advancement = "{\n" +
					"    \"criteria\": {\n" +
					"      \"trigger\": {\n" +
					"        \"trigger\": \"minecraft:impossible\"\n" +
					"      }\n" +
					"    },\n" +
					"    \"display\": {\n" +
					"      \"icon\": {\n" +
					"        \"id\": \"" + icon.getBukkitStack().getType().getKey().asString() + "\"\n" +
					"      },\n" +
					"      \"title\": {\n" +
					"        \"text\": \"" + title + "\"\n" +
					"      },\n" +
					"      \"description\": {\n" +
					"        \"text\": \"" + description + "\"\n" +
					"      },\n" +
					"      \"background\": \"minecraft:textures/gui/advancements/backgrounds/adventure.png\",\n" +
					"      \"frame\": \"task\",\n" +
					"      \"announce_to_chat\": false,\n" +
					"      \"show_toast\": true,\n" +
					"      \"hidden\": true\n" +
					"    }\n" +
					"  }";
				Advancement possible = Bukkit.getAdvancement(CraftNamespacedKey.fromMinecraft(OriginsPaper.apoliIdentifier(title.replace(" ", "_").toLowerCase())));
				Advancement a = possible == null ?
					Bukkit.getUnsafe().loadAdvancement(possible.getKey(), advancement) : possible;
				// Advancement is loaded now
				player.getAdvancementProgress(a).awardCriteria("trigger");
				new BukkitRunnable() {
					@Override
					public void run() {
						player.getAdvancementProgress(a).revokeCriteria("trigger");
					}
				}.runTaskLater(OriginsPaper.getPlugin(), 5);
			}
		}

	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("show_toast"),
			InstanceDefiner.instanceDefiner()
				.add("title", SerializableDataTypes.TEXT)
				.add("description", SerializableDataTypes.TEXT)
				.add("texture", SerializableDataTypes.IDENTIFIER, OriginsPaper.apoliIdentifier("toast/custom"))
				.add("icon", SerializableDataTypes.ITEM_STACK, ItemStack.EMPTY)
				.add("duration", SerializableDataTypes.POSITIVE_INT, 5000),
			ShowToastAction::action
		);
	}
}
