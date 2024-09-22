package com.example.demospringbootquartzschedule.dto;

import lombok.Data;
import org.quartz.JobDataMap;

@Data
public class AddJobDTO {
    private String jobName;
    private String groupName;
    private String triggerName;
    private int intervalInSeconds;
    private String jobClassName;
    private String description;
    private JobDataMap jobDataMap;
}