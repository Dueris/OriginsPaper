package io.github.dueris.calio.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RegistryOps.HolderLookupAdapter.class)
public interface HolderLookupAdapterAccessor {

	@Final
	@Accessor
	HolderLookup.Provider getLookupProvider();

}
