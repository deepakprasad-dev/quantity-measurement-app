package com.bridgelabz.quantitymeasurement.model;

/**
 * Enum representing different units of measurement and their base conversion factors.
 * All units are normalized to a base unit (e.g., INCH for Length) to facilitate arithmetic.
 */
public enum Unit {
    FEET(12.0),
    INCH(1.0),
    YARD(36.0),
    CENTIMETER(0.4),
    METER(39.37);

    private final double baseConversionFactor;

    /**
     * Constructor to initialize the unit with its base conversion factor.
     * * @param baseConversionFactor the multiplier to convert this unit to the base unit.
     */
    Unit(double baseConversionFactor) {
        this.baseConversionFactor = baseConversionFactor;
    }

    /**
     * Converts a given value of this unit into the normalized base unit.
     * * @param value the numeric value to convert.
     * @return the converted value in the base unit.
     */
    public double convertToBaseUnit(double value) {
        return value * this.baseConversionFactor;
    }
}