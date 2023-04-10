package com.seamew.mybatisplusdemo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seamew.mybatisplusdemo.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User>
{
}
