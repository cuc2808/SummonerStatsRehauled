package app.concurrency;

import app.exceptions.RiotApiException;
import io.javalin.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class ParallelExecutor {
    // All clients share these four workers and the same bounded queue.
    public static final ParallelExecutor SHARED = new ParallelExecutor(
            new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(100),
                    Thread.ofPlatform().name("riot-match-", 0).daemon(true).factory(),
                    new ThreadPoolExecutor.AbortPolicy()));

    private final ExecutorService workers;

    ParallelExecutor(ExecutorService workers) {
        this.workers = workers;
    }

    public <T, R> List<R> map(List<T> items, Function<T, R> action) {
        List<Future<R>> tasks = new ArrayList<>();
        boolean completed = false;
        try {
            for (T item : items) {
                tasks.add(workers.submit(() -> action.apply(item)));
            }

            // Preserve input order, even when tasks finish out of order.
            List<R> results = new ArrayList<>();
            for (Future<R> task : tasks) {
                results.add(task.get());
            }
            completed = true;
            return results;
        } catch (RejectedExecutionException e) {
            throw new RiotApiException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Too many match requests. Please try again later.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RiotApiException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Match history request was interrupted.");
        } catch (ExecutionException e) {
            // Preserve API status codes and Retry-After for the exception handler.
            if (e.getCause() instanceof RuntimeException cause) {
                throw cause;
            }
            throw new RiotApiException(HttpStatus.BAD_GATEWAY,
                    "Could not retrieve match history.");
        } finally {
            if (!completed) {
                tasks.forEach(task -> task.cancel(true));
            }
        }
    }

    public void shutdown() {
        workers.shutdown();
    }
}
