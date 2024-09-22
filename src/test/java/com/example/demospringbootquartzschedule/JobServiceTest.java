package com.example.demospringbootquartzschedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demospringbootquartzschedule.dto.AddJobDTO;
import com.example.demospringbootquartzschedule.dto.AddOneTimeJobDTO;
import com.example.demospringbootquartzschedule.service.JobService;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;

class JobServiceTest {

    @Mock
    private Scheduler scheduler;

    @InjectMocks
    private JobService jobService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddNewJob() throws SchedulerException {
        AddJobDTO addJobDTO = new AddJobDTO();
        addJobDTO.setJobName("testJob");
        addJobDTO.setGroupName("testGroup");
        addJobDTO.setTriggerName("testTrigger");
        addJobDTO.setIntervalInSeconds(10);
        addJobDTO.setJobClassName("com.example.demospringbootquartzschedule.config.SampleJob");

        jobService.addNewJob(addJobDTO);

        verify(scheduler, times(1)).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void testAddNewJobAlreadyExists() throws SchedulerException {
        AddJobDTO addJobDTO = new AddJobDTO();
        addJobDTO.setJobName("testJob");
        addJobDTO.setGroupName("testGroup");
        addJobDTO.setTriggerName("testTrigger");
        addJobDTO.setIntervalInSeconds(10);
        addJobDTO.setJobClassName("com.example.demospringbootquartzschedule.config.SampleJob");

        doThrow(new ObjectAlreadyExistsException("Job already exists"))
            .when(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));

        assertThrows(ObjectAlreadyExistsException.class, () -> jobService.addNewJob(addJobDTO));
    }

    @Test
    void testAddOneTimeJob() throws SchedulerException {
        AddOneTimeJobDTO addOneTimeJobDTO = new AddOneTimeJobDTO();
        addOneTimeJobDTO.setJobName("testOneTimeJob");
        addOneTimeJobDTO.setGroupName("testGroup");
        addOneTimeJobDTO.setTriggerName("testTrigger");
        addOneTimeJobDTO.setDelayInSeconds(60);
        addOneTimeJobDTO.setJobClassName("com.example.demospringbootquartzschedule.config.SampleJob");

        jobService.addOneTimeJob(addOneTimeJobDTO);

        verify(scheduler, times(1)).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void testPauseJob() throws SchedulerException {
        String jobName = "testJob";
        String groupName = "testGroup";

        jobService.pauseJob(jobName, groupName);

        verify(scheduler, times(1)).pauseJob(JobKey.jobKey(jobName, groupName));
    }

    @Test
    void testResumeJob() throws SchedulerException {
        String jobName = "testJob";
        String groupName = "testGroup";

        jobService.resumeJob(jobName, groupName);

        verify(scheduler, times(1)).resumeJob(JobKey.jobKey(jobName, groupName));
    }

    @Test
    void testDeleteJob() throws SchedulerException {
        String jobName = "testJob";
        String groupName = "testGroup";

        jobService.deleteJob(jobName, groupName);

        verify(scheduler, times(1)).deleteJob(JobKey.jobKey(jobName, groupName));
    }

    @Test
    void testGetAllJobs() throws SchedulerException {
        // Setup
        Set<JobKey> jobKeys = new HashSet<>();
        jobKeys.add(JobKey.jobKey("testJob1", "testGroup"));
        jobKeys.add(JobKey.jobKey("testJob2", "testGroup"));

        when(scheduler.getJobGroupNames()).thenReturn(Collections.singletonList("testGroup"));
        when(scheduler.getJobKeys(GroupMatcher.jobGroupEquals("testGroup"))).thenReturn(jobKeys);

        // Act
        List<String> jobs = jobService.getAllJobs();

        // Assert
        assertEquals(2, jobs.size());
        verify(scheduler, times(1)).getJobGroupNames();
        verify(scheduler, times(1)).getJobKeys(GroupMatcher.jobGroupEquals("testGroup"));
    }
}
