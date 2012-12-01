package com.infinitegraph.samples.ndc.generators;

import java.util.Random;

import java.util.ArrayList;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NameGenerator {

	private static Logger logger = null;
	private ArrayList<String> _nameList = new ArrayList<String>();
	private int _listCount = 0;
	private Random _numberGen = null;

	public void GenerateListFromStats(String statsFilePath, long seed) {

		logger = LoggerFactory.getLogger(NameGenerator.class);
		logger.info(">>> NameGenerator");

		try {
			_nameList.clear();
			_listCount = 0;
			_numberGen = null;

			FileReader statsFile = new FileReader(statsFilePath);
			BufferedReader fileReader = new BufferedReader(statsFile);
			String nameEntry;

			while ((nameEntry = fileReader.readLine()) != null) {
				try {
					int weight = 0;
					String[] nameParams = nameEntry.split("\\s+");

					// Get the weight of the name relative to the others
					if ((nameParams != null) && (nameParams.length == 4)) {
						weight = (int) (Float.parseFloat(nameParams[1]) * 1000.0f);
						if (weight <= 0) {
							weight = 1;
						}

						while (weight-- > 0) {
							_nameList.add(nameParams[0]);
						}
					}
				}
				catch (Exception e) {
					// Eat the exception and ignore the entry
				}

			}

			if (_nameList.size() > 0) {
				if (seed == 0) {
					_numberGen = new Random(System.currentTimeMillis());
				} else {
					_numberGen = new Random(seed);
				}
				_listCount = _nameList.size();

			}

		}

		catch (FileNotFoundException fnfex) {
		}
		catch (IOException ioex) {
		}
		logger.info("<<< NameGenerator");
	}

	public String GenerateName() {
		if (_numberGen != null) {
			return _nameList.get(_numberGen.nextInt(_listCount));
		}
		return null;
	}
}
