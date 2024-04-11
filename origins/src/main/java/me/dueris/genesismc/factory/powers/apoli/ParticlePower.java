package me.dueris.genesismc.factory.powers.apoli;

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
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class ParticlePower extends CraftPower {

    public static boolean containsParams(Power power) {
        return !(power.getObject("particle") instanceof String) && power.getObject("particle") instanceof JSONObject && power.get("particle").containsKey("params");
    }

    private static int calculateValue(float value) {
        if (Math.round(value * 255) > 255) {
            return 254;
        } else {
            return Math.round(value * 255);
        }
    }

    public static Particle computeParticleArgs(Object root) {
        if (root instanceof String particleSt) {
            return Particle.valueOf(ensureCorrectNamespace(particleSt).split(":")[1].toUpperCase());
        } else if (root instanceof JSONObject particle) {
            return Particle.valueOf(ensureCorrectNamespace(particle.get("type").toString()).split(":")[1].toUpperCase());
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
                    int interval = power.getInt("frequency");

                    if (Bukkit.getServer().getCurrentTick() % interval != 0) {
                        return;
                    } else {
                        if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) player)) {
                            if (!getPowerArray().contains(player)) return;
                            Particle particle = computeParticleArgs(power.getObject("particle"));
                            if (particle == null)
                                throw new IllegalStateException("Unable to create CraftBukkit particle instance");
                            boolean visible_while_invis = power.getBooleanOrDefault("visible_while_invisible", false);
                            boolean pass = visible_while_invis || !player.isInvisible();
                            setActive(player, power.getTag(), pass);

                            float offset_x = 0.25f;
                            float offset_y = 0.50f;
                            float offset_z = 0.25f;

                            if (power.get("spread").get("y") != null) {
                                offset_y = Float.parseFloat(String.valueOf(power.get("spread").get("y")));
                            }
                            if (power.get("spread").get("x") != null) {
                                offset_x = Float.parseFloat(String.valueOf(power.get("spread").get("x")));
                            }
                            if (power.get("spread").get("z") != null) {
                                offset_z = Float.parseFloat(String.valueOf(power.get("spread").get("z")));
                            }

                            Particle.DustOptions data = null;
                            if (containsParams(power)) {
                                String provided = power.get("particle").getOrDefault("params", "").toString();
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
                                    power.getIntOrDefault("count", 1), offset_x, offset_y, offset_z, 0, data
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
