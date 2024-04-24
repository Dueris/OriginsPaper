package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import net.minecraft.world.level.material.FluidState;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_lava_speed;

public class ModifyLavaSpeed extends CraftPower implements Listener {

    @Override
    public String getType() {
        return "apoli:modify_lava_speed";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return modify_lava_speed;
    }

    @Override
    public void run(Player p, Power power) {
        Block be = p.getLocation().getBlock();
        if (!getPlayersWithPower().contains(p) || p.isFlying() || be == null ||
            !p.getLocation().getBlock().isLiquid() || !p.isSprinting()) return;
        CraftBlock nmsBlockAccessor = CraftBlock.at(((CraftWorld) p.getWorld()).getHandle(), CraftLocation.toBlockPosition(p.getLocation()));
        if (nmsBlockAccessor.getNMS().getFluidState() != null) {
            FluidState state = nmsBlockAccessor.getNMSFluid();
            if (state.getType().builtInRegistryHolder().key().location().equals(CraftNamespacedKey.toMinecraft(NamespacedKey.fromString("minecraft:lava")))) {
                float multiplyBy = 0.1F;
                for (Modifier modifier : power.getModifiers()) {
                    Map<String, BinaryOperator<Float>> floatBinaryOperator = Utils.getOperationMappingsFloat();
                    floatBinaryOperator.get(modifier.operation()).apply(multiplyBy, modifier.value() * 10);
                }
                p.setVelocity(p.getLocation().getDirection().multiply(multiplyBy));
            }
        }
    }
}
