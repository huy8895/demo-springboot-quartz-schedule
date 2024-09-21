package com.example.demospringbootquartzschedule;

import lombok.Data;
import java.util.Map;

@Data
public class JobUpdateDTO {
    private String jobName;
    private String groupName;
    private String newTriggerName;
    private Integer newIntervalInSeconds;
    private Map<String, Object> newJobData;
}