package com.infinitegraph.samples.ndc.plugins;

import com.infinitegraph.VertexHandle;
import com.infinitegraph.navigation.Guide;
import com.infinitegraph.navigation.Hop;
import com.infinitegraph.plugins.Parameter;

public class SearchOrderGuide implements Guide{

	@Parameter
	public boolean breadthFirst = true;
	@Override
	public Strategy getHopOrder(VertexHandle currentVertex, java.util.List<Hop> order)
	{
		return breadthFirst ? Strategy.BREADTH_FIRST : Strategy.DEPTH_FIRST;
	}
}
