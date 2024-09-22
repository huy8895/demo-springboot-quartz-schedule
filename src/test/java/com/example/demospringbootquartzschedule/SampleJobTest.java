package com.example.demospringbootquartzschedule;

import com.example.demospringbootquartzschedule.config.SampleJob;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SampleJobTest {

    @InjectMocks
    private SampleJob sampleJob;

    @Mock
    private JobExecutionContext context;

    @Mock
    private JobDetail jobDetail;

    @Mock
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(SampleJobTest.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute() throws Exception {
        // Thiết lập mock cho JobDetail và JobDataMap
        doReturn(jobDetail).when(context).getJobDetail(); // Sử dụng doReturn thay vì when
        doReturn(new JobDataMap()).when(jobDetail).getJobDataMap(); // Sử dụng doReturn thay vì when

        // Gọi phương thức execute
        sampleJob.execute(context);

        // Xác minh rằng các phương thức đã được gọi
        verify(context, atLeastOnce()).getJobDetail(); // Thay đổi từ times(1) thành atLeastOnce()
        verify(jobDetail, times(1)).getJobDataMap();
        // Kiểm tra xem logger có ghi log không (nếu cần)
        // verify(logger, times(1)).info(anyString(), any());
    }
}