/**
 * @author wangyj23925
 * @date Created in 2019-10-16  15:22
 * @description
 * @version
 */

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：wangyj23925
 * @date ：Created in 2019-10-16 15:22
 * @description： 贪心算法
 */
public class GreedyAlgorithm{
    /** 30种药物的维持时间数组*/
    private int[] holdDays = new int[]{180, 120, 60, 180, 120, 60, 180, 120, 60, 120, 75, 30, 120, 75, 30, 60, 45, 30, 60, 45, 30, 60, 45, 30, 60, 45, 30, 120, 60, 30};
    /** 10类药物的注射最小间隔数组*/
    private int[] period = new int[]{180, 180, 180, 90, 90, 30, 30, 14, 14, 0};
    /** 10类药物是否注射数组(禁忌列表)*/
    private boolean[] wetherTake = new boolean[]{false, false, false, false, false, false, false, false, false, false};
    /**注射药物的种类*/
    private int takeMediKind = 0, takeMediType = 0;
    /** 10种药物的体内累计量*/
    private int[] accmu = new int[10];
    /** 注射药物次数*/
    private  int takeTime = 0;
    /**是否应该吃药标志：true为应该吃药 */
    private boolean flag = true;
    /**新数组药物最优序号*/
    private int medicineId = takeTime % 30;
    /**注射药物方案结果*/
    private static int[][] result = new int[365][4];
    /** 替补药物成本 */
    private static double supplyCost;
    /**注射药物日期*/
    private static int takeDay;
    /**
     * 计算获取初步吃药方案
     *@param         date    日期
     *@param         num     病毒数量
     *@param         index   药物排序
     *@return        null
     *@author        wangyj23925
     *@date          2019-10-24
     */
    private void getSchemesFirst(int date, int num, int[] index) {
        // 病毒数量为负无需注射
        if (num < 0) {
            num = 0;
        }
        // 需要剂量=需要杀灭病毒数量/30
        int dose = num / 30;
        // 原数组药物最优序号
        int drugId = index[medicineId];
        int medicineType = drugId / 3;

        if (flag) {
            // 药物没有超过最短注射日期,取下一药物
            while (wetherTake[index[takeTime % 30] / 3]) {
                takeTime++;
            }
            medicineId = takeTime % 30;
            drugId = index[medicineId];
            medicineType = drugId / 3;
            wetherTake[medicineType] = true;
            accmu[medicineType] += dose;
            flag = false;
            String id = (medicineType + 1) + "-" + (drugId % 3 + 1);
            System.out.println("    今天该吃药物名称为:" + id);
            takeMediKind = drugId / 3;
            takeMediType = drugId % 3;
            takeTime++;
            takeDay = date;
        }
        if (wetherTake[medicineType] && period[medicineType] != 0) {
            period[medicineType]--;
        }
        /*到达注射最短注射间隔时间更新吃药禁忌*/
        if (period[medicineType] == 0) {
            /*重置禁忌状态列表*/
            wetherTake[medicineType] = false;
            /*重置禁忌时间列表*/
            period[medicineType] = Param.INIT_PERIOD[medicineType];
        }
        /*药物维持时间递减*/
        holdDays[drugId]--;
        /*药物维持时间到期*/
        if (holdDays[drugId] == 0) {
            flag = true;
            /* 药物维持时间重置*/
            holdDays[drugId] = Param.DRUG_HOLD_DAYS[drugId];
            /* 药物累积量清零*/
            accmu[medicineType] = 0;
        }
        // 保存当日吃药结果
        result[date][0] = takeMediKind;
        result[date][1] = takeMediType;
        result[date][2] = dose;
        result[date][3] = takeDay;

        //System.out.println("    今天是否该吃药(禁忌列表):" + Arrays.toString(wetherTake));
        //System.out.println("    药物间隔时间:" + Arrays.toString(period));
        //System.out.println("    体内累计量(累计列表):" + Arrays.toString(accmu));
    }

    private static int[] sortByCostPerDost(int[] drugHoldDays, double[] costPreDose) {
        //药物种类数量(按规格)
        int kinds = costPreDose.length;
        //性价比数组
        double[] r = new double[kinds];
        //按性价比排序物品的下标
        int[] index = new int[kinds];
        for (int i = 0; i < kinds; i++) {
            // 按照 药物性价比=药物维持时间/每剂量成本
            r[i] = (double) drugHoldDays[i] / costPreDose[i];
            //默认排序
            index[i] = i;
        }
        //对性价比进行排序（降序）
        double temp = 0;
        for (int i = 0; i < kinds - 1; i++) {
            for (int j = i + 1; j < kinds; j++) {
                if (r[i] < r[j]) {
                    temp = r[i];
                    r[i] = r[j];
                    r[j] = temp;
                    int x = index[i];
                    index[i] = index[j];
                    index[j] = x;
                }
            }
        }
        return index;
    }

