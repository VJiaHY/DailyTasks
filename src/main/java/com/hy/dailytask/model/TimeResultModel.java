package com.hy.dailytask.model;

import com.hy.dailytask.enums.ProgressStatusEnum;

public class TimeResultModel {

    //完成状态
    private ProgressStatusEnum progressStatusEnum;

    //相距时间
    private String localTimeStr;


    public ProgressStatusEnum getProgressStatusEnum() {
        return progressStatusEnum;
    }

    public void setProgressStatusEnum(ProgressStatusEnum progressStatusEnum) {
        this.progressStatusEnum = progressStatusEnum;
    }

    public String getLocalTimeStr() {
        return localTimeStr;
    }

    public void setLocalTimeStr(String localTimeStr) {
        this.localTimeStr = localTimeStr;
    }
}
