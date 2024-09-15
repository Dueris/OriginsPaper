package io.github.dueris.originspaper.mixin;

import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import com.dragoncommissions.mixbukkit.api.locator.HookLocator;
import com.dragoncommissions.mixbukkit.api.locator.impl.HLocatorHead;
import com.dragoncommissions.mixbukkit.api.locator.impl.HLocatorMethodInvoke;
import com.dragoncommissions.mixbukkit.api.locator.impl.HLocatorReturn;
import com.dragoncommissions.mixbukkit.utils.PostPreState;
import io.github.dueris.calio.util.holder.ObjectProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

public final class At {
	private final HookLocator locator;

	private At(HookLocator locator) {
		this.locator = locator;
	}

	public static At buildAt(HookLocator locator) {
		return new At(locator);
	}

	public HookLocator getLocator() {
		return locator;
	}

	public enum Value {
		HEAD(new HLocatorHead()),
		/**
		 * IF NO RETURN IS CALLED, IT WILL USE THE END OF THE METHOD
		 */
		RETURN(new HLocatorReturn()),
		@ApiStatus.Internal
		NONE(null),

		// OriginsPaper AT locators:
		DEATH_CHECK(((ObjectProvider<HLocatorMethodInvoke>) () -> {
			try {
				return new HLocatorMethodInvoke(LivingEntity.class.getDeclaredMethod("checkTotemDeathProtection", DamageSource.class), PostPreState.PRE, (i) -> i == 0);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}).get()),
		ON_DIE(((ObjectProvider<HLocatorMethodInvoke>) () -> {
			try {
				return new HLocatorMethodInvoke(LivingEntity.class.getDeclaredMethod("die", DamageSource.class), PostPreState.PRE, (i) -> i == 0);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}).get()),
		PICKUP_ITEM(((ObjectProvider<HLocatorMethodInvoke>) () -> {
			try {
				return new HLocatorMethodInvoke(Mob.class.getDeclaredMethod("pickUpItem", ItemEntity.class), PostPreState.PRE, (i) -> i == 0);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}).get()),
		ADD_INVENTORY_STACK(((ObjectProvider<HLocatorMethodInvoke>) () -> {
			try {
				return new HLocatorMethodInvoke(Inventory.class.getDeclaredMethod("add", ItemStack.class), PostPreState.PRE, (i) -> i == 0);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}).get()),
		SET_BED_RESPAWN_POS(((ObjectProvider<HLocatorMethodInvoke>) () -> {
			try {
				return new HLocatorMethodInvoke(ServerPlayer.class.getDeclaredMethod("setRespawnPosition", ResourceKey.class, BlockPos.class, float.class, boolean.class, boolean.class, PlayerSetSpawnEvent.Cause.class), PostPreState.PRE, (i) -> i == 0);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}).get()),
		GET_ENCHANT_LEVEL(((ObjectProvider<HLocatorMethodInvoke>) () -> {
			try {
				return new HLocatorMethodInvoke(ItemEnchantments.class.getDeclaredMethod("getLevel", Holder.class), PostPreState.PRE, (i) -> i == 0);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}).get()),
		IN_WATER_OR_RAIN_CHECK(((ObjectProvider<HLocatorMethodInvoke>) () -> {
			try {
				return new HLocatorMethodInvoke(Entity.class.getDeclaredMethod("isInWaterOrRain"), PostPreState.PRE, (i) -> i == 0);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}).get()),
		MAP_PUT(((ObjectProvider<HLocatorMethodInvoke>) () -> {
			try {
				return new HLocatorMethodInvoke(Map.class.getDeclaredMethod("put", Object.class, Object.class), PostPreState.PRE, (i) -> i == 0);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}).get());

		private final HookLocator locator;

		Value(HookLocator locator) {
			this.locator = locator;
		}

		public HookLocator getLocator() {
			return locator;
		}
	}
}

