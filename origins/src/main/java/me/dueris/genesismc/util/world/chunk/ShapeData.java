package me.dueris.genesismc.util.world.chunk;

public class ShapeData {
    String shape;
    boolean useTopBottomThinger;

    public ShapeData(String shape, boolean useTopToBottom) {
        this.useTopBottomThinger = useTopToBottom;
        this.shape = shape;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean isUseTopBottomThinger() {
        return useTopBottomThinger;
    }

    public void setUseTopBottomThinger(boolean useTopBottomThinger) {
        this.useTopBottomThinger = useTopBottomThinger;
    }
}
