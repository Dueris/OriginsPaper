package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ElytraFlightPowerType extends PowerType {

	public static final TypedDataObjectFactory<ElytraFlightPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("texture_location", SerializableDataTypes.IDENTIFIER.optional(), Optional.empty())
			.add("render_elytra", SerializableDataTypes.BOOLEAN),
		(data, condition) -> new ElytraFlightPowerType(
			data.get("texture_location"),
			data.get("render_elytra"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("texture_location", powerType.textureLocation)
			.set("render_elytra", powerType.renderElytra)
	);

	private final Optional<ResourceLocation> textureLocation;
	private final boolean renderElytra;
	public boolean renderChanged = false;
	public boolean overwritingFlight = false;

	public ElytraFlightPowerType(Optional<ResourceLocation> textureLocation, boolean renderElytra, Optional<EntityCondition> condition) {
		super(condition);
		this.textureLocation = textureLocation;
		this.renderElytra = renderElytra;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.ELYTRA_FLIGHT;
	}

	@Override
	public boolean isActive() {
		return super.isActive();
	}

	public Optional<ResourceLocation> getTextureLocation() {
		return textureLocation;
	}

	public boolean shouldRenderElytra() {
		return renderElytra;
	}

	public BukkitRunnable createRunnable() {
		return new BukkitRunnable() {
			@Override
			public void run() {
				CraftPlayer p = (CraftPlayer) getHolder().getBukkitEntity();
				if (!p.getInventory().getItem(EquipmentSlot.CHEST).getType().equals(Material.ELYTRA)) {
					p.sendEquipmentChange(p, EquipmentSlot.CHEST, new org.bukkit.inventory.ItemStack(Material.ELYTRA));
					renderChanged = true;
				}

				if (p.isOnGround() || p.isFlying() || p.isInsideVehicle()) {
					this.cancel();
					if (renderChanged) {
						renderChanged = false;
						p.updateInventory();
					}
					overwritingFlight = false;
				}

				overwritingFlight = true;
				p.setFallDistance(0.0F);
				p.setGliding(true);
			}
		};
	}
}
