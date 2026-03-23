package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.entity.Registration;
import org.example.vo.RegistrationVO;

import java.util.List;

@Mapper
public interface RegistrationMapper extends BaseMapper<Registration> {
    
    @Select("SELECT r.id, r.activity_id, a.title as activity_title, a.location, a.volunteer_hours, " +
            "a.start_time, r.registration_time, r.check_in_status, r.hours_confirmed, r.status " +
            "FROM vol_registration r " +
            "LEFT JOIN vol_activity a ON r.activity_id = a.id " +
            "WHERE r.user_id = #{userId} " +
            "ORDER BY r.create_time DESC")
    List<RegistrationVO> selectUserRegistrations(Long userId);
}
