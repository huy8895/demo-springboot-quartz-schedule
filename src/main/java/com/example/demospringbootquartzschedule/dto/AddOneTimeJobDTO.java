package com.example.demospringbootquartzschedule.dto;

import java.util.Date;
import java.util.Map;
import lombok.Data;

@Data
public class AddOneTimeJobDTO {
    private String jobName;
    private String groupName;
    private String triggerName;
    private String jobClassName;
    private Date startTime; // Thêm trường startTime
    private Map<String, Object> jobData;

    // Các getter và setter được tạo tự động bởi @Data của Lombok
}