    public static void main(String[] args) {
        // 1.按照药物性价比降序排列
        int[] index = sortByCostPerDost(Param.DRUG_HOLD_DAYS, Param.COST_PRE_DOSE);
        // 2.贪心算法处理得到初次方案（全年需要药物剂量分布、药物局部最优种类）
        GreedyAlgorithm ga = new GreedyAlgorithm();
        // 循环全年病毒数据
        for (int days = 0; days < Param.VIRUSES_NUMBER.length; days++) {
            // 实际日期
            int day = days + 1;
            System.out.println("第" + day + "天:病毒数量" + Param.VIRUSES_NUMBER[days]);
            ga.getSchemesFirst(days, Param.VIRUSES_NUMBER[days] - 10000, index);
        }
        // 3.汇总吃药方案list
        List<Scheme> schemeList = getSchemes();
        // 4.对药物剂量超过人体上限做 药物补充处理(选择用维持时间相同，成本最优的替补药物)
        double supplyCost = supplyDeal(schemeList);
        // 5.计算总成本
        double sumCost = getSumCost(schemeList);
        double cost = sumCost+supplyCost;
        System.out.println("最优方案总成本:" +cost );

    }
    /**
     * 汇总吃药方案list，取区间最大
     *@return        List<Scheme>
     *@author        wangyj23925
     *@date          2019-10-24
     */
    private static List<Scheme> getSchemes() {
        int lastKind = result[0][0];
        int lastType = result[0][1];
        int maxDose = result[0][2];
        int takeDay = result[0][3];
        // 注射药物id
        int id = 0;
        // 初次吃药方案链表
        List<Scheme> schemeList = new ArrayList(16);
        Scheme scheme0 = new Scheme();
        schemeList.add(scheme0);
        scheme0.setKind(lastKind);
        scheme0.setType(lastType);
        scheme0.setDose(maxDose);
        scheme0.setDay(takeDay);
        for (int[] i : result) {
            Scheme scheme1;
            // 药物种类不同
            if (i[0] != lastKind || i[1] != lastType) {
                scheme1 = new Scheme();
                lastKind = i[0];
                lastType = i[1];
                maxDose = i[2];
                id++;
                schemeList.add(id, scheme1);
            // 药物种类相同，取最大剂量
            } else {
                scheme1 = schemeList.get(id);
                if (i[2] > maxDose) {
                    maxDose = i[2];
                }
                schemeList.set(id, scheme1);
            }
            scheme1.setKind(i[0]);
            scheme1.setType(i[1]);
            scheme1.setDose(maxDose);
            scheme1.setDay(i[3]);
        }
        return schemeList;
    }
    /**
     * 计算总成本
     *@param         schemeList 方案数组
     *@return        sumCost    总成本
     *@author        wangyj23925
     *@date          2019-10-25
     */
    private static double getSumCost(List<Scheme> schemeList) {
        double sumCost = 0;
        for (Scheme scheme : schemeList) {
            int kind = scheme.getKind();
            int type = scheme.getType();
            int dose;
            if (scheme.getDose() > Param.DRUG_DOSE_LIMIT[scheme.getKind()]){
                dose = Param.DRUG_DOSE_LIMIT[scheme.getKind()];
            }else {
                dose = scheme.getDose();
            }
            int i = kind + 1;
            int d = type + 1;
            int day = scheme.getDay()+1;
            System.out.print("第" + day + "天,");
            System.out.println("吃药种类" + i + "-" + d + "，吃药剂量" + dose);
            double price = Param.PRICE_PARAM[kind * 3 + type];
            double cost = price * dose;
            sumCost = sumCost + cost;
        }
        return sumCost;
    }

    private static double supplyDeal(List<Scheme> schemeList) {
        int[] newDrugHoldDays = new int[30];
        double[] newCostPreDose = new double[30];
        int[] newId = new int[30];
        int j=0;
        int takeTime=0;
        List<Scheme> schemeList2 = new ArrayList(16);
        for (Scheme s : schemeList) {
            // 超过人体注射药物上限最大限制
            if (s.getDose() > Param.DRUG_DOSE_LIMIT[s.getKind()]) {
                // 确定补充的药物:选择用维持时间相同
                // int n = s.getKind() + 1;
                // int d = s.getType() + 1;
                // int day = s.getDay()+1;
                // System.out.print("第" + day + "天，需要补充其他药物");
                // System.out.println("吃药种类" + n + "-" + d + "，需要搭配其他药物");
                int holdDay = Param.DRUG_HOLD_DAYS[s.getKind() * 3 + s.getType()];
                for (int i = 0; i < Param.DRUG_HOLD_DAYS.length; i++) {
                    if (holdDay == Param.DRUG_HOLD_DAYS[i] && i != s.getKind() * 3 + s.getType()) {
                        newDrugHoldDays[j] = Param.DRUG_HOLD_DAYS[i];
                        newCostPreDose[j] = Param.COST_PRE_DOSE[i];
                        newId[j++] = i;
                    }
                }
                int[] index1 = sortByCostPerDost(newDrugHoldDays, newCostPreDose);
                int kind = newId[index1[takeTime]] / 3 ;
                int type = newId[index1[takeTime]] % 3 ;
                takeTime++;
                int suplyDose = s.getDose() - Param.DRUG_DOSE_LIMIT[s.getKind()];
                s.setDose(Param.DRUG_DOSE_LIMIT[s.getKind()]);
                Scheme scheme0 = new Scheme();
                schemeList2.add(scheme0);
                scheme0.setKind(kind);
                scheme0.setType(type);
                scheme0.setDay(s.getDay());
                scheme0.setDose(suplyDose);
                //int t = kind + 1;
                //int m = type + 1;
                //if(suplyDose>Param.DRUG_DOSE_LIMIT[s.getKind()]){
                //    System.out.println(" 补充药物种类" + t + "-" + m + "，吃药剂量" + Param.DRUG_DOSE_LIMIT[s.getKind()]);
                //}else {
                //    System.out.println(" 补充药物种类" + t + "-" + m + "，吃药剂量" + suplyDose);
                //}
            }
        }
        double tempCost =0;
        if(schemeList2.size()!=0){
            tempCost=getSumCost(schemeList2);
            supplyCost =tempCost+supplyDeal(schemeList2);
            return supplyCost;
        }else {
            return supplyCost;
        }

    }
}
