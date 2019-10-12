package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Transaction {
    private int Tid ;
    private String name ;
    private List<String> attributes;

    //构造
    public Transaction(){

    }

    public  Transaction(int id, String s ,List<String> attrs){
        this.Tid = id ;
        this.name= s;
        this.attributes= new LinkedList<>(attrs);
    }

    //get & set
    public int getTid() {
        return Tid;
    }

    public void setTid(int tid) {
        Tid = tid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAttritudes() {
        return attributes;
    }

    public void setAttritudes(List<String> attritudes) {
        this.attributes = attritudes;
    }

    //打印
    public String toString(){
        return "Tid:" +this.getTid() + " name:"+this.getName() +" attr:"+this.getAttritudes()+" \n";
    }

    public void Print(){
        System.out.println(this.toString());
    }


    //本事务是否含有属性
    public boolean hasAttritude(String c){
        for(int i = 0 ; i < attributes.size();i++)
            if(attributes.get(i)==c)
                return true;

        return false;
    }
}
