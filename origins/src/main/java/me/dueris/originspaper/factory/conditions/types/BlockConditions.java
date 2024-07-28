package me.dueris.originspaper.factory.conditions.types;

import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.conditions.meta.MetaConditions;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;


public class BlockConditions {

	public static void registerAll() {
		MetaConditions.register(Registries.BLOCK_CONDITION, BlockConditions::register);
		/*register(new ConditionFactory(OriginsPaper.apoliIdentifier("offset"), (data, block) -> {
			return ConditionExecutor.testBlock(data.getJsonObject("condition"), block.getWorld().getBlockAt(block.getLocation().offset(
				data.getNumberOrDefault("x", 0).getInt(),
				data.getNumberOrDefault("y", 0).getInt(),
				data.getNumberOrDefault("z", 0).getInt()
			).toLocation(block.getWorld())));
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("height"), (data, block) -> {
			return Comparison.fromString(data.getString("comparison")).compare(block.getY(), data.getNumber("compare_to").getInt());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("distance_from_coordinates"), (data, block) -> {
			boolean scaleReferenceToDimension = data.getBooleanOrDefault("scale_reference_to_dimension", true),
				setResultOnWrongDimension = data.isPresent("result_on_wrong_dimension"),
				resultOnWrongDimension = setResultOnWrongDimension && data.getBoolean("result_on_wrong_dimension");
			double x = 0, y = 0, z = 0;
			Comparison comparison = Comparison.fromString(data.getString("comparison"));
			Vec3 pos;
			Level world;
			BlockPos blockPos = CraftLocation.toBlockPosition(block.getLocation());
			pos = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
			LevelReader worldView = block.getCraftWorld().getHandle();
			if (!(worldView instanceof Level))
				return warnCouldNotGetObject("world", "block", compareOutOfBounds(Comparison.fromString(data.getString("comparison"))));
			else
				world = (Level) worldView;
			double currentDimensionCoordinateScale = world.dimensionType().coordinateScale();

			// Get the reference's scaled coordinates
			switch (data.getStringOrDefault("reference", "world_origin")) {
				case "player_spawn":
//                 if (entity instanceof ServerPlayerEntity) { // null instance of AnyClass is always false so the block case is covered
//
//                 }
//                 // No break on purpose (defaulting to natural spawn)
				case "world_spawn":
					if (setResultOnWrongDimension && world.dimension() != Level.OVERWORLD)
						return resultOnWrongDimension;
					BlockPos spawnPos;
					if (world instanceof ServerLevel)
						spawnPos = world.getSharedSpawnPos();
					else
						return warnCouldNotGetObject("world with spawn position", "entity", compareOutOfBounds(comparison));
					x = spawnPos.getX();
					y = spawnPos.getY();
					z = spawnPos.getZ();
					break;
				case "world_origin":
					break;
			}
			Vec3 coords = VectorGetter.getNMSVector(data.getJsonObject("coordinates"));
			Vec3 offset = VectorGetter.getNMSVector(data.getJsonObject("offset"));
			x += coords.x + offset.x;
			y += coords.y + offset.y;
			z += coords.z + offset.z;
			if (scaleReferenceToDimension && (x != 0 || z != 0)) {
				if (currentDimensionCoordinateScale == 0) // pocket dimensions?
					// coordinate scale 0 means it takes 0 blocks to travel in the OW to travel 1 block in the dimension,
					// so the dimension is folded on 0 0, so unless the OW reference is at 0 0, it gets scaled to infinity
					return compareOutOfBounds(comparison);
				x /= currentDimensionCoordinateScale;
				z /= currentDimensionCoordinateScale;
			}

			// Get the distance to these coordinates
			double distance,
				xDistance = data.getBooleanOrDefault("ignore_x", false) ? 0 : Math.abs(pos.x() - x),
				yDistance = data.getBooleanOrDefault("ignore_y", false) ? 0 : Math.abs(pos.y() - y),
				zDistance = data.getBooleanOrDefault("ignore_z", false) ? 0 : Math.abs(pos.z() - z);
			if (data.getBooleanOrDefault("scale_distance_to_dimension", false)) {
				xDistance *= currentDimensionCoordinateScale;
				zDistance *= currentDimensionCoordinateScale;
			}

			distance = Shape.getDistance(data.getEnumValueOrDefault("shape", Shape.class, Shape.CUBE), xDistance, yDistance, zDistance);

			if (data.isPresent("round_to_digit"))
				distance = new BigDecimal(distance).setScale(data.getNumber("round_to_digit").getInt(), RoundingMode.HALF_UP).doubleValue();

			return comparison.compare(distance, data.getNumber("compare_to").getDouble());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("block"), (data, block) -> {
			return block.getNMS().is(BuiltInRegistries.BLOCK.get(data.getResourceLocation("block")));
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("in_tag"), (data, block) -> {
			Block nms = block.getNMS().getBlock();
			return nms.defaultBlockState().is(data.getTagKey("tag", net.minecraft.core.registries.Registries.BLOCK));
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("adjacent"), (data, block) -> {
			FactoryJsonObject adjacentCondition = data.getJsonObject("adjacent_condition");
			int adjacent = 0;
			for (Direction d : Direction.values()) {
				Location relativeLocation = CraftLocation.toBukkit(block.getPosition().relative(d));
				relativeLocation.setWorld(block.getWorld());
				if (adjacentCondition.isEmpty() || ConditionExecutor.testBlock(adjacentCondition, block.getWorld().getBlockAt(relativeLocation))) {
					adjacent++;
				}
			}
			return Comparison.fromString(data.getString("comparison")).compare(adjacent, data.getNumber("compare_to").getInt());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("replacable"), (data, block) -> {
			return block.getNMS().canBeReplaced();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("attachable"), (data, block) -> {
			for (Direction d : Direction.values()) {
				BlockPos adjacent = block.getPosition().relative(d);
				if (block.getHandle().getBlockState(adjacent).isFaceSturdy(block.getHandle(), block.getPosition(), d.getOpposite())) {
					return true;
				}
			}
			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("fluid"), (data, block) -> {
			return ConditionExecutor.testFluid(data.getJsonObject("fluid_condition"), block.getHandle().getFluidState(block.getPosition()).getType());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("movement_blocking"), (data, block) -> {
			BlockState state = block.getNMS();
			return state.blocksMotion() && !state.getCollisionShape(block.getHandle(), block.getPosition()).isEmpty();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("light_blocking"), (data, block) -> {
			return !block.getNMS().canOcclude();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("water_loggable"), (data, block) -> {
			return block.getNMS().getBlock() instanceof LiquidBlockContainer;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("exposed_to_sky"), (data, block) -> {
			return block.getHandle().canSeeSky(block.getPosition());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("light_level"), (data, block) -> {
			Level world = (Level) block.getHandle();
			BlockPos blockPos = block.getPosition();

			Comparison comparison = Comparison.fromString(data.getString("comparison"));

			int compareTo = data.getNumber("compare_to").getInt();
			int lightLevel;

			if (data.isPresent("light_type")) {
				lightLevel = world.getBrightness(data.getEnumValue("light_type", LightLayer.class), blockPos);
			} else {
				if (world.isClientSide) {
					world.updateSkyBrightness();   //  Re-calculate the world's ambient darkness, since it's only calculated once in the client
				}

				lightLevel = world.getMaxLocalRawBrightness(blockPos);

			}

			return comparison.compare(lightLevel, compareTo);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("block_state"), (data, block) -> {
			BlockState state = block.getNMS();
			Collection<Property<?>> properties = state.getProperties();
			String desiredPropertyName = data.getString("property");
			Property<?> property = null;
			for (Property<?> p : properties) {
				if (p.getName().equals(desiredPropertyName)) {
					property = p;
					break;
				}
			}
			if (property != null) {
				Object value = state.getValue(property);
				if (data.isPresent("enum") && value instanceof Enum) {
					return ((Enum) value).name().equalsIgnoreCase(data.getString("enum"));
				} else if (data.isPresent("value") && value instanceof Boolean) {
					return (Boolean) value == data.getBoolean("value");
				} else if (data.isPresent("comparison") && data.isPresent("compare_to") && value instanceof Integer) {
					return Comparison.fromString(data.getString("comparison")).compare((Integer) value, data.getNumber("compare_to").getInt());
				}
				return true;
			}
			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("block_entity"), (data, block) -> {
			return block.getState() instanceof TileState;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("nbt"), (data, block) -> {
			CompoundTag nbt = new CompoundTag();
			return NbtUtils.compareNbt(data.transformWithCalio("nbt", CalioDataTypes::compoundTag), nbt, true);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("slipperiness"), (data, block) -> {
			BlockState state = block.getNMS();
			return Comparison.fromString(data.getString("comparison")).compare(state.getBlock().getFriction(), data.getNumber("compare_to").getFloat());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("blast_resistance"), (data, block) -> {
			BlockState state = block.getNMS();
			return Comparison.fromString(data.getString("comparison")).compare(state.getBlock().getExplosionResistance(), data.getNumber("compare_to").getFloat());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("hardness"), (data, block) -> {
			BlockState state = block.getNMS();
			return Comparison.fromString(data.getString("comparison")).compare(state.getBlock().defaultDestroyTime(), data.getNumber("compare_to").getFloat());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("material"), (data, block) -> {
			List<TagKey<Block>> tagKeyList = new ArrayList<>();
			if (data.isPresent("material")) {
				tagKeyList.add(TagKey.create(net.minecraft.core.registries.Registries.BLOCK, ResourceLocation.parse("apoli:material/" + data.getString("material"))));
			}

			if (data.isPresent("materials")) {
				data.getJsonArray("materials").asList.stream().map(FactoryElement::getString).forEach(s -> {
					tagKeyList.add(TagKey.create(net.minecraft.core.registries.Registries.BLOCK, ResourceLocation.parse("apoli:material/" + s)));
				});
			}

			BlockState state = block.getNMS();
			for (TagKey<Block> blockTagKey : tagKeyList) {
				if (state.is(blockTagKey)) {
					return true;
				}
			}

			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("command"), (data, block) -> {
			MinecraftServer server = block.getHandle().getServer();
			if (server == null) {
				return false;
			}

			AtomicInteger result = new AtomicInteger();
			CommandSourceStack source = server.createCommandSourceStack()
				.withPosition(block.getPosition().getCenter())
				.withPermission(4)
				.withCallback((successful, returnValue) -> result.set(returnValue))
				.withSuppressedOutput();

			Comparison comparison = data.isPresent("comparison") ? Comparison.fromString(data.getString("comparison")) : Comparison.GREATER_THAN_OR_EQUAL;
			String command = data.getString("command");

			int compareTo = data.getNumberOrDefault("compare_to", 1).getInt();
			server.getCommands().performPrefixedCommand(source, command);

			return comparison.compare(result.get(), compareTo);
		}));*/
	}

	public static void register(@NotNull ConditionFactory<BlockInWorld> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BLOCK_CONDITION).register(factory, factory.getSerializerId());
	}

}
