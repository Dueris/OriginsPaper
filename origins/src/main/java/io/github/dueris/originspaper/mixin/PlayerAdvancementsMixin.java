package io.github.dueris.originspaper.mixin;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.OriginComponent;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginManager;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {

	@Shadow
	private ServerPlayer player;

	@Shadow
	public abstract AdvancementProgress getOrStartProgress(AdvancementHolder advancement);

	@Inject(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerAdvancements;unregisterListeners(Lnet/minecraft/advancements/AdvancementHolder;)V"))
	private void checkOriginUpgrade(AdvancementHolder advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {

		AdvancementProgress progress = this.getOrStartProgress(advancement);
		if (!progress.isDone() || !OriginComponent.ORIGIN.isProvidedBy(player)) {
			return;
		}

		Origin.get(player).forEach((originLayer, origin) -> origin.getUpgrade(advancement).ifPresent(originUpgrade -> {
			try {

				Origin upgradeTo = OriginManager.get(originUpgrade.upgradeToOrigin());
				OriginComponent component = OriginComponent.ORIGIN.get(player);

				component.setOrigin(originLayer, upgradeTo);
				component.sync();

				String announcement = originUpgrade.announcement();
				if (announcement != null) {
					player.displayClientMessage(Component.translatable(announcement).withStyle(ChatFormatting.GOLD), false);
				}

			} catch (Exception e) {
				OriginsPaper.LOGGER.error("Could not perform Origins upgrade from \"{}\" to \"{}\", as the upgrade origin did not exist!", origin.getId(), originUpgrade.upgradeToOrigin().toString());
			}
		}));

	}
}
