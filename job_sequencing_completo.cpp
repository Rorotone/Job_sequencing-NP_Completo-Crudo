#include <iostream>
#include <vector>
#include <algorithm>
#include <chrono>

using namespace std;

struct Job {
    string id;
    int duration;
    int deadline;
    int profit;
};

bool isFeasible(vector<Job> subset) {
    sort(subset.begin(), subset.end(), [](const Job& a, const Job& b) {
        return a.deadline < b.deadline;
    });

    int t = 0;
    for (const auto& j : subset) {
        t += j.duration;
        if (t > j.deadline) return false;
    }
    return true;
}

pair<vector<Job>, int> bruteForce(const vector<Job>& jobs) {
    int n = jobs.size();
    int bestProfit = 0;
    vector<Job> bestSubset;

    for (int mask = 0; mask < (1 << n); mask++) {
        vector<Job> subset;
        int profit = 0;

        for (int i = 0; i < n; i++) {
            if (mask & (1 << i)) {
                subset.push_back(jobs[i]);
                profit += jobs[i].profit;
            }
        }

        if (isFeasible(subset) && profit > bestProfit) {
            bestProfit = profit;
            bestSubset = subset;
        }
    }

    sort(bestSubset.begin(), bestSubset.end(), [](const Job& a, const Job& b) {
        return a.deadline < b.deadline;
    });

    return {bestSubset, bestProfit};
}

pair<vector<Job>, int> dpExacta(vector<Job> jobs) {
    sort(jobs.begin(), jobs.end(), [](const Job& a, const Job& b) {
        return a.deadline < b.deadline;
    });

    int n = jobs.size();
    int maxDeadline = 0;

    for (const auto& j : jobs) {
        maxDeadline = max(maxDeadline, j.deadline);
    }

    vector<vector<int>> dp(n + 1, vector<int>(maxDeadline + 1, 0));
    vector<vector<bool>> tomar(n + 1, vector<bool>(maxDeadline + 1, false));

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

    vector<Job> seleccion;
    int t = bestTime;

    for (int i = n; i > 0; i--) {
        if (tomar[i][t]) {
            Job job = jobs[i - 1];
            seleccion.push_back(job);
            t -= job.duration;
        }
    }

    reverse(seleccion.begin(), seleccion.end());
    return {seleccion, bestProfit};
}

void imprimirSeleccion(const vector<Job>& seleccion) {
    for (const auto& j : seleccion) {
        cout << j.id << "(p=" << j.duration
             << ", d=" << j.deadline
             << ", w=" << j.profit << ") ";
    }
    cout << endl;
}

int main() {
    vector<Job> jobs = {
        {"A", 3, 4, 100},
        {"B", 2, 3, 19},
        {"C", 5, 7, 27},
        {"D", 1, 2, 25},
        {"E", 4, 6, 15},
        {"F", 2, 5, 30},
        {"G", 6, 8, 50},
        {"H", 3, 10, 60},
        {"I", 7, 9, 20},
        {"J", 4, 7, 45},
        {"K", 2, 4, 35},
        {"L", 5, 12, 80},
        {"M", 3, 5, 40},
        {"N", 4, 8, 55},
        {"O", 1, 3, 22}
    };

    cout << "=== JOB SEQUENCING CON DURACIONES VARIABLES - C++ ===\n";

    auto start = chrono::high_resolution_clock::now();
    auto fb = bruteForce(jobs);
    auto end = chrono::high_resolution_clock::now();
    double tFB = chrono::duration<double, milli>(end - start).count();

    start = chrono::high_resolution_clock::now();
    auto dp = dpExacta(jobs);
    end = chrono::high_resolution_clock::now();
    double tDP = chrono::duration<double, milli>(end - start).count();

    cout << "\nFuerza Bruta:\n";
    cout << "Seleccion: ";
    imprimirSeleccion(fb.first);
    cout << "Ganancia : " << fb.second << endl;
    cout << "Tiempo   : " << tFB << " ms\n";

    cout << "\nDP Exacta:\n";
    cout << "Seleccion: ";
    imprimirSeleccion(dp.first);
    cout << "Ganancia : " << dp.second << endl;
    cout << "Tiempo   : " << tDP << " ms\n";

    return 0;
}