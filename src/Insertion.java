/**
 * Cài đặt thuật toán Insertion Sort theo giáo trình Algorithms 4th Edition
 * (Robert Sedgewick & Kevin Wayne)
 * 
 * Hỗ trợ sắp xếp trên một đoạn con a[lo..hi].
 */
public class Insertion {

    // Sắp xếp đoạn con a[lo..hi]
    public static void sort(Comparable[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++) {
            for (int j = i; j > lo && less(a[j], a[j - 1]); j--) {
                exch(a, j, j - 1);
            }
        }
    }

    private static boolean less(Comparable v, Comparable w) {
        return v.compareTo(w) < 0;
    }

    private static void exch(Object[] a, int i, int j) {
        Object swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }
}
