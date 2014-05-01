package com.prouty.leagueusa.sdsolschedule;

public class PhotoItem {
    private String mPhotoName;
    private String mPhotoId;
    private String mUserName;
    private String mUserId;
    private String mUrl;
    
    public String getPhotoName() {
        return mPhotoName;
    }
    public void setPhotoName(String photoName) {
        mPhotoName = photoName;
    }
    
    public String getPhotoId() {
        return mPhotoId;
    }
    public void setPhotoId(String PhotoId) {
        mPhotoId = PhotoId;
    }
    
    public String getUserId() {
        return mUserId;
    }
    public void setUserId(String userId) {
        mUserId = userId;
    }
    
    public String getUserName() {
        return mUserName;
    }
    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getUrl() {
        return mUrl;
    }
    public void setUrl(String url) {
        mUrl = url;
    }

}
