import itertools
import time

class Job:
    def __init__(self, id, duration, deadline, profit):
        self.id = id
        self.duration = duration
        self.deadline = deadline
        self.profit = profit

    def __repr__(self):
        return f"{self.id}(p={self.duration}, d={self.deadline}, w={self.profit})"


jobs = [
    Job("A", 3, 4, 100),
    Job("B", 2, 3, 19),
    Job("C", 5, 7, 27),
    Job("D", 1, 2, 25),
    Job("E", 4, 6, 15),
    Job("F", 2, 5, 30),
    Job("G", 6, 8, 50),
    Job("H", 3, 10, 60),
    Job("I", 7, 9, 20),
    Job("J", 4, 7, 45),
    Job("K", 2, 4, 35),
    Job("L", 5, 12, 80),
    Job("M", 3, 5, 40),
    Job("N", 4, 8, 55),
    Job("O", 1, 3, 22)
]


def is_feasible(subset):
    subset = sorted(subset, key=lambda j: j.deadline)
    t = 0
    for j in subset:
        t += j.duration
        if t > j.deadline:
            return False
    return True


def brute_force(jobs):
    best_profit = 0
    best_subset = []

    n = len(jobs)

    for r in range(n + 1):
        for subset in itertools.combinations(jobs, r):
            if is_feasible(subset):
                profit = sum(j.profit for j in subset)
                if profit > best_profit:
                    best_profit = profit
                    best_subset = list(subset)

    best_subset = sorted(best_subset, key=lambda j: j.deadline)
    return best_subset, best_profit


def dp_exacta(jobs):
    jobs = sorted(jobs, key=lambda j: j.deadline)

    n = len(jobs)
    max_deadline = max(j.deadline for j in jobs)

    dp = [[0] * (max_deadline + 1) for _ in range(n + 1)]
    tomar = [[False] * (max_deadline + 1) for _ in range(n + 1)]

    for i in range(1, n + 1):
        job = jobs[i - 1]

        for t in range(max_deadline + 1):
            dp[i][t] = dp[i - 1][t]

            if job.duration <= t and t <= job.deadline:
                valor = dp[i - 1][t - job.duration] + job.profit

                if valor > dp[i][t]:
                    dp[i][t] = valor
                    tomar[i][t] = True

    best_time = max(range(max_deadline + 1), key=lambda t: dp[n][t])
    best_profit = dp[n][best_time]

    seleccion = []
    t = best_time

    for i in range(n, 0, -1):
        if tomar[i][t]:
            job = jobs[i - 1]
            seleccion.append(job)
            t -= job.duration

    seleccion.reverse()
    return seleccion, best_profit


if __name__ == "__main__":
    print("=== JOB SEQUENCING CON DURACIONES VARIABLES ===")

    inicio = time.perf_counter()
    sol_fb, val_fb = brute_force(jobs)
    t_fb = (time.perf_counter() - inicio) * 1000

    inicio = time.perf_counter()
    sol_dp, val_dp = dp_exacta(jobs)
    t_dp = (time.perf_counter() - inicio) * 1000

    print("\nFuerza Bruta:")
    print("Selección:", sol_fb)
    print("Ganancia :", val_fb)
    print(f"Tiempo   : {t_fb:.6f} ms")

    print("\nDP Exacta:")
    print("Selección:", sol_dp)
    print("Ganancia :", val_dp)
    print(f"Tiempo   : {t_dp:.6f} ms")