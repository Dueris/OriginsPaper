package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.builder.inst.factory.FactoryElement;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ParticlePower extends CraftPower {

    public static boolean containsParams(Power power) {
        return !(power.getElement("particle").isString()) && power.getElement("particle").isJsonObject() && power.getJsonObject("particle").isPresent("params");
    }

    private static int calculateValue(float value) {
        if (Math.round(value * 255) > 255) {
            return 254;
        } else {
            return Math.round(value * 255);
        }
    }

    public static Particle computeParticleArgs(FactoryElement root) {
        if (root.isString()) {
            String particleSt = root.getString();
            return Particle.valueOf(ensureCorrectNamespace(particleSt).split(":")[1].toUpperCase());
        } else if (root.isJsonObject()) {
            FactoryJsonObject particle = root.toJsonObject();
            return Particle.valueOf(ensureCorrectNamespace(particle.getString("type")).split(":")[1].toUpperCase());
        }
        return null;
    }

    private static String ensureCorrectNamespace(String string) {
        if (string.endsWith("dust")) {
            string = string.replace("dust", "redstone");
        }
        return string.contains(":") ? string : "minecraft:" + string;
    }


    @Override
    public String getPowerFile() {
        return "apoli:particle";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return particle;
    }

    @Override
    public void run(Player player) {
        if (getPowerArray().contains(player)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                    if (power == null) continue;
                    int interval = power.getNumber("frequency").getInt();

                    if (Bukkit.getServer().getCurrentTick() % interval != 0) {
                        return;
                    } else {
                        if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) player)) {
                            if (!getPowerArray().contains(player)) return;
                            Particle particle = computeParticleArgs(power.getElement("particle"));
                            if (particle == null)
                                throw new IllegalStateException("Unable to create CraftBukkit particle instance");
                            boolean visible_while_invis = power.getBooleanOrDefault("visible_while_invisible", false);
                            boolean pass = visible_while_invis || !player.isInvisible();
                            setActive(player, power.getTag(), pass);

                            float offset_x = 0.25f;
                            float offset_y = 0.50f;
                            float offset_z = 0.25f;


                            if (power.getJsonObject("spread").isPresent("y")) {
                                offset_y = power.getJsonObject("spread").getNumber("y").getFloat();
                            }
                            if (power.getJsonObject("spread").isPresent("x")) {
                                offset_x = power.getJsonObject("spread").getNumber("x").getFloat();
                            }
                            if (power.getJsonObject("spread").isPresent("z")) {
                                offset_z = power.getJsonObject("spread").getNumber("z").getFloat();
                            }

                            Particle.DustOptions data = null;
                            if (containsParams(power)) {
                                String provided = power.getJsonObject("particle").getStringOrDefault("params", "");
                                if (provided.contains(" ")) {
                                    String[] splitArgs = provided.split(" ");
                                    float arg1 = Float.valueOf(splitArgs[0]);
                                    float arg2 = Float.valueOf(splitArgs[1]);
                                    float arg3 = Float.valueOf(splitArgs[2]);
                                    float size = Float.valueOf(splitArgs[3]);
                                    data = new Particle.DustOptions(Color.fromRGB(calculateValue(arg1), calculateValue(arg2), calculateValue(arg3)), size);
                                }
                            }

                            if (pass) {
                                player.getWorld().spawnParticle(
                                    particle.builder().source(player).force(false).location(player.getLocation()).count(1).particle(),
                                    new Location(player.getWorld(), player.getEyeLocation().getX(), player.getEyeLocation().getY() - 0.7, player.getEyeLocation().getZ()),
                                    power.getNumberOrDefault("count", 1).getInt(), offset_x, offset_y, offset_z, 0, data
                                );
                            }
                        } else {
                            setActive(player, power.getTag(), false);
                        }
                    }
                }
            }
        }
    }
}
