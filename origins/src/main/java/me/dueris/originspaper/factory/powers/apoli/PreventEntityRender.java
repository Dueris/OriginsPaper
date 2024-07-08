package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import io.papermc.paper.util.MCUtil;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.util.ClipContextUtils;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.data.types.Shape;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Set;

public class PreventEntityRender extends PowerType {
	private final FactoryJsonObject entityCondition;
	private final FactoryJsonObject bientityCondition;

	public PreventEntityRender(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject entityCondition, FactoryJsonObject bientityCondition) {
		super(name, description, hidden, condition, loading_priority);
		this.entityCondition = entityCondition;
		this.bientityCondition = bientityCondition;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("prevent_entity_render"))
			.add("entity_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("bientity_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	public static boolean canSeeEntity(Entity actor, Entity target, FactoryJsonObject source) {
		net.minecraft.world.entity.Entity nmsActor = ((CraftEntity) actor).getHandle();
		net.minecraft.world.entity.Entity nmsTarget = ((CraftEntity) target).getHandle();

		if ((nmsActor == null || nmsTarget == null) || nmsActor.level() != nmsTarget.level()) return false;
		ClipContext.Block shapeType;
		ClipContext.Fluid fluidHandling;
		if (source != null) {
			shapeType = ClipContextUtils.getShapeType(source.getStringOrDefault("shape_type", "visual"));
			fluidHandling = ClipContextUtils.getFluidHandling(source.getStringOrDefault("fluid_handling", "none"));
		} else {
			shapeType = ClipContext.Block.VISUAL;
			fluidHandling = ClipContext.Fluid.NONE;
		}

		Vec3 actorEyePos = nmsActor.getEyePosition();
		Vec3 targetEyePos = nmsTarget.getEyePosition();
		return nmsActor.level().clip(new ClipContext(actorEyePos, targetEyePos, shapeType, fluidHandling, nmsActor)).getType() == HitResult.Type.MISS;
	}

	@Override
	public void tick(Player p) {
		if (Bukkit.getServer().getCurrentTick() % 20L == 0) {
			Set<net.minecraft.world.entity.Entity> gotten = Shape.getEntities(Shape.CUBE, ((CraftWorld) p.getWorld()).getHandle(), CraftLocation.toVec3D(p.getLocation()), 80);
			Entity[] show = new Entity[gotten.size()];
			Entity[] hide = new Entity[gotten.size()];
			l:
			for (CraftEntity entity : gotten.stream().map(net.minecraft.world.entity.Entity::getBukkitEntity).toList()) {
				if (ConditionExecutor.testEntity(entityCondition, entity) && isActive(p)) {
					if (ConditionExecutor.testBiEntity(bientityCondition, (CraftEntity) p, entity)) {
						if (canSeeEntity(p, entity, null)) {
							p.hideEntity(OriginsPaper.getPlugin(), entity);
							for (int i = 0; i < hide.length; i++) {
								if (hide[i] == null) {
									hide[i] = entity;
									continue l;
								}
							}
						}
					} else {
						if (!canSeeEntity(p, entity, null)) {
							for (int i = 0; i < show.length; i++) {
								if (show[i] == null) {
									show[i] = entity;
									continue l;
								}
							}
						}
					}
				} else {
					if (!canSeeEntity(p, entity, null)) {
						for (int i = 0; i < show.length; i++) {
							if (show[i] == null) {
								show[i] = entity;
								continue l;
							}
						}
					}
				}
			}

			MCUtil.scheduleAsyncTask(() -> {
				for (Entity entity : hide) {
					if (entity == null) break;
					p.hideEntity(OriginsPaper.getPlugin(), entity);
				}
				for (Entity entity : show) {
					if (entity == null) break;
					p.showEntity(OriginsPaper.getPlugin(), entity);
				}
			});
		}
	}

}
