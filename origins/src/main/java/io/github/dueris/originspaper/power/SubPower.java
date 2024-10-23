package io.github.dueris.originspaper.power;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SubPower extends Power {

	private final ResourceLocation superPowerId;
	private final String subName;

	SubPower(ResourceLocation superPowerId, String subName, @NotNull Power basePower) {
		super(basePower.getFactoryInstance(), basePower.data);
		this.superPowerId = superPowerId;
		this.subName = subName;
	}

	public ResourceLocation getSuperPowerId() {
		return superPowerId;
	}

	public String getSubName() {
		return subName;
	}

}
