package com.bridgelabz.quantitymeasurement.model;

public enum Unit {
    FEET(12.0),
    INCH(1.0),
    YARD(36.0),
    CENTIMETER(0.4),
    METER(39.37);


    private final double baseConversionFactor;

    Unit(double baseConversionFactor) {
        this.baseConversionFactor = baseConversionFactor;
    }

    public double getBaseConversionFactor(){
        return baseConversionFactor;
    }

}
