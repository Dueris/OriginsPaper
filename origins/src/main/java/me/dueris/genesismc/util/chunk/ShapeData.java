package me.dueris.genesismc.util.chunk;

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
    public String toString() {
	return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
	return super.equals(obj);
    }

    public boolean isUseTopBottomThinger() {
	return useTopBottomThinger;
    }

    public void setUseTopBottomThinger(boolean useTopBottomThinger) {
	this.useTopBottomThinger = useTopBottomThinger;
    }
}
