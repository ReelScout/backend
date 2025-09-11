package click.reelscout.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Configuration class for setting up a ThreadPoolExecutor bean.
 */
@Configuration
public class ThreadPoolConfig {
    /**
     * Creates and configures a ThreadPoolExecutor bean.
     * Used for managing asynchronous tasks in the application.
     * Example use case: handling search requests concurrently.
     *
     * @return a configured ThreadPoolExecutor instance
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
