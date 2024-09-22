package io.github.dueris.originspaper.util;

import net.minecraft.world.entity.LivingEntity;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AdditionalEntityAttributesHolder {
	private static final Map<LivingEntity, AdditionalEntityAttributesHolder> HOLDERS = new ConcurrentHashMap<>();
	private final LivingEntity entity;
	private final Map<EntityAttribute, Float> attribute2ValueMap = new ConcurrentHashMap<>();

	public AdditionalEntityAttributesHolder(LivingEntity entity) {
		this.entity = entity;
		load();
	}

	public static AdditionalEntityAttributesHolder getOrCreateHolder(LivingEntity entity) {
		if (!HOLDERS.containsKey(entity)) {
			HOLDERS.put(entity, new AdditionalEntityAttributesHolder(entity));
		}

		return HOLDERS.get(entity);
	}

	public float get(EntityAttribute attribute) {
		if (!has(attribute)) {
			throw new NullPointerException("Entity does not have EntityAttribute instance of " + attribute.name());
		}

		return attribute2ValueMap.get(attribute);
	}

	public boolean has(EntityAttribute attribute) {
		return attribute2ValueMap.containsKey(attribute);
	}

	public void set(EntityAttribute attribute, float f) {
		attribute2ValueMap.put(attribute, f);
	}

	public void clear(EntityAttribute attribute) {
		attribute2ValueMap.remove(attribute);
	}

	public void save() {
		org.bukkit.entity.LivingEntity livingEntity = entity.getBukkitLivingEntity();

		for (EntityAttribute attribute : attribute2ValueMap.keySet()) {

			livingEntity.getPersistentDataContainer().set(attribute.getKey(), PersistentDataType.FLOAT, attribute2ValueMap.get(attribute));
		}
	}

	public void load() {
		org.bukkit.entity.LivingEntity livingEntity = entity.getBukkitLivingEntity();

		for (EntityAttribute value : EntityAttribute.values()) {

			if (livingEntity.getPersistentDataContainer().has(value.getKey())) {
				attribute2ValueMap.put(value, livingEntity.getPersistentDataContainer().get(value.getKey(), PersistentDataType.FLOAT));
			}
		}
	}

	public LivingEntity getEntity() {
		return entity;
	}

	public enum EntityAttribute {
		SWIM_SPEED(new NamespacedKey("additionalentityattributes", "swim_speed"), 0.02D);

		private final NamespacedKey key;
		private final double d;

		EntityAttribute(NamespacedKey key, double d) {
			this.key = key;
			this.d = d;
		}

		public double getDefault() {
			return d;
		}

		public NamespacedKey getKey() {
			return key;
		}
	}
}
