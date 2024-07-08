package me.dueris.originspaper.util;

public final class ModifiableFloatPair {
	private float a;
	private float b;

	public ModifiableFloatPair(float a, float b) {
		this.a = a;
		this.b = b;
	}

	public float a() {
		return a;
	}

	public float b() {
		return b;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (ModifiableFloatPair) obj;
		return Float.floatToIntBits(this.a) == Float.floatToIntBits(that.a) &&
			Float.floatToIntBits(this.b) == Float.floatToIntBits(that.b);
	}

	public float setA(float a) {
		this.a = a;
		return a;
	}

	public float setB(float b) {
		this.b = b;
		return b;
	}
}
