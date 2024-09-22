package com.example.demospringbootquartzschedule.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SampleJob implements Job {
    @Autowired
    ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public void execute(JobExecutionContext context) {
        log.info("[START] =====> execute() called with: [{}]", context.getJobDetail());
        // Lấy thông tin jobDetail từ context
        JobDetail jobDetail = context.getJobDetail();
        // Ghi thông tin jobDetail vào log
        log.info("Thông tin JobDetail sau khi lấy từ context: {}", jobDetail);
        // Lấy thông tin jobDataMap từ jobDetail
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        // Ghi thông tin jobDataMap vào log
        log.info("[END] Thông tin jobDataMap: {}", objectMapper.writeValueAsString(jobDataMap));
    }
}
