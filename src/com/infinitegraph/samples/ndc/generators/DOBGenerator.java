package com.infinitegraph.samples.ndc.generators;

import java.util.*;

public class DOBGenerator {

	private static short[] _daysInMonth = { 31, 29, 31, 30, 31, 30, 31, 31, 30,
			31, 30, 31 };

	private Random _numberGen = null;

	public DOBGenerator(long seed) {
		if (seed == 0) {
			_numberGen = new Random(System.currentTimeMillis());
		} else {
			_numberGen = new Random(seed);
		}
	}

	public Date GenerateDOB() {
		int year = 2007 - _numberGen.nextInt(100);
		int month = _numberGen.nextInt(12);
		int day = _numberGen.nextInt(_daysInMonth[month]) + 1;

		// Check the leap year
		if (month == 1 && day == 29) {
			if ((year % 100 == 0) || (year % 4 != 0)) {
				day = _numberGen.nextInt(28) + 1;
			}
		}

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);

		return cal.getTime();
	}
}
