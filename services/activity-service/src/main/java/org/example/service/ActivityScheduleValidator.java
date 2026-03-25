package org.example.service;

import org.example.common.exception.BusinessException;
import org.example.dto.ActivityCreateRequest;

/**
 * 活动时间与招募窗口校验（创建与更新共用）。
 */
final class ActivityScheduleValidator {

    private ActivityScheduleValidator() {}

    static void validate(ActivityCreateRequest request) {
        if (request.getRegistrationStartTime() == null || request.getRegistrationDeadline() == null) {
            throw new BusinessException("请填写招募开始时间与截止时间");
        }
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new BusinessException("请填写活动开始时间与结束时间");
        }
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new BusinessException("活动开始时间须早于结束时间");
        }
        if (!request.getRegistrationStartTime().isBefore(request.getRegistrationDeadline())) {
            throw new BusinessException("志愿招募开始时间须早于招募截止时间");
        }
        if (request.getRegistrationDeadline().isAfter(request.getStartTime())) {
            throw new BusinessException("报名截止时间不能晚于活动开始时间");
        }
    }
}
