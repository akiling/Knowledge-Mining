package model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ConceptLattice {
    List<LatticeNode> lattices;

    //无参构造，格里面有一个空结点
    public ConceptLattice(){
        lattices = new LinkedList<>();
    }
    public List<LatticeNode> getLattices() {
        return lattices;
    }

    public void setLattices(List<LatticeNode> lattice) {
        this.lattices = lattice;
    }

    //向格中添加一个结点,按照|c.intension|升序增加
    public void addNode(LatticeNode c){
        /*
        能否用游标来控制？？？
         */
        if(lattices.size()==0) {
            lattices.add(c);
        }
        else {
            int i = 0;
            for (; i < lattices.size(); i++)
                if (c.getIntension().size() <= lattices.get(i).getIntension().size()) {
                    lattices.add(i, c);
                    break;
                }
            if(i==lattices.size())
                lattices.add(c);
        }
    }
    //降序插入
    public void addNodeDes(LatticeNode c){
        if(lattices.size()==0) {
            lattices.add(c);
        }
        else {
            int i = 0;
            for (; i < lattices.size(); i++)
                if (c.getIntension().size() > lattices.get(i).getIntension().size()) {
                    lattices.add(i, c);
                    break;
                }
            if(i==lattices.size())
                lattices.add(c);
        }
    }
    public String toString(){
        String s = "";
        for(LatticeNode c :this.lattices){
            s = s + c.toString();
        }
        return  s;
    }

}
