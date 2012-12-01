package com.infinitegraph.samples.ndc.plugins;

import java.io.IOException;
import java.io.OutputStreamWriter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infinitegraph.Vertex;
import com.infinitegraph.navigation.Hop;
import com.infinitegraph.navigation.Navigator;
import com.infinitegraph.navigation.Path;
import com.infinitegraph.samples.ndc.types.Drug;
import com.infinitegraph.transformation.FormatResultHandler;

public class PrintPathFormatResultHandler extends FormatResultHandler {

	/*
	 * For every result passing through all the qualifiers in the navigation, this
	 * will print out all the possible paths.
	 */
	private Logger logger = null;

	public PrintPathFormatResultHandler() {
		logger = LoggerFactory.getLogger(PrintPathFormatResultHandler.class);
		return;
	}
	@Override
	public void formatResultPath(Path result) {

		Vertex lastVertex = null;
		StringBuffer path = new StringBuffer();
		path.append(result.get(0).getVertex().toString());
		// For h in p
		for (Hop h : result) {
			if (h.hasEdge()) {
				path.append("\n\t < ");
				path.append(h.getEdge().toString());
				path.append(" ");
				path.append(h.getEdgeHandle().getKind());
				path.append(" > ");
				path.append(h.getVertex().toString());
				lastVertex = h.getVertex();
			}
		}
		//	logger.info("{}", path);
		Drug drug = (Drug) lastVertex;
		logger.info("DO NOT PRESCRIBE DRUG {} Patient has allergy!!!",
				drug.getProprietaryName());
		OutputStreamWriter writer = new OutputStreamWriter(getOutputStream());
		try {
			writer.write("Found matching path : ");
			writer.write(path.toString());
			writer.flush();
		} catch (IOException e) {
			e.getMessage();
			e.printStackTrace();
		}

	}

	public void handleNavigatorFinished(Navigator navigator) {
		return;
	}

}

