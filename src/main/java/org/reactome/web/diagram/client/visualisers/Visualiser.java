package org.reactome.web.diagram.client.visualisers;

import com.google.gwt.user.client.ui.IsWidget;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.renderers.common.HoveredItem;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface Visualiser extends IsWidget{

    //ControlActions
    void fitDiagram(boolean animation);

    void zoomDelta(double deltaFactor);

    void padding(int dX, int dY);

    void exportView();

    void contentLoaded(Context context);
    void contentRequested();
    void layoutLoaded(Context context);

    void highlightGraphObject(GraphObject graphObject);
    void highlightInteractor(DiagramInteractor diagramInteractor);

    void resetHighlight();
    void resetSelection();

    void setSelection(HoveredItem hoveredItem, boolean zoom, boolean fireExternally);

    GraphObject getSelected();

//
//    void flag(Collection<DiagramObject> items, Context context);
//
//    void halo(Collection<DiagramObject> items, Context context);
//
//    void highlight(HoveredItem hoveredItem, Context context);
//
//    void highlightInteractor(DiagramInteractor item, Context context);
//
//    void decorators(HoveredItem hoveredItem, Context context);
//
//    void select(List<DiagramObject> items, Context context);
//
//    void setIllustration(String url);
//
//    void resetIllustration();
//
//    void setCursor();
//
//    void clear();
//
//    void clearThumbnail();

}