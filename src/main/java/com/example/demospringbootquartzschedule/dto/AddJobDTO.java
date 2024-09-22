package com.example.demospringbootquartzschedule.dto;

import lombok.Data;

@Data
public class AddJobDTO {
    private String jobName;
    private String groupName;
    private String triggerName;
    private int intervalInSeconds;
    private String jobClassName;
}