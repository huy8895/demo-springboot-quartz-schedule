package com.example.demospringbootquartzschedule;

import lombok.Data;

@Data
public class AddOneTimeJobDTO {
    private String jobName;
    private String groupName;
    private String triggerName;
    private int delayInSeconds;
    private String jobClassName;
}