package com.infinitegraph.samples.ndc.generators;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MMIGenerator {
	private static Logger logger = null;
	private Random _numberGen = null;
	private int _baseMMI = 0;
	private int _mmiRange = 0;
	
	public MMIGenerator(int lowestMMI, int highestMMI, long seed) {
		logger = LoggerFactory.getLogger(MMIGenerator.class);
		logger.info(">>> MMIGenerator");
		if (seed == 0)
		{
			_numberGen = new Random(System.currentTimeMillis());
		}
		else
		{
			_numberGen = new Random(seed);
		}
		_baseMMI = lowestMMI;
		_mmiRange = highestMMI - lowestMMI + 1;		
		logger.info("<<< MMIGenerator");
	}
	
	public int GenerateMMI() {
		return _baseMMI + _numberGen.nextInt(_mmiRange); 
	}
}
