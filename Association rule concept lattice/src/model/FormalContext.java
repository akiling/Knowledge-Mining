package model;

import sun.security.pkcs11.wrapper.CK_SSL3_KEY_MAT_PARAMS;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static jdk.nashorn.internal.objects.NativeArray.push;

public class FormalContext {
    private List<Transaction> trList;//transactionList
    private List<String> M; //all attributes
    //构造
    public FormalContext() {
        trList = new ArrayList<>();
        M = new ArrayList<>();
    }
    //get & set
    public List<Transaction> getTrList() {
        return trList;
    }

    public void setTrList(List<Transaction> trList) {
        this.trList = trList;
    }

    public List<String> getM() {
        return M;
    }

    public void setM(List<String> m) {
        M = m;
    }

    public void setAttrOfM(String attr) {
        this.M.add(attr);
    }
    //添加一条事务
    public void setTr(Transaction tr) {
        this.trList.add(tr);
    }
    //输出一个形式背景的表示
    public String toString() {
        String c = null;
        c = "Attritudes: " + M.toString() + "\n";
        for (int i = 0; i < trList.size(); i++)
            c = c + trList.get(i).toString();
        return c;
    }

    public void Print() {
        System.out.println(this.toString());
    }

    public void printM(FormalContext context) {
        System.out.println(context.M);
    }
    //取得顶结点
    public LatticeNode top() {
        LatticeNode top = new LatticeNode();

        top.setIntension(g(this.trList));
        top.setExtension(this.trList);
        top.setExtCount(top.getExtension().size());
        //都没有父节点，所以不用设置int_red
        return top;
    }

    public LatticeNode bottom() {
        LatticeNode bottom = new LatticeNode();

        bottom.setId(0);
        bottom.setIntension(this.getM());

        return bottom;
    }
    //返回 事务集合 的 共有属性集合，存储在 List<String>
    public List<String> g(List<Transaction> D) {
        List<String> O = new ArrayList<>();//存储结果

        Queue<String> q = new LinkedList<>();
        for (int i = 0; i < D.get(0).getAttritudes().size(); i++) {
            q.offer(D.get(0).getAttritudes().get(i));
        }
        while (!q.isEmpty()) {
            String c = q.poll();
            //flag控制是否到下一个事物查找属性
            boolean flag = true;
            for (int i = 1; i < D.size(); i++) {
                if (!D.get(i).hasAttritude(c)) {
                    flag = false;
                    break;
                }
            }
            if (flag == true)
                O.add(c);
        }
        return O;
    }

    //返回 属性集合 的 共有事务集合，存储在 List<Transction>
    //public List<Transction> f(List<String> A){}

