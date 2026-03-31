package com.ch.managementsystem.utils;

import java.util.*;

public class AlgorithmUtils {

    /**
     * 算法1：TOP-K 最小堆算法
     * 用途：在仪表盘中计算“部门月度出勤排行TopK”
     */
    public static List<Map.Entry<String, Double>> topKByMinHeap(Map<String, Double> source, int k) {
        PriorityQueue<Map.Entry<String, Double>> heap =
                new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));
        for (Map.Entry<String, Double> entry : source.entrySet()) {
            if (heap.size() < k) {
                heap.offer(entry);
            } else if (entry.getValue() > Objects.requireNonNull(heap.peek()).getValue()) {
                heap.poll();
                heap.offer(entry);
            }
        }
        List<Map.Entry<String, Double>> result = new ArrayList<>(heap);
        result.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        return result;
    }

    /**
     * 算法2：加权评分算法
     * 用途：在仪表盘中计算“员工综合贡献分”
     */
    public static double weightedScore(double attendanceRate, double performanceScore, double projectProgress) {
        // 权重与系统设计保持一致：出勤40%，绩效40%，项目20%
        return attendanceRate * 0.4 + performanceScore * 0.4 + projectProgress * 0.2;
    }
}
