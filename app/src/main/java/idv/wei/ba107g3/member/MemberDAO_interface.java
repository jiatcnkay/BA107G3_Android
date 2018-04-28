package idv.wei.ba107g3.member;

import java.util.List;

public interface MemberDAO_interface {
    Boolean isMember(String account,String password);
    void memberUpdate(MemberVO member);
    MemberVO getOneByAccount(String account);
    MemberVO getOneByMemNo(String mem_no);
    List<MemberVO> getLike(String map);
    List<MemberVO> getAll();
    List<MemberVO> getPopular();
}
