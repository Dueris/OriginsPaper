package me.dueris.genesismc.factory.data.types;

public enum Shape {
    CUBE,
    STAR,
    SPHERE;

    public static Shape getShape(Object raw) {
        if (raw == null) return CUBE;
        switch (raw.toString().toLowerCase()) {
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
