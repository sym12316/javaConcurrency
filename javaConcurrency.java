import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class javaConcurrency {
    public static void main(String[] args) {
        int n = 12; //потоки
        List<Result> results = new ArrayList<>();

        for (int threads = 1; threads <= n; threads++) {
            long startTime = System.currentTimeMillis();

            double a = 0; 
            double b = 4*Math.PI; 
            int intervals = 100000000; // интервалы

            double dx = (b - a) / intervals; 

            List<Thread> threadList = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                int startIndex = i * (intervals / threads);
                int endIndex = (i+1) * (intervals / threads);

                Thread thread = new Thread(new IntegrationTask(startIndex, endIndex, dx));
                threadList.add(thread);
                thread.start();
            }

            for (Thread thread : threadList) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            double sum = 0;
            for (IntegrationTask task : IntegrationTask.tasks) {
                sum += task.getResult();
            }

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            Result result = new Result(sum, threads, executionTime);
            results.add(result);
        }
        // Collections.sort(results, Comparator.comparingLong(Result::getExecutionTime));

        for (Result result : results) {
            System.out.printf("Поток - %2d результат - %9.20f время - %6d\n", result.getThreads(), result.getResult(), result.getExecutionTime());
        }
    }

    static class IntegrationTask implements Runnable {
        private static List<IntegrationTask> tasks = new ArrayList<>();
        private int startIndex;
        private int endIndex;
        private double dx;
        private double result;

        public IntegrationTask(int startIndex, int endIndex, double dx) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.dx = dx;
            tasks.add(this);
        }

        public double getResult() {
            return result;
        }

        @Override
        public void run() {
            double sum = 0;
            for (int i = startIndex; i < endIndex; i++) {
                double x = i * dx;
                double y = calculateFunction(x);
                sum += y;
            }
            result = sum * dx;
        }

        private double calculateFunction(double x) {
            return 2*Math.sin(x);
        }
    }

    static class Result {
        private double result;
        private int threads;
        private long executionTime;

        public Result(double result, int threads, long executionTime) {
            this.result = result;
            this.threads = threads;
            this.executionTime = executionTime;
            
}

public double getResult() {
    return result;
}

public int getThreads() {
    return threads;
}

public long getExecutionTime() {
    return executionTime;
}
}
}