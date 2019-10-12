package model;

import java.util.LinkedList;
import java.util.List;

public class Pair {
    public int Lid ; //存储L中id位的pair信息
    List<LatticeNode> pair = new LinkedList<>();

    public int getLid() {
        return Lid;
    }

    public void setLid(int lid) {
        Lid = lid;
    }

    public List<LatticeNode> getPair() {
        return pair;
    }

    public void setPair(List<LatticeNode> pair) {
        this.pair = pair;
    }

    public void removePair(Pair pair){
        for(int i = 0 ; i< this.getPair().size();i++){
            for(LatticeNode p :pair.getPair())
                if(this.getPair().get(i).getId()==p.getId())
                    this.getPair().remove(i);
        }
    }
}
