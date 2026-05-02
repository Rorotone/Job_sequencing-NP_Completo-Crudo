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


def ordenar_por_deadline(lista):
    lista = lista[:]
    n = len(lista)

    for i in range(n):
        for j in range(0, n - i - 1):
            if lista[j].deadline > lista[j + 1].deadline:
                lista[j], lista[j + 1] = lista[j + 1], lista[j]

    return lista


def is_feasible(subset):
    subset = ordenar_por_deadline(subset)

    tiempo = 0
    for job in subset:
        tiempo += job.duration
        if tiempo > job.deadline:
            return False

    return True


def calcular_ganancia(subset):
    total = 0
    for job in subset:
        total += job.profit
    return total


def brute_force_backtracking(jobs):
    mejor = {
        "profit": 0,
        "subset": []
    }

    def backtrack(i, actual):
        if i == len(jobs):
            if is_feasible(actual):
                ganancia = calcular_ganancia(actual)
                if ganancia > mejor["profit"]:
                    mejor["profit"] = ganancia
                    mejor["subset"] = actual[:]
            return

        backtrack(i + 1, actual)

        actual.append(jobs[i])
        backtrack(i + 1, actual)
        actual.pop()

    backtrack(0, [])

    mejor["subset"] = ordenar_por_deadline(mejor["subset"])
    return mejor["subset"], mejor["profit"]


def dp_exacta(jobs):
    trabajos = ordenar_por_deadline(jobs)

    n = len(trabajos)

    H = 0
    for job in trabajos:
        if job.deadline > H:
            H = job.deadline

    dp = []
    tomar = []

    for i in range(n + 1):
        fila_dp = []
        fila_tomar = []
        for t in range(H + 1):
            fila_dp.append(0)
            fila_tomar.append(False)
        dp.append(fila_dp)
        tomar.append(fila_tomar)

    for i in range(1, n + 1):
        job = trabajos[i - 1]

        for t in range(H + 1):
            dp[i][t] = dp[i - 1][t]

            if job.duration <= t and t <= job.deadline:
                valor = dp[i - 1][t - job.duration] + job.profit

                if valor > dp[i][t]:
                    dp[i][t] = valor
                    tomar[i][t] = True

    best_time = 0
    best_profit = 0

    for t in range(H + 1):
        if dp[n][t] > best_profit:
            best_profit = dp[n][t]
            best_time = t

    seleccion = []
    t = best_time

    for i in range(n, 0, -1):
        if tomar[i][t]:
            job = trabajos[i - 1]
            seleccion.append(job)
            t -= job.duration

    seleccion = seleccion[::-1]
    return seleccion, best_profit


if __name__ == "__main__":
    print("=== JOB SEQUENCING CRUDO - PYTHON ===")

    inicio = time.perf_counter()
    sol_fb, val_fb = brute_force_backtracking(jobs)
    t_fb = (time.perf_counter() - inicio) * 1000

    inicio = time.perf_counter()
    sol_dp, val_dp = dp_exacta(jobs)
    t_dp = (time.perf_counter() - inicio) * 1000

    print("\nFuerza Bruta / Backtracking:")
    print("Seleccion:", sol_fb)
    print("Ganancia :", val_fb)
    print(f"Tiempo   : {t_fb:.6f} ms")

    print("\nDP Exacta:")
    print("Seleccion:", sol_dp)
    print("Ganancia :", val_dp)
    print(f"Tiempo   : {t_dp:.6f} ms")
