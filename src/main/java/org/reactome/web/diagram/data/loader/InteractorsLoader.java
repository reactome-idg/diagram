package org.reactome.web.diagram.data.loader;

import com.google.gwt.http.client.*;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.data.interactors.raw.RawInteractors;
import org.reactome.web.diagram.data.interactors.raw.factory.InteractorsException;
import org.reactome.web.diagram.data.interactors.raw.factory.InteractorsFactory;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsLoader implements RequestCallback {

    public interface Handler {
        void interactorsLoaded(RawInteractors interactors, long time);
        void onInteractorsLoaderError(Throwable exception);
    }

    private final static String PREFIX = DiagramFactory.SERVER + "/ContentService/interactors/";

    private Handler handler;
    private Request request;

    InteractorsLoader(Handler handler) {
        this.handler = handler;
    }

    public void cancel(){
        if(this.request!=null && this.request.isPending()){
            this.request.cancel();
        }
    }

    public void load(String stId, String resource){
        if(resource==null){
            this.handler.onInteractorsLoaderError(new InteractorsException("Resource not specified"));
            return;
        }
        String url = PREFIX + resource + "/" + stId + ".json?v=" + LoaderManager.version;
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            this.request = requestBuilder.sendRequest(null, this);
        } catch (RequestException e) {
            this.handler.onInteractorsLoaderError(e);
        }
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        switch (response.getStatusCode()){
            case Response.SC_OK:
                try {
                    long start = System.currentTimeMillis();
                    RawInteractors interactors = InteractorsFactory.getInteractorObject(RawInteractors.class, response.getText());
                    long time = System.currentTimeMillis() - start;
                    this.handler.interactorsLoaded(interactors, time);
                } catch (InteractorsException e) {
                    this.handler.onInteractorsLoaderError(e);
                }
                break;
            default:
                this.handler.onInteractorsLoaderError(new Exception(response.getStatusText()));
        }

    }

    @Override
    public void onError(Request request, Throwable exception) {
        this.handler.onInteractorsLoaderError(exception);
    }
}
