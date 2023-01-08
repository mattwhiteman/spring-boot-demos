package matt.sb.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AppConfig {

    @Value("${app.config.core-pool-size}")
    private int corePoolSize;
    @Value("${app.config.max-pool-size}")
    private int maxPoolSize;
    @Value("${app.config.max-queue-capacity}")
    private int maxQueueCapacity;

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Set the initial amount of threads in the threadpool. This value must be equal to or
        // smaller than the max pool size.
        executor.setCorePoolSize(corePoolSize);

        // Set the max amount of threads in the threadpool. This value must be equal to or greater
        // than the core pool size
        executor.setMaxPoolSize(maxPoolSize);

        // Set the max queue capacity for pending requests. This queue is where requests go when
        // all threads are busy and a new request to an async method comes in.
        executor.setQueueCapacity(maxQueueCapacity);
        executor.setThreadNamePrefix("AsyncDemo-");

        // If the queue is full and all threads are busy, tell the caller to run the function
        // in its own thread context
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
