import model.*;

import java.util.LinkedList;
import java.util.List;

public class main {
    public static void main(String... args) {
        new main();
    }

    public main(){
        try {
            String path = "E:\\87-semi.csv";
            FormalContext context = new FormalContext();

            CSVReader csv = new CSVReader(context,path);

            long startTime1 = System.currentTimeMillis();
            ConceptLattice L = new ConceptLattice();
            LatticeNode bottom = context.bottom();
            L.addNode(bottom);

            /**
             * 生成概念格
             */
            for(Transaction tr : context.getTrList()){
                context.getConceptLattice(L,tr);
            }

            long endTime1 = System.currentTimeMillis();
            /**
             * 生成每个节点生成最小内涵，以便之后的规则生成
             */
            //for(LatticeNode c :L.getLattices()) //顶节点的intred 个数为1，为空。？？？
            //    c.setIntRed(c.INTRED());
            /**
             * 根据需求字段，给出合适的结点

            List<String> search = new LinkedList<>();
            search.add("ch-oil");
            search.add("hh-air");
            if(L.getLattices().size()!=0){
                Queue<LatticeNode> q = new LinkedList();
                LatticeNode l = new LatticeNode();//记录当前结点
                q.add(L.getLattices().get(0));
                int i = search.size() ; //i 记录遍历search
                //从顶节点开始搜索
                while(!q.isEmpty() & i>0 ){
                    l.setIntension(q.peek().getIntension());
                    l.setExtCount(q.peek().getExtCount());
                    l.setExtension(q.peek().getExtension());
                    l.setChildren(q.peek().getChildren());
                    q.poll();
                    if(l.getIntension().contains( search.get(i-1))){
                        q.clear();
                        i--;
                    }
                    q.addAll(l.getChildren());
                }
                if(i==0){
                    System.out.println(l.getExtension().get(0));//搜索到了
                }
                else {
                    System.out.println("not found!");
                }
            }
           else{
                System.out.println("L is null!");
            }
             */

            long startTime2 = System.currentTimeMillis();
            /**
             * 根据概念格生成规则集
             */
            double theta = 0.2; //支持度
            double omega = 0.8 ;  //可信度
            //Rules rules = new Rules();
            //rules.getRuleList().addAll(context.getRules(L,theta,omega).getRuleList());

            /**
             * 根据概念格生成指定后件的规则集
             */

            List<String> rhs = new LinkedList<>();
            rhs.add("Semi");
            Rules rules = new Rules();
            context.getSpecifedRules(L,rhs);
            long endTime2 = System.currentTimeMillis();

            System.out.println("概念格生成时间：" + (endTime1 - startTime1) + "ms");
            System.out.println("规则生成时间：" + (endTime2 - startTime2) + "ms");
            System.out.println("总共运行时间：" + (endTime1 - startTime1 + endTime2 - startTime2) + "ms");
            System.out.println("概念格节点数：" + (L.getLattices().size()));
            //System.out.println("规则数：" + (endTime1 - startTime1 + endTime2 - startTime2) + "ms");
        }
        catch (Exception ex){
            System.out.println(ex);
        }
    }
}
