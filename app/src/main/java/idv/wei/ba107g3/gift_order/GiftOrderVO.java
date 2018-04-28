package idv.wei.ba107g3.gift_order;

import java.io.Serializable;
import java.sql.Timestamp;


public class GiftOrderVO implements Serializable {
    String gifto_no;//訂單編號
    String mem_no;//會員編號
    String coup_no;//折價券編號
    Timestamp gifto_time;//訂單時間
    String gifto_remark;//訂單備註

    public GiftOrderVO() {
        super();
    }

    public String getGifto_no() {
        return gifto_no;
    }

    public void setGifto_no(String gifto_no) {
        this.gifto_no = gifto_no;
    }

    public String getMem_no() {
        return mem_no;
    }

    public void setMem_no(String mem_no) {
        this.mem_no = mem_no;
    }

    public String getCoup_no() {
        return coup_no;
    }

    public void setCoup_no(String coup_no) {
        this.coup_no = coup_no;
    }

    public Timestamp getGifto_time() {
        return gifto_time;
    }

    public void setGifto_time(Timestamp gifto_time) {
        this.gifto_time = gifto_time;
    }

    public String getGifto_remark() {
        return gifto_remark;
    }

    public void setGifto_remark(String gifto_remark) {
        this.gifto_remark = gifto_remark;
    }

}


