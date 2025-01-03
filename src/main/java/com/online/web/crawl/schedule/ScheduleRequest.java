package com.online.web.crawl.schedule;

import java.time.LocalTime;

public class ScheduleRequest {

    private String scheduleTarget;
    private LocalTime scheduleTime;

    private String convertToCron(LocalTime time) {
        int second = time.getSecond();
        int minute = time.getMinute();
        int hour = time.getHour();
        return String.format("%d %d %d * * *", second, minute, hour);
    }

    public String getScheduleTarget() {
        return this.scheduleTarget;
    }
    public String getCron() {
        return convertToCron(this.scheduleTime);
    }
}
