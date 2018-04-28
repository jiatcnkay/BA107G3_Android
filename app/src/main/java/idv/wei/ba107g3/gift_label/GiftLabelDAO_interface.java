package idv.wei.ba107g3.gift_label;

import java.util.List;

public interface GiftLabelDAO_interface {
    public void insert(GiftLabelVO giftLabelVO);
    public void update(GiftLabelVO giftLabelVO);
    public void delete(String giftl_no);
    public GiftLabelVO getByPrimaryKey(String giftl_no);
    public GiftLabelVO getByLabelName(String giftl_name);
    public List<GiftLabelVO> getAll();
}
