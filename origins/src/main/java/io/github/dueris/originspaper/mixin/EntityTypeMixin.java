package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import io.github.dueris.originspaper.power.type.ModifyTypeTagPower;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Mixin(EntityType.class)
public class EntityTypeMixin {
	public static HashMap<EntityType<?>, List<Player>> PLAYER_TAG_TIE = new HashMap<>();

	@Inject(method = "is", locator = At.Value.HEAD, params = HolderSet.class)
	public static void apoli$inTagEntryListProxy(@NotNull EntityType<?> instance, @NotNull HolderSet<EntityType<?>> entryList, CallbackInfo info) {
		boolean original = entryList.contains(instance.builtInRegistryHolder());
		boolean modifyTypeTag = false;

		for (Player player : PLAYER_TAG_TIE.getOrDefault(instance, new LinkedList<>())) {
			if (ModifyTypeTagPower.doesApply(player, entryList)) {
				modifyTypeTag = true;
				break;
			}
		}

		info.setReturned(true);
		info.setReturnValue(
			original || modifyTypeTag
		);
	}

	@Inject(method = "is", locator = At.Value.RETURN, params = TagKey.class)
	public static void apoli$inTagProxy(@NotNull EntityType<?> instance, TagKey<EntityType<?>> tag, CallbackInfo info) {
		boolean original = instance.builtInRegistryHolder().is(tag);
		boolean modifyTypeTag = false;

		for (Player player : PLAYER_TAG_TIE.getOrDefault(instance, new LinkedList<>())) {
			if (ModifyTypeTagPower.doesApply(player, tag)) {
				modifyTypeTag = true;
				break;
			}
		}

		info.setReturned(true);
		info.setReturnValue(
			original || modifyTypeTag
		);
	}
}
