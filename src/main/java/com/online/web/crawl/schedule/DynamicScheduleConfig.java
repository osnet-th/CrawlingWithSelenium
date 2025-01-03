package com.online.web.crawl.schedule;


import com.online.web.crawl.crawling.service.CrawlingService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
public class DynamicScheduleConfig {

    private static Map<String, ThreadPoolTaskScheduler> schedulerMap = Collections.synchronizedMap(new HashMap<>());

    private ScheduledFuture<?> scheduledFuture;

    // 주기적인 작업을 시작하는 메서드
    public final void startDynamicScheduledTask(ScheduleRequest scheduleRequest, ScheduleService scheduleService) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        scheduler.schedule(scheduleService.scheduleTask(), getTrigger(scheduleRequest.getCron()));
        schedulerMap.put(scheduleRequest.getScheduleTarget(), scheduler);
    }

    private Trigger getTrigger(String cron) {
        // cronSetting
        return new CronTrigger(cron);
    }

    // 주기 변경 메서드 (예: 5초마다 실행으로 변경)
    public final void changeSchedule(ScheduleRequest scheduleRequest, ScheduleService scheduleService) {
        ThreadPoolTaskScheduler scheduler = schedulerMap.get(scheduleRequest.getScheduleTarget());
        scheduler.shutdown();
        scheduler.initialize();
        scheduler.schedule(scheduleService.scheduleTask(), getTrigger(scheduleRequest.getCron()));
    }

    // 작업을 중지하는 메서드
    public final void stopTask(ScheduleRequest scheduleRequest) {
        ThreadPoolTaskScheduler scheduler = schedulerMap.get(scheduleRequest.getCron());
        scheduler.shutdown();
    }
}
