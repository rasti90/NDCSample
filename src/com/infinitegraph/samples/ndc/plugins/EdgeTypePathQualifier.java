package com.infinitegraph.samples.ndc.plugins;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infinitegraph.navigation.Hop;
import com.infinitegraph.navigation.Path;
import com.infinitegraph.navigation.Qualifier;
import com.infinitegraph.navigation.qualifiers.EdgeTypes;
import com.infinitegraph.plugins.PathQualifier;
import com.infinitegraph.samples.ndc.Navigate;

/*
 * This path qualifier only return true if the path is name
 */
@PathQualifier
public class EdgeTypePathQualifier extends EdgeTypes implements Qualifier
{
	private Logger logger = null;
	private long[] typeIds = {1000007,1000008};
	
	public EdgeTypePathQualifier()
	{
		logger = LoggerFactory.getLogger(EdgeTypePathQualifier.class);
		if (Navigate.trace) logger.info(">>> EdgeTypePathQualifier (Path qualifier) default constructor");
		if (Navigate.trace) logger.info("<<< EdgeTypePathQualifier (Path qualifier) default constructor");
	}
	
	public EdgeTypePathQualifier(long...typeIds)
	{
		super(typeIds);
		logger = LoggerFactory.getLogger(EdgeTypePathQualifier.class);
		if (Navigate.trace) logger.info(">>> EdgeTypePathQualifier (Path qualifier) constructor");
		if (Navigate.trace) logger.info("<<< EdgeTypePathQualifier (Path qualifier) constructor");
	}
	@Override
	public boolean qualify(Path currentPath)
	{
		if (Navigate.trace) logger.info(">>> EdgeTypePathQualifier qualify");
		Hop finalHop = currentPath.getFinalHop();
		if (finalHop.hasEdge())
		{
			long typeId = finalHop.getEdgeHandle().getTypeId();
			if (typeId == typeIds[0] || typeId == typeIds[1])
			{
				if (Navigate.trace) logger.warn("path doesn't qualify");
				return false;
			}
		}
		if (Navigate.trace) logger.info("<<< EdgeTypePathQualifier qualify");
		return true;
	}
}