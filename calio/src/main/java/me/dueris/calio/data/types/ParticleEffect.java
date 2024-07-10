package me.dueris.calio.data.types;

import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;

import java.util.Optional;

public record ParticleEffect(Particle particle, Optional<Particle.DustOptions> dustOptions,
							 Optional<BlockData> blockData) {
}
