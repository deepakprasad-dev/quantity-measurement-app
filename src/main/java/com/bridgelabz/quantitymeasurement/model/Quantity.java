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
     * @param value the numeric magnitude.
     * @param unit the unit of measurement.
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
    public Quantity add(Quantity other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot add a null quantity");
        }

        double sumInBaseUnit = this.unit.convertToBaseUnit(this.value) +
                other.unit.convertToBaseUnit(other.value);

        return Quantity.of(sumInBaseUnit, Unit.INCH);
    }

    /**
     * Overloaded Add Method: Allows adding a raw value and unit directly.
     * @param value the numeric value to add.
     * @param unit the unit of the value to add.
     * @return a new Quantity representing the sum in INCH.
     */
    public Quantity add(double value, Unit unit) {
        // Reuses the main add method to strictly enforce the DRY principle
        return this.add(Quantity.of(value, unit));
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