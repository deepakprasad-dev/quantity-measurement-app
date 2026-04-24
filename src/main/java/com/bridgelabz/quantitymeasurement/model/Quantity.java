package com.bridgelabz.quantitymeasurement.model;

import java.util.Objects;

public class Quantity {
    private final double value;
    private final Unit unit;

    public Quantity(double value, Unit unit){
        this.value = value;
        this.unit = unit;
    }

    public Quantity add(Quantity other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot add a null quantity");
        }

        // Convert both values to the base unit (Inches) and add them
        double sumInBaseUnit = (this.value * this.unit.getBaseConversionFactor()) +
                (other.value * other.unit.getBaseConversionFactor());

        // Return a new immutable Quantity object representing the sum in Inches
        return new Quantity(sumInBaseUnit, Unit.INCH);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Quantity quantity = (Quantity) obj;

        // Normalize both to the base unit (inches) for comparison
        return Double.compare(this.value * this.unit.getBaseConversionFactor(),
                quantity.value * quantity.unit.getBaseConversionFactor()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value * unit.getBaseConversionFactor());
    }
}
