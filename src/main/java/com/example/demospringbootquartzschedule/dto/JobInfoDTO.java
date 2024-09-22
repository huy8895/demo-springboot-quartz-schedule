package com.example.demospringbootquartzschedule.dto;

import java.util.Date;
import java.util.List;
import lombok.Data;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;

/**
 * Lớp JobInfo đại diện cho thông tin chi tiết của một công việc (job) trong Quartz Scheduler.
 */
@Data
public class JobInfoDTO {
    /**
     * Khóa duy nhất của công việc, bao gồm tên và nhóm.
     */
    private JobKey jobKey;

    /**
     * Mô tả về công việc.
     */
    private String description;

    /**
     * Lớp Java thực hiện công việc này.
     */
    private Class<?> jobClass;

    /**
     * Xác định liệu công việc có bền vững hay không.
     * Nếu true, công việc sẽ được giữ lại trong scheduler ngay cả khi không có trigger nào liên kết.
     * Nếu false, công việc sẽ bị xóa khỏi scheduler khi không còn trigger nào liên kết.
     */
    private boolean durability;

    /**
     * Xác định liệu công việc có yêu cầu khôi phục khi xảy ra lỗi hay không.
     * Nếu true, công việc sẽ được khôi phục (thực hiện lại) sau khi hệ thống gặp sự cố.
     */
    private boolean requestsRecovery;

    /**
     * Danh sách các trigger liên kết với công việc này.
     */
    private List<TriggerInfo> triggerInfoList;

    /**
     * Bản đồ dữ liệu của công việc, chứa các thông tin bổ sung cần thiết cho việc thực thi.
     */
    private JobDataMap jobDataMap;

    /**
     * Khởi tạo một đối tượng JobInfo từ JobDetail và danh sách TriggerInfo.
     *
     * @param jobDetail JobDetail chứa thông tin chi tiết về công việc
     * @param triggerInfoList Danh sách các TriggerInfo liên kết với công việc
     */
    public JobInfoDTO(JobDetail jobDetail, List<TriggerInfo> triggerInfoList) {
        this.jobKey = jobDetail.getKey();
        this.description = jobDetail.getDescription();
        this.jobClass = jobDetail.getJobClass();
        this.durability = jobDetail.isDurable();
        this.requestsRecovery = jobDetail.requestsRecovery();
        this.triggerInfoList = triggerInfoList;
        this.jobDataMap = jobDetail.getJobDataMap();
    }

    // Getters và Setters được tạo tự động bởi @Data của Lombok

    /**
     * Lớp TriggerInfo đại diện cho thông tin chi tiết của một trigger trong Quartz Scheduler.
     */
    @Data
    public static class TriggerInfo {
        /**
         * Tên của trigger.
         */
        private String triggerName;

        /**
         * Tên nhóm của trigger.
         */
        private String triggerGroup;

        /**
         * Thời điểm bắt đầu của trigger.
         */
        private Date startTime;

        /**
         * Thời điểm kết thúc của trigger (nếu có).
         */
        private Date endTime;

        /**
         * Thời điểm kích hoạt tiếp theo của trigger.
         */
        private Date nextFireTime;

        /**
         * Thời điểm kích hoạt trước đó của trigger.
         */
        private Date previousFireTime;

        /**
         * Thời điểm kích hoạt cuối cùng của trigger (nếu có).
         */
        private Date finalFireTime;

        /**
         * Xác định liệu trigger có thể kích hoạt lại trong tương lai hay không.
         */
        private boolean mayFireAgain;

        /**
         * Trạng thái hiện tại của trigger.
         */
        private TriggerState triggerState;

        /**
         * Tên của calendar liên kết với trigger (nếu có).
         */
        private String calendarName;

        /**
         * Mô tả về trigger.
         */
        private String description;

        /**
         * Hướng dẫn xử lý khi trigger bị bỏ lỡ (misfire).
         */
        private int misfireInstruction;

        /**
         * Độ ưu tiên của trigger.
         */
        private int priority;

        /**
         * Khoảng thời gian lặp lại (chỉ áp dụng cho SimpleTrigger).
         */
        private long repeatInterval;

        /**
         * Số lần lặp lại (chỉ áp dụng cho SimpleTrigger).
         */
        private int repeatCount;

        /**
         * Bản đồ dữ liệu của trigger, chứa các thông tin bổ sung.
         */
        private JobDataMap jobDataMap;

        /**
         * Khởi tạo một đối tượng TriggerInfo từ Trigger và TriggerState.
         *
         * @param trigger Đối tượng Trigger chứa thông tin chi tiết về trigger
         * @param triggerState Trạng thái hiện tại của trigger
         */
        public TriggerInfo(Trigger trigger, TriggerState triggerState) {
            this.triggerName = trigger.getKey().getName();
            this.triggerGroup = trigger.getKey().getGroup();
            this.startTime = trigger.getStartTime();
            this.endTime = trigger.getEndTime();
            this.nextFireTime = trigger.getNextFireTime();
            this.previousFireTime = trigger.getPreviousFireTime();
            this.finalFireTime = trigger.getFinalFireTime();
            this.mayFireAgain = trigger.mayFireAgain();
            this.triggerState = triggerState;
            this.calendarName = trigger.getCalendarName();
            this.description = trigger.getDescription();
            this.misfireInstruction = trigger.getMisfireInstruction();
            this.priority = trigger.getPriority();
            
            if (trigger instanceof SimpleTrigger) {
                SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
                this.repeatInterval = simpleTrigger.getRepeatInterval();
                this.repeatCount = simpleTrigger.getRepeatCount();
            } else {
                this.repeatInterval = -1;
                this.repeatCount = -1;
            }
            
            this.jobDataMap = trigger.getJobDataMap();
        }

        // Getters và Setters được tạo tự động bởi @Data của Lombok
    }
}

