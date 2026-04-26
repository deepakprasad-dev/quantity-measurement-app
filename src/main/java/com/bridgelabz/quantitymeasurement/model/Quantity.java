package com.bridgelabz.quantitymeasurement.model;

import java.util.Objects;

/**
 * Represents a physical quantity with a numeric value and a unit of measurement.
 * Implements the Factory Pattern and Method Overloading for arithmetic.
 */
public class Quantity<T extends IUnit> {
    private final double value;
    private final T unit;

    private static final double EPSILON = 0.001;

    /**
     * PRIVATE Constructor to enforce the Factory Pattern.
     */
    private Quantity(double value, T unit) {
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
    public static <T extends  IUnit>Quantity of(double value, T unit) {
        return new Quantity(value, unit);
    }

    /**
     * Adds another Quantity to this instance.
     * @param other the other Quantity to add.
     * @return a new Quantity representing the sum in INCH.
     */


    /**
     * UC7: Adds another Quantity and returns the result in the specified Target Unit.
     *
     * @param other      the other Quantity to add.
     * @param targetUnit the unit the resulting Quantity should be in.
     * @return a new Quantity representing the sum in the target unit.
     */
    public Quantity<T> add(Quantity<T> other, T targetUnit) {
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit cannot be null");
        }

        double sumInBaseUnit = calculateSumInBaseUnit(other);
        double targetValue = targetUnit.convertFromBaseUnit(sumInBaseUnit);

        return Quantity.of(targetValue, targetUnit);
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

        // Use wildcard <?> because we are checking equality against a potentially unknown object
        Quantity<?> quantity = (Quantity<?>) obj;

        // CRITICAL: Prevent cross-category equality (e.g., 1 Inch == 1 Gram)
        if (!this.unit.getClass().equals(quantity.unit.getClass())) {
            return false;
        }

        return areValuesEqual(this.unit.convertToBaseUnit(this.value),
                ((IUnit) quantity.unit).convertToBaseUnit(quantity.value));
    }

    private boolean areValuesEqual(double val1, double val2) {
        return Math.abs(val1 - val2) <= EPSILON;
    }

    @Override
    public int hashCode() {
        // Include the unit's class in the hash to separate 1 Inch from 1 Gram
        return Objects.hash(Math.round(unit.convertToBaseUnit(value) * 1000.0) / 1000.0, unit.getClass());
    }
}