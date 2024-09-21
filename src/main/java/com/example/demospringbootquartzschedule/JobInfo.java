package com.example.demospringbootquartzschedule;

import java.util.Date;
import java.util.List;
import lombok.Data;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;

@Data
public class JobInfo {
    private String jobName;
    private String groupName;
    private List<TriggerInfo> triggerInfoList;
    private JobDataMap jobDataMap;

    public JobInfo(JobDetail jobDetail, List<TriggerInfo> triggerInfoList) {
        this.jobName = jobDetail.getKey().getName();
        this.groupName = jobDetail.getKey().getGroup();
        this.triggerInfoList = triggerInfoList;
        this.jobDataMap = jobDetail.getJobDataMap();
    }

    // Getters và Setters

    @Data
    public static class TriggerInfo {
        private String triggerName;
        private String triggerGroup;
        private Date startTime;
        private Date nextFireTime;
        private TriggerState triggerState;

        public TriggerInfo(Trigger trigger, TriggerState triggerState) {
            this.triggerName = trigger.getKey().getName();
            this.triggerGroup = trigger.getKey().getGroup();
            this.startTime = trigger.getStartTime();
            this.nextFireTime = trigger.getNextFireTime();
            this.triggerState = triggerState;
        }

        // Getters và Setters
    }
}

