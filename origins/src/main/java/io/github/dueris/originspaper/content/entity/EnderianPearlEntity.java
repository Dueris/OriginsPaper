package io.github.dueris.originspaper.content.entity;

import io.github.dueris.originspaper.OriginsPaper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EnderianPearlEntity extends ThrowableItemProjectile {
	public static final EntityType<EnderianPearlEntity> ENDERIAN_PEARL = register("enderian_pearl", EntityType.Builder.of(EnderianPearlEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));

	public EnderianPearlEntity(EntityType<? extends EnderianPearlEntity> type, Level world) {
		super(type, world);
	}

	public static void bootstrap() {
	}

	private static <T extends Entity> @NotNull EntityType<T> register(String id, EntityType.@NotNull Builder type) {
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, OriginsPaper.originIdentifier(id), (EntityType<T>) type.build(id));
	}

	@Override
	protected @NotNull Item getDefaultItem() {
		return Items.ENDER_PEARL;
	}
}
