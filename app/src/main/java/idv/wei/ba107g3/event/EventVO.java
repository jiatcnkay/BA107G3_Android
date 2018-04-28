package idv.wei.ba107g3.event;

import java.sql.Timestamp;

public class EventVO implements java.io.Serializable{
    private String eve_no;
    private String evec_no;
    private String eve_name;
    private Timestamp eve_start;
    private Timestamp eve_end;
    private Timestamp eve_time;
    private String eve_cnt;
    private byte[] eve_pic;
    private Integer eve_quota;
    private String eve_site;
    private Integer eve_regfee;
    private String eve_sts;


    public String getEve_sts() {
        return eve_sts;
    }
    public void setEve_sts(String eve_sts) {
        this.eve_sts = eve_sts;
    }
    public String getEve_no() {
        return eve_no;
    }
    public void setEve_no(String eve_no) {
        this.eve_no = eve_no;
    }
    public String getEvec_no() {
        return evec_no;
    }
    public void setEvec_no(String evec_no) {
        this.evec_no = evec_no;
    }
    public String getEve_name() {
        return eve_name;
    }
    public void setEve_name(String eve_name) {
        this.eve_name = eve_name;
    }
    public Timestamp getEve_start() {
        return eve_start;
    }
    public void setEve_start(Timestamp eve_start) {
        this.eve_start = eve_start;
    }
    public Timestamp getEve_end() {
        return eve_end;
    }
    public void setEve_end(Timestamp eve_end) {
        this.eve_end = eve_end;
    }
    public Timestamp getEve_time() {
        return eve_time;
    }
    public void setEve_time(Timestamp eve_time) {
        this.eve_time = eve_time;
    }
    public String getEve_cnt() {
        return eve_cnt;
    }
    public void setEve_cnt(String eve_cnt) {
        this.eve_cnt = eve_cnt;
    }
    public byte[] getEve_pic() {
        return eve_pic;
    }
    public void setEve_pic(byte[] eve_pic) {
        this.eve_pic = eve_pic;
    }
    public Integer getEve_quota() {
        return eve_quota;
    }
    public void setEve_quota(Integer eve_quota) {
        this.eve_quota = eve_quota;
    }
    public String getEve_site() {
        return eve_site;
    }
    public void setEve_site(String eve_site) {
        this.eve_site = eve_site;
    }
    public Integer getEve_regfee() {
        return eve_regfee;
    }
    public void setEve_regfee(Integer eve_regfee) {
        this.eve_regfee = eve_regfee;
    }
}

