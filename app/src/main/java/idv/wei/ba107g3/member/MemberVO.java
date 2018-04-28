package idv.wei.ba107g3.member;

import java.io.Serializable;
import java.sql.Date;

import idv.wei.ba107g3.gift.GiftVO;

public class MemberVO implements Serializable {
    private String mem_no;
    private String mem_account;
    private String mem_password;
    private Date mem_join_time;
    private String mem_name;
    private String mem_gender;
    private Date mem_birthday;
    private String mem_county;
    private Integer mem_deposit;
    private String mem_contact;
    private String mem_emotion;
    private Integer mem_bonus;
    private String mem_bloodtype;
    private Integer mem_height;
    private Integer mem_weight;
    private String mem_interest;
    private String mem_intro;
    private String mem_online;
    private Double mem_longitude;
    private Double mem_latitude;
    private String mem_phone;
    private String mem_mail;
    private byte[] mem_photo;
    private String mem_prohibit;
    private String mem_set_notify;
    private Date mem_time_notify;
    private String mem_age;
    private Integer mem_receive_gift;

    public MemberVO(){
    }

    @Override
    // 要比對欲加入商品與購物車內商品的gift_no是否相同，true則值相同
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof MemberVO)) {
            return false;
        }
        return this.getMem_no().equals(((MemberVO)obj).getMem_no());
    }

    public String getMem_no() {
        return mem_no;
    }

    public void setMem_no(String mem_no) {
        this.mem_no = mem_no;
    }

    public String getMem_account() {
        return mem_account;
    }

    public void setMem_account(String mem_account) {
        this.mem_account = mem_account;
    }

    public String getMem_password() {
        return mem_password;
    }

    public void setMem_password(String mem_password) {
        this.mem_password = mem_password;
    }

    public Date getMem_join_time() {
        return mem_join_time;
    }

    public void setMem_join_time(Date mem_join_time) {
        this.mem_join_time = mem_join_time;
    }

    public String getMem_name() {
        return mem_name;
    }

    public void setMem_name(String mem_name) {
        this.mem_name = mem_name;
    }

    public String getMem_gender() {
        return mem_gender;
    }

    public void setMem_gender(String mem_gender) {
        this.mem_gender = mem_gender;
    }

    public Date getMem_birthday() {
        return mem_birthday;
    }

    public void setMem_birthday(Date mem_birthday) {
        this.mem_birthday = mem_birthday;
    }

    public String getMem_county() {
        return mem_county;
    }

    public void setMem_county(String mem_county) {
        this.mem_county = mem_county;
    }

    public Integer getMem_deposit() {
        return mem_deposit;
    }

    public void setMem_deposit(Integer mem_deposit) {
        this.mem_deposit = mem_deposit;
    }

    public String getMem_contact() {
        return mem_contact;
    }

    public void setMem_contact(String mem_contact) {
        this.mem_contact = mem_contact;
    }

    public String getMem_emotion() {
        return mem_emotion;
    }

    public void setMem_emotion(String mem_emotion) {
        this.mem_emotion = mem_emotion;
    }

    public Integer getMem_bonus() {
        return mem_bonus;
    }

    public void setMem_bonus(Integer mem_bonus) {
        this.mem_bonus = mem_bonus;
    }

    public String getMem_bloodtype() {
        return mem_bloodtype;
    }

    public void setMem_bloodtype(String mem_bloodtype) {
        this.mem_bloodtype = mem_bloodtype;
    }

    public Integer getMem_height() {
        return mem_height;
    }

    public void setMem_height(Integer mem_height) {
        this.mem_height = mem_height;
    }

    public Integer getMem_weight() {
        return mem_weight;
    }

    public void setMem_weight(Integer mem_weight) {
        this.mem_weight = mem_weight;
    }

    public String getMem_interest() {
        return mem_interest;
    }

    public void setMem_interest(String mem_interest) {
        this.mem_interest = mem_interest;
    }

    public String getMem_intro() {
        return mem_intro;
    }

    public void setMem_intro(String mem_intro) {
        this.mem_intro = mem_intro;
    }

    public String getMem_online() {
        return mem_online;
    }

    public void setMem_online(String mem_online) {
        this.mem_online = mem_online;
    }

    public Double getMem_longitude() {
        return mem_longitude;
    }

    public void setMem_longitude(Double mem_longitude) {
        this.mem_longitude = mem_longitude;
    }

    public Double getMem_latitude() {
        return mem_latitude;
    }

    public void setMem_latitude(Double mem_latitude) {
        this.mem_latitude = mem_latitude;
    }

    public String getMem_phone() {
        return mem_phone;
    }

    public void setMem_phone(String mem_phone) {
        this.mem_phone = mem_phone;
    }

    public String getMem_mail() {
        return mem_mail;
    }

    public void setMem_mail(String mem_mail) {
        this.mem_mail = mem_mail;
    }

    public byte[] getMem_photo() {
        return mem_photo;
    }

    public void setMem_photo(byte[] mem_photo) {
        this.mem_photo = mem_photo;
    }

    public String getMem_prohibit() {
        return mem_prohibit;
    }

    public void setMem_prohibit(String mem_prohibit) {
        this.mem_prohibit = mem_prohibit;
    }

    public String getMem_set_notify() {
        return mem_set_notify;
    }

    public void setMem_set_notify(String mem_set_notify) {
        this.mem_set_notify = mem_set_notify;
    }

    public Date getMem_time_notify() {
        return mem_time_notify;
    }

    public void setMem_time_notify(Date mem_time_notify) {
        this.mem_time_notify = mem_time_notify;
    }

    public String getMem_age() {
        return mem_age;
    }

    public void setMem_age(String mem_age) {
        this.mem_age = mem_age;
    }

    public Integer getMem_receive_gift() {
        return mem_receive_gift;
    }

    public void setMem_receive_gift(Integer mem_receive_gift) {
        this.mem_receive_gift = mem_receive_gift;
    }
}