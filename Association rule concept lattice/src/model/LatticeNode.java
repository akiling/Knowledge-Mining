package model;

import java.util.*;

import static java.lang.Math.sqrt;

public class LatticeNode {
    private int id;//点的添加顺序
    private List<Transaction> extension;//外延集
    private List<String> intension;//内涵集
    private int extCount;//外延数目
    private List<List<String>> intRed;//内涵缩减集
    private List<LatticeNode> parents;
    private List<LatticeNode> children;
    boolean visit = false; //标记是否访问

    //构造
    public LatticeNode() {
        extension = new LinkedList<>();
        intension = new LinkedList<>();
        extCount = 0;
        intRed = new LinkedList<>();
        parents = new LinkedList<>();
        children = new LinkedList<>();
    }

    public LatticeNode(LatticeNode c) {
        this.id = c.getId();
        this.extension = c.getExtension();
        this.intension = c.getIntension();
        this.extCount = c.getExtCount();
        this.intRed = c.getIntRed();
        this.parents = c.getParents();
        this.children = c.getChildren();
        this.visit = c.isVisit();
    }

    //set & get
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Transaction> getExtension() {
        return extension;
    }

    public void setExtension(List<Transaction> extension) {
        this.extension = extension;
    }

    public List<String> getIntension() {
        return intension;
    }

    public void setIntension(List<String> intension) {
        this.intension = intension;
    }

    public int getExtCount() {
        return extCount;
    }

    public void setExtCount(int extCount) {
        this.extCount = extCount;
    }

    public List<List<String>> getIntRed() {
        return intRed;
    }

    public void setIntRed(List<List<String>> intRed) {
        this.intRed = intRed;
    }

    public List<LatticeNode> getParents() {
        return parents;
    }

    public void setParents(List<LatticeNode> parents) {
        this.parents = parents;
    }

    public List<LatticeNode> getChildren() {
        return children;
    }

    public void setChildren(List<LatticeNode> children) {
        this.children = children;
    }

    public boolean isVisit() {
        return visit;
    }

    public void setVisit(boolean visit) {
        this.visit = visit;
    }

