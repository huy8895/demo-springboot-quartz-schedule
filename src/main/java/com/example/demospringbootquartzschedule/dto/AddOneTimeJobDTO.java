package com.example.demospringbootquartzschedule.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import lombok.Data;

@Data
public class AddOneTimeJobDTO {
    private String jobName;
    private String groupName;
    private String triggerName;
    private String jobClassName;
    private String startTime; // Thêm trường startTime
    private Map<String, Object> jobData;

    public LocalDateTime getStartTime() {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(startTime, dateTimeFormatter);
    }

    // Phương thức mới để lấy startTime dưới dạng java.util.Date
    public Date getStartTimeAsDate() {
        return Date.from(getStartTime().atZone(ZoneId.systemDefault()).toInstant());
    }

    // Các getter và setter được tạo tự động bởi @Data của Lombok
}