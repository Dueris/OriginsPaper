package me.dueris.genesismc.factory.conditions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.TagRegistryParser;
import me.dueris.genesismc.registry.Registerable;
import me.dueris.genesismc.registry.Registries;
import net.minecraft.world.level.material.Fluid;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.CraftFluid;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class FluidConditions {
    public void prep(){
        register(new ConditionFactory(GenesisMC.apoliIdentifier("empty"), (condition, fluid) -> {
            return fluid.defaultFluidState().isEmpty();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("in_tag"), (condition, fluid) -> {
            for (String flu : TagRegistryParser.getRegisteredTagFromFileKey(condition.get("tag").toString())) {
                if (flu == null) continue;
                if (fluid == null) continue;
                return flu.equalsIgnoreCase(fluid.toString());
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("still"), (condition, fluid) -> {
            return fluid.defaultFluidState().isSource();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("fluid"), (condition, fluid) -> {
            return CraftFluid.minecraftToBukkit(fluid).getKey().equals(NamespacedKey.fromString(condition.get("fluid").toString()));
        }));
    }

    public void register(ConditionFactory factory){
        GenesisMC.getPlugin().registry.retrieve(Registries.FLUID_CONDITION).register(factory);
    }

    public class ConditionFactory implements Registerable {
        NamespacedKey key;
        BiPredicate<JSONObject, Fluid> test;

        public ConditionFactory(NamespacedKey key, BiPredicate<JSONObject, Fluid> test){
            this.key = key;
            this.test = test;
        }

        public boolean test(JSONObject condition, net.minecraft.world.level.material.Fluid tester){
            return test.test(condition, tester);
        }

        @Override
        public NamespacedKey getKey() {
            return key;
        }
    }
}
