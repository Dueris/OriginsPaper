package me.dueris.genesismc.utils;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.CraftRegistry;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.enchantment.Enchantment;

public class Utils {
    public MinecraftServer server = MinecraftServer.getServer();
    public CraftServer bukkitServer = server.server;
    public static Registry<DamageType> DAMAGE_REGISTRY = CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.DAMAGE_TYPE);

    public static DamageSource getDamageSource(DamageType type){
        DamageSource source = null;
        for(ResourceKey<DamageType> dkey : DAMAGE_REGISTRY.registryKeySet()){
            if(DAMAGE_REGISTRY.get(dkey).equals(type)){
                source = new DamageSource(DAMAGE_REGISTRY.getHolderOrThrow(dkey));
                break;
            }
        }
        return source;
    }

    public static Enchantment registerEnchantment(String name, Enchantment enchantment) {
        return Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation("origins", name), enchantment);
    }
}
