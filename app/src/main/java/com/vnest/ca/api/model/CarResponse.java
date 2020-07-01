package com.vnest.ca.api.model;

public class CarResponse {
    //    {
//        "code":"1",
//            "description":"OK",
//            "notchangesessionid":false,
//            "updateversion":{
//        "update":0,
//                "forced":0,
//                "description":"",
//                "url":""
//    }
//    }
    private int code;
    private String description;
    private String versionCode;
    private UpdateVersion updateversion;
    private boolean notchangesessionid;

    public UpdateVersion getVersion() {
        return updateversion;
    }

    public void setVersion(UpdateVersion version) {
        this.updateversion = version;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public static class UpdateVersion {
        private int update;
        private int forced;

        public int getUpdate() {
            return update;
        }

        public void setUpdate(int update) {
            this.update = update;
        }

        public int getForced() {
            return forced;
        }

        public void setForced(int forced) {
            this.forced = forced;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        private String description;
        private String url;
    }
}
