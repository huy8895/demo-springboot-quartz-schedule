package com.example.demospringbootquartzschedule;

import com.example.demospringbootquartzschedule.JobInfo.TriggerInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.quartz.*;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JobService {

    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    @Autowired
    private Scheduler scheduler;

    // Thêm một job mới
    public void addNewJob(Class<? extends Job> jobClass, String jobName, String groupName, String triggerName, int intervalInSeconds) throws SchedulerException {
        logger.info("Adding new job: jobClass={}, jobName={}, groupName={}, triggerName={}, intervalInSeconds={}", jobClass, jobName, groupName, triggerName, intervalInSeconds);
        final HashMap<String, String> map = new HashMap<>();

        map.put("jobClass", jobClass.getName());
        map.put("jobName", jobName);
        map.put("groupName", groupName);
        map.put("triggerName", triggerName);
        map.put("intervalInSeconds", String.valueOf(intervalInSeconds));

        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, groupName)
            .withDescription("description")
            .setJobData(new JobDataMap(map))
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, groupName)
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(intervalInSeconds)
                        .repeatForever())
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void addOneTimeJob(Class<? extends Job> jobClass, String jobName, String groupName, String triggerName, int delayInSeconds) throws SchedulerException {
        // Tạo JobDetail
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
            .withIdentity(jobName, groupName)
            .build();

        // Tạo trigger sẽ chỉ chạy một lần sau delayInSeconds
        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(triggerName, groupName)
            .startAt(DateBuilder.futureDate(delayInSeconds, DateBuilder.IntervalUnit.SECOND))  // Thời gian bắt đầu
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0))  // Chỉ chạy 1 lần
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
    public void updateJobTrigger(String oldTriggerName, String groupName, String newTriggerName, int newIntervalInSeconds) throws SchedulerException {
        logger.info("Updating job trigger: oldTriggerName={}, groupName={}, newTriggerName={}, newIntervalInSeconds={}", oldTriggerName, groupName, newTriggerName, newIntervalInSeconds);
        Trigger newTrigger = TriggerBuilder.newTrigger()
                .withIdentity(newTriggerName, groupName)
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(newIntervalInSeconds)
                        .repeatForever())
                .build();

        scheduler.rescheduleJob(new TriggerKey(oldTriggerName, groupName), newTrigger);
    }

    // Xóa job
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