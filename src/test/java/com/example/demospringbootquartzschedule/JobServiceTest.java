package com.example.demospringbootquartzschedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demospringbootquartzschedule.dto.AddJobDTO;
import com.example.demospringbootquartzschedule.dto.AddOneTimeJobDTO;
import com.example.demospringbootquartzschedule.dto.JobInfoDTO;
import com.example.demospringbootquartzschedule.dto.JobUpdateDTO;
import com.example.demospringbootquartzschedule.service.JobService;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.quartz.TriggerKey;
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
        .when(scheduler)
        .scheduleJob(any(JobDetail.class), any(Trigger.class));

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
    Set<JobKey> jobKeys = new HashSet<>();
    jobKeys.add(JobKey.jobKey("testJob1", "testGroup"));
    jobKeys.add(JobKey.jobKey("testJob2", "testGroup"));

    when(scheduler.getJobGroupNames()).thenReturn(Collections.singletonList("testGroup"));
    when(scheduler.getJobKeys(GroupMatcher.jobGroupEquals("testGroup"))).thenReturn(jobKeys);

    List<String> jobs = jobService.getAllJobs();

    assertEquals(2, jobs.size());
    verify(scheduler, times(1)).getJobGroupNames();
    verify(scheduler, times(1)).getJobKeys(GroupMatcher.jobGroupEquals("testGroup"));
  }

  @Test
  void testUpdateJob() throws SchedulerException {
    JobUpdateDTO updateDTO = new JobUpdateDTO();
    updateDTO.setJobName("testJob");
    updateDTO.setGroupName("testGroup");
    updateDTO.setNewTriggerName("newTrigger");
    updateDTO.setNewIntervalInSeconds(20);
    Map<String, Object> newJobData = new HashMap<>();
    newJobData.put("key", "value");
    updateDTO.setNewJobData(newJobData);

    JobDetail mockJobDetail = mock(JobDetail.class);
    when(scheduler.getJobDetail(any(JobKey.class))).thenReturn(mockJobDetail);

    Trigger mockTrigger = mock(Trigger.class);
    doReturn(Collections.singletonList(mockTrigger))
        .when(scheduler).getTriggersOfJob(any(JobKey.class));
    jobService.updateJob(updateDTO);

    verify(scheduler).addJob(any(JobDetail.class), eq(true));
    verify(scheduler).rescheduleJob(any(TriggerKey.class), any(Trigger.class));
  }

  @Test
  void testSearchJob() throws SchedulerException {
    String jobName = "testJob";
    String groupName = "testGroup";

    JobDetail mockJobDetail = mock(JobDetail.class);
    when(scheduler.getJobDetail(any(JobKey.class))).thenReturn(mockJobDetail);

    Trigger mockTrigger = mock(Trigger.class);
    when(scheduler.getTriggerState(any(TriggerKey.class))).thenReturn(Trigger.TriggerState.NORMAL);

    doReturn(Collections.singletonList(mockTrigger))
        .when(scheduler).getTriggersOfJob(any(JobKey.class));

    List<JobInfoDTO> result = jobService.searchJob(jobName, groupName);

    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    verify(scheduler).getJobDetail(JobKey.jobKey(jobName, groupName));
  }

  @Test
  void testSearchAllJobs() throws SchedulerException {
    when(scheduler.getJobGroupNames()).thenReturn(Collections.singletonList("testGroup"));
    Set<JobKey> jobKeys = new HashSet<>();
    jobKeys.add(JobKey.jobKey("testJob1", "testGroup"));
    jobKeys.add(JobKey.jobKey("testJob2", "testGroup"));
    when(scheduler.getJobKeys(any(GroupMatcher.class))).thenReturn(jobKeys);

    JobDetail mockJobDetail = mock(JobDetail.class);
    when(scheduler.getJobDetail(any(JobKey.class))).thenReturn(mockJobDetail);

    Trigger mockTrigger = mock(Trigger.class);
    doReturn(Collections.singletonList(mockTrigger))
        .when(scheduler).getTriggersOfJob(any(JobKey.class));

    when(scheduler.getTriggerState(any(TriggerKey.class))).thenReturn(Trigger.TriggerState.NORMAL);

    List<JobInfoDTO> result = jobService.searchJob(null, null);

    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    verify(scheduler).getJobGroupNames();
    verify(scheduler, times(2)).getJobDetail(any(JobKey.class));
  }

  @Test
  void testSearchJobByGroup() throws SchedulerException {
    String groupName = "testGroup";
    Set<JobKey> jobKeys = new HashSet<>();
    jobKeys.add(JobKey.jobKey("testJob1", groupName));
    jobKeys.add(JobKey.jobKey("testJob2", groupName));
    when(scheduler.getJobKeys(any(GroupMatcher.class))).thenReturn(jobKeys);

    JobDetail mockJobDetail = mock(JobDetail.class);
    when(scheduler.getJobDetail(any(JobKey.class))).thenReturn(mockJobDetail);

    Trigger mockTrigger = mock(Trigger.class);
    doReturn(Collections.singletonList(mockTrigger))
        .when(scheduler).getTriggersOfJob(any(JobKey.class));
    when(scheduler.getTriggerState(any(TriggerKey.class))).thenReturn(Trigger.TriggerState.NORMAL);

    List<JobInfoDTO> result = jobService.searchJob(null, groupName);

    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    verify(scheduler).getJobKeys(GroupMatcher.jobGroupEquals(groupName));
  }
}
