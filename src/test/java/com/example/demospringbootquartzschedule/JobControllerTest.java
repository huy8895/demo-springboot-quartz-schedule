package com.example.demospringbootquartzschedule;

import com.example.demospringbootquartzschedule.controller.JobController;
import com.example.demospringbootquartzschedule.service.JobService;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class JobControllerTest {

  @Mock
  private JobService jobService;

  @InjectMocks
  private JobController jobController;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(jobController)
        .build();
  }

  @Test
  void testAddJob() throws Exception {
    mockMvc.perform(post("/jobs/add")
            .param("jobName", "testJob")
            .param("groupName", "testGroup")
            .param("triggerName", "testTrigger")
            .param("intervalInSeconds", "10"))
        .andExpect(status().isOk())
        .andExpect(content().string("Job added successfully!"));

    verify(jobService, times(1)).addNewJob(any(), eq("testJob"), eq("testGroup"), eq("testTrigger"),
        eq(10));
  }

  @Test
  void testPauseJob() throws Exception {
    mockMvc.perform(post("/jobs/pause")
            .param("jobName", "testJob")
            .param("groupName", "testGroup"))
        .andExpect(status().isOk())
        .andExpect(content().string("Job paused successfully!"));

    verify(jobService, times(1)).pauseJob("testJob", "testGroup");
  }

  @Test
  void testResumeJob() throws Exception {
    mockMvc.perform(post("/jobs/resume")
            .param("jobName", "testJob")
            .param("groupName", "testGroup"))
        .andExpect(status().isOk())
        .andExpect(content().string("Job resumed successfully!"));

    verify(jobService, times(1)).resumeJob("testJob", "testGroup");
  }

  @Test
  void testDeleteJob() throws Exception {
    mockMvc.perform(delete("/jobs/delete")
            .param("jobName", "testJob")
            .param("groupName", "testGroup"))
        .andExpect(status().isOk())
        .andExpect(content().string("Job deleted successfully!"));

    verify(jobService, times(1)).deleteJob("testJob", "testGroup");
  }

  @Test
  void testListAllJobs() throws Exception {
    when(jobService.getAllJobs()).thenReturn(
        Collections.singletonList("Job name: testJob1, Group name: testGroup"));

    mockMvc.perform(get("/jobs/list"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]").value("Job name: testJob1, Group name: testGroup"));

    verify(jobService, times(1)).getAllJobs();
  }
}
