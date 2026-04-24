package com.bridgelabz.quantitymeasurement.model;

import java.util.Objects;

/**
 * Represents a physical quantity with a numeric value and a unit of measurement.
 * Designed as an Immutable Value Object.
 */
public class Quantity {
    private final double value;
    private final Unit unit;

    private static final double EPSILON = 0.001;

    /**
     * Constructs a new Quantity.
     * * @param value the numeric magnitude (cannot be negative).
     * @param unit the unit of measurement.
     * @throws IllegalArgumentException if the value is negative.
     */
    public Quantity(double value, Unit unit) {
        if (value < 0.0) {
            throw new IllegalArgumentException("Quantity value cannot be negative");
        }
        this.value = value;
        this.unit = unit;
    }

    /**
     * Adds another Quantity to this instance without mutating the original objects.
     * * @param other the other Quantity to add.
     * @return a new, immutable Quantity representing the sum in the base unit (INCH).
     * @throws IllegalArgumentException if the provided quantity is null.
     */
    public Quantity add(Quantity other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot add a null quantity");
        }

        // Delegating the math logic directly to the Enum
        double sumInBaseUnit = this.unit.convertToBaseUnit(this.value) +
                other.unit.convertToBaseUnit(other.value);

        return new Quantity(sumInBaseUnit, Unit.INCH);
    }

    /**
     * Compares two quantities based on their normalized base unit values.
     * * @param obj the object to compare against.
     * @return true if both quantities represent the exact same physical length.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Quantity quantity = (Quantity) obj;

        return areValuesEqual(this.unit.convertToBaseUnit(this.value),
                quantity.unit.convertToBaseUnit(quantity.value));
    }

    /**
     * Private helper method to handle floating-point precision comparisons.
     * * @param val1 first double value.
     * @param val2 second double value.
     * @return true if values are equal within the defined EPSILON margin of error.
     */
    private boolean areValuesEqual(double val1, double val2) {
        return Math.abs(val1 - val2) <= EPSILON;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Math.round(unit.convertToBaseUnit(value) * 1000.0) / 1000.0);
    }
}