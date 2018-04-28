package idv.wei.ba107g3.giftbox;

import java.util.List;

public interface GiftboxDAO_interface {

    List<GiftboxVO> getByMemGift(String mem_no);
}
