import os
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

def main():
    csv_file = os.path.join("data", "results", "benchmark_results.csv")
    output_dir = os.path.join("report", "figures")

    if not os.path.exists(csv_file):
        print(f"[!] Khong tim thay file: {csv_file}")
        return

    os.makedirs(output_dir, exist_ok=True)

    # Đọc dữ liệu thực nghiệm bằng pandas
    df = pd.read_csv(csv_file)

    # Cấu hình phong cách biểu đồ khoa học với Seaborn
    sns.set_theme(style="whitegrid", font="DejaVu Sans")
    plt.figure(figsize=(10, 6))

    # Vẽ đường biểu diễn theo từng kích thước N kèm thanh sai số (error bar)
    for n, group in df.groupby("N"):
        sorted_group = group.sort_values("Cutoff")
        plt.errorbar(
            sorted_group["Cutoff"],
            sorted_group["MeanTimeMs"],
            yerr=sorted_group["StdDevMs"],
            marker="o",
            capsize=4,
            linewidth=2,
            label=f"N = {n:,}"
        )

    # Đánh dấu các mốc CUTOFF theo giáo trình GS. Sedgewick
    plt.axvline(x=7, color="#d97706", linestyle="--", linewidth=1.5, alpha=0.85, label="Cutoff = 7 (Giáo trình cũ)")
    plt.axvline(x=10, color="#059669", linestyle="--", linewidth=1.5, alpha=0.85, label="Cutoff = 10 (Giáo trình mới)")

    plt.title("Khảo sát thời gian thực thi MergeSort theo giá trị CUTOFF", fontsize=14, fontweight="bold", pad=15)
    plt.xlabel("Giá trị CUTOFF (Kích thước mảng con chuyển sang InsertionSort)", fontsize=11)
    plt.ylabel("Thời gian thực thi trung bình (ms)", fontsize=11)
    plt.legend(frameon=True, facecolor="white")
    plt.tight_layout()

    # Lưu biểu đồ dạng PNG độ nét cao (300 DPI)
    output_png = os.path.join(output_dir, "cutoff_vs_time.png")
    plt.savefig(output_png, dpi=300)
    print(f"[OK] Da xuat bieu do thanh cong: {output_png}")

if __name__ == "__main__":
    main()
