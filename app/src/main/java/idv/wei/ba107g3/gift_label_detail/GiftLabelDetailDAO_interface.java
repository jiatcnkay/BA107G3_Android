package idv.wei.ba107g3.gift_label_detail;

import java.sql.Connection;
import java.util.List;

public interface GiftLabelDetailDAO_interface {
    public void insert(GiftLabelDetailVO giftLabelDetailVO, Connection con);
    public void deleteOne(GiftLabelDetailVO giftLabelDetailVO, Connection con);
    public void deleteByGiftNo(String gift_no, Connection con);
    public List<GiftLabelDetailVO> getByGiftNo(String gift_no);
    public List<GiftLabelDetailVO> getByGiftLabelNo(String giftl_no);
}
