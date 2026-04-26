package com.bridgelabz.quantitymeasurement.model;

import java.util.Objects;

/**
 * Represents a physical weight with a numeric value and a WeightUnit.
 */
public class WeightQuantity {
    private final double value;
    private final WeightUnit unit;

    private static final double EPSILON = 0.001;

    private WeightQuantity(double value, WeightUnit unit) {
        if (value < 0.0) {
            throw new IllegalArgumentException("Weight value cannot be negative");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }
        this.value = value;
        this.unit = unit;
    }

    public static WeightQuantity of(double value, WeightUnit unit) {
        return new WeightQuantity(value, unit);
    }

    public WeightQuantity add(WeightQuantity other, WeightUnit targetUnit) {
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit cannot be null");
        }

        double sumInBaseUnit = calculateSumInBaseUnit(other);
        double targetValue = targetUnit.convertFromBaseUnit(sumInBaseUnit);

        return WeightQuantity.of(targetValue, targetUnit);
    }

    public WeightQuantity add(WeightQuantity other) {
        return this.add(other, WeightUnit.GRAM);
    }

    private double calculateSumInBaseUnit(WeightQuantity other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot add a null weight");
        }
        return this.unit.convertToBaseUnit(this.value) +
                other.unit.convertToBaseUnit(other.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        WeightQuantity quantity = (WeightQuantity) obj;

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