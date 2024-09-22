package com.example.demospringbootquartzschedule;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // API để thêm job mới
    @PostMapping("/add")
    public String addJob(@RequestBody AddJobDTO addJobDTO) throws SchedulerException {
        jobService.addNewJob(addJobDTO);
        log.info("Job added successfully");
        return "Job đã được thêm thành công!";
    }

    @PostMapping("/addOneTimeJob")
    public String addOneTimeJob(@RequestParam String jobName,
        @RequestParam String groupName,
        @RequestParam String triggerName,
        @RequestParam int delayInSeconds) throws SchedulerException {
        log.info("start addOneTimeJob");
        jobService.addOneTimeJob(SampleJob.class, jobName, groupName, triggerName, delayInSeconds);
        log.info("Job added successfully!");
        return "One-time job added successfully!";
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
    public String updateJob(@RequestBody JobUpdateDTO updateDTO) throws SchedulerException {
        jobService.updateJob(updateDTO);
        log.info("Job updated successfully!");
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

    // API để tìm kiếm job và trigger
    @GetMapping("/searchJob")
    public List<JobInfo> searchJob(@RequestParam(required = false) String jobName,
        @RequestParam(required = false) String groupName) throws SchedulerException {
        // Gọi service để xử lý logic tìm kiếm job và trigger
        log.info("start searchJob with groupName {}", groupName);
        return jobService.searchJob(jobName, groupName);
    }
}
