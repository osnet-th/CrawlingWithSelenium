package com.online.web.crawl.schedule;


import com.online.web.crawl.crawling.service.CrawlingService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Configuration
public class DynamicScheduleConfig {

    private static Map<String, ThreadPoolTaskScheduler> schedulerMap = Collections.synchronizedMap(new HashMap<>());

    private ScheduledFuture<?> scheduledFuture;



    // 주기적인 작업을 시작하는 메서드
    public void startDynamicScheduledTask(String scheduleTarget, LocalTime scheduleTime) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        scheduler.schedule(getRunnable(), getTrigger(convertToCron(scheduleTime)));
        schedulerMap.put(scheduleTarget, scheduler);
    }


    private static String convertToCron(LocalTime time) {
        int second = time.getSecond();
        int minute = time.getMinute();
        int hour = time.getHour();
        return String.format("%d %d %d * * *", second, minute, hour);
    }

    private Runnable getRunnable(CrawlingService crawlingService) {
        return () -> {
            crawlingService.
        };
    }

    public final Trigger getTrigger(String cron) {
        // cronSetting
        return new CronTrigger(cron);
    }

    // 주기 변경 메서드 (예: 5초마다 실행으로 변경)
    public void changeSchedule() {
        ThreadPoolTaskScheduler scheduler = schedulerMap.get("");
        scheduler.shutdown();
        scheduler.initialize();
        scheduler.schedule(getRunnable(), getTrigger(""));
        schedulerMap.put("", scheduler);
    }

    // 작업을 중지하는 메서드
    public void stopTask(String item) {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }
}
