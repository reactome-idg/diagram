package org.reactome.web.diagram.renderers.interactor.s050;

import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.renderers.interactor.abs.InteractorEntityAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorEntityRenderer050 extends InteractorEntityAbstractRenderer {
    @Override
    public void draw(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        ctx.save();
        ctx.setGlobalAlpha((factor - 0.5) * 2);
        super.draw(ctx, item, factor, offset);
        ctx.restore();
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        double alpha = ctx.getGlobalAlpha();
        ctx.save();
        ctx.setGlobalAlpha((factor - 0.5) * alpha * 2);
        super.drawText(ctx, item, factor, offset);
        ctx.restore();
    }
}