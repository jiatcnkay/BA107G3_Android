package idv.wei.ba107g3.gift_label;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GiftLabelVO implements Serializable {
    private String giftl_no;
    private String giftl_name;

    public GiftLabelVO() {
        super();
    }

    public String getGiftl_no() {
        return giftl_no;
    }

    public void setGiftl_no(String giftl_no) {
        this.giftl_no = giftl_no;
    }

    public String getGiftl_name() {
        return giftl_name;
    }

    public void setGiftl_name(String giftl_name) {
        this.giftl_name = giftl_name;
    }
}

