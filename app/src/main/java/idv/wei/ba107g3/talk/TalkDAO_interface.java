package idv.wei.ba107g3.talk;

import idv.wei.ba107g3.friends.FriendsListVO;

public interface TalkDAO_interface {

    String findTalkByFriends(String self_no,String fri_no);
}
