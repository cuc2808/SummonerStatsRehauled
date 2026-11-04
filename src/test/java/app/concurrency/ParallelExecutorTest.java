package app.concurrency;

import app.exceptions.RiotApiException;
import io.javalin.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(10)
class ParallelExecutorTest {
    @Test
    void fetchesMatchesInParallelWithFourWorkersAndPreservesOrder() throws Exception {
        var workers = Executors.newFixedThreadPool(4);
        var caller = Executors.newSingleThreadExecutor();
        var firstFourStarted = new CountDownLatch(4);
        var release = new CountDownLatch(1);
        var active = new AtomicInteger();
        var peak = new AtomicInteger();
        List<Integer> matches = List.of(0, 1, 2, 3, 4);
        ParallelExecutor executor = new ParallelExecutor(workers);

        try {
            var result = caller.submit(() -> executor.map(matches, matchId -> {
                peak.accumulateAndGet(active.incrementAndGet(), Math::max);
                firstFourStarted.countDown();
                try {
                    assertTrue(release.await(5, TimeUnit.SECONDS));
                    return matchId;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError(e);
                } finally {
                    active.decrementAndGet();
                }
            }));
            assertTrue(firstFourStarted.await(5, TimeUnit.SECONDS));
            release.countDown();
            assertEquals(matches, result.get(5, TimeUnit.SECONDS));
            assertEquals(4, peak.get());
        } finally {
            release.countDown();
            caller.shutdownNow();
            workers.shutdownNow();
        }
    }

    @Test
    void preservesRiotExceptionAndRetryAfter() {
        var workers = Executors.newSingleThreadExecutor();
        RiotApiException expected = new RiotApiException(
                HttpStatus.TOO_MANY_REQUESTS, "Rate limited", 10L);
        ParallelExecutor executor = new ParallelExecutor(workers);

        try {
            assertSame(expected, assertThrows(RiotApiException.class,
                    () -> executor.map(List.of("1"), id -> { throw expected; })));
        } finally {
            workers.shutdownNow();
        }
    }

    @Test
    void returnsServiceUnavailableWhenQueueIsFull() {
        var workers = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1));
        var release = new CountDownLatch(1);
        Runnable blockingTask = () -> {
            try {
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        try {
            workers.submit(blockingTask);
            workers.submit(blockingTask);
            ParallelExecutor executor = new ParallelExecutor(workers);
            RiotApiException exception = assertThrows(RiotApiException.class,
                    () -> executor.map(List.of("1"), id -> id));
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE.getCode(), exception.getCode());
        } finally {
            release.countDown();
            workers.shutdownNow();
        }
    }
}
