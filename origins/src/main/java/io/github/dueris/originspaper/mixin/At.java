package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.locator.HookLocator;
import com.dragoncommissions.mixbukkit.api.locator.impl.HLocatorHead;
import com.dragoncommissions.mixbukkit.api.locator.impl.HLocatorMethodInvoke;
import com.dragoncommissions.mixbukkit.api.locator.impl.HLocatorReturn;
import com.dragoncommissions.mixbukkit.utils.PostPreState;
import io.github.dueris.calio.util.holder.ObjectProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

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
				return new HLocatorMethodInvoke(LivingEntity.class.getDeclaredMethod("checkTotemDeathProtection", DamageSource.class), PostPreState.PRE, (i) -> {
					return i == 0;
				});
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}).get()),
		ON_DIE(((ObjectProvider<HLocatorMethodInvoke>) () -> {
			try {
				return new HLocatorMethodInvoke(LivingEntity.class.getDeclaredMethod("die", DamageSource.class), PostPreState.PRE, (i) -> {
					return i == 0;
				});
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

