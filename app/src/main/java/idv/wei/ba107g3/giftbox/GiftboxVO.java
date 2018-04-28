package idv.wei.ba107g3.giftbox;

import java.io.Serializable;

import idv.wei.ba107g3.gift.GiftVO;

public class GiftboxVO implements Serializable {
    private String mem_no_self;
    private String gift_no;
    private Integer giftr_amount;

    public GiftboxVO() {
        super();
    }

    @Override
    // 要比對欲加入商品與購物車內商品的gift_no是否相同，true則值相同
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof GiftboxVO)) {
            return false;
        }
        return this.getGift_no().equals(((GiftboxVO)obj).getGift_no());
    }


    public String getMem_no_self() {
        return mem_no_self;
    }


    public void setMem_no_self(String mem_no_self) {
        this.mem_no_self = mem_no_self;
    }


    public String getGift_no() {
        return gift_no;
    }


    public void setGift_no(String gift_no) {
        this.gift_no = gift_no;
    }


    public Integer getGiftr_amount() {
        return giftr_amount;
    }


    public void setGiftr_amount(Integer giftr_amount) {
        this.giftr_amount = giftr_amount;
    }
}

