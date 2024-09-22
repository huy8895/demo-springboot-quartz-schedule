package com.example.demospringbootquartzschedule.dto;

import lombok.Data;

@Data
public class AddCronJobDTO {
    private String jobName;
    private String groupName;
    private String triggerName;
    private String jobClassName; // Tên lớp job
    private String cronExpression; // Biểu thức cron
}