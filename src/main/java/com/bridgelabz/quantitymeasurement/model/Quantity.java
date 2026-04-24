package com.bridgelabz.quantitymeasurement.model;

import java.util.Objects;

/**
 * Represents a physical quantity with a numeric value and a unit of measurement.
 * Implements the Factory Pattern and Method Overloading for arithmetic.
 */
public class Quantity {
    private final double value;
    private final Unit unit;

    private static final double EPSILON = 0.001;

    /**
     * PRIVATE Constructor to enforce the Factory Pattern.
     */
    private Quantity(double value, Unit unit) {
        if (value < 0.0) {
            throw new IllegalArgumentException("Quantity value cannot be negative");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }
        this.value = value;
        this.unit = unit;
    }

    /**
     * Factory Method to create a new Quantity.
     *
     * @param value the numeric magnitude.
     * @param unit  the unit of measurement.
     * @return a new Quantity instance.
     */
    public static Quantity of(double value, Unit unit) {
        return new Quantity(value, unit);
    }

    /**
     * Adds another Quantity to this instance.
     * @param other the other Quantity to add.
     * @return a new Quantity representing the sum in INCH.
     */


    /**
     * Overloaded Add Method: Allows adding a raw value and unit directly.
     *
     * @param value the numeric value to add.
     * @param unit  the unit of the value to add.
     * @return a new Quantity representing the sum in INCH.
     */
    public Quantity add(double value, Unit unit) {
        // Reuses the main add method to strictly enforce the DRY principle
        return this.add(Quantity.of(value, unit));
    }

    /**
     * UC7: Adds another Quantity and returns the result in the specified Target Unit.
     *
     * @param other      the other Quantity to add.
     * @param targetUnit the unit the resulting Quantity should be in.
     * @return a new Quantity representing the sum in the target unit.
     */
    public Quantity add(Quantity other, Unit targetUnit) {
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit cannot be null");
        }

        double sumInBaseUnit = calculateSumInBaseUnit(other);

        double targetValue = targetUnit.convertFromBaseUnit(sumInBaseUnit);

        return Quantity.of(targetValue, targetUnit);
    }

    /**
     * Backwards compatible add method: Defaults to INCH if no target unit is specified.
     */
    public Quantity add(Quantity other) {
        return this.add(other, Unit.INCH);
    }

    /**
     * Private utility method to enforce DRY principle for arithmetic.
     */
    private double calculateSumInBaseUnit(Quantity other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot add a null quantity");
        }
        return this.unit.convertToBaseUnit(this.value) +
                other.unit.convertToBaseUnit(other.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Quantity quantity = (Quantity) obj;

        return areValuesEqual(this.unit.convertToBaseUnit(this.value),
                quantity.unit.convertToBaseUnit(quantity.value));
    }

    private boolean areValuesEqual(double val1, double val2) {
        return Math.abs(val1 - val2) <= EPSILON;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Math.round(unit.convertToBaseUnit(value) * 1000.0) / 1000.0);
    }
}