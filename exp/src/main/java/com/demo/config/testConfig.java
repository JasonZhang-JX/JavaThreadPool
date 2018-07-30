//package com.demo.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import java.util.concurrent.Executor;
//import java.util.concurrent.ThreadPoolExecutor;
//
//@Configuration
//@EnableAsync
//public class testConfig {
//
//    @Bean("MyExecutor")
//    public Executor myTaskPool(){
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        //更改参数
//        //线程池维护线程的最少数量
//        executor.setCorePoolSize(5);
//        //线程池维护线程的最大数量
//        executor.setMaxPoolSize(10);
//        //缓存队列
//        executor.setQueueCapacity(20);
//        //线程池空闲时，线程存活的时间
//        executor.setKeepAliveSeconds(200);
//        //线程池名
//        executor.setThreadNamePrefix("MyExecutor-");
//        //拒绝task处理策略
//        //AbortPolicy 直接抛出异常 CallerRunsPolicy用调用者所在的线程来执行任务；
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        //设置ExecutorService
//        executor.initialize();
//        return executor;
//    }
//}
