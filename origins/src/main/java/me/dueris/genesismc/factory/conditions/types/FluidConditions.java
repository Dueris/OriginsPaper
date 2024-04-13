package me.dueris.genesismc.factory.conditions.types;

import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registerable;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.registry.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;

import java.util.function.BiPredicate;

public class FluidConditions {
    public void prep() {
        register(new ConditionFactory(GenesisMC.apoliIdentifier("empty"), (condition, fluid) -> fluid.defaultFluidState().isEmpty()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("in_tag"), (condition, fluid) -> {
            NamespacedKey tag = NamespacedKey.fromString(condition.getString("tag").toString());
            TagKey key = TagKey.create(net.minecraft.core.registries.Registries.FLUID, CraftNamespacedKey.toMinecraft(tag));
            return fluid.is(key);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("still"), (condition, fluid) -> fluid.defaultFluidState().isSource()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("fluid"), (condition, fluid) -> fluid.builtInRegistryHolder().key().registry().equals(CraftNamespacedKey.toMinecraft(NamespacedKey.fromString(condition.getString("fluid").toString())))));
    }

    private void register(ConditionFactory factory) {
        GenesisMC.getPlugin().registry.retrieve(Registries.FLUID_CONDITION).register(factory);
    }

    public class ConditionFactory implements Registerable {
        NamespacedKey key;
        BiPredicate<FactoryJsonObject, Fluid> test;

        public ConditionFactory(NamespacedKey key, BiPredicate<FactoryJsonObject, Fluid> test) {
            this.key = key;
            this.test = test;
        }

        public boolean test(FactoryJsonObject condition, net.minecraft.world.level.material.Fluid tester) {
            return test.test(condition, tester);
        }

        @Override
        public NamespacedKey getKey() {
            return key;
        }
    }
}
