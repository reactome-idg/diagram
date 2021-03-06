package org.reactome.web.diagram.renderers.layout.s100;

import org.reactome.web.diagram.renderers.layout.abs.ConnectorAbstractRenderer;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ConnectorRenderer100 extends ConnectorAbstractRenderer {
    @Override
    public boolean stoichiometryVisible() {
        return true;
    }
}
