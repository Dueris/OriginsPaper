package me.dueris.originspaper.factory.conditions.types;

import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.conditions.meta.MetaConditions;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.util.Util;
import net.minecraft.world.damagesource.DamageSource;
import org.bukkit.craftbukkit.damage.CraftDamageType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class DamageConditions {

	public static void registerConditions() {
		MetaConditions.register(Registries.DAMAGE_CONDITION, DamageConditions::register);
		/*register(new ConditionFactory(OriginsPaper.apoliIdentifier("amount"), (data, damageEvent) -> {
			return Comparison.fromString(data.getString("comparison")).compare(damageEvent.getDamage(), data.getNumber("compare_to").getFloat());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("fire"), (data, damageEvent) -> {
			return nmsDamageSource(damageEvent).is(DamageTypeTags.IS_FIRE);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("name"), (data, damageEvent) -> {
			return nmsDamageSource(damageEvent).getMsgId().equals(data.getString("name"));
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("projectile"), (data, damageEvent) -> {
			if (damageEvent.getDamageSource().getDirectEntity() == null) return false;
			DamageSource nms = new DamageSource(nmsDamageSource(damageEvent).typeHolder(), ((CraftEntity) damageEvent.getDamageSource().getDirectEntity()).getHandle());
			if (nms.is(DamageTypeTags.IS_PROJECTILE)) {
				Entity projectile = nms.getDirectEntity();
				if (projectile != null) {
					if (data.isPresent("projectile") && projectile.getType() != CraftRegistry.getMinecraftRegistry().registry(net.minecraft.core.registries.Registries.ENTITY_TYPE).get().get(data.getResourceLocation("projectile"))) {
						return false;
					}
					@NotNull FactoryJsonObject projectileCondition = data.getJsonObject("projectile_condition");
					return projectileCondition.isEmpty() || ConditionExecutor.testEntity(projectileCondition, projectile.getBukkitEntity());
				}
			}
			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("attacker"), (data, damageEvent) -> {
			if (damageEvent.getDamageSource() == null || damageEvent.getDamageSource().getDirectEntity() == null)
				return false;
			Entity attacker = new DamageSource(nmsDamageSource(damageEvent).typeHolder(), ((CraftEntity) damageEvent.getDamageSource().getDirectEntity()).getHandle()).getEntity();
			if (attacker instanceof LivingEntity) {
				return !data.isPresent("entity_condition") || ConditionExecutor.testEntity(data.getJsonObject("entity_condition"), attacker.getBukkitEntity());
			}
			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("bypasses_armor"), (data, damageEvent) -> {
			return nmsDamageSource(damageEvent).is(DamageTypeTags.BYPASSES_ARMOR);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("explosive"), (data, damageEvent) -> {
			return nmsDamageSource(damageEvent).is(DamageTypeTags.IS_EXPLOSION);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("from_falling"), (data, damageEvent) -> {
			return nmsDamageSource(damageEvent).is(DamageTypeTags.IS_FALL);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("unblockable"), (data, damageEvent) -> {
			return nmsDamageSource(damageEvent).is(DamageTypeTags.BYPASSES_SHIELD);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("out_of_world"), (data, damageEvent) -> {
			return nmsDamageSource(damageEvent).is(DamageTypeTags.BYPASSES_INVULNERABILITY);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("in_tag"), (data, damageEvent) -> {
			return nmsDamageSource(damageEvent).is(data.getTagKey("tag", net.minecraft.core.registries.Registries.DAMAGE_TYPE));
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("type"), (data, damageEvent) -> {
			return nmsDamageSource(damageEvent).is(data.resourceKey("damage_type", net.minecraft.core.registries.Registries.DAMAGE_TYPE));
		}));*/
	}

	public static void register(@NotNull ConditionFactory<EntityDamageEvent> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.DAMAGE_CONDITION).register(factory, factory.getSerializerId());
	}

	private DamageSource nmsDamageSource(@NotNull EntityDamageEvent event) {
		return Util.getDamageSource(CraftDamageType.bukkitToMinecraft(event.getDamageSource().getDamageType()));
	}

}