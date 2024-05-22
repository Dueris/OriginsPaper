package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.util.Util;
import net.minecraft.world.level.material.FluidState;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BinaryOperator;

public class ModifyLavaSpeed extends PowerType {
	private final Modifier[] modifiers;

	public ModifyLavaSpeed(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, @Nullable FactoryJsonObject modifier, @Nullable FactoryJsonArray modifiers) {
		super(name, description, hidden, condition, loading_priority);
		this.modifiers = Modifier.getModifiers(modifier, modifiers);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("modify_lava_speed"))
			.add("modifier", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("modifiers", FactoryJsonArray.class, new FactoryJsonArray(new JsonArray()));
	}

	@Override
	public void tick(Player p) {
		Block be = p.getLocation().getBlock();
		if (!getPlayers().contains(p) || p.isFlying() || be == null ||
			!p.getLocation().getBlock().isLiquid() || !p.isSprinting()) return;
		CraftBlock nmsBlockAccessor = CraftBlock.at(((CraftWorld) p.getWorld()).getHandle(), CraftLocation.toBlockPosition(p.getLocation()));
		if (nmsBlockAccessor.getNMS().getFluidState() != null) {
			FluidState state = nmsBlockAccessor.getNMSFluid();
			if (state.getType().builtInRegistryHolder().key().location().equals(CraftNamespacedKey.toMinecraft(NamespacedKey.fromString("minecraft:lava")))) {
				float multiplyBy = 0.1F;
				for (Modifier modifier : modifiers) {
					Map<String, BinaryOperator<Float>> floatBinaryOperator = Util.getOperationMappingsFloat();
					floatBinaryOperator.get(modifier.operation()).apply(multiplyBy, modifier.value() * 10);
				}
				p.setVelocity(p.getLocation().getDirection().multiply(multiplyBy));
			}
		}
	}
}
