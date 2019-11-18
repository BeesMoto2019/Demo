package com.example.bikeservice;

public class User {

    private int date;
    private String status;
    public String name;
    public String email;
    public String phone;
    public String landmark;
    public String address;
    public String state;
    public String password;
    public String city;
    public String garage;
    public String reqtime;
    public String apptime;
    public int id;
    public Double latitute;
    public Double longitute;
    public String pincode;
    public Boolean selected=false;
    public String service;
    public double rate;
    public String contactno;
    public String reqtype;
    public String reqdate;
    public String bikemodels;


    public User(String city,Double latitute,Double longitute) {
        this.city=city;
        this.latitute = latitute;
        this.longitute=longitute;
    }

    public User(String name,String email,String phone,String landmark,String city){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.landmark = landmark;
        this.city = city;
    }

    public User(int id,String email,String service,String name,String status){
        this.id = id;
        this.email = email;
        this.service = service;
        this.name = name;
        this.status = status;
    }

    public User(String name,String email,String phone,String landmark,String city,String pincode){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.landmark = landmark;
        this.city = city;
        this.pincode = pincode;
    }

    public User(int Date,String email,String phone,String landmark,String city,String pincode){
        this.date = Date;
        this.email = email;
        this.phone = phone;
        this.landmark = landmark;
        this.city = city;
        this.pincode = pincode;
    }


    public User(String name,String email,String phone,String landmark,String city,String pincode,Boolean selected){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.landmark = landmark;
        this.city = city;
        this.pincode = pincode;
        this.selected=selected;
    }
    public User(String name,String email,String phone,String landmark,String city,String pincode,double rate){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.landmark = landmark;
        this.city = city;
        this.pincode = pincode;
        this.rate=rate;
    }
    public User(String name,String email,String phone,String landmark,String city,String pincode,String service){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.landmark = landmark;
        this.city = city;
        this.pincode = pincode;
        this.service=service;
    }

    public User(int id,String name,String email,String phone,String landmark,String city,String pincode){
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.landmark = landmark;
        this.city = city;
        this.pincode = pincode;
    }

    public User(int id,String name,String garage,String email,String phone,String city,String landmark,String pincode){
        this.id = id;
        this.name = name;
        this.garage= garage;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.landmark = landmark;
        this.pincode = pincode;
    }

    public User(String name,String email,String phone,String landmark,String city,String garage,String pincode,String service){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.landmark = landmark;
        this.city = city;
        this.garage=garage;
        this.pincode = pincode;
        this.service=service;
    }

    public User(int reqid, String mech_email, String mech_garage, String status) {
        this.id = reqid;
        this.email = mech_email;
        this.garage = mech_garage;
        this.status = status;
    }

    public User(int id,String name,String email,String contact,String address,String state,String city,String landmark,String pincode)
    {
        this.id=id;
        this.name=name;
        this.email=email;
        this.phone=contact;
        this.address=address;
        this.state=state;
        this.city=city;
        this.landmark=landmark;
        this.pincode=pincode;
    }

    public User(int id,int date,String name,String email,String contact,String address,String state,String city,String landmark,String pincode)
    {
        this.id=id;
        this.date=date;
        this.name=name;
        this.email=email;
        this.phone=contact;
        this.address=address;
        this.state=state;
        this.city=city;
        this.landmark=landmark;
        this.pincode=pincode;
    }

    public User(int id,String name,String email,String contact,String address,String state,String city,String landmark,String pincode,String password)
    {
        this.id=id;
        this.name=name;
        this.email=email;
        this.phone=contact;
        this.address=address;
        this.state=state;
        this.city=city;
        this.landmark=landmark;
        this.pincode=pincode;
        this.password=password;
    }

    public User(int id,String name,String email,String contact,String address,String state,String city,String landmark,String pincode,String password,String status,String garage, String service)
    {
        this.id=id;
        this.name=name;
        this.email=email;
        this.phone=contact;
        this.address=address;
        this.state=state;
        this.city=city;
        this.landmark=landmark;
        this.pincode=pincode;
        this.password=password;
        this.status=status;
        this.garage=garage;
        this.service=service;
    }

