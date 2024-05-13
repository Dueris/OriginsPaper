package me.dueris.calio.data.types;

import org.bukkit.Particle;

import java.util.Optional;

public class ParticleEffect {

	private final Particle particle;
	private final Optional<Particle.DustOptions> dustOptions;

	public ParticleEffect(Particle particle, Optional<Particle.DustOptions> dustOptions) {
		this.particle = particle;

		this.dustOptions = dustOptions;
	}

	public Particle getParticle() {
		return particle;
	}

	public Optional<Particle.DustOptions> getDustOptions() {
		return dustOptions;
	}
}
