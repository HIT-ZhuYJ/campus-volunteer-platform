package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.example.entity.Activity;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {
    
    @Update("UPDATE vol_activity SET current_participants = current_participants + 1 WHERE id = #{activityId}")
    int incrementParticipants(Long activityId);
    
    @Update("UPDATE vol_activity SET current_participants = current_participants - 1 WHERE id = #{activityId} AND current_participants > 0")
    int decrementParticipants(Long activityId);
}
