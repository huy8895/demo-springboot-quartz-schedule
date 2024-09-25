package com.example.demospringbootquartzschedule.config;

import java.lang.reflect.Method;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@DisallowConcurrentExecution
@Slf4j
@Component
@RequiredArgsConstructor
public class MethodCronJob extends QuartzJobBean {

    private final ApplicationContext applicationContext;

    @SneakyThrows
    @Override
    protected void executeInternal( JobExecutionContext context) {
        // Lấy JobDetail và JobDataMap từ context
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();

        // In ra thông tin về phương thức và group job
        log.info("executeInternal {} in Group {} Started at {}", jobDataMap.get("METHOD_NAME"), jobDataMap.get("GROUP"),
                new Date());

        // Lấy tên phương thức đầy đủ từ jobDataMap
        String methodFullName = jobDataMap.get("METHOD_NAME").toString();

        // 1. Tách lấy tên class và tên method từ methodFullName
        String className = methodFullName.substring(0, methodFullName.lastIndexOf('.')); // Lấy phần trước dấu '.'
        String methodName = methodFullName.substring(methodFullName.lastIndexOf('.') + 1); // Lấy phần sau dấu '.'

        // In thông tin về class và method
        log.info("Class: {}, Method: {}", className, methodName);

        try {
            // 2. Lấy Class theo tên class
            Class<?> clazz = Class.forName(className);  // Tìm class
            // 3. Lấy bean từ ApplicationContext dựa trên class
            Object bean = applicationContext.getBean(clazz);  // Lấy bean của class từ Spring context

            // 4. Lấy method từ class dựa trên tên method
            Method method = clazz.getMethod(methodName); // Tìm method cleanupGeneralInfo

            // 5. Thực thi method không có tham số
            method.invoke(bean);  // Gọi method cleanupGeneralInfo

            // In ra log sau khi method được gọi thành công
            log.info("Method {} executed successfully.", methodName);
        } catch (Exception e) {
            // In lỗi nếu xảy ra
            log.error("Failed to execute method {}: {}", methodName, e.getMessage(), e);
        }
    }
}
