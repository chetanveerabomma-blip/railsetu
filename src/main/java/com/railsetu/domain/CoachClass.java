package com.railsetu.domain;

public enum CoachClass {
    FIRST_AC("1A", "AC First Class", 1.80, 24),
    SECOND_AC("2A", "AC 2 Tier", 1.40, 48),
    THIRD_AC("3A", "AC 3 Tier", 1.15, 64),
    SLEEPER("SL", "Sleeper Class", 1.00, 72),
    CHAIR_CAR("CC", "AC Chair Car", 1.10, 73),
    SECOND_SITTING("2S", "Second Sitting", 0.60, 108);

    private final String code;
    private final String displayName;
    private final double baseMultiplier;
    private final int defaultCapacity;

    CoachClass(String code, String displayName, double baseMultiplier, int defaultCapacity) {
        this.code = code;
        this.displayName = displayName;
        this.baseMultiplier = baseMultiplier;
        this.defaultCapacity = defaultCapacity;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getBaseMultiplier() {
        return baseMultiplier;
    }

    public int getDefaultCapacity() {
        return defaultCapacity;
    }

    public static CoachClass fromCode(String code) {
        for (CoachClass cc : values()) {
            if (cc.code.equalsIgnoreCase(code) || cc.name().equalsIgnoreCase(code)) {
                return cc;
            }
        }
        return SLEEPER;
    }
}
