package idv.wei.ba107g3.gift;

import java.sql.Connection;
import java.util.List;

import idv.wei.ba107g3.gift_label_detail.GiftLabelDetailVO;

public interface GiftDAO_interface {
    public void insert(GiftVO giftVO, List<GiftLabelDetailVO> giftLabelDetailList);
    public void update(GiftVO giftVO, List<GiftLabelDetailVO> giftLabelDetailList);
    public void updateTrackQty(String gift_no, Integer gift_track_qty, Connection con);
    public void updateBuyQty(GiftVO giftVO, Integer gift_buy_qty, Connection con);
    public void delete(String gift_no);
    public GiftVO getByPrimaryKey(String gift_no);
    public List getAll();
    public byte[] getPic(String gift_no);
    public List<GiftVO> getByKeyWord(String keyword);

}
