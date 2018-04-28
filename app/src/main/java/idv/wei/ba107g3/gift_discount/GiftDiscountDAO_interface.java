package idv.wei.ba107g3.gift_discount;

import java.util.List;

import idv.wei.ba107g3.gift.GiftVO;

public interface GiftDiscountDAO_interface {
    public void insert(GiftDiscountVO giftDiscountVO);
    public void update(GiftDiscountVO giftDiscountVO);
    public void delete(String giftd_no);
    public GiftDiscountVO getByPrimaryKey(String giftd_no);
    public List<GiftDiscountVO> getAll();
    public List<GiftVO> getGiftD();

}
