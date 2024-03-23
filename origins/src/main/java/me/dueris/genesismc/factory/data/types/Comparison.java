package me.dueris.genesismc.factory.data.types;

public enum Comparison {
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL_TO("<="),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL_TO(">="),
    EQUAL_TO("=="),
    NOT_EQUAL_TO("!=");

    String val;
    private Comparison(String raw){
        this.val = raw;
    }

    public boolean compare(double val, double compare_to){
        switch (this) {
            case LESS_THAN:
                return val < compare_to;
            case LESS_THAN_OR_EQUAL_TO:
                return val <= compare_to;
            case GREATER_THAN:
                return val > compare_to;
            case GREATER_THAN_OR_EQUAL_TO:
                return val >= compare_to;
            case EQUAL_TO:
                return val == compare_to;
            case NOT_EQUAL_TO:
                return val != compare_to;
            default:
                return false;
        }
    }

    public String getRaw(){
        return val;
    }

    public static Comparison getFromString(String string){
        for(Comparison value : Comparison.values()){
            if(value.getRaw().equalsIgnoreCase(string)){
                return value;
            }
        }
        return null;
    }
}
