package com.example.demospringbootquartzschedule;

import lombok.Data;

@Data
public class AddJobDTO {
    private String jobName;
    private String groupName;
    private String triggerName;
    private int intervalInSeconds;
    private String jobClassName;
}