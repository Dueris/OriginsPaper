package io.github.dueris.calio.mixin;

import net.minecraft.resources.RegistryOps;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RegistryOps.class)
public interface RegistryOpsAccessor {

	@Final
	@Accessor
	RegistryOps.RegistryInfoLookup getLookupProvider();

}
