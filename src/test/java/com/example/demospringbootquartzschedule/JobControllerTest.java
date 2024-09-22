package com.example.demospringbootquartzschedule;

import com.example.demospringbootquartzschedule.controller.JobController;
import com.example.demospringbootquartzschedule.service.JobService;
import com.example.demospringbootquartzschedule.dto.AddJobDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
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
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(jobController).build();
		objectMapper = new ObjectMapper();
	}

	@Test
	void testAddJob() throws Exception {
		AddJobDTO addJobDTO = new AddJobDTO();
		addJobDTO.setJobName("testJob");
		addJobDTO.setGroupName("testGroup");
		addJobDTO.setTriggerName("testTrigger");
		addJobDTO.setIntervalInSeconds(10);
		addJobDTO.setJobClassName("com.example.TestJob");

		mockMvc.perform(post("/jobs/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(addJobDTO)))
			.andExpect(status().isOk())
			.andExpect(content().string("Job added successfully"));

		verify(jobService, times(1)).addNewJob(any(AddJobDTO.class));
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
			Collections.singletonList("Tên job: testJob1, Tên nhóm: testGroup"));

		mockMvc.perform(get("/jobs/list"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0]").value("Tên job: testJob1, Tên nhóm: testGroup"));

		verify(jobService, times(1)).getAllJobs();
	}
}
