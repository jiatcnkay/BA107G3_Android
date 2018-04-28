package idv.wei.ba107g3.gift_receive;

import java.io.Serializable;
import java.sql.Timestamp;

public class GiftReceiveVO implements Serializable{
    String gift_no;
    String giftr_no;// 收贈禮編號
    String mem_no_self;// 會員編號(贈禮人)
    String mem_no_other;// 會員編號(收禮人)
    String giftod_no;// 訂單明細編號
    Integer giftr_amount;// 收贈禮數量
    Timestamp giftr_time;// 收贈禮時間
    String giftr_is_found;// 收贈禮是否成立
    String giftr_is_open;// 收贈禮是否公開
    String giftr_notice;// 收贈禮通知
    String giftr_message;// 收贈禮留言

    @Override
    // 要比對欲加入商品與購物車內商品的gift_no是否相同，true則值相同
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof GiftReceiveVO)) {
            return false;
        }
        return (this.getGift_no().equals(((GiftReceiveVO)obj).getGift_no()) && this.getMem_no_other().equals(((GiftReceiveVO)obj).getMem_no_other()));
    }

    public String getGift_no() {
        return gift_no;
    }

    public void setGift_no(String gift_no) {
        this.gift_no = gift_no;
    }

    public GiftReceiveVO() {
        super();
    }

    public String getGiftr_no() {
        return giftr_no;
    }

    public void setGiftr_no(String giftr_no) {
        this.giftr_no = giftr_no;
    }

    public String getMem_no_self() {
        return mem_no_self;
    }

    public void setMem_no_self(String mem_no_self) {
        this.mem_no_self = mem_no_self;
    }

    public String getMem_no_other() {
        return mem_no_other;
    }

    public void setMem_no_other(String mem_no_other) {
        this.mem_no_other = mem_no_other;
    }

    public String getGiftod_no() {
        return giftod_no;
    }

    public void setGiftod_no(String giftod_no) {
        this.giftod_no = giftod_no;
    }

    public Integer getGiftr_amount() {
        return giftr_amount;
    }

    public void setGiftr_amount(Integer giftr_amount) {
        this.giftr_amount = giftr_amount;
    }

    public Timestamp getGiftr_time() {
        return giftr_time;
    }

    public void setGiftr_time(Timestamp giftr_time) {
        this.giftr_time = giftr_time;
    }

    public String getGiftr_is_found() {
        return giftr_is_found;
    }

    public void setGiftr_is_found(String giftr_is_found) {
        this.giftr_is_found = giftr_is_found;
    }

    public String getGiftr_is_open() {
        return giftr_is_open;
    }

    public void setGiftr_is_open(String giftr_is_open) {
        this.giftr_is_open = giftr_is_open;
    }

    public String getGiftr_notice() {
        return giftr_notice;
    }

    public void setGiftr_notice(String giftr_notice) {
        this.giftr_notice = giftr_notice;
    }

    public String getGiftr_message() {
        return giftr_message;
    }

    public void setGiftr_message(String giftr_message) {
        this.giftr_message = giftr_message;
    }
}

