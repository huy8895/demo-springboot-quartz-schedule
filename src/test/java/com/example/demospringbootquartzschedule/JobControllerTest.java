package com.example.demospringbootquartzschedule;

import com.example.demospringbootquartzschedule.controller.JobController;
import com.example.demospringbootquartzschedule.dto.JobInfoDTO;
import com.example.demospringbootquartzschedule.dto.JobUpdateDTO;
import com.example.demospringbootquartzschedule.service.JobService;
import com.example.demospringbootquartzschedule.dto.AddJobDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
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

		doNothing().when(jobService).addNewJob(any(AddJobDTO.class));

		mockMvc.perform(post("/jobs/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(addJobDTO)))
			.andExpect(status().isOk())
			.andExpect(content().string("Job added successfully"));

		verify(jobService, times(1)).addNewJob(any(AddJobDTO.class));
	}

	@Test
	void testPauseJob() throws Exception {
		doNothing().when(jobService).pauseJob(anyString(), anyString());

		mockMvc.perform(post("/jobs/pause")
				.param("jobName", "testJob")
				.param("groupName", "testGroup"))
			.andExpect(status().isOk())
			.andExpect(content().string("Job paused successfully!"));

		verify(jobService, times(1)).pauseJob("testJob", "testGroup");
	}

	@Test
	void testResumeJob() throws Exception {
		doNothing().when(jobService).resumeJob(anyString(), anyString());

		mockMvc.perform(post("/jobs/resume")
				.param("jobName", "testJob")
				.param("groupName", "testGroup"))
			.andExpect(status().isOk())
			.andExpect(content().string("Job resumed successfully!"));

		verify(jobService, times(1)).resumeJob("testJob", "testGroup");
	}

	@Test
	void testDeleteJob() throws Exception {
		doNothing().when(jobService).deleteJob(anyString(), anyString());

		mockMvc.perform(delete("/jobs/delete")
				.param("jobName", "testJob")
				.param("groupName", "testGroup"))
			.andExpect(status().isOk())
			.andExpect(content().string("Job deleted successfully!"));

		verify(jobService, times(1)).deleteJob("testJob", "testGroup");
	}

	@Test
	void testListAllJobs() throws Exception {
		List<String> jobs = Arrays.asList("Job1", "Job2");
		when(jobService.getAllJobs()).thenReturn(jobs);

		mockMvc.perform(get("/jobs/list"))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(jobs)));

		verify(jobService, times(1)).getAllJobs();
	}

	@Test
	void testUpdateJob() throws Exception {
		JobUpdateDTO updateDTO = new JobUpdateDTO();
		updateDTO.setJobName("testJob");
		updateDTO.setGroupName("testGroup");
		updateDTO.setNewTriggerName("newTrigger");
		updateDTO.setNewIntervalInSeconds(20);
		Map<String, Object> newJobData = new HashMap<>();
		newJobData.put("key", "value");
		updateDTO.setNewJobData(newJobData);

		doNothing().when(jobService).updateJob(any(JobUpdateDTO.class));

		mockMvc.perform(put("/jobs/update") // Đảm bảo đường dẫn khớp với mapping
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDTO)))
			.andExpect(status().isOk())
			.andExpect(content().string("Job updated successfully!"));

		verify(jobService, times(1)).updateJob(any(JobUpdateDTO.class));
	}

	@Test
	void testSearchJob() throws Exception {
		// Tạo một JobDetail mock
		JobDetail mockJobDetail = mock(JobDetail.class);
		JobKey mockJobKey = JobKey.jobKey("testJob", "testGroup");
		
		// Thiết lập hành vi cho JobDetail mock
		doReturn(mockJobKey).when(mockJobDetail).getKey();
		doReturn("Mô tả công việc").when(mockJobDetail).getDescription();
		doReturn(Object.class).when(mockJobDetail).getJobClass();
		doReturn(true).when(mockJobDetail).isDurable();
		doReturn(false).when(mockJobDetail).requestsRecovery();
		doReturn(new JobDataMap()).when(mockJobDetail).getJobDataMap();

		// Tạo danh sách TriggerInfo
		List<JobInfoDTO.TriggerInfo> triggerInfoList = Collections.emptyList(); // Hoặc tạo các TriggerInfo mock nếu cần

		// Khởi tạo JobInfoDTO bằng JobDetail mock và danh sách TriggerInfo
		JobInfoDTO jobInfo = new JobInfoDTO(mockJobDetail, triggerInfoList);
		List<JobInfoDTO> jobInfoList = Arrays.asList(jobInfo);

		// Thiết lập hành vi cho jobService
		doReturn(jobInfoList).when(jobService).searchJob(anyString(), anyString());

		// Thực hiện yêu cầu và kiểm tra phản hồi
		mockMvc.perform(get("/jobs/searchJob") // Đảm bảo đường dẫn khớp với mapping
				.param("jobName", "testJob")
				.param("groupName", "testGroup"))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(jobInfoList)));

		verify(jobService, times(1)).searchJob("testJob", "testGroup");
	}
}
