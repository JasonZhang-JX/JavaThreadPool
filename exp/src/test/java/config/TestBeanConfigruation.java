package config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 测试的一个配置类、这个配置类、是用于测试。
 * <br />
 * 1、先测试容器里是否有这个类。先注释掉注解。
 * @Author:Administrator
 * @Date:2017/4/10 0:15
 */
@TestConfiguration // 只能用于测试用。
//@Configuration  // 用于 正常使用。因为你放在这里、正常的扫描不到所以也会报错的。
public class TestBeanConfigruation {
    @Bean("MyExecutor")
    public Executor myTaskPool(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //更改参数
        //线程池维护线程的最少数量
        executor.setCorePoolSize(5);
        //线程池维护线程的最大数量
        executor.setMaxPoolSize(10);
        //缓存队列
        executor.setQueueCapacity(20);
        //线程池空闲时，线程存活的时间
        executor.setKeepAliveSeconds(200);
        //线程池名
        executor.setThreadNamePrefix("MyExecutor-");
        //拒绝task处理策略
        //AbortPolicy 直接抛出异常 CallerRunsPolicy用调用者所在的线程来执行任务；
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //设置ExecutorService
        executor.initialize();
        return executor;
    }
}