"""
Script đọc kết quả đo từ benchmark_results.csv và vẽ đồ thị trực quan.
Sử dụng matplotlib để vẽ biểu đồ và lưu vào thư mục report/figures/.
"""

import os
import csv

def main():
    csv_file = os.path.join("data", "results", "benchmark_results.csv")
    output_dir = os.path.join("report", "figures")

    if not os.path.exists(csv_file):
        print(f"[!] Không tìm thấy file dữ liệu: {csv_file}")
        print("    Vui lòng chạy BenchmarkRunner trước để sinh dữ liệu!")
        return

    os.makedirs(output_dir, exist_ok=True)

    try:
        import matplotlib.pyplot as plt
    except ImportError:
        print("[!] Chưa cài đặt thư viện 'matplotlib'.")
        print("    Bạn có thể cài đặt bằng lệnh: pip install matplotlib")
        return

    # Đọc dữ liệu từ file CSV
    data_by_n = {}
    with open(csv_file, mode="r", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            n = int(row["N"])
            cutoff = int(row["Cutoff"])
            mean_time = float(row["MeanTimeMs"])
            std_dev = float(row["StdDevMs"])

            if n not in data_by_n:
                data_by_n[n] = []
            data_by_n[n].append((cutoff, mean_time, std_dev))

    # Vẽ đồ thị cho từng kích thước N
    plt.figure(figsize=(10, 6))

    for n, records in data_by_n.items():
        records.sort(key=lambda x: x[0])
        cutoffs = [r[0] for r in records]
        times = [r[1] for r in records]
        errors = [r[2] for r in records]

        plt.errorbar(
            cutoffs, 
            times, 
            yerr=errors, 
            marker="o", 
            capsize=4, 
            label=f"N = {n:,}"
        )

    # Đánh dấu các mốc CUTOFF đặc biệt trong giáo trình
    plt.axvline(x=7, color="orange", linestyle="--", alpha=0.7, label="Cutoff = 7 (Cũ)")
    plt.axvline(x=10, color="green", linestyle="--", alpha=0.7, label="Cutoff = 10 (Mới)")

    plt.title("Khảo sát thời gian thực thi MergeSort theo giá trị CUTOFF", fontsize=14, fontweight="bold")
    plt.xlabel("Giá trị CUTOFF (Kích thước mảng con chuyển sang InsertionSort)", fontsize=11)
    plt.ylabel("Thời gian thực thi trung bình (ms)", fontsize=11)
    plt.grid(True, linestyle=":", alpha=0.6)
    plt.legend(fontsize=10)
    plt.tight_layout()

    output_path = os.path.join(output_dir, "cutoff_vs_time.png")
    plt.savefig(output_path, dpi=300)
    print(f"[✓] Đã xuất biểu đồ thành công: {output_path}")

if __name__ == "__main__":
    main()
