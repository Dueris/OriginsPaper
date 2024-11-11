package io.github.dueris.originspaper.component;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.MultiplePower;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.util.GainedPowerCriterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PowerHolderComponentImpl implements PowerHolderComponent {

	private final ConcurrentHashMap<Power, PowerType> powers = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Power, List<ResourceLocation>> powerSources = new ConcurrentHashMap<>();

	private final LivingEntity owner;

	public PowerHolderComponentImpl(LivingEntity owner) {
		this.owner = owner;
	}

	@Override
	public boolean hasPower(Power power) {
		return powers.containsKey(power);
	}

	@Override
	public boolean hasPower(Power power, ResourceLocation source) {
		return powerSources.containsKey(power) && powerSources.get(power).contains(source);
	}

	@Override
	public PowerType getPowerType(Power power) {
		return powers.get(power);
	}

	@Override
	public List<PowerType> getPowerTypes() {
		return new LinkedList<>(powers.values());
	}

	@Override
	public Set<Power> getPowers(boolean includeSubPowers) {
		return powers.keySet()
			.stream()
			.filter(pt -> includeSubPowers || !pt.isSubPower())
			.collect(Collectors.toCollection(HashSet::new));
	}

	@Override
	public <T extends PowerType> List<T> getPowerTypes(Class<T> typeClass) {
		return getPowerTypes(typeClass, false);
	}

	@Override
	public <T extends PowerType> List<T> getPowerTypes(@NotNull Class<T> typeClass, boolean includeInactive) {
		return powers.values()
			.stream()
			.filter(typeClass::isInstance)
			.map(typeClass::cast)
			.filter(type -> includeInactive || type.isActive())
			.collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public List<ResourceLocation> getSources(Power power) {

		if (powerSources.containsKey(power)) {
			return List.copyOf(powerSources.get(power));
		} else {
			return List.of();
		}

	}

	@Override
	public boolean removePower(Power power, ResourceLocation source) {

		ConcurrentHashMap.KeySetView<Power, Boolean> powersToRemove = ConcurrentHashMap.newKeySet();
		boolean result = this.removePower(power, source, powersToRemove::add);

		powers.keySet().removeAll(powersToRemove);
		powerSources.keySet().removeAll(powersToRemove);

		return result;

	}

	protected boolean removePower(@NotNull Power power, ResourceLocation source, Consumer<Power> adder) {

		ResourceLocation powerId = power.getId();
		if (power instanceof PowerReference powerReference) {
			power = powerReference.getReference();
		}

		if (power == null) {
			OriginsPaper.LOGGER.error("Cannot remove a non-existing power with ID \"{}\" from entity {}! (UUID: {})", powerId, owner.getName().getString(), owner.getStringUUID());
			return false;
		}

		List<ResourceLocation> sources = powerSources.getOrDefault(power, new ArrayList<>());
		if (!sources.remove(source)) {
			return false;
		}

		if (sources.isEmpty() && powers.containsKey(power)) {

			PowerType powerType = powers.get(power);
			adder.accept(power);

			powerType.onRemoved();
			powerType.onLost();

		}

		if (power instanceof MultiplePower multiplePower) {
			multiplePower.getSubPowers().forEach(subPower -> this.removePower(subPower, source, adder));
		}

		return true;

	}

	@Override
	public int removeAllPowersFromSource(ResourceLocation source) {
		//noinspection MappingBeforeCount
		return (int) this.getPowersFromSource(source)
			.stream()
			.filter(Predicate.not(Power::isSubPower))
			.peek(pt -> this.removePower(pt, source))
			.count();
	}

	@Override
	public List<Power> getPowersFromSource(ResourceLocation source) {
		return powerSources.entrySet()
			.stream()
			.filter(e -> e.getValue().contains(source))
			.map(Map.Entry::getKey)
			.collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public boolean addPower(Power power, ResourceLocation source) {

		ConcurrentHashMap<Power, PowerType> powersToAdd = new ConcurrentHashMap<>();
		boolean result = this.addPower(power, source, powersToAdd::put);

		powersToAdd.forEach((powerToAdd, powerTypeToAdd) -> {

			powerTypeToAdd.onAdded();
			powerTypeToAdd.onGained();

			if (owner instanceof ServerPlayer serverPlayer) {
				GainedPowerCriterion.INSTANCE.trigger(serverPlayer, powerToAdd);
			}

		});

		return result;

	}

	protected boolean addPower(Power power, ResourceLocation source, BiConsumer<Power, PowerType> adder) {

		ResourceLocation powerId = power.getId();
		if (power instanceof PowerReference powerReference) {
			power = powerReference.getReference();
		}

		if (power == null) {
			OriginsPaper.LOGGER.error("Cannot add a non-existing power with ID \"{}\" to entity {} (UUID: {})!", powerId, owner.getName().getString(), owner.getStringUUID());
			return false;
		}

		List<ResourceLocation> sources = powerSources.computeIfAbsent(power, pt -> new LinkedList<>());
		if (sources.contains(source)) {
			return false;
		}

		PowerType powerType = power.create(owner);
		sources.add(source);

		powerSources.put(power, sources);
		powers.put(power, powerType);

		adder.accept(power, powerType);

		if (power instanceof MultiplePower multiplePower) {
			multiplePower.getSubPowers().forEach(subPower -> this.addPower(subPower, source, adder));
		}

		return true;

	}

	@Override
	public void serverTick() {
		this.getPowerTypes(PowerType.class, true)
			.stream()
			.filter(PowerType::shouldTick)
			.filter(type -> type.shouldTickWhenInactive() || type.isActive())
			.forEach(PowerType::tick);
	}

	@Override
	public void readFromNbt(@NotNull CompoundTag compoundTag, HolderLookup.Provider lookup) {

		powers.clear();
		ListTag powersTag = compoundTag.getList("powers", Tag.TAG_COMPOUND);

		//  Migrate compound NBTs from the old 'Powers' NBT path to the new 'powers' NBT path
		if (compoundTag.contains("Powers")) {
			powersTag.addAll(compoundTag.getList("Powers", Tag.TAG_COMPOUND));
		}

		for (int i = 0; i < powersTag.size(); i++) {

			CompoundTag powerTag = powersTag.getCompound(i);

			try {

				Power.Entry powerEntry = Power.Entry.CODEC.read(lookup.createSerializationContext(NbtOps.INSTANCE), powerTag).getOrThrow();
				ResourceLocation powerId = powerEntry.power().getId();

				try {

					Power power = powerEntry.power().getStrictReference();
					PowerType powerType = power.create(owner);

					try {

						Tag powerData = powerEntry.nbtData();

						if (powerData != null) {
							powerType.fromTag(powerData);
						}

					} catch (ClassCastException cce) {
						//  Occurs when the power was overridden by a data pack since last resource reload,
						//  where the overridden power may encode/decode different NBT types
						OriginsPaper.LOGGER.warn("Data type of power \"{}\" has changed, skipping data for that power on entity {} (UUID: {})", powerId, owner.getName().getString(), owner.getStringUUID());
					}

					powerSources.put(power, powerEntry.sources());
					powers.put(power, powerType);

				} catch (Throwable t) {
					OriginsPaper.LOGGER.warn("Unregistered power \"{}\" found on entity {} (UUID: {}), skipping...", powerId, owner.getName().getString(), owner.getStringUUID());
				}

			} catch (Throwable t) {
				OriginsPaper.LOGGER.warn("Error trying to decode NBT element ({}) at index {} into a power from NBT of entity {} (UUID: {}) (skipping): {}", powerTag, i, owner.getName().getString(), owner.getStringUUID(), t.getMessage());
			}

		}

	}

	@Override
	public void writeToNbt(@NotNull CompoundTag compoundTag, HolderLookup.Provider lookup) {

		ListTag powersTag = new ListTag();
		powers.forEach((power, powerType) -> {

			PowerTypeFactory<?> typeFactory = power.getFactoryInstance().getFactory();
			PowerReference powerReference = PowerReference.of(power.getId());

			Power.Entry.CODEC.codec().encodeStart(lookup.createSerializationContext(NbtOps.INSTANCE), new Power.Entry(typeFactory, powerReference, powerType.toTag(), powerSources.get(power)))
				.mapError(err -> "Error encoding power \"" + power.getId() + "\" to NBT of entity " + owner.getName().getString() + " (UUID: " + owner.getStringUUID() + ") (skipping): " + err)
				.resultOrPartial(OriginsPaper.LOGGER::warn)
				.ifPresent(powersTag::add);

		});

		compoundTag.put("powers", powersTag);

	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("PowerHolderComponent[\n");
		for (Map.Entry<Power, PowerType> powerEntry : powers.entrySet()) {
			str.append("\t").append(powerEntry.getKey().getId()).append(": ").append(powerEntry.getValue().toTag().toString()).append("\n");
		}
		str.append("]");
		return str.toString();
	}

}

