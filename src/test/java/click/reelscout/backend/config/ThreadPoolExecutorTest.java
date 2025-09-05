package click.reelscout.backend.config;

import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ThreadPoolConfig that produces a ThreadPoolExecutor bean.
 * <p>
 * Pure unit tests: no Spring context is loaded, we directly instantiate
 * ThreadPoolConfig and verify the properties of the created ThreadPoolExecutor.
 */
class ThreadPoolExecutorTest {

    private final ThreadPoolConfig config = new ThreadPoolConfig();

    @Test
    void threadPoolExecutor_shouldNotBeNull() {
        // Act
        ThreadPoolExecutor executor = config.threadPoolExecutor();

        // Assert
        assertNotNull(executor, "Executor must not be null");
        assertFalse(executor.isShutdown(), "Executor must be active initially");
    }

    @Test
    void threadPoolExecutor_hasCorrectConfiguration() {
        ThreadPoolExecutor executor = config.threadPoolExecutor();

        int processors = Runtime.getRuntime().availableProcessors();

        // Core and maximum pool sizes
        assertEquals(processors, executor.getCorePoolSize(), "Core pool size must equal available processors");
        assertEquals(processors * 2, executor.getMaximumPoolSize(), "Max pool size must be double the processors");

        // Keep-alive time
        assertEquals(60L, executor.getKeepAliveTime(TimeUnit.SECONDS), "KeepAliveTime must be 60 seconds");

        // Queue type
        BlockingQueue<Runnable> queue = executor.getQueue();
        assertInstanceOf(LinkedBlockingQueue.class, queue, "Queue must be LinkedBlockingQueue");

        // Rejection policy
        assertInstanceOf(ThreadPoolExecutor.CallerRunsPolicy.class, executor.getRejectedExecutionHandler(), "Rejection policy must be CallerRunsPolicy");
    }

    @Test
    void threadPoolExecutor_shouldExecuteTasks() throws Exception {
        ThreadPoolExecutor executor = config.threadPoolExecutor();

        // Submit a simple task
        Future<Integer> future = executor.submit(() -> 42);

        // Assert the task result
        assertEquals(42, future.get());

        // Shut down the executor to avoid leaked threads
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS), "Executor should terminate gracefully");
    }
}