package org.reactome.web.diagram.tooltips;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class TooltipContainer extends AbsolutePanel implements DiagramRequestedHandler, DiagramLoadedHandler,
        DatabaseObjectHoveredHandler, DiagramZoomHandler, DiagramPanningHandler {

    private EventBus eventBus;
    private DiagramContext context;
    private DiagramObject hovered;

    private Timer hoveredTimer;

    private int width;
    private int height;

    public TooltipContainer(EventBus eventBus, int width, int height) {
        this.eventBus = eventBus;
        this.hoveredTimer = new Timer() {
            @Override
            public void run() {
                showTooltipExecute();
            }
        };
        setWidth(width);
        setHeight(height);
        initHandlers();
    }

    private void initHandlers() {
//        this.eventBus.addHandler(DatabaseObjectHoveredEvent.TYPE, this);
        this.eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
//        this.eventBus.addHandler(DiagramPanningEvent.TYPE, this);
        this.eventBus.addHandler(DiagramRequestedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramZoomEvent.TYPE, this);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void onDatabaseObjectHovered(DatabaseObjectHoveredEvent event) {
        this.hovered = !event.getHoveredObjects().isEmpty() ? event.getHoveredObjects().get(0) : null;
        if(this.hovered == null) {
            showTooltipExecute(); //this will quickly hide the tooltip ;)
        }else{
            if(this.hoveredTimer.isRunning()){
                this.hoveredTimer.cancel();
            }
            this.hoveredTimer.schedule(500);
        }
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        this.context = event.getContext();
    }

    @Override
    public void onDiagramPanningEvent(DiagramPanningEvent event) {
//        showTooltip();
        Tooltip tooltip = Tooltip.getTooltip(hovered);
        if(tooltip.isVisible()) {
            tooltip.hide();
        }
    }

    @Override
    public void onDiagramRequested(DiagramRequestedEvent event) {
        this.context = null;
    }

    @Override
    public void onDiagramZoomEvent(DiagramZoomEvent event) {
        showTooltip();
    }

    public void setWidth(int width) {
        setWidth(width + "px");
        this.width = width;
    }

    public void setHeight(int height) {
        setHeight(height + "px");
        this.height = height;
    }

    private void showTooltip(){
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                showTooltipExecute();
            }
        });
    }

    private void showTooltipExecute(){
        double factor = context.getDiagramStatus().getFactor();
        Tooltip tooltip = Tooltip.getTooltip(hovered);
        if(hovered==null || factor > 1.0) {
            tooltip.hide();
        }else{
            Coordinate offset = context.getDiagramStatus().getOffset();
            if(hovered instanceof Node){
                Node node = (Node) hovered;
                NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
                tooltip.setPositionAndShow(this, prop.getX(), prop.getY(), prop.getHeight());
            }else if(hovered instanceof Edge){
                Edge edge = (Edge) hovered;
                //TODO
            }else {
                tooltip.hide(); //just in case :)
            }
        }
    }
}
