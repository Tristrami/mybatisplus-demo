package com.seamew.mybatisplusdemo.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
// 配置 Java 类和数据库表的映射关系
@TableName("user")
public class User
{
    // 配置主键生成策略，AUTO 表示使用数据库自增策略
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    // 配置 Java 字段和数据库字段的映射关系，select = false 表示该字段不参与查询
    @TableField(value = "password", select = false)
    private String password;

    private Integer age;

    private String phone;

    // exist = false 表示该字段在数据库表中不存在
    @TableField(exist = false)
    private Boolean online;

    // 配置逻辑删除字段
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;

    // 配置乐观锁版本字段
    @Version
    private Integer version;
}
