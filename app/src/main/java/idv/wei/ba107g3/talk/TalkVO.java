package idv.wei.ba107g3.talk;

import java.sql.Timestamp;

public class TalkVO implements java.io.Serializable{
    private String talk_no;
    private String mem_no_send;
    private String mem_no_get;
    private Timestamp talk_time;
    private String talk_cnt;

    public TalkVO(){
    }

    public String getTalk_no(){
        return talk_no;
    }

    public void setTalk_no(String talk_no){
        this.talk_no = talk_no;
    }


    public String getMem_no_send() {
        return mem_no_send;
    }

    public void setMem_no_send(String mem_no_send) {
        this.mem_no_send = mem_no_send;
    }

    public String getMem_no_get() {
        return mem_no_get;
    }

    public void setMem_no_get(String mem_no_get) {
        this.mem_no_get = mem_no_get;
    }

    public Timestamp getTalk_time() {
        return talk_time;
    }

    public void setTalk_time(Timestamp talk_time) {
        this.talk_time = talk_time;
    }

    public String getTalk_cnt() {
        return talk_cnt;
    }

    public void setTalk_cnt(String talk_cnt) {
        this.talk_cnt = talk_cnt;
    }
}

