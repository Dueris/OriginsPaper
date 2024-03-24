package me.dueris.genesismc.factory.data.types;

public enum ShapeType {
    COLLIDER,
    OUTLINE,
    VISUAL;

    public static ShapeType getShapeType(String raw) {
        switch (raw.toLowerCase()) {
            case "collider" -> {
                return COLLIDER;
            }
            case "outline" -> {
                return OUTLINE;
            }
            default -> {
                return VISUAL;
            }
        }
    }
}
