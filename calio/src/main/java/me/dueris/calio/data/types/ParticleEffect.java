package me.dueris.calio.data.types;

import org.bukkit.Particle;

import java.util.Optional;

public record ParticleEffect(Particle particle, Optional<Particle.DustOptions> dustOptions) {}
