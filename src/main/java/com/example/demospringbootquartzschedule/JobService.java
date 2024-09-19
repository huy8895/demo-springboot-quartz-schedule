package com.example.demospringbootquartzschedule;

import java.util.ArrayList;
import java.util.List;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobService {

    @Autowired
    private Scheduler scheduler;

    // Thêm một job mới
    public void addNewJob(Class<? extends Job> jobClass, String jobName, String groupName, String triggerName, int intervalInSeconds) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, groupName)
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

    // Tạm dừng một job
    public void pauseJob(String jobName, String groupName) throws SchedulerException {
        scheduler.pauseJob(JobKey.jobKey(jobName, groupName));
    }

    // Tiếp tục một job
    public void resumeJob(String jobName, String groupName) throws SchedulerException {
        scheduler.resumeJob(JobKey.jobKey(jobName, groupName));
    }

    // Cập nhật job trigger (thay đổi lịch trình)
    public void updateJobTrigger(String jobName, String groupName, String newTriggerName, int newIntervalInSeconds) throws SchedulerException {
        Trigger newTrigger = TriggerBuilder.newTrigger()
                .withIdentity(newTriggerName, groupName)
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(newIntervalInSeconds)
                        .repeatForever())
                .build();

        scheduler.rescheduleJob(new TriggerKey(newTriggerName, groupName), newTrigger);
    }

    // Xóa job
    public void deleteJob(String jobName, String groupName) throws SchedulerException {
        scheduler.deleteJob(JobKey.jobKey(jobName, groupName));
    }

    // Lấy danh sách các job đang chạy
    public List<String> getAllJobs() throws SchedulerException {
        List<String> jobList = new ArrayList<>();
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                jobList.add("Job name: " + jobKey.getName() + ", Group name: " + jobKey.getGroup());
            }
        }
        return jobList;
    }
}
