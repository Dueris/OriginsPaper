package me.dueris.originspaper.factory.conditions.types;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.util.holder.ObjectProvider;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.conditions.meta.MetaConditions;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import me.dueris.originspaper.factory.data.types.Comparison;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.util.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.damage.CraftDamageType;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class DamageConditions {

	public static void registerAll() {
		MetaConditions.register(Registries.DAMAGE_CONDITION, DamageConditions::register);
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("amount"),
			InstanceDefiner.instanceDefiner()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.FLOAT),
			(data, event) -> {
				return ((Comparison) data.get("comparison")).compare(event.getDamage(), data.getFloat("compare_to"));
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("name"),
			InstanceDefiner.instanceDefiner(),
			(data, event) -> {
				return nmsDamageSource(event).getMsgId().equals(data.getString("name"));
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("projectile"),
			InstanceDefiner.instanceDefiner()
				.add("projectile", SerializableDataTypes.ENTITY_TYPE, null)
				.add("projectile_condition", ApoliDataTypes.ENTITY_CONDITION, null),
			(data, event) -> {
				DamageSource source = ((ObjectProvider<DamageSource>) () -> {
					if (event.getDamageSource().getDirectEntity() != null) {
						return Util.getDamageSource(CraftDamageType.bukkitToMinecraft(event.getDamageSource().getDamageType()), ((CraftEntity) event.getDamageSource().getDirectEntity()).getHandle());
					}
					return nmsDamageSource(event);
				}).get();
				if (source.is(DamageTypeTags.IS_PROJECTILE)) {
					Entity projectile = source.getDirectEntity();
					if (projectile != null) {
						if (data.isPresent("projectile") && projectile.getType() != data.get("projectile")) {
							return false;
						}
						Predicate<Entity> projectileCondition = data.get("projectile_condition");
						return projectileCondition == null || projectileCondition.test(projectile);
					}
				}
				return false;
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("attacker"),
			InstanceDefiner.instanceDefiner()
				.add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null),
			(data, event) -> {
				DamageSource source = ((ObjectProvider<DamageSource>) () -> {
					if (event.getDamageSource().getDirectEntity() != null) {
						return Util.getDamageSource(CraftDamageType.bukkitToMinecraft(event.getDamageSource().getDamageType()), ((CraftEntity) event.getDamageSource().getDirectEntity()).getHandle());
					}
					return nmsDamageSource(event);
				}).get();
				Entity attacker = source.getEntity();
				if (attacker instanceof LivingEntity) {
					return !data.isPresent("entity_condition") || ((ConditionFactory<Entity>) data.get("entity_condition")).test(attacker);
				}
				return false;
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("fire"),
			InstanceDefiner.instanceDefiner(),
			(data, event) -> {
				return nmsDamageSource(event).is(DamageTypeTags.IS_FIRE);
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("bypasses_armor"),
			InstanceDefiner.instanceDefiner(),
			(data, event) -> {
				return nmsDamageSource(event).is(DamageTypeTags.BYPASSES_ARMOR);
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("explosive"),
			InstanceDefiner.instanceDefiner(),
			(data, event) -> {
				return nmsDamageSource(event).is(DamageTypeTags.IS_EXPLOSION);
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("from_falling"),
			InstanceDefiner.instanceDefiner(),
			(data, event) -> {
				return nmsDamageSource(event).is(DamageTypeTags.IS_FALL);
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("unblockable"),
			InstanceDefiner.instanceDefiner(),
			(data, event) -> {
				return nmsDamageSource(event).is(DamageTypeTags.BYPASSES_SHIELD);
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("out_of_world"),
			InstanceDefiner.instanceDefiner(),
			(data, event) -> {
				return nmsDamageSource(event).is(DamageTypeTags.BYPASSES_INVULNERABILITY);
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("in_tag"),
			InstanceDefiner.instanceDefiner()
				.add("tag", SerializableDataTypes.tag(net.minecraft.core.registries.Registries.DAMAGE_TYPE)),
			(data, event) -> {
				return nmsDamageSource(event).is((TagKey<DamageType>) data.get("tag"));
			}
		));
		register(new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("type"),
			InstanceDefiner.instanceDefiner()
				.add("damage_type", SerializableDataTypes.DAMAGE_TYPE),
			(data, event) -> {
				return nmsDamageSource(event).is((ResourceKey<DamageType>) data.get("damage_type"));
			}
		));
	}

	public static void register(@NotNull ConditionFactory<EntityDamageEvent> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.DAMAGE_CONDITION).register(factory, factory.getSerializerId());
	}

	private static DamageSource nmsDamageSource(@NotNull EntityDamageEvent event) {
		return Util.getDamageSource(CraftDamageType.bukkitToMinecraft(event.getDamageSource().getDamageType()));
	}

}