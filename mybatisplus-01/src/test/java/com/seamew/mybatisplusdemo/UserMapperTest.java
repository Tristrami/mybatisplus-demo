package com.seamew.mybatisplusdemo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seamew.mybatisplusdemo.dao.UserMapper;
import com.seamew.mybatisplusdemo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
public class UserMapperTest
{
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testGetAllUsers()
    {
        List<User> userList = userMapper.selectList(null);
        log.debug("The userList is [{}]", userList);
    }

    @Test
    public void testPagination()
    {
        // 使用分页查询步骤:
        // 1. 创建 MybatisPlusInterceptor 对象，并向其中加入 PaginationInnerInterceptor
        //    分页拦截器，并将 MybatisPlusInterceptor 对象放入 Spring 容器中
        // 2. 查询时使用 mapper.selectPage() 方法，并使用 IPage 对象封装分页参数进行查询
        // 3. 使用 page.getRecord() 方法获取查询结果
        IPage<User> page = new Page<>(2, 3);
        userMapper.selectPage(page, null);
        List<User> userList = page.getRecords();
        log.debug("查询结果:");
        printList(userList);
        log.debug("总页数为: {}", page.getPages());
        log.debug("总记录数为: {}", page.getTotal());
        log.debug("每页记录数为: {}", page.getSize());
        log.debug("当前页数为: {}", page.getCurrent());
    }

    @Test
    public void testQueryWrapper()
    {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 通过 lambda 方法名获取 Java 中的字段名称，再通过字段上 @TableField 注解 (如果有)
        // 定义的 orm 映射关系获取该字段对应的数据库字段名称
        // 参考 com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper.columnToString(com.baomidou.mybatisplus.core.toolkit.support.SFunction<T,?>, boolean)
        wrapper.ge(User::getAge, 30)
               .or().le(User::getAge, 20);
        List<User> userList = userMapper.selectList(wrapper);
        printList(userList);
    }

    @Test
    public void testInsert()
    {
        // 配置主键生成策略的方法:
        // 1. 局部配置: User 类中的主键字段可以使用 @TableId 指定主键的生成策略
        // 2. 全局配置: 在 application.yml 中指定 mybatis-plus.global-config.db-config.id-type 属性
        User user = new User();
        user.setUsername("seamew");
        user.setPassword("123");
        user.setAge(22);
        user.setPhone("15902685501");
        userMapper.insert(user);
    }

    @Test
    public void testUpdateWithOptimisticLocker()
    {
        // 测试带乐观锁的更新，配置乐观锁步骤:
        // 1. 在类中乐观锁字段上加上 @Version 注解
        // 2. 在 MybatisPlusInterceptor 对象中添加 OptimisticLockerInnerInterceptor
        //    拦截器，并把 MybatisPlusInterceptor放入容器中
        // 配置好乐观锁后，在每次进行数据库 UPDATE 操作时，MP 都会为我们在 SQL 语句中加上
        // SET version = version + 1 和 WHERE version = ${version} 子句，所以我们在
        // 进行更新前首先需要获取 version 字段
        // eg. update user set username = 'lulu', version = version + 1 where id = 1 and version = 1
        User userDTO = new User();
        userDTO.setId(1L);
        userDTO.setUsername("lulu");
        User user = userMapper.selectById(userDTO.getId());
        userDTO.setVersion(user.getVersion());
        userMapper.updateById(userDTO);
    }

    @Test
    public void testLogicalDelete()
    {
        // 配置逻辑删除的方法:
        // 1. 局部配置: 在 User 类中标识逻辑删除的字段上加上 @TableLogic 注解，并指定
        //    value (标识未删除用的值) 和 delval (标识已删除用的值) 字段
        // 2. 全局配置: 在 application.yml 中指定 mybatis-plus.global-config.db-config
        //    下的 logic-delete-field，logic-delete-value，logic-not-delete-value 属性
        // 配置逻辑删除后，在执行 SELECT 操作时 MP 会加上 WHERE is_deleted = 0 的子句，在执
        // 行删除操作时，MP 会使用 UPDATE ... set is_deleted = 1 where is_deleted = 0 进
        // 行逻辑删除
        User userDTO = new User();
        userDTO.setId(5L);
        User user = userMapper.selectById(userDTO.getId());
        userDTO.setVersion(user.getVersion());
        userMapper.deleteById(userDTO);
    }

    private void printList(List<?> list)
    {
        for (Object o : list) {
            log.debug("{}", o);
        }
    }
}
