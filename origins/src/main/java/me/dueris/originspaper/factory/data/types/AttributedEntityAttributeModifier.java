package me.dueris.originspaper.factory.data.types;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record AttributedEntityAttributeModifier(Holder<Attribute> attribute, AttributeModifier modifier) {

	@Deprecated
	public Holder<Attribute> getAttribute() {
		return this.attribute();
	}

	@Deprecated
	public AttributeModifier getModifier() {
		return this.modifier();
	}

}
