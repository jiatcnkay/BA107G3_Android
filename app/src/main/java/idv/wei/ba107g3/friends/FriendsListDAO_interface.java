package idv.wei.ba107g3.friends;

import java.util.List;

import idv.wei.ba107g3.member.MemberVO;

public interface FriendsListDAO_interface {
    public void insert(String mem_no_self,String mem_no_other);
    public void update(FriendsListVO frilistVO);
    public void delete(String mem_no_self,String mem_no_other);
    public FriendsListVO findByPrimaryKey(String mem_no_self,String mem_no_other);
    public List<FriendsListVO> getAll();
    public List<MemberVO> getMemberFriends(String mem_no);
    public Boolean havewait(String mem_no_self,String mem_no_other);
    public void add(String mem_no_self,String mem_no_other);

}
