/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ham.citybuildersim;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Jerus
 */
public class HistoryGrapher {

    

    public void printLineGraph(List<Double> data, List<Integer> months, String title) {
        
        int height = 12;
        int widthLimit = 60;
        int yLabels = 4;
        int xStep = 12;

        if (data == null || data.isEmpty()) {
            System.out.println("No data to graph.");
            return;
        }

        int start = Math.max(0, data.size() - widthLimit);

        List<Double> recent = data.subList(start, data.size());
        List<Integer> recentMonths = months.subList(start, months.size());

        double max = Collections.max(recent);
        double min = Collections.min(recent);

        if (max == min) {
            max += 1;
        }

        int width = recent.size();

        int[] points = new int[width];

        // normalize data to graph height
        for (int i = 0; i < width; i++) {
            double normalized = (recent.get(i) - min) / (max - min);
            points[i] = (int) Math.round(normalized * height);
        }

        System.out.println("\n==== " + title + " ====\n");

        for (int row = height; row >= 0; row--) {

            double labelValue = min + (max - min) * row / height;

            if (row % (height / yLabels) == 0) {
                System.out.printf("%8.0f | ", labelValue);
            } else {
                System.out.print("         | ");
            }

            for (int col = 0; col < width; col++) {

                if (points[col] == row) {
                    System.out.print("- ");
                } else if (col > 0) {

                    int prev = points[col - 1];
                    int curr = points[col];

                    int high = Math.max(prev, curr);
                    int low = Math.min(prev, curr);

                    if (row <= high && row >= low) {

                        if (curr > prev) {
                            System.out.print("/ ");
                        } else if (curr < prev) {
                            System.out.print("\\ ");
                        } else {
                            System.out.print("- ");
                        }

                    } else {
                        System.out.print("  ");
                    }

                } else {
                    System.out.print("  ");
                }

            }

            System.out.println();
        }

        System.out.print("         +");
        System.out.println("-".repeat(width * 2));

        System.out.print("           ");

        for (int i = 0; i < recentMonths.size(); i++) {

            if (i % xStep == 0) {
                System.out.printf("%-24d", recentMonths.get(i));
            }
        }

        System.out.println();

    }

}
