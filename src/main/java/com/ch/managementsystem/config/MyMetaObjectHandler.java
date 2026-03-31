package com.ch.managementsystem.config; // 定义当前类所在的包路径，表示该类属于系统配置(config)模块

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler; // 导入MyBatis Plus提供的自动填充处理器接口
import org.apache.ibatis.reflection.MetaObject; // 导入MyBatis中的MetaObject对象，用于操作实体对象的属性
import org.springframework.stereotype.Component; // 导入Spring组件注解，用于将该类注册为Spring容器中的Bean

import java.time.LocalDateTime; // 导入Java8时间类LocalDateTime，用于获取当前时间

@Component // 标记该类为Spring组件，使SpringBoot启动时自动扫描并注册到容器中
public class MyMetaObjectHandler implements MetaObjectHandler { // 定义自动字段填充处理类，实现MyBatis Plus的MetaObjectHandler接口

    @Override // 重写MetaObjectHandler接口中的insertFill方法
    public void insertFill(MetaObject metaObject) { // 当执行数据库新增操作时自动调用该方法
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now()); // 在插入数据时自动填充createTime字段为当前时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now()); // 在插入数据时自动填充updateTime字段为当前时间
    }

    @Override // 重写MetaObjectHandler接口中的updateFill方法
    public void updateFill(MetaObject metaObject) { // 当执行数据库更新操作时自动调用该方法
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now()); // 在更新数据时自动填充updateTime字段为当前时间
    }
}