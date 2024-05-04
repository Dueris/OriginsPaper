package me.dueris.genesismc.factory.data.types;

import java.util.function.BiFunction;

public enum Comparison {
    NONE((a, b) -> false),
    EQUAL(Double::equals),
    LESS_THAN((a, b) -> a < b),
    GREATER_THAN((a, b) -> a > b),
    LESS_THAN_OR_EQUAL((a, b) -> a <= b),
    GREATER_THAN_OR_EQUAL((a, b) -> a >= b),
    NOT_EQUAL((a, b) -> !a.equals(b));

    private final BiFunction<Double, Double, Boolean> comparison;

    Comparison(BiFunction<Double, Double, Boolean> comparison) {
	this.comparison = comparison;
    }

    public static Comparison fromString(String comparisonString) {
	return switch (comparisonString) {
	    case "==" -> EQUAL;
	    case "<" -> LESS_THAN;
	    case ">" -> GREATER_THAN;
	    case "<=" -> LESS_THAN_OR_EQUAL;
	    case ">=" -> GREATER_THAN_OR_EQUAL;
	    case "!=" -> NOT_EQUAL;
	    default -> NONE;
	};
    }

    public boolean compare(double a, double b) {
	return comparison.apply(a, b);
    }

}