    /*
        public List<List<String>> INT_RED(){

            List<List<String>> int_red = new LinkedList<>();
            List<String> mix = new LinkedList<>();


            这里不太确定是不是这样：如果没有父节点，就不用计算int_red

            if(this.getParents().size()==0){
                return int_red;
            }
            else{
                //通过循环 先找到 内涵缩减族集里 最小又最大的集合mix
                //得出最小的mix∈ D2 ∩ (D1 - 所有父节点D3 ) ≠ 空集
                //不知道要不要加上集合本身，例子里一个加了一个没加
                //？？？？？？？？？
                for(LatticeNode n : this.getParents())
                {
                    List<String> temp = new LinkedList<>();
                    temp.addAll(this.getIntension());
                    temp.removeAll(n.getIntension());
                    mix.addAll(temp);
                }
                //获得所有父节点的子集
                List<List<String>> parentsubsets = new LinkedList<>();
                for(int i = 0 ; i< this.getParents().size() ;i++){
                    parentsubsets.addAll(this.getSubSets(this.getParents().get(i).getIntension()));
                }
                //array存储所有父节点子集数
                int numOfParents = parentsubsets.size();
                int[] array = new int[numOfParents] ;
                for(int i = 0 ;i <numOfParents;i++)
                    array[i]=parentsubsets.get(i).size();
                //for(int j = 0 ; j<parentsubsets.size();j++)
                 //   for( int k = 0 ; k < parentsubsets.get(j).size(); k++)
                //算出 最小集合 和 该节点的 差diff ;
                List<List<String>> subsetsAddAll = new LinkedList<>();
                List<String> subsets = new LinkedList<>();
                for(int i =0 ; i < numOfParents ;i++){//控制第i位循环
                    for(int j = 0 ; j < parentsubsets.get(i).size();j++){
                        //subsets.addAll(parentsubsets.get(i));
                    }
                }
                List<String> diff = new LinkedList<>();
                diff.addAll(this.getIntension());
                diff.removeAll(mix);
                // 找出 差diff 所有子集 ∪ mix 存入 int_red
                int length =diff.size();
                int_red.add(mix);
                for(int i = 1 ; i< Math.pow(length,2)-1  ; i++){ //去掉 子集本身 2^-2是真子集个数
                    List<String> temp = new LinkedList<>(); //存入找到的当前二进制对应的一个x的真子集
                    for(int j = 0 ; j<length ;j++)
                        // 1移j个位置
                        if((1<<j&i)!=0)
                            temp.add(diff.get(j));
                    //这里之后temp存储的值 不在按照人为的顺序，可能出现 AF D ，等等。
                    temp.addAll(mix);
                    int_red.add(temp);
                }
                return int_red;
            }
        }
    */
    public List<List<String>> INTRED() {
        List<List<String>> int_red = new LinkedList<>();
        if (this.getParents().size() == 0) {
            //不知道这个判断要不要
            List<String> kongji = new LinkedList<>();
            int_red.add(kongji);
            return int_red;
        } else if (this.getParents().size() == 1) {
            List<String> D = new LinkedList<>();
            D.addAll(this.getIntension());
            D.removeAll(this.getParents().get(0).getIntension());

            for (int i = 0; i < D.size(); i++) {
                List<String> a = new LinkedList<>();
                a.add(D.get(i));
                int_red.add(a);
            }
        } else {
            for (int i = 0; i < this.getParents().size() - 1; i++) {
                for (int j = i + 1; j < this.getParents().size(); j++) {
                    for (int n = 0; n < this.getParents().get(i).getIntension().size(); n++) {
                        for (int m = 0; m < this.getParents().get(j).getIntension().size(); m++) {
                            String bi = this.getParents().get(i).getIntension().get(n);
                            String bj = this.getParents().get(j).getIntension().get(m);
                            boolean flag = true;
                            for (int k = 0; k < this.getParents().size(); k++) {
                                boolean one = this.getParents().get(k).getIntension().contains(bi);
                                boolean two = this.getParents().get(k).getIntension().contains(bj);
                                if (one & two) {//只要有集合包含bibj
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag == true) {
                                List<String> bibj = new LinkedList<>();
                                bibj.add(bi);
                                bibj.add(bj);
                                int_red.add(bibj);
                            }
                        }
                    }
                }
            }
        }
        return int_red;
    }

    public List<LatticeNode> getAncestors() {
        List<LatticeNode> Ancestors = new LinkedList<>();
        Queue<LatticeNode> q = new LinkedList();
        q.addAll(getParents());
        while (!q.isEmpty()) {
            if (q.peek().getParents().size() != 0) {
                q.addAll(q.peek().getParents());
            }
            Ancestors.add(q.poll());
        }
        return Ancestors;
    }

    public List<List<String>> getSubSets(List<String> A) {
        int length = A.size();
        List<List<String>> subsets = new LinkedList<>();
        for (int i = 1; i < Math.pow(length, 2) - 1; i++) { //去掉 空集
            List<String> temp = new LinkedList<>(); //存入找到的当前二进制对应的一个x的真子集
            for (int j = 0; j < length; j++)
                // 1移j个位置
                if ((1 << j & i) != 0)
                    temp.add(A.get(j));
            subsets.add(temp);
        }
        return subsets;
    }

    public String toString() {
        //在算法里 就没有改变extension的值的
        return "{" + extCount + ",(" + intension.toString() + "}\n";
    }
/*
    public class Latticenode implements Cloneable {
        public String name[];

        public Latticenode(){
            name=new String[2];
        }

        public Object clone() {
            Latticenode o = null;
            try {
                o = (Latticenode) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return o;
        }
    }
    for(int i=0;i<n;i+=){
        copy.add((Latticenode)src.get(i).clone());
    }
   */
}

    /**
     * 重写对象的eqauls()方法。
     */