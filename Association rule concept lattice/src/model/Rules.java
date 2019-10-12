package model;

import java.util.LinkedList;
import java.util.List;

public class Rules {
    public List<Rule> ruleList ;


    public Rules(){
        ruleList = new LinkedList<>();
    }
    public List<Rule> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<Rule> ruleList) {
        this.ruleList = ruleList;
    }

}
