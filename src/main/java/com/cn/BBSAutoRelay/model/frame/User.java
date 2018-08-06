package com.cn.BBSAutoRelay.model.frame;

import java.util.Date;
import javax.persistence.*;

@Table(name = "tb_user")
public class User {
    @Id
    private Long uid;

    @Column(name = "e_mail")
    private String eMail;

    /**
     * 昵称：唯一
     */
    private String nickname;

    private String password;

    /**
     * 0:女，1:男，2：保密
     */
    private String sex;

    private Date birthday;

    private String address;

    private String phone;

    @Column(name = "e_code")
    private String eCode;

    /**
     * 0:未激活，1，正常，2，禁用
     */
    private String status;

    @Column(name = "create_time")
    private Date createTime;

    /**
     * @return uid
     */
    public Long getUid() {
        return uid;
    }

    /**
     * @param uid
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * @return e_mail
     */
    public String geteMail() {
        return eMail;
    }

    /**
     * @param eMail
     */
    public void seteMail(String eMail) {
        this.eMail = eMail == null ? null : eMail.trim();
    }

    /**
     * 获取昵称：唯一
     *
     * @return nickname - 昵称：唯一
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 设置昵称：唯一
     *
     * @param nickname 昵称：唯一
     */
    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    /**
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    /**
     * 获取0:女，1:男，2：保密
     *
     * @return sex - 0:女，1:男，2：保密
     */
    public String getSex() {
        return sex;
    }

    /**
     * 设置0:女，1:男，2：保密
     *
     * @param sex 0:女，1:男，2：保密
     */
    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    /**
     * @return birthday
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * @param birthday
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address
     */
    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    /**
     * @return phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone
     */
    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    /**
     * @return e_code
     */
    public String geteCode() {
        return eCode;
    }

    /**
     * @param eCode
     */
    public void seteCode(String eCode) {
        this.eCode = eCode == null ? null : eCode.trim();
    }

    /**
     * 获取0:未激活，1，正常，2，禁用
     *
     * @return status - 0:未激活，1，正常，2，禁用
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置0:未激活，1，正常，2，禁用
     *
     * @param status 0:未激活，1，正常，2，禁用
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}