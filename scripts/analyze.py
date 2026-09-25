import csv
import math
import statistics
from collections import defaultdict
from pathlib import Path

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt


ROOT = Path(__file__).resolve().parents[1]
RESULTS = ROOT / "results"
GROUPS = defaultdict(list)
ORDER = [
    "random_access", "search", "insert_front", "remove_front",
    "insert_middle", "remove_middle", "heap_insert", "heap_extract",
]
TITLES = {
    "random_access": "Random access", "search": "Search",
    "insert_front": "Front insertion", "remove_front": "Front removal",
    "insert_middle": "Middle insertion", "remove_middle": "Middle removal",
    "heap_insert": "Heap insertion", "heap_extract": "Heap extraction",
}
COLORS = {"DynamicArray": "#2563eb", "LinkedList": "#d97706", "MinHeap": "#15803d"}


def complexity(workload, structure):
    if workload == "heap_insert":
        return "Theta(n) expected random distinct order; O(n log n) worst"
    if workload == "heap_extract":
        return "O(n log n)"
    if workload == "random_access":
        return "Theta(m)" if structure == "DynamicArray" else "Theta(mn) expected"
    if workload == "search":
        return "Theta(mn) expected"
    if workload.startswith("insert"):
        if structure == "DynamicArray":
            return "Theta(mn + m^2)"
        return "Theta(m)" if workload.endswith("front") else "Theta(mn)"
    if structure == "LinkedList" and workload.endswith("front"):
        return "Theta(m)"
    return "Theta(mn) under batching rule"


def expected_movements(workload, n):
    index = n // 2 if workload.endswith("middle") else 0
    if workload.startswith("insert"):
        capacity = 8
        while capacity < n:
            capacity *= 2
        copied = 0
        while capacity < n + 1000:
            copied += capacity
            capacity *= 2
        return 1000 * (n - index) + 1000 * 999 // 2 + copied
    remaining = 1000
    movements = 0
    while remaining:
        count = min(remaining, n - index)
        movements += count * (n - index - 1) - count * (count - 1) // 2
        remaining -= count
    return movements


with (RESULTS / "tables" / "raw.csv").open(newline="", encoding="utf-8") as source:
    for row in csv.DictReader(source):
        for field in row.keys() - {"workload", "structure"}:
            row[field] = int(row[field])
        GROUPS[(row["workload"], row["structure"], row["n"])].append(row)

expected_keys = {
    (workload, structure, n)
    for workload in ORDER
    for structure in (["MinHeap"] if workload.startswith("heap") else ["DynamicArray", "LinkedList"])
    for n in [100, 1000, 10000, 100000]
}
assert set(GROUPS) == expected_keys
summary = []
for (workload, structure, n), rows in sorted(GROUPS.items(), key=lambda item: (ORDER.index(item[0][0]), item[0][2], item[0][1])):
    assert len(rows) == 5 and {row["repetition"] for row in rows} == set(range(1, 6))
    for field in ["accesses", "movements", "comparisons", "batches", "checksum", "m"]:
        assert len({row[field] for row in rows}) == 1, (workload, structure, n, field)
    first = rows[0]
    assert all(row["elapsed_ns"] > 0 for row in rows)
    assert first["m"] == (n if workload.startswith("heap") else 10000 if workload == "random_access" else 1000)
    index = n // 2 if workload.endswith("middle") else 0
    if workload.startswith("remove"):
        assert first["batches"] == math.ceil(1000 / (n - index))
    else:
        assert first["batches"] == 1
    if workload == "random_access" and structure == "DynamicArray":
        assert first["accesses"] == 10000
    if workload == "search":
        assert first["checksum"] == 500
        assert first["comparisons"] == first["accesses"]
        other = "LinkedList" if structure == "DynamicArray" else "DynamicArray"
        assert first["comparisons"] == GROUPS[(workload, other, n)][0]["comparisons"]
    if workload.startswith(("insert", "remove")):
        if structure == "DynamicArray":
            assert first["movements"] == expected_movements(workload, n)
            assert first["accesses"] == first["movements"] + (1000 if workload.startswith("remove") else 0)
        else:
            assert first["movements"] == 0
            assert first["accesses"] == 1000 * (index + (1 if workload.startswith("remove") else 0))
    times = [row["elapsed_ns"] / 1000000 for row in rows]
    summary.append({
        "workload": workload, "structure": structure, "n": n, "m": first["m"],
        "mean_ms": statistics.mean(times), "stdev_ms": statistics.stdev(times),
        "min_ms": min(times), "max_ms": max(times),
        "accesses": first["accesses"], "movements": first["movements"],
        "comparisons": first["comparisons"], "batches": first["batches"],
        "theory": complexity(workload, structure),
    })

