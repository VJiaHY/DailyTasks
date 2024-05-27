package com.hy.dailytask.entry;

import com.hy.dailytask.enums.ProgressStatusEnum;
import com.hy.dailytask.model.TimeResultModel;

import java.time.Duration;
import java.time.LocalTime;

public class Task {
    private Long id;
    private  String content;
    private  LocalTime startTime;
    private  LocalTime endTime;
    private boolean isCompleted;
    private final String TimePeriod;

    private boolean notified;


    public Task( Long id,String content, LocalTime startTime, LocalTime endTime, String timePeriod) {
        this.id = id;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
        TimePeriod = timePeriod;
        this.isCompleted = false;
    }


    public void setContent(String content) {
        this.content = content;
    }


    public boolean getNotified() {
        return notified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNotify(boolean notified) {
        this.notified = notified;
    }

    public String getContent() {
        return content;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }


    public TimeResultModel getRemainingTime() {
        return getTimeResult();
    }

    public String getTimePeriod() {
        return TimePeriod;
    }

    public String getStatus() {
        return getTimeResult().getProgressStatusEnum().getValue();
    }

    private TimeResultModel getTimeResult(){
        TimeResultModel timeResultModel = new TimeResultModel();
        //已完成
        if (isCompleted) {
            timeResultModel.setProgressStatusEnum(ProgressStatusEnum.FINISH);
            timeResultModel.setLocalTimeStr("00:00:00");
        }else {
            LocalTime now = LocalTime.now();
            //当前时间小于开始时间 未开始
            if (now.isBefore(startTime)){
                timeResultModel.setProgressStatusEnum(ProgressStatusEnum.NOTSTART);
                Duration duration = Duration.between(now, startTime);
                timeResultModel.setLocalTimeStr(formatDuration(duration));

            }else if (now.isAfter(endTime)) {
                //当前时间超过结束时间 已超时
                timeResultModel.setProgressStatusEnum(ProgressStatusEnum.TIMEOUT);
                Duration duration = Duration.between(endTime, now);
                timeResultModel.setLocalTimeStr(formatDuration(duration));
            } else {
                timeResultModel.setProgressStatusEnum(ProgressStatusEnum.START);
                Duration duration = Duration.between(now, endTime);
                timeResultModel.setLocalTimeStr(formatDuration(duration));
                //进行中
            }
        }
        return timeResultModel;
    }

    private static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        duration = duration.minusHours(hours);
        long minutes = duration.toMinutes();
        duration = duration.minusMinutes(minutes);
        long seconds = duration.getSeconds();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


}