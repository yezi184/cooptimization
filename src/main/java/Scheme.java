/**
 * @author wangyj23925
 * @date Created in 2019-10-21  9:44
 * @description
 * @version
 */

/**
 * @author     ：wangyj23925
 * @date       ：Created in 2019-10-21 9:44
 * @description：吃药方案
 * @modified By：
 * @version: $
 */
public class Scheme {
    private int kind;
    private int type;
    private int dose;
    private int day;


    public int getDose() {

        return this.dose;

    }

    public int getType() {

        return this.type;

    }

    public int getDay() {

        return this.day;

    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getKind() {

        return this.kind;

    }

    public void setDose(int doss) {
        this.dose = doss;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }
}
