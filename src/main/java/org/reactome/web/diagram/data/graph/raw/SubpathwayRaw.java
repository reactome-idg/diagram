package org.reactome.web.diagram.data.graph.raw;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface SubpathwayRaw {

    Long getDbId();

    String getStId();

    String getDisplayName();

    List<Long> getEvents();

}
