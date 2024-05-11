package me.dueris.calio.data.factory;

import com.google.gson.JsonPrimitive;

import java.math.BigDecimal;

public class FactoryNumber {
    protected JsonPrimitive handle;

    public FactoryNumber(JsonPrimitive primative) {
        this.handle = primative;
    }

    public String asString() {
        return this.handle.getAsString();
    }

    public Number asNumber() {
        return this.handle.getAsNumber();
    }

    public int getInt() {
        return this.handle.getAsInt();
    }

    public long getLong() {
        return this.handle.getAsLong();
    }

    public double getDouble() {
        return this.handle.getAsDouble();
    }

    public byte getByte() {
        return this.handle.getAsByte();
    }

    public float getFloat() {
        return this.handle.getAsFloat();
    }

    public BigDecimal getBigDecimal() {
        return this.handle.getAsBigDecimal();
    }

    public int round() {
        return Math.round(getFloat());
    }

    public float multiply(float multiplyBy) {
        return getFloat() * multiplyBy;
    }

    public boolean isNegative() {
        return getFloat() < 0;
    }
}
