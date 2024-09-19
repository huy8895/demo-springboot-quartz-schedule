package com.example.demospringbootquartzschedule;

import java.util.Date;
import java.util.List;
import lombok.Data;
import org.quartz.Trigger;

@Data
public class JobInfo {
    private String jobName;
    private String groupName;
    private List<TriggerInfo> triggers;

    public JobInfo(String jobName, String groupName, List<TriggerInfo> triggers) {
        this.jobName = jobName;
        this.groupName = groupName;
        this.triggers = triggers;
    }

    // Getters và Setters

    @Data
    public static class TriggerInfo {
        private String triggerName;
        private String groupName;
        private Date startTime;
        private Date nextFireTime;
        private Trigger.TriggerState triggerState;

        public TriggerInfo(String triggerName, String groupName, Date startTime, Date nextFireTime, Trigger.TriggerState triggerState) {
            this.triggerName = triggerName;
            this.groupName = groupName;
            this.startTime = startTime;
            this.nextFireTime = nextFireTime;
            this.triggerState = triggerState;
        }

        // Getters và Setters
    }
}