with (RESULTS / "tables" / "summary.csv").open("w", newline="", encoding="utf-8") as target:
    writer = csv.DictWriter(target, fieldnames=list(summary[0]))
    writer.writeheader()
    writer.writerows(summary)

lines = ["# Measured results", "", "Each mean and sample standard deviation uses five measured repetitions. Counts are identical across all five repetitions; they are totals per workload run, not per operation. Times are milliseconds. Theory describes the entire timed workload; m is shown explicitly.", ""]
for workload in ORDER:
    lines.extend([
        "## " + TITLES[workload], "",
        "| Structure | n | m | Mean ms | SD ms | Accesses | Movements | Comparisons | Batches | Workload theory |",
        "|---|---:|---:|---:|---:|---:|---:|---:|---:|---|",
    ])
    for row in summary:
        if row["workload"] == workload:
            lines.append(f"| {row['structure']} | {row['n']:,} | {row['m']:,} | {row['mean_ms']:.6f} | {row['stdev_ms']:.6f} | {row['accesses']:,} | {row['movements']:,} | {row['comparisons']:,} | {row['batches']} | {row['theory']} |")
    lines.append("")
(RESULTS / "tables" / "results.md").write_text("\n".join(lines), encoding="utf-8")
readme_path = ROOT / "README.md"
if readme_path.exists():
    readme = readme_path.read_text(encoding="utf-8")
    start_marker = "<!-- RESULTS_START -->"
    end_marker = "<!-- RESULTS_END -->"
    if start_marker in readme and end_marker in readme:
        prefix, remainder = readme.split(start_marker, 1)
        _, suffix = remainder.split(end_marker, 1)
        tables = "\n".join(line.replace("## ", "### ", 1) if line.startswith("## ") else line for line in lines[2:])
        readme_path.write_text(prefix + start_marker + "\n\n" + tables + "\n" + end_marker + suffix, encoding="utf-8")

plt.rcParams.update({"font.size": 10, "axes.spines.top": False, "axes.spines.right": False})
(RESULTS / "plots").mkdir(exist_ok=True)
for metric in ["time", "operations"]:
    figure, axes = plt.subplots(2, 4, figsize=(18, 9), constrained_layout=True)
    for axis, workload in zip(axes.flat, ORDER):
        structures = ["MinHeap"] if workload.startswith("heap") else ["DynamicArray", "LinkedList"]
        for structure in structures:
            points = sorted([row for row in summary if row["workload"] == workload and row["structure"] == structure], key=lambda row: row["n"])
            x = [row["n"] for row in points]
            if metric == "time":
                y = [row["mean_ms"] for row in points]
                axis.plot(x, y, "o-", label=structure, color=COLORS[structure])
                axis.fill_between(x, [row["min_ms"] for row in points], [row["max_ms"] for row in points], color=COLORS[structure], alpha=0.12)
            else:
                field = "comparisons" if workload == "search" or workload.startswith("heap") else "movements" if structure == "DynamicArray" and workload.startswith(("insert", "remove")) else "accesses"
                y = [row[field] for row in points]
                axis.plot(x, y, "o-", label=f"{structure}: {field}", color=COLORS[structure])
        axis.set_xscale("log")
        axis.set_yscale("log" if metric == "time" else "symlog", **({"linthresh": 1} if metric == "operations" else {}))
        if metric == "operations":
            axis.set_ylim(bottom=0)
        axis.set_title(TITLES[workload])
        axis.set_xlabel("Initial size n")
        axis.set_ylabel("Total time (ms)" if metric == "time" else "Count per workload run")
        axis.grid(alpha=0.2, which="both")
        axis.legend(fontsize=8, loc="best")
    figure.suptitle("Execution time vs. n (mean; shaded range across 5 runs)" if metric == "time" else "Operations vs. n (zero shown on symmetric log scale)", fontsize=17)
    figure.savefig(RESULTS / "plots" / f"{metric}_vs_n.png", dpi=170)
    plt.close(figure)

print(f"Validated {sum(map(len, GROUPS.values()))} trials and wrote {len(summary)} summary rows, eight tables, and two plots.")
