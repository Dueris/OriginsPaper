package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.action.BlockAction;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Space;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.function.Predicate;

public class RaycastEntityActionType extends EntityActionType {

	public static final TypedDataObjectFactory<RaycastEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("before_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("hit_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("miss_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("bientity_action", BiEntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("block_action", BlockAction.DATA_TYPE.optional(), Optional.empty())
			.add("bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty())
			.add("shape_type", SerializableDataTypes.SHAPE_TYPE, ClipContext.Block.OUTLINE)
			.add("fluid_handling", SerializableDataTypes.FLUID_HANDLING, ClipContext.Fluid.ANY)
			.add("direction", SerializableDataTypes.VECTOR.optional(), Optional.empty())
			.add("space", ApoliDataTypes.SPACE, Space.WORLD)
			.add("entity_distance", SerializableDataTypes.DOUBLE.optional(), Optional.empty())
			.add("block_distance", SerializableDataTypes.DOUBLE.optional(), Optional.empty())
			.add("distance", SerializableDataTypes.DOUBLE.optional(), Optional.empty())
			.add("command_at_hit", SerializableDataTypes.STRING.optional(), Optional.empty())
			.add("command_along_ray", SerializableDataTypes.STRING.optional(), Optional.empty())
			.add("command_hit_offset", SerializableDataTypes.DOUBLE.optional(), Optional.empty())
			.add("command_step", SerializableDataTypes.DOUBLE, 1.0D)
			.add("command_along_ray_only_on_hit", SerializableDataTypes.BOOLEAN, false)
			.add("entity", SerializableDataTypes.BOOLEAN, true)
			.add("block", SerializableDataTypes.BOOLEAN, true),
		data -> new RaycastEntityActionType(
			data.get("before_action"),
			data.get("hit_action"),
			data.get("miss_action"),
			data.get("bientity_action"),
			data.get("block_action"),
			data.get("bientity_condition"),
			data.get("shape_type"),
			data.get("fluid_handling"),
			data.get("direction"),
			data.get("space"),
			data.get("entity_distance"),
			data.get("block_distance"),
			data.get("distance"),
			data.get("command_at_hit"),
			data.get("command_along_ray"),
			data.get("command_hit_offset"),
			data.get("command_step"),
			data.get("command_along_ray_only_on_hit"),
			data.get("entity"),
			data.get("block")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("before_action", actionType.beforeAction)
			.set("hit_action", actionType.hitAction)
			.set("miss_action", actionType.missAction)
			.set("bientity_action", actionType.biEntityAction)
			.set("block_action", actionType.blockAction)
			.set("bientity_condition", actionType.biEntityCondition)
			.set("shape_type", actionType.shapeType)
			.set("fluid_handling", actionType.fluidHandling)
			.set("direction", actionType.direction)
			.set("space", actionType.space)
			.set("entity_distance", actionType.entityDistance)
			.set("block_distance", actionType.blockDistance)
			.set("distance", actionType.distance)
			.set("command_at_hit", actionType.commandAtHit)
			.set("command_along_ray", actionType.commandAlongRay)
			.set("command_hit_offset", actionType.commandHitOffset)
			.set("command_step", actionType.commandStep)
			.set("command_along_ray_only_on_hit", actionType.commandAlongRayOnlyOnHit)
			.set("entity", actionType.entity)
			.set("block", actionType.block)
	);

	private final Optional<EntityAction> beforeAction;
	private final Optional<EntityAction> hitAction;
	private final Optional<EntityAction> missAction;

	private final Optional<BiEntityAction> biEntityAction;
	private final Optional<BlockAction> blockAction;

	private final Optional<BiEntityCondition> biEntityCondition;

	private final ClipContext.Block shapeType;
	private final ClipContext.Fluid fluidHandling;

	private final Optional<Vec3> direction;
	private final Space space;

	private final Optional<Double> entityDistance;
	private final Optional<Double> blockDistance;
	private final Optional<Double> distance;

	private final Optional<String> commandAtHit;
	private final Optional<String> commandAlongRay;

	private final Optional<Double> commandHitOffset;
	private final double commandStep;

	private final boolean commandAlongRayOnlyOnHit;

	private final boolean entity;
	private final boolean block;

	public RaycastEntityActionType(Optional<EntityAction> beforeAction, Optional<EntityAction> hitAction, Optional<EntityAction> missAction, Optional<BiEntityAction> biEntityAction, Optional<BlockAction> blockAction, Optional<BiEntityCondition> biEntityCondition, ClipContext.Block shapeType, ClipContext.Fluid fluidHandling, Optional<Vec3> direction, Space space, Optional<Double> entityDistance, Optional<Double> blockDistance, Optional<Double> distance, Optional<String> commandAtHit, Optional<String> commandAlongRay, Optional<Double> commandHitOffset, double commandStep, boolean commandAlongRayOnlyOnHit, boolean entity, boolean block) {
		this.beforeAction = beforeAction;
		this.hitAction = hitAction;
		this.missAction = missAction;
		this.biEntityAction = biEntityAction;
		this.blockAction = blockAction;
		this.biEntityCondition = biEntityCondition;
		this.shapeType = shapeType;
		this.fluidHandling = fluidHandling;
		this.direction = direction;
		this.space = space;
		this.entityDistance = entityDistance;
		this.blockDistance = blockDistance;
		this.distance = distance;
		this.commandAtHit = commandAtHit;
		this.commandAlongRay = commandAlongRay;
		this.commandHitOffset = commandHitOffset;
		this.commandStep = commandStep;
		this.commandAlongRayOnlyOnHit = commandAlongRayOnlyOnHit;
		this.entity = entity;
		this.block = block;
	}

	private static boolean overrideHitResult(Entity caster, @Nullable HitResult prev, HitResult next) {
		return prev == null
			|| prev.getType() == HitResult.Type.MISS
			|| prev.distanceTo(caster) > next.distanceTo(caster);
	}

	@Override
	protected void execute(Entity entity) {

		beforeAction.ifPresent(action -> action.execute(entity));

		Vec3 origin = entity.position();
		Vec3 direction = this.direction
			.map(dir -> transformDirection(entity, dir))
			.orElseGet(() -> entity.getViewVector(1.0F));

		double distance = getReach(entity);

		Vec3 destination = origin.add(direction.scale(distance));
		HitResult hitResult = null;

		if (this.entity) {
			hitResult = entityRaycast(entity, origin, destination);
		}

		if (this.block) {

			BlockHitResult blockResult = blockRaycast(entity, origin, destination);

			if (blockResult.getType() != HitResult.Type.MISS && overrideHitResult(entity, hitResult, blockResult)) {
				hitResult = blockResult;
			}

		}

		boolean hit = hitResult != null
			&& hitResult.getType() != HitResult.Type.MISS;

		if (hit && commandAtHit.isPresent()) {

			Vec3 hitPos = hitResult.getLocation();
			Offset offset = getOffset(entity, hitResult, direction);

			hitPos = hitPos.subtract(offset.direction().scale(offset.amount()));
			executeCommandAtHit(entity, hitPos);

		}

		if (commandAlongRay.isPresent() && (!commandAlongRayOnlyOnHit || hit)) {
			executeCommandAtSteps(entity, origin, destination);
		}

		if (hit) {

			switch (hitResult) {
				case BlockHitResult blockResult ->
					blockAction.ifPresent(action -> action.execute(entity.level(), blockResult.getBlockPos(), Optional.of(blockResult.getDirection())));
				case EntityHitResult entityResult ->
					biEntityAction.ifPresent(action -> action.execute(entity, entityResult.getEntity()));
				default -> {

				}
			}

			hitAction.ifPresent(action -> action.execute(entity));

		} else {
			missAction.ifPresent(action -> action.execute(entity));
		}

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.RAYCAST;
	}

	private EntityHitResult entityRaycast(Entity caster, Vec3 origin, Vec3 destination) {

		Vec3 ray = destination.subtract(origin);
		AABB box = caster.getBoundingBox().expandTowards(ray).inflate(1.0D);

		Predicate<Entity> intersectPredicate = EntitySelector.NO_SPECTATORS
			.and(intersected -> biEntityCondition
				.map(condition -> condition.test(caster, intersected))
				.orElse(true));

		return ProjectileUtil.getEntityHitResult(
			caster,
			origin,
			destination,
			box,
			intersectPredicate,
			ray.lengthSqr()
		);

	}

	private BlockHitResult blockRaycast(Entity caster, Vec3 origin, Vec3 destination) {
		ClipContext context = new ClipContext(origin, destination, shapeType, fluidHandling, caster);
		return caster.level().clip(context);
	}

	private Vec3 transformDirection(Entity entity, Vec3 direction) {

		Vector3f normalizedDirection = new Vector3f((float) direction.x(), (float) direction.y(), (float) direction.z()).normalize();
		space.toGlobal(normalizedDirection, entity);

		return new Vec3(normalizedDirection);

	}

	private Offset getOffset(Entity entity, HitResult hitResult, Vec3 direction) {

		if (commandHitOffset.isPresent()) {
			return new Offset(direction, commandHitOffset.get());
		} else {

			Vec3 offsetDirection = direction;
			double offset = 0.0D;

			if (hitResult instanceof BlockHitResult blockResult) {

				Direction hitSide = blockResult.getDirection();

				switch (hitSide) {
					case DOWN -> offset = entity.getBbHeight();
					case UP -> offset = 0;
					default -> {

						double offsetX = hitSide.getStepX();
						double offsetY = hitSide.getStepY();
						double offsetZ = hitSide.getStepZ();

						offset = entity.getBbWidth() / 2;
						offsetDirection = new Vec3(offsetX, offsetY, offsetZ).reverse();

					}
				}

			}

			offset += 0.05;
			return new Offset(offsetDirection, offset);

		}

	}

	private double getReach(Entity entity) {

		if (this.entity) {
			return getEntityReach(entity);
		} else if (this.block) {
			return this.getBlockReach(entity);
		} else {
			return distance.orElse(1.0D);
		}

	}

	private double getEntityReach(Entity entity) {
		return entityDistance
			.or(() -> distance)
			.orElseGet(() -> entity instanceof LivingEntity livingEntity
				? livingEntity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE)
				: 1.0);
	}

	private double getBlockReach(Entity entity) {
		return blockDistance
			.or(() -> distance)
			.orElseGet(() -> entity instanceof LivingEntity livingEntity
				? livingEntity.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE)
				: 1.0);
	}

	private void executeCommandAtSteps(Entity entity, Vec3 origin, Vec3 destination) {

		MinecraftServer server = entity.getServer();
		if (server == null) {
			return;
		}

		Vec3 direction = destination.subtract(origin);
		double distance = origin.distanceTo(destination);

		CommandSourceStack commandSource = entity.createCommandSourceStack()
			.withSource(CommandSource.NULL)
			.withPermission(OriginsPaper.config.executeCommand.permissionLevel);

		if (OriginsPaper.config.executeCommand.showOutput) {
			commandSource = commandSource.withSource(entity instanceof ServerPlayer serverPlayer && serverPlayer.connection != null
				? serverPlayer
				: server);
		}

		for (double steps = 0; steps < distance; steps += commandStep) {

			Vec3 offsetPos = direction.scale(steps);
			Vec3 newPos = origin.add(offsetPos);

			CommandSourceStack offsetCommandSource = commandSource.withPosition(newPos);
			commandAlongRay.ifPresent(command -> server.getCommands().performPrefixedCommand(offsetCommandSource, command));

		}

	}

	private void executeCommandAtHit(Entity entity, Vec3 hitPos) {

		MinecraftServer server = entity.getServer();
		if (server == null) {
			return;
		}

		CommandSourceStack commandSource = entity.createCommandSourceStack()
			.withSource(CommandSource.NULL)
			.withPosition(hitPos)
			.withPermission(OriginsPaper.config.executeCommand.permissionLevel);

		if (OriginsPaper.config.executeCommand.showOutput) {
			commandSource = commandSource.withSource(entity instanceof ServerPlayer serverPlayer && serverPlayer.connection != null
				? serverPlayer
				: server);
		}

		CommandSourceStack finalCommandSource = commandSource;
		commandAtHit.ifPresent(command -> server.getCommands().performPrefixedCommand(finalCommandSource, command));

	}

	private record Offset(Vec3 direction, double amount) {

	}

}
