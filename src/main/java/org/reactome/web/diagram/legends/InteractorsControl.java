package org.reactome.web.diagram.legends;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.InteractorsContent;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.diagram.util.interactors.InteractorsExporter;
import org.reactome.web.diagram.util.slider.Slider;
import org.reactome.web.diagram.util.slider.SliderValueChangedEvent;
import org.reactome.web.diagram.util.slider.SliderValueChangedHandler;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsControl extends LegendPanel implements ClickHandler, SliderValueChangedHandler,
        DiagramRequestedHandler, DiagramLoadedHandler,
        InteractorsResourceChangedHandler, InteractorsLoadedHandler, InteractorsErrorHandler, InteractorsLayoutUpdatedHandler {

    @SuppressWarnings("FieldCanBeLocal")
    private static String MSG_LOADING = "Loading interactors for ";
    @SuppressWarnings("FieldCanBeLocal")
    private static String MSG_LOADING_PSICQUIC = "Loading PSICQUIC interactors from ";
    @SuppressWarnings("FieldCanBeLocal")
    private static String MSG_NO_INTERACTORS_FOUND = "No interactors found in ";
    @SuppressWarnings("FieldCanBeLocal")
    private static String MSG_DOWNLOAD_TOOLTIP = "Download all diagram interactors from ";

    private String currentResource;

    private DiagramContext context;
    private Image loadingIcon;
    private InlineLabel message;
    private FlowPanel controlsFP;
    private Slider slider;
    private PwpButton downloadBtn;
    private PwpButton closeBtn;

    public InteractorsControl(EventBus eventBus) {
        super(eventBus);

        this.initUI();
        this.setVisible(false);

        this.initHandlers();
    }

    @Override
    public void onClick(ClickEvent event) {
        Object source = event.getSource();
        if (source.equals(this.closeBtn)) {
            //Is safe to do this here (even if there is not loading in progress because that scenario is checked by the loader)
            setVisible(false);
            if (loadingIcon.isVisible()) {
                eventBus.fireEventFromSource(new InteractorsRequestCanceledEvent(), this);
            } else {
                eventBus.fireEventFromSource(new InteractorsCollapsedEvent(currentResource), this);
            }
            setVisible(false);
        } else if(source.equals(this.downloadBtn)) {
            if (context != null) {
                MapSet<String, RawInteractor> interactors = context.getInteractors().getRawInteractorsPerResource(currentResource);
                if(interactors != null && !interactors.isEmpty()) {
                    String filename = context.getContent().getStableId() + "_Interactors_" + formatName(currentResource)+ ".tsv";
                    InteractorsExporter.exportInteractors(filename, interactors);
                }
            }
        }
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        context = event.getContext();
        if (context.getInteractors().isInteractorResourceCached(currentResource)) {
            update();
        }

    }

    @Override
    public void onDiagramRequested(DiagramRequestedEvent event) {
        context = null;
    }

    @Override
    public void onInteractorsLayoutUpdated(InteractorsLayoutUpdatedEvent event) {
        update();
    }

    @Override
    public void onInteractorsResourceChanged(InteractorsResourceChangedEvent event) {
        currentResource = event.getResource();
        //context is null when the diagram is in the process of loading (loading message is meant to be displayed)
        if (context == null || !context.getInteractors().isInteractorResourceCached(event.getResource())) {
            setVisible(true);
            displayLoader(true, event.getResource());
        } else {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    update();
                }
            });
        }
    }

    @Override
    public void onInteractorsLoaded(InteractorsLoadedEvent event) {
        currentResource = event.getInteractors().getResource();
        //TODO Check why no warning is shown in case no interactors are present
        int totalInteractorsLoaded = event.getInteractors().getEntities().size();
        if(totalInteractorsLoaded==0) {
            Console.warn(">> No interactors present <<");
            displayWarning(true);
        }else {
            update();
        }
    }

    @Override
    public void onInteractorsError(InteractorsErrorEvent event) {
        setVisible(true);
        displayError(event.getMessage());
    }

    @Override
    public void onSliderValueChanged(SliderValueChangedEvent event) {
        double threshold = event.getPercentage();
        InteractorsContent.setInteractorsThreshold(currentResource, threshold);
        eventBus.fireEventFromSource(new InteractorsFilteredEvent(threshold), this);
    }

    private void update() {
        int burstEntities = context.getContent().getNumberOfBurstEntities();
        if (burstEntities == 0) {
            setVisible(false);
        } else {
            this.removeStyleName(RESOURCES.getCSS().interactorsControlError());
            this.removeStyleName(RESOURCES.getCSS().interactorsControlWarning());
            setVisible(true);
            setMessage(currentResource);
            controlsFP.setVisible(true);
            slider.setValue(InteractorsContent.getInteractorsThreshold(currentResource));
            downloadBtn.setTitle(MSG_DOWNLOAD_TOOLTIP + formatName(currentResource));
        }
    }

    private void initUI() {
        addStyleName(RESOURCES.getCSS().analysisControl());
        addStyleName(RESOURCES.getCSS().interactorsControl());
        addStyleName(RESOURCES.getCSS().unselectable());

        this.loadingIcon = new Image(RESOURCES.loader());
        this.loadingIcon.setStyleName(RESOURCES.getCSS().interactorsControlLoadingIcon());

        this.message = new InlineLabel("");
        this.message.setStyleName(RESOURCES.getCSS().interactorsControlMessage());
        this.closeBtn = new PwpButton("Close and clear interactors", RESOURCES.getCSS().close(), this);

        this.slider = new Slider(100, 24, 0.45, 1, 0.45, true);
        this.slider.addSliderValueChangedHandler(this);
        this.slider.setStyleName(RESOURCES.getCSS().interactorsControlSlider());

        this.downloadBtn = new PwpButton(MSG_DOWNLOAD_TOOLTIP, RESOURCES.getCSS().download(), this);

        this.controlsFP = new FlowPanel();
        this.controlsFP.setStyleName(RESOURCES.getCSS().interactorsControlControls());
        this.controlsFP.add(this.slider);
        this.controlsFP.add(this.downloadBtn);

        this.add(this.loadingIcon);
        this.add(this.message);
        this.add(this.closeBtn);
        this.add(this.controlsFP);
    }

    private void initHandlers() {
        eventBus.addHandler(DiagramRequestedEvent.TYPE, this);
        eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
        eventBus.addHandler(InteractorsResourceChangedEvent.TYPE, this);
        eventBus.addHandler(InteractorsLayoutUpdatedEvent.TYPE, this);
        eventBus.addHandler(InteractorsLoadedEvent.TYPE, this);
        eventBus.addHandler(InteractorsErrorEvent.TYPE, this);
    }

    private void displayLoader(boolean visible, String resource) {
        this.removeStyleName(RESOURCES.getCSS().interactorsControlError());
        this.removeStyleName(RESOURCES.getCSS().interactorsControlWarning());
        loadingIcon.setVisible(visible);
        controlsFP.setVisible(!visible);
        if (visible) {
            String msg;
            if(resource.equals("static")) {
                msg = MSG_LOADING + "Static (IntAct)";
            } else {
                msg = MSG_LOADING_PSICQUIC + formatName(resource);
            }
            message.setText(msg + "...");
        }
    }

    private void displayError(String serverMsg) {
        if (serverMsg == null) {
            displayError(false);
        } else {
            setMessage(serverMsg);
            displayError(true);
        }
    }

    private void displayError(boolean visible) {
        if (visible) {
            this.addStyleName(RESOURCES.getCSS().interactorsControlError());
            this.removeStyleName(RESOURCES.getCSS().interactorsControlWarning());
        }
        controlsFP.setVisible(!visible);
    }

    private void displayWarning(boolean visible) {
        if (visible) {
            this.addStyleName(RESOURCES.getCSS().interactorsControlWarning());
            this.removeStyleName(RESOURCES.getCSS().interactorsControlError());
        }
        setMessage(MSG_NO_INTERACTORS_FOUND+ currentResource);
        controlsFP.setVisible(!visible);
    }

    private void setMessage(String msg) {
        loadingIcon.setVisible(false);
        message.setText(msg.equals("static") ? "Static (IntAct)" : formatName(msg));
    }

    /**
     *  Changes the name by capitalizing the first character
     *  only in case all letters are lowercase
     */
    private String formatName(String originalName) {
        String output;
        if(originalName.equals(originalName.toLowerCase())){
            output = originalName.substring(0, 1).toUpperCase() + originalName.substring(1);
        } else {
            output = originalName;
        }
        return output;
    }
}