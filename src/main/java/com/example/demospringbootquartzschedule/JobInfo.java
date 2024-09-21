package com.example.demospringbootquartzschedule;

import java.util.Date;
import java.util.List;
import lombok.Data;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
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
        private Date endTime;
        private Date nextFireTime;
        private Date previousFireTime;
        private Date finalFireTime;
        private boolean mayFireAgain;
        private TriggerState triggerState;
        private String calendarName;
        private String description;
        private int misfireInstruction;
        private int priority;
        private long repeatInterval;
        private int repeatCount;
        private JobDataMap jobDataMap;

        public TriggerInfo(Trigger trigger, TriggerState triggerState) {
            this.triggerName = trigger.getKey().getName();
            this.triggerGroup = trigger.getKey().getGroup();
            this.startTime = trigger.getStartTime();
            this.endTime = trigger.getEndTime();
            this.nextFireTime = trigger.getNextFireTime();
            this.previousFireTime = trigger.getPreviousFireTime();
            this.finalFireTime = trigger.getFinalFireTime();
            this.mayFireAgain = trigger.mayFireAgain();
            this.triggerState = triggerState;
            this.calendarName = trigger.getCalendarName();
            this.description = trigger.getDescription();
            this.misfireInstruction = trigger.getMisfireInstruction();
            this.priority = trigger.getPriority();
            
            // Các thuộc tính sau có thể cần kiểm tra loại Trigger cụ thể
            if (trigger instanceof SimpleTrigger) {
                SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
                this.repeatInterval = simpleTrigger.getRepeatInterval();
                this.repeatCount = simpleTrigger.getRepeatCount();
            } else {
                this.repeatInterval = -1;
                this.repeatCount = -1;
            }
            
            this.jobDataMap = trigger.getJobDataMap();
        }

        // Getters và Setters (được tạo tự động bởi @Data của Lombok)
    }
}

