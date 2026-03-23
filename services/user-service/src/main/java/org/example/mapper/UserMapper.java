package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.example.entity.User;

import java.math.BigDecimal;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Update("UPDATE sys_user SET total_volunteer_hours = total_volunteer_hours + #{hours} WHERE id = #{userId}")
    int addVolunteerHours(Long userId, BigDecimal hours);
}
