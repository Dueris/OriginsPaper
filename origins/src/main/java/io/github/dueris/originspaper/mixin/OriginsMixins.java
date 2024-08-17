package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.MixBukkit;
import com.dragoncommissions.mixbukkit.addons.AutoMapper;
import com.dragoncommissions.mixbukkit.api.MixinPlugin;
import com.dragoncommissions.mixbukkit.api.action.impl.MActionInsertShellCode;
import com.dragoncommissions.mixbukkit.api.locator.impl.HLocatorHead;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.ShellCodeReflectionMixinPluginMethodCall;
import io.github.dueris.originspaper.OriginsPaper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.EnderMan;

public class OriginsMixins {

	public static void init(MixBukkit bukkit) {
		MixinPlugin mixinPlugin = bukkit.registerMixinPlugin(OriginsPaper.getPlugin(), AutoMapper.getMappingAsStream());
		try {
			mixinPlugin.registerMixin(
				"Test Mixin", // Namespace of the mixin, used to identify them/avoid imjecting same mixin multiple times, so any char is allowed
				new MActionInsertShellCode(
					new ShellCodeReflectionMixinPluginMethodCall(TestMixin.class.getDeclaredMethod("hurt", EnderMan.class, DamageSource.class, float.class, CallbackInfo.class)),
					// If you want a document of ShellCodeReflectionMixinPluginMethodCall, check the docs for that (obviously not Getting Started.md)
					new HLocatorHead()
					// Inject to top of the method
				),
				EnderMan.class, // Target class
				"hurt",  // Deobfuscated Method Name
				boolean.class,  // Return Type
				DamageSource.class, float.class // Parameter Types
			);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
}
