"""
Script đọc kết quả đo từ benchmark_results.csv và vẽ đồ thị trực quan.
Hỗ trợ cả xuất ảnh vector SVG (chuẩn, không cần cài thêm thư viện ngoài)
và xuất ảnh PNG nếu môi trường có sẵn matplotlib.
"""

import os
import csv
import math

def generate_svg_chart(data_by_n, output_path):
    """
    Tạo biểu đồ dạng vector SVG chất lượng cao hoàn toàn bằng Python standard library,
    không phụ thuộc vào bất kỳ thư viện ngoài nào (như matplotlib/pandas).
    Hỗ trợ hiển thị trực tiếp trên GitHub và tài liệu Markdown/HTML.
    """
    width = 900
    height = 550
    margin = {"top": 60, "right": 50, "bottom": 70, "left": 80}
    plot_width = width - margin["left"] - margin["right"]
    plot_height = height - margin["top"] - margin["bottom"]

    # Xác định giới hạn trục
    all_cutoffs = set()
    all_times = []
    for n, records in data_by_n.items():
        for r in records:
            all_cutoffs.add(r[0])
            all_times.append(r[1])

    min_cutoff = min(all_cutoffs)
    max_cutoff = max(all_cutoffs)
    min_time = 0
    max_time = max(all_times) * 1.15  # Cho thêm chút padding phía trên

    def scale_x(x):
        return margin["left"] + (x - min_cutoff) / (max_cutoff - min_cutoff) * plot_width

    def scale_y(y):
        return margin["top"] + plot_height - (y - min_time) / (max_time - min_time) * plot_height

    colors = ["#2563eb", "#dc2626", "#059669", "#7c3aed", "#d97706"]

    svg = []
    svg.append(f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {width} {height}" width="100%" height="100%" style="background:#ffffff; font-family: Segoe UI, Roboto, Helvetica, Arial, sans-serif;">')

    # Định nghĩa style và shadow
    svg.append("""
    <defs>
      <filter id="shadow" x="-5%" y="-5%" width="110%" height="110%">
        <feDropShadow dx="0" dy="2" stdDeviation="3" flood-opacity="0.15"/>
      </filter>
    </defs>
    """)

    # Tiêu đề biểu đồ
    svg.append(f'<text x="{width / 2}" y="35" text-anchor="middle" font-size="18" font-weight="bold" fill="#1e293b">Khảo sát thời gian thực thi MergeSort theo giá trị CUTOFF</text>')

    # Đường lưới trục Y (Grid lines)
    y_ticks = 6
    for i in range(y_ticks + 1):
        val = min_time + i * (max_time - min_time) / y_ticks
        y_pos = scale_y(val)
        svg.append(f'<line x1="{margin["left"]}" y1="{y_pos}" x2="{width - margin["right"]}" y2="{y_pos}" stroke="#e2e8f0" stroke-width="1" stroke-dasharray="4,4"/>')
        svg.append(f'<text x="{margin["left"] - 12}" y="{y_pos + 4}" text-anchor="end" font-size="11" fill="#64748b">{val:.1f} ms</text>')

    # Đường lưới trục X (Grid lines & ticks)
    cutoff_ticks = sorted(list(all_cutoffs))
    for c in cutoff_ticks:
        if c in [0, 2, 4, 7, 10, 14, 16, 20, 25, 30]:
            x_pos = scale_x(c)
            svg.append(f'<line x1="{x_pos}" y1="{margin["top"]}" x2="{x_pos}" y2="{margin["top"] + plot_height}" stroke="#f1f5f9" stroke-width="1"/>')
            svg.append(f'<text x="{x_pos}" y="{margin["top"] + plot_height + 20}" text-anchor="middle" font-size="11" fill="#64748b">{c}</text>')

    # Trục toạ độ chính
    svg.append(f'<line x1="{margin["left"]}" y1="{margin["top"] + plot_height}" x2="{width - margin["right"]}" y2="{margin["top"] + plot_height}" stroke="#475569" stroke-width="1.5"/>')
    svg.append(f'<line x1="{margin["left"]}" y1="{margin["top"]}" x2="{margin["left"]}" y2="{margin["top"] + plot_height}" stroke="#475569" stroke-width="1.5"/>')

    # Nhãn các trục
    svg.append(f'<text x="{width / 2}" y="{height - 20}" text-anchor="middle" font-size="13" font-weight="600" fill="#334155">Giá trị CUTOFF (Kích thước mảng con chuyển sang InsertionSort)</text>')
    svg.append(f'<text x="25" y="{margin["top"] + plot_height / 2}" text-anchor="middle" font-size="13" font-weight="600" fill="#334155" transform="rotate(-90 25 {margin["top"] + plot_height / 2})">Thời gian thực thi trung bình (ms)</text>')

    # Đường đánh dấu CUTOFF = 7 và CUTOFF = 10 (theo giáo trình Sedgewick)
    if min_cutoff <= 7 <= max_cutoff:
        x7 = scale_x(7)
        svg.append(f'<line x1="{x7}" y1="{margin["top"]}" x2="{x7}" y2="{margin["top"] + plot_height}" stroke="#d97706" stroke-width="2" stroke-dasharray="6,4"/>')
        svg.append(f'<text x="{x7 + 5}" y="{margin["top"] + 20}" font-size="11" font-weight="600" fill="#d97706">Cutoff = 7 (Cũ)</text>')

    if min_cutoff <= 10 <= max_cutoff:
        x10 = scale_x(10)
        svg.append(f'<line x1="{x10}" y1="{margin["top"]}" x2="{x10}" y2="{margin["top"] + plot_height}" stroke="#059669" stroke-width="2" stroke-dasharray="6,4"/>')
        svg.append(f'<text x="{x10 + 5}" y="{margin["top"] + 40}" font-size="11" font-weight="600" fill="#059669">Cutoff = 10 (Mới)</text>')

    # Vẽ các đường dữ liệu
    legend_items = []
    color_idx = 0
    for n, records in data_by_n.items():
        records.sort(key=lambda x: x[0])
        color = colors[color_idx % len(colors)]
        color_idx += 1

        points = []
        for r in records:
            px = scale_x(r[0])
            py = scale_y(r[1])
            points.append((px, py, r[0], r[1]))

        # Vẽ đường nối polyline
        pts_str = " ".join([f"{px},{py}" for px, py, _, _ in points])
        svg.append(f'<polyline fill="none" stroke="{color}" stroke-width="2.5" stroke-linejoin="round" points="{pts_str}"/>')

        # Vẽ các điểm tròn (dots)
        for px, py, c, t in points:
            svg.append(f'<circle cx="{px}" cy="{py}" r="4" fill="{color}" stroke="#ffffff" stroke-width="1.5"/>')

        legend_items.append((f"N = {n:,}", color))

    # Chú thích (Legend)
    leg_x = width - margin["right"] - 140
    leg_y = margin["top"] + 15
    svg.append(f'<rect x="{leg_x - 10}" y="{leg_y - 10}" width="140" height="{len(legend_items) * 24 + 10}" rx="6" fill="#f8fafc" stroke="#cbd5e1" stroke-width="1"/>')
    for i, (label, col) in enumerate(legend_items):
        iy = leg_y + i * 24
        svg.append(f'<line x1="{leg_x}" y1="{iy + 6}" x2="{leg_x + 20}" y2="{iy + 6}" stroke="{col}" stroke-width="3"/>')
        svg.append(f'<circle cx="{leg_x + 10}" cy="{iy + 6}" r="3.5" fill="{col}"/>')
        svg.append(f'<text x="{leg_x + 28}" y="{iy + 10}" font-size="11" font-weight="600" fill="#334155">{label}</text>')

    svg.append("</svg>")

    with open(output_path, "w", encoding="utf-8") as f:
        f.write("\n".join(svg))
    print(f"[OK] Da xuat bieu do vector SVG thanh cong: {output_path}")


def main():
    csv_file = os.path.join("data", "results", "benchmark_results.csv")
    output_dir = os.path.join("report", "figures")

    if not os.path.exists(csv_file):
        print(f"[!] Không tìm thấy file dữ liệu: {csv_file}")
        print("    Vui lòng chạy BenchmarkRunner trước để sinh dữ liệu!")
        return

    os.makedirs(output_dir, exist_ok=True)

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

    # 1. Luôn xuất biểu đồ vector SVG chất lượng cao (không cần thư viện bên ngoài)
    svg_output = os.path.join(output_dir, "cutoff_vs_time.svg")
    generate_svg_chart(data_by_n, svg_output)

    # 2. Nếu môi trường có matplotlib, xuất thêm cả PNG
    try:
        import matplotlib.pyplot as plt
        plt.figure(figsize=(10, 6))
        for n, records in data_by_n.items():
            records.sort(key=lambda x: x[0])
            cutoffs = [r[0] for r in records]
            times = [r[1] for r in records]
            errors = [r[2] for r in records]

            plt.errorbar(cutoffs, times, yerr=errors, marker="o", capsize=4, label=f"N = {n:,}")

        plt.axvline(x=7, color="orange", linestyle="--", alpha=0.7, label="Cutoff = 7 (Cũ)")
        plt.axvline(x=10, color="green", linestyle="--", alpha=0.7, label="Cutoff = 10 (Mới)")
        plt.title("Khảo sát thời gian thực thi MergeSort theo giá trị CUTOFF", fontsize=14, fontweight="bold")
        plt.xlabel("Giá trị CUTOFF (Kích thước mảng con chuyển sang InsertionSort)", fontsize=11)
        plt.ylabel("Thời gian thực thi trung bình (ms)", fontsize=11)
        plt.grid(True, linestyle=":", alpha=0.6)
        plt.legend(fontsize=10)
        plt.tight_layout()

        png_output = os.path.join(output_dir, "cutoff_vs_time.png")
        plt.savefig(png_output, dpi=300)
        print(f"[OK] Da xuat bieu do PNG thanh cong: {png_output}")
    except ImportError:
        pass

if __name__ == "__main__":
    main()
