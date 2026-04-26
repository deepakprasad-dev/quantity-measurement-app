package com.bridgelabz.quantitymeasurement;

import com.bridgelabz.quantitymeasurement.model.LengthUnit;
import com.bridgelabz.quantitymeasurement.model.Quantity;
import com.bridgelabz.quantitymeasurement.model.VolumeUnit;
import com.bridgelabz.quantitymeasurement.model.WeightUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QuantityTest {



	@Test
	public void givenZeroFeetAndZeroFeet_WhenCompared_ShouldReturnEqual() {
		Quantity<LengthUnit> feet1 = Quantity.of(0.0, LengthUnit.FEET);
		Quantity<LengthUnit> feet2 = Quantity.of(0.0, LengthUnit.FEET);
		Assertions.assertEquals(feet1, feet2);
	}

	@Test
	public void givenOneFootAndTwelveInches_WhenCompared_ShouldReturnEqual() {
		Quantity<LengthUnit> foot = Quantity.of(1.0, LengthUnit.FEET);
		Quantity<LengthUnit> inches = Quantity.of(12.0, LengthUnit.INCH);
		Assertions.assertEquals(foot, inches);
	}

	@Test
	public void givenThreeFeetAndOneYard_WhenCompared_ShouldReturnEqual() {
		Quantity<LengthUnit> feet = Quantity.of(3.0, LengthUnit.FEET);
		Quantity<LengthUnit> yard = Quantity.of(1.0, LengthUnit.YARD);
		Assertions.assertEquals(feet, yard);
	}



	@Test
	public void givenTwoInchesAndTwoInches_WhenAdded_ShouldReturnFourInches() {
		Quantity<LengthUnit> q1 = Quantity.of(2.0, LengthUnit.INCH);
		Quantity<LengthUnit> q2 = Quantity.of(2.0, LengthUnit.INCH);
		Quantity<LengthUnit> expected = Quantity.of(4.0, LengthUnit.INCH);

		Quantity<LengthUnit> actual = q1.add(q2, LengthUnit.INCH);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void givenOneFootAndTwoInches_WhenAdded_ShouldReturnFourteenInches() {
		Quantity<LengthUnit> foot = Quantity.of(1.0, LengthUnit.FEET);
		Quantity<LengthUnit> inches = Quantity.of(2.0, LengthUnit.INCH);
		Quantity<LengthUnit> expected = Quantity.of(14.0, LengthUnit.INCH);

		Quantity<LengthUnit> actual = foot.add(inches, LengthUnit.INCH);
		Assertions.assertEquals(expected, actual);
	}


	@Test
	public void givenOneKilogramAndOneThousandGrams_WhenCompared_ShouldReturnEqual() {
		Quantity<WeightUnit> kg = Quantity.of(1.0, WeightUnit.KILOGRAM);
		Quantity<WeightUnit> grams = Quantity.of(1000.0, WeightUnit.GRAM);
		Assertions.assertEquals(kg, grams);
	}

	@Test
	public void givenOneTonneAndOneThousandKilograms_WhenCompared_ShouldReturnEqual() {
		Quantity<WeightUnit> tonne = Quantity.of(1.0, WeightUnit.TONNE);
		Quantity<WeightUnit> kg = Quantity.of(1000.0, WeightUnit.KILOGRAM);
		Assertions.assertEquals(tonne, kg);
	}

	@Test
	public void givenOneTonneAndOneThousandGrams_WhenAdded_ShouldReturn1001Kg() {
		Quantity<WeightUnit> tonne = Quantity.of(1.0, WeightUnit.TONNE);
		Quantity<WeightUnit> grams = Quantity.of(1000.0, WeightUnit.GRAM);
		Quantity<WeightUnit> expected = Quantity.of(1001.0, WeightUnit.KILOGRAM);

		Quantity<WeightUnit> actual = tonne.add(grams, WeightUnit.KILOGRAM);
		Assertions.assertEquals(expected, actual);
	}



	@Test
	public void givenOneInchAndOneGram_WhenCompared_ShouldNotBeEqual() {
		Quantity<LengthUnit> inch = Quantity.of(1.0, LengthUnit.INCH);
		Quantity<WeightUnit> gram = Quantity.of(1.0, WeightUnit.GRAM);

		// This proves our .equals() method successfully stops someone from equating lengths and weights!
		Assertions.assertNotEquals(inch, gram);
	}

	// ==========================================
	// UC 11: VOLUME EQUALITY & ADDITION
	// ==========================================

	@Test
	public void givenOneGallonAndLitres_WhenCompared_ShouldReturnEqual() {
		Quantity<VolumeUnit> gallon = Quantity.of(1.0, VolumeUnit.GALLON);
		Quantity<VolumeUnit> litres = Quantity.of(3.78541, VolumeUnit.LITRE);
		Assertions.assertEquals(gallon, litres);
	}

	@Test
	public void givenOneLitreAndOneThousandMilliLitres_WhenCompared_ShouldReturnEqual() {
		Quantity<VolumeUnit> litre = Quantity.of(1.0, VolumeUnit.LITRE);
		Quantity<VolumeUnit> ml = Quantity.of(1000.0, VolumeUnit.MILLILITRE);
		Assertions.assertEquals(litre, ml);
	}

	@Test
	public void givenOneGallonAndLitres_WhenAdded_ShouldReturnTotalInLitres() {
		Quantity<VolumeUnit> gallon = Quantity.of(1.0, VolumeUnit.GALLON);
		Quantity<VolumeUnit> litres = Quantity.of(3.78541, VolumeUnit.LITRE);
		Quantity<VolumeUnit> expected = Quantity.of(7.57082, VolumeUnit.LITRE);

		Quantity<VolumeUnit> actual = gallon.add(litres, VolumeUnit.LITRE);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void givenNegativeValue_WhenCreated_ShouldThrowException() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Quantity.of(-1.0, LengthUnit.INCH);
		});
	}

	@Test
	public void givenNullUnit_WhenCreated_ShouldThrowException() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Quantity.of(1.0, null);
		});
	}
}