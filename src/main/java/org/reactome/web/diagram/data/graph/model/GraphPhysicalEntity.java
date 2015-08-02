package org.reactome.web.diagram.data.graph.model;

import org.reactome.web.diagram.data.graph.raw.EntityNode;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class GraphPhysicalEntity extends GraphObject {

    protected String identifier;
    protected String sampleIdentifier;
    protected List<GraphPhysicalEntity> children = new ArrayList<>();
            
    private List<GraphReactionLikeEvent> isInputIn = new ArrayList<>();
    private List<GraphReactionLikeEvent> isOutputIn = new ArrayList<>();
    private List<GraphReactionLikeEvent> isCatalystIn = new ArrayList<>();
    private List<GraphReactionLikeEvent> isActivatorIn = new ArrayList<>();
    private List<GraphReactionLikeEvent> isInhibitorIn = new ArrayList<>();

    public GraphPhysicalEntity(EntityNode node) {
        super(node);
        this.identifier = node.getIdentifier();
    }

    public boolean addParent(List<GraphPhysicalEntity> parents){
        return this.parents.addAll(parents);
    }

    public boolean addChildren(List<GraphPhysicalEntity> children){
        return this.children.addAll(children);
    }

    public boolean addInputIn(GraphReactionLikeEvent rle){
        return isInputIn.add(rle);
    }

    public boolean addOutputIn(GraphReactionLikeEvent rle){
        return isOutputIn.add(rle);
    }

    public boolean addCatalystIn(GraphReactionLikeEvent rle){
        return isCatalystIn.add(rle);
    }

    public boolean addActivatorIn(GraphReactionLikeEvent rle){
        return isActivatorIn.add(rle);
    }

    public boolean addInhibitorIn(GraphReactionLikeEvent rle){
        return isInhibitorIn.add(rle);
    }


    public boolean isHit() {
        return sampleIdentifier!=null;
    }

    public void setIsHit(String sampleIdentifier, List<Double> expression) {
        this.sampleIdentifier = sampleIdentifier;
        this.expression = expression;
    }

    public void resetHit(){
        this.sampleIdentifier = null;
        this.expression = null;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Set<String> getParticipants(){
        Set<String> rtn = new HashSet<>();
        rtn.add(identifier);
        return rtn;
    }

    public Map<String, Double> getParticipantsExpression(int column){
        Map<String, Double> rtn = new HashMap<>();
        if(this.isHit()) {
            rtn.put(sampleIdentifier, this.getExpression(column));
        }
        return rtn;
    }

    public Set<String> getHitParticipants(){
        Set<String> rtn = new HashSet<>();
        if(this.isHit()) {
            rtn.add(sampleIdentifier);
        }
        return rtn;
    }

    public Set<GraphPhysicalEntity> getParentLocations() {
        Set<GraphPhysicalEntity> rtn = new HashSet<>();
        for (GraphPhysicalEntity parent : parents) {
            rtn.addAll(parent.getParentDiagramIds());
        }
        return rtn;
    }

    private Set<GraphPhysicalEntity> getParentDiagramIds(){
        Set<GraphPhysicalEntity> rtn = new HashSet<>();
        if(!getDiagramObjects().isEmpty()){
            rtn.add(this);
        }
        for (GraphPhysicalEntity parent : parents) {
            rtn.addAll(parent.getParentDiagramIds());
        }
        return rtn;
    }

    public Set<GraphReactionLikeEvent> participatesIn(){
        Set<GraphReactionLikeEvent> rtn = new HashSet<>();
        for (GraphReactionLikeEvent rle : isInputIn) {
            rtn.add(rle);
        }
        for (GraphReactionLikeEvent rle : isOutputIn) {
            rtn.add(rle);
        }
        for (GraphReactionLikeEvent rle : isCatalystIn) {
            rtn.add(rle);
        }
        for (GraphReactionLikeEvent rle : isActivatorIn) {
            rtn.add(rle);
        }
        for (GraphReactionLikeEvent rle : isInhibitorIn) {
            rtn .add(rle);
        }
        return rtn;
    }
}