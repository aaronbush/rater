package rater;

import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static final int NTHREADS = 24;
    private static final RateLimiter rateLimiter = RateLimiter.create(0.5);
    private static final AtomicLong counter = new AtomicLong(0);

    public static void main( String[] args ) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);

        LOGGER.info("Hello World!");

        for (int i =0; i < NTHREADS; i++) {
            executor.execute(new MyRunnable());
        }

        long lastTime = System.currentTimeMillis();
        long lastCount = 0;

        while (true) {
            Thread.sleep(5000);

            long currentTime = System.currentTimeMillis();
            long currentCount = counter.get();
            long elapsedCount = currentCount - lastCount;
            double elapsedTime = (currentTime - lastTime)/1000;

            LOGGER.info("rate {}/{}: {} ", elapsedCount, elapsedTime, elapsedCount / elapsedTime);

            lastCount = currentCount;
            lastTime = currentTime;
        }
    }

    private static class MyRunnable implements Runnable {

        @Override
        public void run() {
            while (true) {
                rateLimiter.acquire();
                LOGGER.info("{} . ", Thread.currentThread().getName());
                counter.incrementAndGet();
            }
         }
    }
}
