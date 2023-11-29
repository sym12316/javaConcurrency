import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class lb1 {
    public static void main(String[] args) {
        int n = 12; // количество потоков
        List<Result> results = new ArrayList<>(); // список для хранения результатов

        for (int threads = 1; threads <= n; threads++) { // запускаем цикл для разного количества потоков

            long startTime = System.currentTimeMillis(); // засекаем время начала выполнения

            double a = 0; // первая точка
            double b = 3.141592; // последняя точка
            int intervals = 100000000; // количество интервалов для интегрирования

            double dx = (b - a) / intervals; // ширина каждого интервала

            List<Thread> threadList = new ArrayList<>(); // список для хранения потоков

            for (int i = 0; i < threads; i++) {
                int startIndex = i * (intervals / threads); // начальный индекс для текущего потока
                int endIndex = (i + 1) * (intervals / threads); // конечный индекс для текущего потока

                // Создаем поток и добавляем его в список
                Thread thread = new Thread(new IntegrationTask(startIndex, endIndex, dx));
                threadList.add(thread);
                thread.start();
            }

            for (Thread thread : threadList) {
                try {
                    thread.join(); // ожидаем завершения каждого потока
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            double sum = 0;
            for (IntegrationTask task : IntegrationTask.tasks) {
                sum += task.getResult(); // суммируем результаты вычислений каждого потока
            }

            long endTime = System.currentTimeMillis(); // засекаем время окончания выполнения
            long executionTime = endTime - startTime; // вычисляем время выполнения

            Result result = new Result(sum, threads, executionTime); // создаем объект с результатами
            results.add(result); // добавляем результаты в список
        }
        Collections.sort(results, Comparator.comparingLong(Result::getExecutionTime)); // сортируем результаты по времени выполнения в порядке возрастания

        for (Result result : results) { // выводим результаты
            System.out.printf("Поток - %2d результат - %9.20f время - %6d\n", result.getThreads(), result.getResult(), result.getExecutionTime());
        }
    }

    static class IntegrationTask implements Runnable {
        private static List<IntegrationTask> tasks = new ArrayList<>(); // список для хранения задач интегрирования
        private int startIndex;
        private int endIndex;
        private double dx;
        private double result;

        public IntegrationTask(int startIndex, int endIndex, double dx) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.dx = dx;
            tasks.add(this); // добавляем текущую задачу в список задач
        }

        public double getResult() {
            return result; // возвращает результат задачи
        }

        @Override
        public void run() {
            double sum = 0;
            for (int i = startIndex; i < endIndex; i++) {
                double x = i * dx;
                double y = calculateFunction(x); // вычисляем значение функции для текущей точки
                sum += y; // суммируем значения функции
            }
            result = sum * dx; // умножаем сумму на ширину интервала
        }
                private double calculateFunction(double x) {
            return Math.sin(x); // вычисляем значение функции sin(x)
        }
    }

    static class Result {
        private double result; // результат интегрирования
        private int threads; // количество потоков
        private long executionTime; // время выполнения

        public Result(double result, int threads, long executionTime) {
            this.result = result; // инициализация результатов
            this.threads = threads;
            this.executionTime = executionTime;
        }

        public double getResult() {
            return result; // возвращает результат интегрирования
        }

        public int getThreads() {
            return threads; // возвращает количество потоков
        }

        public long getExecutionTime() {
            return executionTime; // возвращает время выполнения
        }
    }
}