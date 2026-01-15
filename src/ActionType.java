public enum ActionType {
    NORMAL, REPLACEMENT, PROMOTION;

    public boolean isNormal(){
        return this == ActionType.NORMAL;
    }
    public boolean isREPLACEMENT(){
        return this == ActionType.REPLACEMENT;
    }
    public boolean isPROMOTION(){
        return this == ActionType.PROMOTION;
    }
}