    public ConceptLattice getConceptLattice(ConceptLattice L, Transaction tr) {

        List<LatticeNode> mark = new ArrayList<>();
        ConceptLattice Ltemp = new ConceptLattice();
        //每个结点C 按 intension 升序插入到 格L 中，所以直接按序访问
        //使用for : 的循环 ，在Collection发生变化时会报错 ；但是在遍历的时候 该算法就是需要实时判断新加入的结点的。
        //if(addNodeTag)
        for (LatticeNode c : L.getLattices()) {

            //如果 c.intension∈Tr
            if (tr.getAttritudes().containsAll(c.getIntension())) { //containsall不会改变原来集合内容，其他∩∪等运算才会
                c.setExtCount(c.getExtCount() + 1);
                c.getExtension().add(tr);//自己添加
                mark.add(c);
                if (c.getIntension().equals(tr.getAttritudes()))
                    break;
            } else {
                //int = c.intension ∩ Tr
                List<String> inten = new LinkedList<>();
                inten.addAll(c.getIntension());
                inten.retainAll(tr.getAttritudes());

                //if 不存在 ck ∈ c.Ancestors 且 ck ∈ mark 使 ck.intension =int。以下是判断本句
                List<LatticeNode> p = new LinkedList<>();
                //应该要获得祖先结点
                p.addAll(c.getAncestors());
                p.retainAll(mark);
                boolean flag = false;
                for (LatticeNode ck : p) {
                    if (ck.getIntension().equals(inten)) {
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    LatticeNode n = new LatticeNode();
                    n.setIntension(inten);
                    int num = c.getExtCount() + 1;
                    n.setExtCount(num);
                    List<Transaction> extension = new LinkedList<>();
                    extension.addAll(c.getExtension());
                    n.setExtension(extension);//自己添加
                    n.getExtension().add(tr);//自己添加

                    int idnum = L.getLattices().size() + Ltemp.getLattices().size();
                    n.setId(idnum);

                    if (n.getIntRed().size() != 0)
                        n.getIntRed().clear();

                    c.getParents().add(n);
                    n.getChildren().add(c);

                    for (LatticeNode m : mark) {
                        //if m.intension 属于 n.intension
                        boolean ok;
                        if (n.getIntension().containsAll(m.getIntension())) {
                            ok = true;
                            for (LatticeNode mc : m.getChildren()) {
                                if (n.getIntension().containsAll(mc.getIntension())) {
                                    ok = false;
                                    break;
                                }
                            }
                            if (ok) {
                                if (c.getParents().contains(m)) {
                                    c.getParents().remove(m);
                                    m.getChildren().remove(c);
                                }
                                m.getChildren().add(n);
                                n.getParents().add(m);
                            }
                        }
                    }
                    //对产生子节点和新增的结点需要计算其内涵缩减集
                    //打算所有结点生成后计算
                    //n.setIntRed(n.INT_RED());
                    //c.setIntRed(c.INT_RED());
                    mark.add(n);//新增点重新循环后访问，一点会加入mark
                    Ltemp.addNode(n);

                    if (inten.equals(tr.getAttritudes()))
                        break;
                }
            }
        }
        //L在添加Ltemp中内容时，需要按顺序添加
        for (LatticeNode c : Ltemp.lattices)
            L.addNode(c);
        return L;
    }
    //不规定后件，冗余很小的关联规则生成，不满足置信度的不加入规则集
    public Rules getRules(ConceptLattice L, double theta, double omega) {
        Rules ruleList = new Rules();
        //最小支持度，判断是否是频繁结点
        double numsup = theta * this.getTrList().size();
        List<LatticeNode> CandNode = new LinkedList<>();
        //pairs顺序对应L中频繁结点的顺序，存储其满足条件的所有祖先结点
        List<Pair> pairs = new LinkedList<>();
        //存储候选二元组

        //整个For循环是对每个频繁节点进行：生成候选二元组
        for (int i = 0; i < L.lattices.size(); i++) {
            LatticeNode c = L.lattices.get(i);
            if (c.getExtCount() > numsup) {
                Queue<LatticeNode> q = new LinkedList();
                double numcon = c.getExtCount() / omega;
                //我先cc取队头，不加入pairsS，看论文的举例是c点本身不加入pairsS,所以直接把c的父亲加入pairsS
                for (LatticeNode ccp : c.getParents()) {
                    if (ccp.getExtCount() <= numcon)//由于Extension(C2)/Extension(C1)>=omega
                        if (q.contains(ccp) == false) //这一步增加的，因为本生list不会判是否重复
                            q.add(ccp);
                }
                Pair pair = new Pair();
                pair.setLid(c.getId());
                while (!q.isEmpty()) {
                    LatticeNode cc = new LatticeNode(q.poll());
                    for (LatticeNode ccp : cc.getParents()) {
                        if (ccp.getExtCount() <= numcon)
                            if (q.contains(ccp) == false) //这一步增加的，因为本生list不会判是否重复
                                q.add(ccp);
                    }
                    if (pair.getPair().contains(cc) == false) //同上
                        pair.getPair().add(cc);
                }
                pairs.add(pair);
                //以下都是按照内函数Des降序插入Candnode记录的是频繁节点
                //CandNode按理不会重复
                if (!CandNode.contains(c)) {
                    if (CandNode.size() == 0)
                        CandNode.add(c);
                    else {
                        int j = 0;
                        for (; j < CandNode.size(); j++)
                            if (c.getIntension().size() > CandNode.get(j).getIntension().size()) {
                                CandNode.add(j, c);
                                break;
                            }
                        if (j == CandNode.size())
                            CandNode.add(c);
                    }
                }
            }
        }
        for (int i = 0; i < CandNode.size(); i++) {
            LatticeNode c = new LatticeNode(CandNode.get(i));
            int Lid = c.getId();
            //pLocation找到该频繁节点对应的候选二元组的下标位置
            int pLocation = -1;
            for (int k = 0; k < pairs.size(); k++) {
                if (pairs.get(k).getLid() == Lid) {
                    pLocation = k;
                    break;
                }
            }
            if (pLocation == -1)
                System.out.println("wrong!");
                ///P A IR S [C ]∶= P AIRS [C ] - P A IR S [C′];
            else {
                for (LatticeNode cp : c.getChildren()) {
                    //获取孩子结点在PAIRS中的下标
                    int cpLocation = -1;
                    for (int m = 0; m < pairs.size(); m++) {
                        if (pairs.get(m).getLid() == cp.getId()) {
                            cpLocation = m;
                            break;
                        }
                    }
                    if (cpLocation != -1)
                        //原先remove没有生效
                        pairs.get(pLocation).removePair(pairs.get(cpLocation));
                }
            }
            //以上消除冗余

            List<List<String>> LH = new LinkedList<>();
            //each C′in P AIR S [C ] DO L H∶= L H∪ C′. int red
            for (LatticeNode cpp : pairs.get(pLocation).getPair())
                LH.addAll(cpp.getIntRed());
            //FOR each M in L H DO
            List<Integer> array = new ArrayList<>(); //存储要删除的位置，array存储p从小到大，但此处可能会删除同一个位置，会导致之后remove动作报错，所以要提前break
            for (int p = 0; p < LH.size(); p++) {
                for (int q = 0; q < LH.size(); q++)
                    if (LH.get(p).containsAll(LH.get(q)) & (!LH.get(p).equals(LH.get(q)))) {
                        array.add(p);
                        break;
                    }

            }
            //消除LH中的冗余IF L H 中存在 M′满足 M'属于 M THEN L H∶= L H - {M };
            for (int p = 0; p < array.size(); p++) {
                int location = array.get(p) - p;
                LH.remove(location);
            }
            for (List<String> a : LH) {
                Rule rule = new Rule();
                rule.setPrimise(a);
                List<String> b = new LinkedList<>();
                b.addAll(c.getIntension());
                b.removeAll(a);
                rule.setConclusion(b);
                rule.Print();
                ruleList.ruleList.add(rule);
            }
        }
        return ruleList;
    }
    //生成指定后件的关联规则，先生成规则，再判断满足不满足置信度
    public void getSpecifedRules(ConceptLattice L, List<String> rhs) {
        Queue<LatticeNode> q = new LinkedList<>();

        double theta = 0.15;
        double numsup = theta * this.getTrList().size();

        //广度优先搜索找到第一个包含rhs的结点H,从顶概念搜过——好像就是我的一般查找方式
        Queue<LatticeNode> deepFirstQueue = new LinkedList<>();
        deepFirstQueue.add(L.getLattices().get(0));
        LatticeNode H = new LatticeNode();
        while (!deepFirstQueue.isEmpty()) {
            H = deepFirstQueue.poll();
            if (H.getIntension().containsAll(rhs))
                break;
            else {
                deepFirstQueue.addAll(H.getChildren());
            }
        }
        //queue← H, rulese t←空集 , sing leset←空集，因为不需要在这里输出，就不创建ruleset和singleset
        q.add(H);
        //方便计算提升度
        int pb = H.getExtCount();
        //List<Rule> SpecifedRules = new LinkedList<>();

        while (!q.isEmpty()) {
            q.addAll(q.peek().getChildren());
            if (!q.peek().isVisit() & q.peek().getExtCount() > numsup) { //支持度满足
                GenRuleFromNode(q.peek(), rhs, L,pb);
                q.peek().visit = true;
            }
            q.poll();
        }
    }
    public List<Rule> GenRuleFromNode(LatticeNode c, List<String> rhs, ConceptLattice L,int pb) {
        List<Rule> SpecifedRules = new LinkedList<>();
        List<String> singset = new LinkedList<>(); //我觉得应该是 单个元素的集合，那么不需要再外套个集合


        double y = (double)pb/L.getLattices().get(0).getExtCount();//用来设置提升度

        int d = c.getParents().size();
        List<String> x = new LinkedList<>();
        x.addAll(c.getIntension());
        //想这一步能不能并入到下面for循环中，同一般取规则算法一样。
        if (d == 1) {
            List<String> x1 = new LinkedList<>();
            x1.addAll(c.getParents().get(0).getIntension());
            x.removeAll(x1);
            x.removeAll(rhs);
            for (String p : x) {
                Rule rule = new Rule();
                rule.set_aPrimise(p);
                //rule.setSupport(c.getExtCount() / this.getTrList().size());    //sup= C ‖ O‖
                rule.setConclusion(rhs);
                rule.setConfidence(1);

                double lift = (double)1 / y;//这里原先没有取小数。
                rule.setLift(lift);
                SpecifedRules.add(rule);

                //做并集，这里p属于x，只要保证x中无冗余就行
                singset.add(p);
                for (Rule r : SpecifedRules) {
                    r.Print();
                }
                return SpecifedRules;
            }
            return null;
        }
        //for H 的每个父节点计算它们的并 S
        List<String> S = new LinkedList<>();
        S.addAll(c.getParents().get(0).getIntension());
        for (int i = 1; i < c.getParents().size(); i++) {
            S.removeAll(c.getParents().get(i).getIntension());
            S.addAll(c.getParents().get(i).getIntension());
        }
        //S去除推出条件
        S.removeAll(rhs);
        //先 生成前件一个，置信度为1的规则
        x.removeAll(S);
        //X-S中可能还包含的元素 属于rhs ，我觉得应该多一步剔除（在搜索含rhs的第一个结点，其父节点可能不含rhs，所以X-S可能包含rhs，仅这种情况）/我添加了 S.removeAll(rhs);这一行 ，则更需要这行代码
        x.removeAll(rhs);
        for (String p : x) {
            Rule rule2 = new Rule();
            rule2.set_aPrimise(p);
            //rule2.setSupport(c.getExtCount() / this.getTrList().size());    //sup= C ‖ O‖
            rule2.setConclusion(rhs);
            rule2.setConfidence(1);
            //增加的
            double lift = (double)1/y ;
            rule2.setLift(lift);
            SpecifedRules.add(rule2);

            //做并集，这里p属于x，只要保证x中无冗余就行
            singset.add(p);
        }
        //生成 前件为1 置信度<1的规则
        for (String pp : S) {//pp属于S
            if(!rhs.contains(pp)){
                boolean flag = false;
                for (LatticeNode cc : c.getParents()) {   // 判断p 和 rhs不包含在同一个父节点中
                    boolean t1 = cc.getIntension().contains(pp);
                    boolean t2 = cc.getIntension().containsAll(rhs);
                    if (t1 & t2) {
                        flag = true;
                        //想要break再外面一个的for
                        break;
                    }
                }
                //p 不属于singleset
                boolean tag = !singset.contains(pp);
                if (tag  && !flag) {
                    Rule rule3 = new Rule();
                    rule3.set_aPrimise(pp);
                    //rule3.setSupport(c.getExtCount() / this.getTrList().size());    //sup= C ‖ O‖
                    rule3.setConclusion(rhs);

                    List<String> s = new LinkedList<>();
                    s.add(pp);
                    int a,b;
                    a=c.getExtCount();
                    LatticeNode l = PX(s, L);
                    b=PX(s, L).getExtCount();
                    double conf =(double)a/b;
                    rule3.setConfidence(conf);
                    //增加的
                    double lift =conf/y;
                    rule3.setLift(lift);

                    SpecifedRules.add(rule3);
                }
            }
        }
        Queue<List<String>> LL = new LinkedList<>();
        //LL是S所有大小为2的子集
        for (int i = 0; i < S.size() - 1; i++) {
            for (int j = i+1; j < S.size(); j++) {
                List<String> temp = new LinkedList<>();
                temp.add(S.get(i));
                temp.add(S.get(j));
                LL.add(temp);
            }
        }
        List<List<String>> L2 = new LinkedList<>();//存储S←S∪ K里的K的集合，置信度不为1的所有前件
        for (int k = 2; k <= d; k++) {
            while (!LL.isEmpty()) {
                S.clear();
                int LLsize  = LL.size();
                for ( int i = 0 ; i < LLsize;i++) {
                    List<String> K = new LinkedList<>();
                    K.addAll(LL.peek());
                    LL.poll();
                    //if K 中的所有属性值对不含在同一父节点中 ,即 没有一个父结点 包含 K
                    boolean flag1 = false;
                    for (LatticeNode kk : c.getParents()) {
                        boolean tag = kk.getIntension().containsAll(K);
                        if (tag) {
                            flag1 = true;//这为true下一步flag也不用了其实
                            break;
                        }
                    }
                    //且没有 R∈ ruleset 使 R K
                    boolean flag2 = false;
                    for (Rule R : SpecifedRules) {
                        if (K.containsAll(R.getPrimise())) {
                            flag2 = true;
                            break;
                        }
                    }
                    //flag 都为false,这种情况少
                    if ( !(flag1 || flag2)) {
                        Rule rule4 = new Rule();
                        rule4.setPrimise(K);
                        rule4.setConclusion(rhs);
                        //rule4.setSupport(c.getExtCount() / this.getTrList().size());
                        rule4.setConfidence(1);
                        //增加的
                        double lift = (double)1/y ;
                        rule4.setLift(lift);
                        SpecifedRules.add(rule4);
                    }
                    else {
                        L2.add(K);
                        //S.addAll(K);
                        //if K 和 rhs不含在同一父节点中
                        boolean flag3 = false;
                        if(flag1==true) { //只有再flag1为true下 ，flag3才可能为true
                            for (LatticeNode three : c.getParents()) {
                                if (three.getIntension().containsAll(K) & three.getIntension().containsAll(rhs)) {
                                    flag3 = true;
                                    break;
                                }
                            }
                        }
                        if (!flag3) {
                            Rule rule5 = new Rule();
                            rule5.setPrimise(K);
                            rule5.setConclusion(rhs);
                            int a,b;
                            a=c.getExtCount();
                            //LatticeNode l = PX(K, L);
                            b=PX(K, L).getExtCount();
                            double conf = (double)a/b ;
                            //rule5.setSupport(c.getExtCount() / this.getTrList().size());
                            rule5.setConfidence(conf);
                            //增加的
                            double lift = conf / y;
                            rule5.setLift(lift);
                            SpecifedRules.add(rule5);
                        }
                    }
                }
                if (k < d) {     // 为保证k+1 <d
                    //因为LL是动态的，所以用了队列，但在这里L2元素组合放入LL，不能判别是否重复，需要改一改
                    int m ,j;
                    int L2size = L2.size();
                    List<List<String>> ll = new LinkedList<>();
                    ll.addAll(LL);
                    //int llsize = ll.size();
                    for ( m = 0; m < L2size - 1; m++) {
                        for ( j = m + 1; j < L2size; j++) {
                            List<String> temp = new LinkedList<>();
                            temp.addAll(L2.get(m));
                            temp.removeAll(L2.get(j));
                            temp.addAll(L2.get(j));
                            if (temp.size() == k + 1){
                                boolean f = true;
                                for(int i =0 ; i <ll.size()  ;i++){
                                    if(!ll.get(i).equals(temp)){
                                        f = false;
                                        break;
                                    }
                                }
                                if(f==true)
                                    ll.add(temp);
                            }
                        }
                    }
                    L2.clear();
                    LL.addAll(ll);
                }
                //L← { S1∪ S2|S1∈ S , S2∈ S,‖ S1∪ S2‖ = ‖ S1‖ + 1}
                //我估计它原来意思是从2个元素的集合的L集合，扩张到3个元素的集合的L集，
            }
        }
        for (Rule r : SpecifedRules) {
            r.Print();
        }
        return SpecifedRules;
    }
    //其实也就是一个查询，查询最顶部出现s的结点
    public LatticeNode PX(List<String> search, ConceptLattice L) {
        if (L.getLattices().size() != 0) {
            Queue<LatticeNode> q = new LinkedList();
            //LatticeNode l = new LatticeNode();//记录当前结点
            q.add(L.getLattices().get(0));
            //int i = search.size(); //i 记录遍历search
            //从顶节点开始搜索
            LatticeNode l = new LatticeNode();
            LatticeNode tag = new LatticeNode();
            tag.setExtCount(0);
            boolean flag = true ; //判别是不是第一次找到research
            while(!q.isEmpty()){
                l.setChildren(q.peek().getChildren());
                l.setExtension(q.peek().getExtension());
                l.setIntension(q.peek().getIntension());
                l.setExtCount(q.peek().getExtCount());
                q.poll();
                if (l.getIntension().containsAll(search)) {
                    //先取Intension小的，再取extcount小的
                    if(flag){
                        tag.setParents(l.getParents());
                        tag.setChildren(l.getChildren());
                        tag.setExtension(l.getExtension());
                        tag.setIntension(l.getIntension());
                        tag.setExtCount(l.getExtCount());
                        flag = false;
                    }
                    else if(l.getIntension().size()<tag.getIntension().size()){
                        tag.setParents(l.getParents());
                        tag.setChildren(l.getChildren());
                        tag.setExtension(l.getExtension());
                        tag.setIntension(l.getIntension());
                        tag.setExtCount(l.getExtCount());
                    }
                    else if ((l.getIntension().size()==tag.getIntension().size() )  && (l.getExtCount()>tag.getExtCount())){
                        tag.setParents(l.getParents());
                        tag.setChildren(l.getChildren());
                        tag.setExtension(l.getExtension());
                        tag.setIntension(l.getIntension());
                        tag.setExtCount(l.getExtCount());
                    }
                }
                q.removeAll(l.getChildren());
                q.addAll(l.getChildren());
            }
            return  tag;
        }
        return null;
    }
}
