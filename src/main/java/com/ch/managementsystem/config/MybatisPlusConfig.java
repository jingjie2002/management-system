package com.ch.managementsystem.config; // 定义当前类所在的包路径，表示该类属于系统配置(config)模块

import org.mybatis.spring.annotation.MapperScan; // 导入MyBatis的Mapper扫描注解，用于自动扫描Mapper接口
import org.springframework.context.annotation.Bean; // 导入Bean注解，用于将方法返回的对象注册到Spring容器中
import org.springframework.context.annotation.Configuration; // 导入Spring配置类注解，标识该类是一个配置类
import org.springframework.core.io.support.PathMatchingResourcePatternResolver; // 导入资源路径解析器，用于加载mapper.xml文件
import org.springframework.core.io.Resource;

import javax.sql.DataSource; // 导入数据源接口，用于提供数据库连接
import org.apache.ibatis.session.SqlSessionFactory; // 导入SqlSessionFactory接口，用于创建SqlSession对象
import com.baomidou.mybatisplus.core.MybatisConfiguration; // 导入MyBatis Plus配置类
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean; // 导入MyBatis Plus提供的SqlSessionFactoryBean实现类

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor; // 导入MyBatis Plus拦截器，用于添加各种插件
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor; // 导入分页插件，用于实现分页查询

@Configuration // 标记该类为Spring配置类，SpringBoot启动时会自动加载
@MapperScan("com.ch.managementsystem.mapper") // 指定扫描Mapper接口的包路径，自动生成Mapper代理对象
public class MybatisPlusConfig { // 定义MyBatis Plus配置类

    /**
     * Pagination Interceptor
     */ // 注释说明：这是分页拦截器配置

    @Bean // 将该方法返回的对象注册为Spring容器中的Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() { // 定义MyBatis Plus拦截器方法
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor(); // 创建MyBatis Plus拦截器对象
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor()); // 添加分页拦截器，用于支持分页查询
        return interceptor; // 返回拦截器对象并交给Spring管理
    }

   @Bean // 将SqlSessionFactory对象注册为Spring容器中的Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception { // 定义SqlSessionFactory工厂方法，并注入数据源
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean(); // 创建MyBatis Plus的SqlSessionFactoryBean对象
        factory.setDataSource(dataSource); // 设置数据源，用于连接数据库
        Resource[] mapperResources = new PathMatchingResourcePatternResolver().getResources("classpath*:/mapper/**/*.xml");
        if (mapperResources.length > 0) {
            factory.setMapperLocations(mapperResources); // 设置Mapper XML文件路径，用于加载SQL映射文件
        }
        factory.setTypeAliasesPackage("com.ch.managementsystem.entity"); // 设置实体类包路径，用于简化Mapper XML中的类名
        MybatisConfiguration configuration = new MybatisConfiguration(); // 创建MyBatis配置对象
        configuration.setMapUnderscoreToCamelCase(true); // 开启下划线转驼峰命名规则（例如user_name自动映射为userName）
        factory.setConfiguration(configuration); // 将配置对象设置到SqlSessionFactory中
        factory.setPlugins(mybatisPlusInterceptor()); // 注册MyBatis Plus插件（这里是分页插件）
        return factory.getObject(); // 返回SqlSessionFactory对象供Spring管理
    }

} // 配置类结束
