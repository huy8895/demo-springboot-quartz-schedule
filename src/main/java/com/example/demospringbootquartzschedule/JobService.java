package com.example.demospringbootquartzschedule;

import com.example.demospringbootquartzschedule.JobInfo.TriggerInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.quartz.*;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobService {

    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    @Autowired
    private Scheduler scheduler;

    // Thêm một job mới
    public void addNewJob(AddJobDTO addJobDTO) throws SchedulerException {
        logger.info("Đang thêm job mới: {}", addJobDTO);
        
        Class<? extends Job> jobClass;
        try {
            jobClass = (Class<? extends Job>) Class.forName(addJobDTO.getJobClassName());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Không tìm thấy lớp job", e);
        }

        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(addJobDTO.getJobName(), addJobDTO.getGroupName())
                .withDescription("Mô tả")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(addJobDTO.getTriggerName(), addJobDTO.getGroupName())
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(addJobDTO.getIntervalInSeconds())
                        .repeatForever())
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    @Transactional
    public void addOneTimeJob(AddOneTimeJobDTO addOneTimeJobDTO) throws SchedulerException {
        logger.info("Đang thêm job một lần: {}", addOneTimeJobDTO);
        
        Class<? extends Job> jobClass;
        try {
            jobClass = (Class<? extends Job>) Class.forName(addOneTimeJobDTO.getJobClassName());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Không tìm thấy lớp job", e);
        }

        // Tạo JobDetail
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
            .withIdentity(addOneTimeJobDTO.getJobName(), addOneTimeJobDTO.getGroupName())
            .build();

        // Tạo trigger sẽ chỉ chạy một lần sau delayInSeconds
        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(addOneTimeJobDTO.getTriggerName(), addOneTimeJobDTO.getGroupName())
            .startAt(DateBuilder.futureDate(addOneTimeJobDTO.getDelayInSeconds(), DateBuilder.IntervalUnit.SECOND))
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0))
            .build();

        // Thêm Job vào scheduler
        scheduler.scheduleJob(jobDetail, trigger);
    }

    // Tạm dừng một job
    public void pauseJob(String jobName, String groupName) throws SchedulerException {
        logger.info("Pausing job: jobName={}, groupName={}", jobName, groupName);
        scheduler.pauseJob(JobKey.jobKey(jobName, groupName));
    }

    // Tiếp tục một job
    public void resumeJob(String jobName, String groupName) throws SchedulerException {
        logger.info("Resuming job: jobName={}, groupName={}", jobName, groupName);
        scheduler.resumeJob(JobKey.jobKey(jobName, groupName));
    }

    // Cập nhật job trigger (thay đổi lịch trình)
    public void     updateJob(JobUpdateDTO updateDTO) throws SchedulerException {
        JobKey jobKey = new JobKey(updateDTO.getJobName(), updateDTO.getGroupName());
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        
        if (jobDetail == null) {
            throw new IllegalArgumentException("Job not found");
        }

        // Cập nhật JobDataMap
        if (updateDTO.getNewJobData() != null && !updateDTO.getNewJobData().isEmpty()) {
            JobBuilder jobBuilder = jobDetail.getJobBuilder();
            
            // Đảm bảo job là durable
            jobBuilder = jobBuilder.storeDurably();
            
            // Cập nhật JobDataMap
            JobDataMap jobDataMap = new JobDataMap(jobDetail.getJobDataMap());
            jobDataMap.putAll(updateDTO.getNewJobData());
            jobBuilder = jobBuilder.usingJobData(jobDataMap);
            
            // Tạo JobDetail mới và cập nhật nó
            JobDetail updatedJobDetail = jobBuilder.build();
            scheduler.addJob(updatedJobDetail, true);
        }

        // Cập nhật Trigger nếu cần
        if (updateDTO.getNewTriggerName() != null || updateDTO.getNewIntervalInSeconds() != null) {
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            if (!triggers.isEmpty()) {
                Trigger oldTrigger = triggers.get(0);
                TriggerBuilder<Trigger> tb = TriggerBuilder.newTrigger();
                
                if (updateDTO.getNewTriggerName() != null) {
                    tb.withIdentity(updateDTO.getNewTriggerName(), updateDTO.getGroupName());
                } else {
                    tb.withIdentity(oldTrigger.getKey());
                }
                
                if (updateDTO.getNewIntervalInSeconds() != null) {
                    if (oldTrigger instanceof SimpleTrigger) {
                        tb.withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(updateDTO.getNewIntervalInSeconds())
                            .repeatForever());
                    } else {
                        // Xử lý các loại trigger khác nếu cần
                    }
                } else {
                    tb.withSchedule(oldTrigger.getScheduleBuilder());
                }
                
                scheduler.rescheduleJob(oldTrigger.getKey(), tb.build());
            }
        }
    }

    // Xóa job
    @Transactional
    public void deleteJob(String jobName, String groupName) throws SchedulerException {
        logger.info("Deleting job: jobName={}, groupName={}", jobName, groupName);
        scheduler.deleteJob(JobKey.jobKey(jobName, groupName));
    }

    // Lấy danh sách các job đang chạy
    public List<String> getAllJobs() throws SchedulerException {
        logger.info("Getting all jobs");
        List<String> jobList = new ArrayList<>();
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                jobList.add("Job name: " + jobKey.getName() + ", Group name: " + jobKey.getGroup());
            }
        }
        return jobList;
    }

    // Tìm kiếm job và các trigger liên quan
    public List<JobInfo> searchJob(String jobName, String groupName) throws SchedulerException {
        logger.info("Tìm kiếm công việc: jobName={}, groupName={}", jobName, groupName);
        List<JobInfo> jobInfoList = new ArrayList<>();

        if (jobName != null && groupName != null) {
            // Tìm kiếm công việc cụ thể
            JobKey jobKey = new JobKey(jobName, groupName);
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail != null) {
                jobInfoList.add(getJobInfo(jobDetail));
            }
        } else if (jobName != null) {
            // Tìm tất cả công việc có tên jobName trong tất cả các nhóm
            for (String group : scheduler.getJobGroupNames()) {
                JobKey jobKey = new JobKey(jobName, group);
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                if (jobDetail != null) {
                    jobInfoList.add(getJobInfo(jobDetail));
                }
            }
        } else if (groupName != null) {
            // Tìm tất cả công việc trong nhóm
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));
            for (JobKey jobKey : jobKeys) {
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                jobInfoList.add(getJobInfo(jobDetail));
            }
        } else {
            // Tìm tất cả công việc
            for (String group : scheduler.getJobGroupNames()) {
                Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(group));
                for (JobKey jobKey : jobKeys) {
                    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                    jobInfoList.add(getJobInfo(jobDetail));
                }
            }
        }

        logger.info("Tìm thấy {} công việc", jobInfoList.size());
        return jobInfoList;
    }

    private JobInfo getJobInfo(JobDetail jobDetail) throws SchedulerException {
        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
        List<TriggerInfo> triggerInfoList = triggers.stream().map(trigger -> {
            TriggerState triggerState;
            try {
                triggerState = scheduler.getTriggerState(trigger.getKey());
            } catch (SchedulerException e) {
                logger.info("Trigger không tồn tại, triggerKey={}, groupName={}", trigger.getKey(), trigger.getKey().getGroup());
                triggerState = TriggerState.NONE;
            }
            return new TriggerInfo(trigger, triggerState);
        }).collect(Collectors.toList());

        return new JobInfo(jobDetail, triggerInfoList);
    }
}