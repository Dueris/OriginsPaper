package me.dueris.genesismc.factory.powers.apoli;

import io.papermc.paper.util.MCUtil;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.calio.util.ClipContextUtils;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Shape;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Set;

import static me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass.prevent_entity_render;

public class PreventEntityRender extends CraftPower {

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
	public void run(Player p, Power power) {
		if (Bukkit.getServer().getCurrentTick() % 20L == 0) {
			Set<net.minecraft.world.entity.Entity> gotten = Shape.getEntities(Shape.CUBE, ((CraftWorld) p.getWorld()).getHandle(), CraftLocation.toVec3D(p.getLocation()), 80);
			Entity[] show = new Entity[gotten.size()];
			Entity[] hide = new Entity[gotten.size()];
			l:
			for (CraftEntity entity : gotten.stream().map(net.minecraft.world.entity.Entity::getBukkitEntity).toList()) {
				if (ConditionExecutor.testEntity(power.getJsonObject("entity_condition"), entity) && ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
					if (ConditionExecutor.testBiEntity(power.getJsonObject("bientity_condition"), (CraftEntity) p, entity)) {
						if (canSeeEntity(p, entity, null)) {
							p.hideEntity(GenesisMC.getPlugin(), entity);
							for (int i = 0; i < hide.length; i++) {
								if (hide[i] == null) {
									hide[i] = entity;
									continue l;
								}
							}
						}
						setActive(p, power.getTag(), true);
					} else {
						setActive(p, power.getTag(), false);
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
					setActive(p, power.getTag(), false);
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
					p.hideEntity(GenesisMC.getPlugin(), entity);
				}
				for (Entity entity : show) {
					if (entity == null) break;
					p.showEntity(GenesisMC.getPlugin(), entity);
				}
			});
		}
	}

	@Override
	public String getType() {
		return "apoli:prevent_entity_render";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return prevent_entity_render;
	}
}