    public User(int id,String name,String email,String contact,String address,String state,String city,String landmark,String pincode,String password,String status,String garage, String service,String contactno,String reqtype,String reqdate,String bikemodels,String reqtime,String apptime)
    {
        this.id=id;
        this.name=name;
        this.email=email;
        this.phone=contact;
        this.address=address;
        this.state=state;
        this.city=city;
        this.landmark=landmark;
        this.pincode=pincode;
        this.password=password;
        this.status=status;
        this.garage=garage;
        this.service=service;
        this.contactno=contactno;
        this.reqtype=reqtype;
        this.reqdate=reqdate;
        this.bikemodels=bikemodels;
        this.reqtime=reqtime;
        this.apptime=apptime;
    }

    public User(int id,String name,String email,String contact,String address,String state,String city,String landmark,String pincode,String password,String status)
    {
        this.id=id;
        this.name=name;
        this.email=email;
        this.phone=contact;
        this.address=address;
        this.state=state;
        this.city=city;
        this.landmark=landmark;
        this.pincode=pincode;
        this.password=password;
        this.status=status;
    }

    public User(int id,String name,String email,String contact,String address,String state,String city,String landmark,String pincode,String password,String apptime,String status)
    {
        this.id=id;
        this.name=name;
        this.email=email;
        this.phone=contact;
        this.address=address;
        this.state=state;
        this.city=city;
        this.landmark=landmark;
        this.pincode=pincode;
        this.password=password;
        this.apptime=apptime;
        this.status=status;
    }

    public User(String name,String contact,String city,String status)
    {
        this.name=name;
        this.phone=contact;
        this.city=city;
        this.status=status;
    }

    public User(String name) {
        this.name = name;
    }

    public User(String name,String email) {
        this.name = name;
        this.email=email;
    }

    public User(String city,String name,String email) {
        this.city=city;
        this.name = name;
        this.email=email;
    }

    public User(String city,String email,Double latitute,Double longitute) {
        this.city=city;
        this.email=email;
        this.latitute = latitute;
        this.longitute=longitute;
    }

    public User(int id,String name,String city,String email,String contact,Double latitute,Double longitute,String garage) {
        this.id=id;
        this.name=name;
        this.city=city;
        this.email=email;
        this.phone=contact;
        this.latitute = latitute;
        this.longitute=longitute;
        this.garage=garage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPassword(String password) { this.password = password; }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public void setBikemodels(String bikemodels) { this.bikemodels = bikemodels; }

    public void setContactno(String contactno) { this.contactno = contactno; }

    public void setReqtype(String reqtype) { this.reqtype = reqtype; }

    public void setReqdate(String reqdate) { this.reqdate = reqdate; }

    public void setGarage(String garage) {
        this.garage = garage;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setRate(double rate) { this.rate = rate; }

    public int getDate() {
        return date;
    }

    public double getRate() { return rate; }

    public String getStatus() {
        return status;
    }

    public String getApptime() { return apptime; }

    public String getReqtime() { return reqtime; }

    public void setApptime(String apptime) { this.apptime = apptime; }

    public void setReqtime(String reqtime) { this.reqtime = reqtime; }

    public void setLatitute(Double latitute) { this.latitute = latitute; }

    public void setLongitute(Double longitute) { this.longitute = longitute; }

    public int getId() {
        return id;
    }

    public String getBikemodels() { return bikemodels; }

    public String getContactno() { return contactno; }

    public String getReqdate() { return reqdate; }

    public String getReqtype() { return reqtype; }

    public String getPassword() { return password; }

    public String getAddress() {
        return address;
    }

    public String getState() {
        return state;
    }

    public String getName() { return name; }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Double getLatitute() { return latitute; }

    public Double getLongitute() { return longitute; }

    public String getLandmark() {
        return landmark;
    }

    public String getCity() {
        return city;
    }

    public String getPincode() {
        return pincode;
    }

    public String getGarage() {
        return garage;
    }

    public Boolean getSelected() { return selected; }

    public void setSelected(Boolean selected) { this.selected = selected; }

    public boolean isSelected() {
        return selected;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}