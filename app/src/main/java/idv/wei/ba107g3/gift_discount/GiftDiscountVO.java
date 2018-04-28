package idv.wei.ba107g3.gift_discount;

import java.io.Serializable;
import java.sql.Timestamp;

@SuppressWarnings("serial")
public class GiftDiscountVO implements Serializable{
    private String giftd_no;
    private String gift_no;
    private Timestamp giftd_start;
    private Timestamp giftd_end;
    private Double giftd_percent;
    private Integer giftd_amount;

    public GiftDiscountVO(){

    }

    public String getGiftd_no() {
        return giftd_no;
    }

    public void setGiftd_no(String giftd_no) {
        this.giftd_no = giftd_no;
    }

    public String getGift_no() {
        return gift_no;
    }

    public void setGift_no(String gift_no) {
        this.gift_no = gift_no;
    }

    public Timestamp getGiftd_start() {
        return giftd_start;
    }

    public void setGiftd_start(Timestamp giftd_start) {
        this.giftd_start = giftd_start;
    }

    public Timestamp getGiftd_end() {
        return giftd_end;
    }

    public void setGiftd_end(Timestamp giftd_end) {
        this.giftd_end = giftd_end;
    }

    public Double getGiftd_percent() {
        return giftd_percent;
    }

    public void setGiftd_percent(Double giftd_percent) {
        this.giftd_percent = giftd_percent;
    }

    public Integer getGiftd_amount() {
        return giftd_amount;
    }

    public void setGiftd_amount(Integer giftd_amount) {
        this.giftd_amount = giftd_amount;
    }
}
