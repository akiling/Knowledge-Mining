import model.FormalContext;
import model.RuleTable;
import model.Transaction;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVReader {
    public CSVReader(FormalContext context,String FilePath) throws IOException {

        FileReader fileread = new FileReader(new File(FilePath));
        BufferedReader buffread = new BufferedReader(fileread);
        String line;

        //提取属性集
        line = buffread.readLine();
        String[] s = line.split(",",2);
        String[] m = s[1].split(",");
        context.setM(Arrays.asList(m));

        //建立事务集
        int Tid = 0;
        /*/
        |||这里 有可能CSV 和EXCEL转的过程中，出现空的一行，但在CSV文件能读取哪一行——》出错。
         */
        while ((line = buffread.readLine()) != null) {
            String[] attr = line.split(",");
            List<String> attrName = new ArrayList<>();

            Transaction tr = new Transaction();
            tr.setTid(Tid);
            tr.setName(attr[0]);

            for (int i = 1; i < attr.length; i++) {
                if (attr[i].equals("1"))
                    attrName.add(m[i-1]);
            }
            tr.setAttritudes(attrName);
            Tid++;
            context.setTr(tr);
        }
        buffread.close();
    }
}


