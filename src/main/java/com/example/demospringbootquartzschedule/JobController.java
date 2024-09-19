package com.example.demospringbootquartzschedule;

import java.util.List;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // API để thêm job mới
    @PostMapping("/add")
    public String addJob(@RequestParam String jobName,
        @RequestParam String groupName,
        @RequestParam String triggerName,
        @RequestParam int intervalInSeconds) throws SchedulerException {
        jobService.addNewJob(SampleJob.class, jobName, groupName, triggerName, intervalInSeconds);
        return "Job added successfully!";
    }

    // API để tạm dừng một job
    @PostMapping("/pause")
    public String pauseJob(@RequestParam String jobName,
        @RequestParam String groupName) throws SchedulerException {
        jobService.pauseJob(jobName, groupName);
        return "Job paused successfully!";
    }

    // API để tiếp tục một job sau khi tạm dừng
    @PostMapping("/resume")
    public String resumeJob(@RequestParam String jobName,
        @RequestParam String groupName) throws SchedulerException {
        jobService.resumeJob(jobName, groupName);
        return "Job resumed successfully!";
    }

    // API để cập nhật một job (thay đổi trigger)
    @PostMapping("/update")
    public String updateJob(@RequestParam String jobName,
        @RequestParam String groupName,
        @RequestParam String newTriggerName,
        @RequestParam int newIntervalInSeconds) throws SchedulerException {
        jobService.updateJobTrigger(jobName, groupName, newTriggerName, newIntervalInSeconds);
        return "Job updated successfully!";
    }

    // API để xóa một job
    @DeleteMapping("/delete")
    public String deleteJob(@RequestParam String jobName,
        @RequestParam String groupName) throws SchedulerException {
        jobService.deleteJob(jobName, groupName);
        return "Job deleted successfully!";
    }

    // API để lấy danh sách các job đang chạy
    @GetMapping("/list")
    public List<String> listAllJobs() throws SchedulerException {
        return jobService.getAllJobs();
    }
}
