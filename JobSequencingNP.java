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

        public String toString() {
            return id + "(p=" + duration + ", d=" + deadline + ", w=" + profit + ")";
        }
    }

    static final int N = 15;
    static final int MAX_H = 100;

    static Job[] jobs = {
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

    static Job[] actual = new Job[N];
    static int actualSize = 0;

    static Job[] mejor = new Job[N];
    static int mejorSize = 0;
    static int mejorGanancia = 0;

    static void copiar(Job[] origen, Job[] destino, int n) {
        for (int i = 0; i < n; i++) {
            destino[i] = origen[i];
        }
    }

    static void ordenarPorDeadline(Job[] arr, int size) {
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (arr[j].deadline > arr[j + 1].deadline) {
                    Job temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    static boolean isFeasible(Job[] subset, int size) {
        Job[] copia = new Job[N];

        for (int i = 0; i < size; i++) {
            copia[i] = subset[i];
        }

        ordenarPorDeadline(copia, size);

        int tiempo = 0;

        for (int i = 0; i < size; i++) {
            tiempo += copia[i].duration;

            if (tiempo > copia[i].deadline) {
                return false;
            }
        }

        return true;
    }

    static int calcularGanancia(Job[] subset, int size) {
        int total = 0;

        for (int i = 0; i < size; i++) {
            total += subset[i].profit;
        }

        return total;
    }

    static void backtracking(int i) {
        if (i == N) {
            if (isFeasible(actual, actualSize)) {
                int ganancia = calcularGanancia(actual, actualSize);

                if (ganancia > mejorGanancia) {
                    mejorGanancia = ganancia;
                    mejorSize = actualSize;

                    for (int k = 0; k < actualSize; k++) {
                        mejor[k] = actual[k];
                    }
                }
            }
            return;
        }

        // No tomar
        backtracking(i + 1);

        // Tomar
        actual[actualSize] = jobs[i];
        actualSize++;

        backtracking(i + 1);

        actualSize--;
    }

    static class ResultadoDP {
        Job[] seleccion = new Job[N];
        int seleccionSize = 0;
        int ganancia = 0;
    }

    static ResultadoDP dpExacta() {
        Job[] trabajos = new Job[N];
        copiar(jobs, trabajos, N);
        ordenarPorDeadline(trabajos, N);

        int H = 0;
        for (int i = 0; i < N; i++) {
            if (trabajos[i].deadline > H) {
                H = trabajos[i].deadline;
            }
        }

        int[][] dp = new int[N + 1][H + 1];
        boolean[][] tomar = new boolean[N + 1][H + 1];

        for (int i = 1; i <= N; i++) {
            Job job = trabajos[i - 1];

            for (int t = 0; t <= H; t++) {
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

        for (int t = 0; t <= H; t++) {
            if (dp[N][t] > bestProfit) {
                bestProfit = dp[N][t];
                bestTime = t;
            }
        }

        Job[] temp = new Job[N];
        int tempSize = 0;
        int t = bestTime;

        for (int i = N; i > 0; i--) {
            if (tomar[i][t]) {
                Job job = trabajos[i - 1];
                temp[tempSize] = job;
                tempSize++;
                t -= job.duration;
            }
        }

        ResultadoDP res = new ResultadoDP();
        res.ganancia = bestProfit;

        for (int i = tempSize - 1; i >= 0; i--) {
            res.seleccion[res.seleccionSize] = temp[i];
            res.seleccionSize++;
        }

        return res;
    }

    static void imprimir(Job[] arr, int size) {
        for (int i = 0; i < size; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("=== JOB SEQUENCING CRUDO - JAVA ===");

        long start = System.nanoTime();

        backtracking(0);

        long end = System.nanoTime();
        double tiempoFB = (end - start) / 1_000_000.0;

        ordenarPorDeadline(mejor, mejorSize);

        start = System.nanoTime();

        ResultadoDP dp = dpExacta();

        end = System.nanoTime();
        double tiempoDP = (end - start) / 1_000_000.0;

        System.out.println("\nFuerza Bruta / Backtracking:");
        System.out.print("Seleccion: ");
        imprimir(mejor, mejorSize);
        System.out.println("Ganancia : " + mejorGanancia);
        System.out.printf("Tiempo   : %.6f ms%n", tiempoFB);

        System.out.println("\nDP Exacta:");
        System.out.print("Seleccion: ");
        imprimir(dp.seleccion, dp.seleccionSize);
        System.out.println("Ganancia : " + dp.ganancia);
        System.out.printf("Tiempo   : %.6f ms%n", tiempoDP);
    }
}
