#include <iostream>
#include <chrono>

using namespace std;

struct Job {
    char id;
    int duration;
    int deadline;
    int profit;
};

const int N = 15;
const int MAX_H = 100;

Job jobs[N] = {
    {'A', 3, 4, 100},
    {'B', 2, 3, 19},
    {'C', 5, 7, 27},
    {'D', 1, 2, 25},
    {'E', 4, 6, 15},
    {'F', 2, 5, 30},
    {'G', 6, 8, 50},
    {'H', 3, 10, 60},
    {'I', 7, 9, 20},
    {'J', 4, 7, 45},
    {'K', 2, 4, 35},
    {'L', 5, 12, 80},
    {'M', 3, 5, 40},
    {'N', 4, 8, 55},
    {'O', 1, 3, 22}
};

void copiar(Job origen[], Job destino[], int n) {
    for (int i = 0; i < n; i++) {
        destino[i] = origen[i];
    }
}

void ordenarPorDeadline(Job arr[], int n) {
    for (int i = 0; i < n - 1; i++) {
        for (int j = 0; j < n - i - 1; j++) {
            if (arr[j].deadline > arr[j + 1].deadline) {
                Job temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
            }
        }
    }
}

bool isFeasible(Job subset[], int size) {
    Job copia[N];
    copiar(subset, copia, size);
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

int calcularGanancia(Job subset[], int size) {
    int total = 0;

    for (int i = 0; i < size; i++) {
        total += subset[i].profit;
    }

    return total;
}

Job actual[N];
Job mejor[N];

int actualSize = 0;
int mejorSize = 0;
int mejorGanancia = 0;

void backtracking(int i) {
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

    // No tomar el trabajo i
    backtracking(i + 1);

    // Tomar el trabajo i
    actual[actualSize] = jobs[i];
    actualSize++;

    backtracking(i + 1);

    actualSize--;
}

void dpExacta(Job seleccion[], int& seleccionSize, int& gananciaFinal) {
    Job trabajos[N];
    copiar(jobs, trabajos, N);
    ordenarPorDeadline(trabajos, N);

    int H = 0;

    for (int i = 0; i < N; i++) {
        if (trabajos[i].deadline > H) {
            H = trabajos[i].deadline;
        }
    }

    int dp[N + 1][MAX_H + 1];
    bool tomar[N + 1][MAX_H + 1];

    for (int i = 0; i <= N; i++) {
        for (int t = 0; t <= H; t++) {
            dp[i][t] = 0;
            tomar[i][t] = false;
        }
    }

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

    Job temp[N];
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

    seleccionSize = 0;

    for (int i = tempSize - 1; i >= 0; i--) {
        seleccion[seleccionSize] = temp[i];
        seleccionSize++;
    }

    gananciaFinal = bestProfit;
}

void imprimir(Job arr[], int size) {
    for (int i = 0; i < size; i++) {
        cout << arr[i].id
             << "(p=" << arr[i].duration
             << ", d=" << arr[i].deadline
             << ", w=" << arr[i].profit << ") ";
    }
    cout << endl;
}

int main() {
    cout << "=== JOB SEQUENCING CON DURACIONES VARIABLES - C++ ===\n";

    auto start = chrono::high_resolution_clock::now();

    backtracking(0);

    auto end = chrono::high_resolution_clock::now();
    double tiempoFB = chrono::duration<double, milli>(end - start).count();

    ordenarPorDeadline(mejor, mejorSize);

    Job seleccionDP[N];
    int seleccionDPSize = 0;
    int gananciaDP = 0;

    start = chrono::high_resolution_clock::now();

    dpExacta(seleccionDP, seleccionDPSize, gananciaDP);

    end = chrono::high_resolution_clock::now();
    double tiempoDP = chrono::duration<double, milli>(end - start).count();

    cout << "\nFuerza Bruta / Backtracking:\n";
    cout << "Seleccion: ";
    imprimir(mejor, mejorSize);
    cout << "Ganancia : " << mejorGanancia << endl;
    cout << "Tiempo   : " << tiempoFB << " ms\n";

    cout << "\nDP Exacta:\n";
    cout << "Seleccion: ";
    imprimir(seleccionDP, seleccionDPSize);
    cout << "Ganancia : " << gananciaDP << endl;
    cout << "Tiempo   : " << tiempoDP << " ms\n";

    return 0;
}
