import os
import pandas as pd  # pyrefly: ignore[missing-import]
import matplotlib.pyplot as plt  # pyrefly: ignore[missing-import]
import seaborn as sns  # pyrefly: ignore[missing-import]

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

    # =========================================================================
    # BIỂU ĐỒ 1: Khảo sát thời gian thực thi theo CUTOFF trên các kích thước N (Random)
    # Tách trục tung riêng (Subplots) cho từng N để thấy rõ hình chữ U và điểm tối ưu
    # =========================================================================
    random_df = df[df["DataType"] == "Random"]
    sizes = sorted(random_df["N"].unique())
    colors = ["#2563eb", "#ea580c", "#16a34a"]

    fig, axes = plt.subplots(1, len(sizes), figsize=(16, 5.2), sharex=True)
    if len(sizes) == 1:
        axes = [axes]

    for idx, n in enumerate(sizes):
        ax = axes[idx]
        sub = random_df[random_df["N"] == n].sort_values("Cutoff")
        color = colors[idx % len(colors)]

        ax.errorbar(
            sub["Cutoff"],
            sub["MeanTimeMs"],
            yerr=sub["StdDevMs"],
            marker="o",
            markersize=5,
            capsize=3,
            linewidth=2,
            color=color,
            label=f"N = {n:,}"
        )

        # Tìm và đánh dấu điểm CUTOFF tối ưu
        best_row = sub.loc[sub["MeanTimeMs"].idxmin()]
        best_c = int(best_row["Cutoff"])
        best_t = best_row["MeanTimeMs"]
        ax.scatter(
            [best_c], [best_t],
            color="#dc2626", s=90, zorder=6,
            label=f"Tối ưu: Cutoff = {best_c} ({best_t:.2f} ms)"
        )

        # Đánh dấu các mốc CUTOFF theo giáo trình GS. Sedgewick
        ax.axvline(x=7, color="#d97706", linestyle="--", linewidth=1.3, alpha=0.85, label="Cutoff = 7 (Giáo trình cũ)")
        ax.axvline(x=10, color="#059669", linestyle="--", linewidth=1.3, alpha=0.85, label="Cutoff = 10 (Giáo trình mới)")

        # Mở rộng nhẹ giới hạn trục Y phía trên để hộp chú thích (legend) không đè lên điểm dữ liệu
        ymin, ymax = ax.get_ylim()
        ax.set_ylim(ymin, ymax + 0.22 * (ymax - ymin))

        ax.set_title(f"Kích thước N = {n:,} (Random)", fontsize=12, fontweight="bold")
        ax.set_xlabel("Giá trị CUTOFF", fontsize=10)
        if idx == 0:
            ax.set_ylabel("Thời gian thực thi trung bình (ms)", fontsize=11)
        ax.legend(frameon=True, facecolor="white", framealpha=0.92, fontsize=8.5, loc="upper left")

    fig.suptitle(
        "Khảo sát thời gian thực thi MergeSort theo giá trị CUTOFF (Dữ liệu Ngẫu nhiên)",
        fontsize=14, fontweight="bold", y=1.02
    )
    plt.tight_layout()

    output_png = os.path.join(output_dir, "cutoff_vs_time.png")
    output_svg = os.path.join(output_dir, "cutoff_vs_time.svg")
    fig.savefig(output_png, dpi=300, bbox_inches="tight")
    fig.savefig(output_svg, bbox_inches="tight")
    plt.close(fig)
    print(f"[OK] Da xuat bieu do 1 thanh cong: {output_png}")

    # =========================================================================
    # BIỂU ĐỒ 2: So sánh ảnh hưởng của 4 phân bố dữ liệu tại N = 1,000,000
    # =========================================================================
    dist_df = df[df["N"] == 1_000_000]
    if not dist_df.empty and dist_df["DataType"].nunique() > 1:
        fig2, axes2 = plt.subplots(2, 2, figsize=(13, 9), sharex=True)
        axes2_flat = axes2.flatten()
        dist_order = ["Random", "Sorted", "Reversed", "Duplicates"]
        dist_titles = {
            "Random": "Random (Ngẫu nhiên đều)",
            "Sorted": "Sorted (Đã sắp xếp tăng dần)",
            "Reversed": "Reversed (Sắp xếp ngược chiều)",
            "Duplicates": "Duplicates (Nhiều phần tử trùng lặp)"
        }
        dist_colors = {
            "Random": "#16a34a",
            "Sorted": "#2563eb",
            "Reversed": "#dc2626",
            "Duplicates": "#7c3aed"
        }

        for idx, dtype in enumerate(dist_order):
            ax = axes2_flat[idx]
            sub = dist_df[dist_df["DataType"] == dtype].sort_values("Cutoff")
            if sub.empty:
                continue

            c = dist_colors.get(dtype, "#1f2937")
            ax.errorbar(
                sub["Cutoff"],
                sub["MeanTimeMs"],
                yerr=sub["StdDevMs"],
                marker="o",
                markersize=5,
                capsize=3,
                linewidth=2,
                color=c,
                label=dist_titles.get(dtype, dtype)
            )

            best_row = sub.loc[sub["MeanTimeMs"].idxmin()]
            best_c = int(best_row["Cutoff"])
            best_t = best_row["MeanTimeMs"]
            ax.scatter(
                [best_c], [best_t],
                color="#111827", s=80, zorder=6,
                label=f"Tối ưu: Cutoff = {best_c} ({best_t:.2f} ms)"
            )

            ax.axvline(x=7, color="#d97706", linestyle="--", linewidth=1.2, alpha=0.85, label="Cutoff = 7")
            ax.axvline(x=10, color="#059669", linestyle="--", linewidth=1.2, alpha=0.85, label="Cutoff = 10")

            ax.set_title(dist_titles.get(dtype, dtype), fontsize=11.5, fontweight="bold")
            if idx >= 2:
                ax.set_xlabel("Giá trị CUTOFF", fontsize=10)
            if idx % 2 == 0:
                ax.set_ylabel("Thời gian trung bình (ms)", fontsize=10)
            ax.legend(frameon=True, facecolor="white", fontsize=8.5, loc="best")

        fig2.suptitle(
            "Ảnh hưởng của phân bố dữ liệu đến hiệu năng CUTOFF (N = 1,000,000)",
            fontsize=14, fontweight="bold", y=1.01
        )
        plt.tight_layout()

        dist_png = os.path.join(output_dir, "cutoff_by_datatype.png")
        dist_svg = os.path.join(output_dir, "cutoff_by_datatype.svg")
        fig2.savefig(dist_png, dpi=300, bbox_inches="tight")
        fig2.savefig(dist_svg, bbox_inches="tight")
        plt.close(fig2)
        print(f"[OK] Da xuat bieu do 2 thanh cong: {dist_png}")

if __name__ == "__main__":
    main()
