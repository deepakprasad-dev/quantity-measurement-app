package com.bridgelabz.quantitymeasurement.model;

import java.util.Objects;
import java.util.function.DoubleBinaryOperator;

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

       return performOperation(other,targetUnit, Double::sum);
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

    // --- UC 12: SUBTRACTION ---
    public Quantity<T> subtract(Quantity<T> other, T targetUnit) {
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit cannot be null");
        }
        if (other == null) {
            throw new IllegalArgumentException("Cannot perform operation with a null quantity");
        }

        return performOperation(other,targetUnit,(a,b)-> a-b);
    }

    // --- UC 12: DIVISION ---
    public Quantity<T> divide(double divisor, T targetUnit) {
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit cannot be null");
        }
        if (divisor == 0.0) {
            throw new ArithmeticException("Cannot divide by zero");
        }

        double valInBase = this.unit.convertToBaseUnit(this.value);

        // The core math
        double targetValue = targetUnit.convertFromBaseUnit(valInBase / divisor);

        return Quantity.of(targetValue, targetUnit);
    }

    // --- UC 13: CENTRALIZED ARITHMETIC LOGIC ---
    private Quantity<T> performOperation(Quantity<T> other, T targetUnit, DoubleBinaryOperator operation) {
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit cannot be null");
        }
        if (other == null) {
            throw new IllegalArgumentException("Cannot perform operation with a null quantity");
        }

        // 1. Convert both to base units
        double val1InBase = this.unit.convertToBaseUnit(this.value);
        double val2InBase = other.unit.convertToBaseUnit(other.value);

        // 2. The Magic: Apply whichever math operator was passed in!
        double resultInBase = operation.applyAsDouble(val1InBase, val2InBase);

        // 3. Convert back to target unit
        double targetValue = targetUnit.convertFromBaseUnit(resultInBase);
        return Quantity.of(targetValue, targetUnit);
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
    @Override
    public String toString() {
        return "Quantity{value=" + value + ", unit=" + unit + "}";
    }
}