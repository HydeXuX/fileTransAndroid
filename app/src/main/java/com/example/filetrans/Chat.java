package com.example.filetrans;

public class Chat {
    private String detail;
    private int isPhone;
    private String userId;
    private String type;
    private String fileName;

    public Chat(){

    }
    public Chat(String detail, int isPhone, String type, String userId, String fileName){
        this.detail = detail;
        this.isPhone = isPhone;
        this.type = type;
        this.userId = userId;
        this.fileName = fileName;
    }
    public String getUserId() { return userId; }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDetail() { return detail; }
    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getIsPhone(){
        return isPhone;
    }
    public void setIsPhone(int isPhone){
        this.isPhone = isPhone;
    }

    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }

    public String getFileName(){
        return fileName;
    }
    public void setFileName(String fileName){
        this.fileName = fileName;
    }

}
