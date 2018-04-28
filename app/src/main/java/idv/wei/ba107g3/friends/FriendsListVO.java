package idv.wei.ba107g3.friends;

import java.sql.Date;

public class FriendsListVO {
    private String mem_no_self;
    private String mem_no_other;
    private String frilist_modify;
    private Date frilist_time;
    private String frilist_notice;

    public FriendsListVO(){

    }

    public String getMem_no_self() {
        return mem_no_self;
    }
    public void setMem_no_self(String mem_no_self) {
        this.mem_no_self = mem_no_self;
    }
    public String getMem_no_other() {
        return mem_no_other;
    }
    public void setMem_no_other(String mem_no_other) {
        this.mem_no_other = mem_no_other;
    }
    public String getFrilist_modify() {
        return frilist_modify;
    }
    public void setFrilist_modify(String frilist_modify) {
        this.frilist_modify = frilist_modify;
    }
    public Date getFrilist_time() {
        return frilist_time;
    }
    public void setFrilist_time(Date frilist_time) {
        this.frilist_time = frilist_time;
    }
    public String getFrilist_notice() {
        return frilist_notice;
    }
    public void setFrilist_notice(String frilist_notice) {
        this.frilist_notice = frilist_notice;
    }

}

