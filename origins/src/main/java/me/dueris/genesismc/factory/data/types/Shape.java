package me.dueris.genesismc.factory.data.types;

public enum Shape {
    CUBE,
    STAR,
    SPHERE;

    public static Shape getShape(String raw){
        switch(raw.toLowerCase()) {
            case "cube" -> {
                return CUBE;
            }
            case "star" -> {
                return STAR;
            }
            case "sphere" -> {
                return SPHERE;
            }
            default -> {
                return CUBE;
            }
        }
    }
}
