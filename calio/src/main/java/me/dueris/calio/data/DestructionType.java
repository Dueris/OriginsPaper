package me.dueris.calio.data;

import net.minecraft.world.level.Explosion;

public enum DestructionType {
    BREAK,
    NONE,
    DESTROY;

    public static DestructionType parse(String string){
        switch(string.toLowerCase()){
            case "none" -> {
                return NONE;
            }
            case "break" -> {
                return BREAK;
            }
            case "destroy" -> {
                return DESTROY;
            }
            default -> {
                return BREAK;
            }
        }
    }

    public Explosion.BlockInteraction getNMS(){
        switch(this){
            case DESTROY -> {
                return Explosion.BlockInteraction.DESTROY_WITH_DECAY;
            }
            case NONE -> {
                return Explosion.BlockInteraction.KEEP;
            }
            case BREAK -> {
                return Explosion.BlockInteraction.DESTROY;
            }
            default -> {
                return Explosion.BlockInteraction.DESTROY_WITH_DECAY;
            }
        }
    }
}
