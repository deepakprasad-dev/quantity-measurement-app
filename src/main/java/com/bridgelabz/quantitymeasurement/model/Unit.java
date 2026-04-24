package com.bridgelabz.quantitymeasurement.model;

public enum Unit {
    FEET(12.0),
    INCH(1.0);

    private final double baseConversionFactor;

    Unit(double baseConversionFactor) {
        this.baseConversionFactor = baseConversionFactor;
    }

    public double getBaseConversionFactor(){
        return baseConversionFactor;
    }

}
