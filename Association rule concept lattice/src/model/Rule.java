package model;

import java.util.LinkedList;
import java.util.List;

public class Rule {
    List<String> primise ;
    List<String> conclusion;
    double support ;//支持度
    double confidence;//置信度
    double lift;//提升度

    public Rule(){
        primise = new LinkedList<>();
        conclusion = new LinkedList<>();
    }

    public List<String> getPrimise() {
        return primise;
    }

    public void setPrimise(List<String> primise) {
        this.primise = primise;
    }

    public void set_aPrimise(String primise) {
        this.primise.add(primise);
    }

    public List<String> getConclusion() {
        return conclusion;
    }

    public void setConclusion(List<String> conclusion) {
        this.conclusion = conclusion;
    }

    public double getSupport() {
        return support;
    }

    public void setSupport(double support) {
        this.support = support;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public double getLift() {
        return lift;
    }

    public void setLift(double lift) {
        this.lift = lift;
    }

    public String toString(){
        return primise.toString()+"====>"+conclusion.toString()+"，Confidence："+confidence+"，Lift："+lift;
    }

    public void Print(){
        System.out.println(this.toString());
    }

}
