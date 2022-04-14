import numpy as np

N = 1000 # exactly N nodes
M = 500 # at most M edges, M <= N * N

if M <= N * N:
    _ = np.split(np.random.choice(N, size=N, replace=False), [np.random.randint(N)])
    i, j = _[0], _[1]
    M = max(M, max(i.shape[0], j.shape[0])) # adjust M
    i = np.hstack((i, np.random.choice(N, size=M - i.shape[0], replace=True)))
    j = np.hstack((j, np.random.choice(N, size=M - j.shape[0], replace=True)))
    with open("in.csv", "w") as f:
        f.write("\n".join(map(lambda z: f"{z[0]},{z[1]}", zip(i.tolist(), j.tolist()))))
    A = np.full((N, N), 0)
    A[i, j] = 1
    A_2 = np.where(np.matmul(A, A) != 0, 1, 0)
    A_4 = np.where(np.matmul(A_2, A_2) != 0, 1, 0)
    (i, j) = np.where(A_4 == 1)
    with open("ref.txt", "w") as f:
        f.write(f"(Total Vertices,{N})\n")
        f.write(f"Set(\n")
        f.write(",".join(map(lambda z: f"({z[0]},{z[1]})", zip(i.tolist(), j.tolist()))))
        f.write(f"\n)\n")