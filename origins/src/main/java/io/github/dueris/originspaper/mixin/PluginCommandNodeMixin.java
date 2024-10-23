package io.github.dueris.originspaper.mixin;

import io.github.dueris.originspaper.plugin.OriginsPlugin;
import io.github.dueris.originspaper.plugin.PluginInstances;
import io.papermc.paper.command.brigadier.PluginCommandNode;
import io.papermc.paper.plugin.configuration.PluginMeta;
import org.bukkit.plugin.Plugin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PluginCommandNode.class)
public class PluginCommandNodeMixin {
	@Shadow
	@Final
	private PluginMeta plugin;

	@Inject(method = "getPlugin", at = @At("HEAD"), cancellable = true)
	public void originspaper$redirectPluginInstance(CallbackInfoReturnable<Plugin> cir) {
		if (this.plugin.equals(PluginInstances.APOLI_META) || this.plugin.equals(PluginInstances.CALIO_META)) {
			cir.setReturnValue(OriginsPlugin.plugin);
		}
	}
}
