package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.annotations.Register;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.ParticleEffect;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftVector;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ParticlePower extends PowerType {

	private final ParticleEffect effect;
	private final int count;
	private final float speed;
	private final boolean force;
	private final Vector spread;
	private final float offsetX;
	private final float offsetY;
	private final float offsetZ;
	private final int frequency;
	private final boolean visibleWhileInvis;

	@Register
	public ParticlePower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, ParticleEffect effect, int count, float speed, boolean force, Vector spread, float offsetX, float offsetY, float offsetZ, int frequency, boolean visibleWhileInvis) {
		super(name, description, hidden, condition, loading_priority);
		this.effect = effect;
		this.count = count;
		this.speed = speed;
		this.force = force;
		this.spread = spread;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.frequency = frequency;
		this.visibleWhileInvis = visibleWhileInvis;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("particle"))
			.add("particle", ParticleEffect.class, null)
			.add("count", int.class, 1)
			.add("speed", float.class, 0.0F)
			.add("force", boolean.class, false)
			.add("spread", Vector.class, new Vector(0.5, 0.5, 0.5))
			.add("offset_x", float.class, 0.25F)
			.add("offset_y", float.class, 0.50F)
			.add("offset_z", float.class, 0.25F)
			.add("frequency", int.class, 0)
			.add("visible_while_invisible", boolean.class, false);
	}

	@Override
	public void tick(Player player) {
		if (Bukkit.getServer().getCurrentTick() % frequency == 0) {
			if (isActive(player)) {
				Particle particle = getEffect().getParticle();
				if (particle == null)
					throw new IllegalStateException("Unable to create CraftBukkit particle instance");
				boolean visible_while_invis = isVisibleWhileInvis();
				boolean pass = visible_while_invis || !player.isInvisible();
				double offset_x = spread.getX();
				double offset_y = spread.getY();
				double offset_z = spread.getZ();

				if (pass) {
					player.getWorld().spawnParticle(
						particle.builder().source(player).force(false).location(player.getLocation()).count(1).particle(),
						new Location(player.getWorld(), player.getEyeLocation().getX(), player.getEyeLocation().getY() - 0.7, player.getEyeLocation().getZ()),
						count, offset_x, offset_y, offset_z, 0, effect.getDustOptions().orElse(null)
					);
				}
			}
		}
	}

	public ParticleEffect getEffect() {
		return effect;
	}

	public int getCount() {
		return count;
	}

	public float getSpeed() {
		return speed;
	}

	public boolean isForce() {
		return force;
	}

	public Vector getSpread() {
		return spread;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public float getOffsetZ() {
		return offsetZ;
	}

	public int getFrequency() {
		return frequency;
	}

	public boolean isVisibleWhileInvis() {
		return visibleWhileInvis;
	}
}
