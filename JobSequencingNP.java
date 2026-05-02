import java.util.*;

public class JobSequencingNP {

    static class Job {
        String id;
        int duration;
        int deadline;
        int profit;

        Job(String id, int duration, int deadline, int profit) {
            this.id = id;
            this.duration = duration;
            this.deadline = deadline;
            this.profit = profit;
        }

        @Override
        public String toString() {
            return id + "(p=" + duration + ", d=" + deadline + ", w=" + profit + ")";
        }
    }

    static class Resultado {
        List<Job> seleccion;
        int ganancia;

        Resultado(List<Job> seleccion, int ganancia) {
            this.seleccion = seleccion;
            this.ganancia = ganancia;
        }
    }

    static boolean isFeasible(List<Job> subset) {
        subset.sort(Comparator.comparingInt(j -> j.deadline));

        int t = 0;
        for (Job j : subset) {
            t += j.duration;
            if (t > j.deadline) return false;
        }
        return true;
    }

    static Resultado bruteForce(Job[] jobs) {
        int n = jobs.length;
        int bestProfit = 0;
        List<Job> bestSubset = new ArrayList<>();

        for (int mask = 0; mask < (1 << n); mask++) {
            List<Job> subset = new ArrayList<>();
            int profit = 0;

            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    subset.add(jobs[i]);
                    profit += jobs[i].profit;
                }
            }

            if (isFeasible(subset) && profit > bestProfit) {
                bestProfit = profit;
                bestSubset = new ArrayList<>(subset);
            }
        }

        bestSubset.sort(Comparator.comparingInt(j -> j.deadline));
        return new Resultado(bestSubset, bestProfit);
    }

    static Resultado dpExacta(Job[] originalJobs) {
        Job[] jobs = originalJobs.clone();
        Arrays.sort(jobs, Comparator.comparingInt(j -> j.deadline));

        int n = jobs.length;
        int maxDeadline = 0;

        for (Job j : jobs) {
            maxDeadline = Math.max(maxDeadline, j.deadline);
        }

        int[][] dp = new int[n + 1][maxDeadline + 1];
        boolean[][] tomar = new boolean[n + 1][maxDeadline + 1];

        for (int i = 1; i <= n; i++) {
            Job job = jobs[i - 1];

            for (int t = 0; t <= maxDeadline; t++) {
                dp[i][t] = dp[i - 1][t];

                if (job.duration <= t && t <= job.deadline) {
                    int valor = dp[i - 1][t - job.duration] + job.profit;

                    if (valor > dp[i][t]) {
                        dp[i][t] = valor;
                        tomar[i][t] = true;
                    }
                }
            }
        }

        int bestTime = 0;
        int bestProfit = 0;

        for (int t = 0; t <= maxDeadline; t++) {
            if (dp[n][t] > bestProfit) {
                bestProfit = dp[n][t];
                bestTime = t;
            }
        }

        List<Job> seleccion = new ArrayList<>();
        int t = bestTime;

        for (int i = n; i > 0; i--) {
            if (tomar[i][t]) {
                Job job = jobs[i - 1];
                seleccion.add(job);
                t -= job.duration;
            }
        }

        Collections.reverse(seleccion);
        return new Resultado(seleccion, bestProfit);
    }

    public static void main(String[] args) {
        Job[] jobs = {
            new Job("A", 3, 4, 100),
            new Job("B", 2, 3, 19),
            new Job("C", 5, 7, 27),
            new Job("D", 1, 2, 25),
            new Job("E", 4, 6, 15),
            new Job("F", 2, 5, 30),
            new Job("G", 6, 8, 50),
            new Job("H", 3, 10, 60),
            new Job("I", 7, 9, 20),
            new Job("J", 4, 7, 45),
            new Job("K", 2, 4, 35),
            new Job("L", 5, 12, 80),
            new Job("M", 3, 5, 40),
            new Job("N", 4, 8, 55),
            new Job("O", 1, 3, 22)
        };

        System.out.println("=== JOB SEQUENCING CON DURACIONES VARIABLES - JAVA ===");

        long start = System.nanoTime();
        Resultado fb = bruteForce(jobs);
        long end = System.nanoTime();
        double tFB = (end - start) / 1_000_000.0;

        start = System.nanoTime();
        Resultado dp = dpExacta(jobs);
        end = System.nanoTime();
        double tDP = (end - start) / 1_000_000.0;

        System.out.println("\nFuerza Bruta:");
        System.out.println("Seleccion: " + fb.seleccion);
        System.out.println("Ganancia : " + fb.ganancia);
        System.out.printf("Tiempo   : %.6f ms%n", tFB);

        System.out.println("\nDP Exacta:");
        System.out.println("Seleccion: " + dp.seleccion);
        System.out.println("Ganancia : " + dp.ganancia);
        System.out.printf("Tiempo   : %.6f ms%n", tDP);
    }
}