package ms.aurora.input.algorithm;

import ms.aurora.input.VirtualMouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A generator that creates curved paths based on bezier splines.
 * Much of the original implementation was written by Beland100 for (if i recall correctly) SCAR.
 * It has then been ported to Java numerous times and we still seem to use it.
 * @author Benland100
 * @author Rick
 */
public final class BezierAlgorithm implements VirtualMouse.MousePathAlgorithm {
    private java.util.Random random = new java.util.Random();

    public Point[] generatePath(Point start, Point dest) {
        return generateSpline(generateControls(start.x,
                start.y, dest.x + random.nextInt(2),
                dest.y + random.nextInt(2), 50, 120));
    }

    /**
     * Applys a midpoint algorithm to the Vector of points to ensure pixel to pixel movement
     *
     * @param points The vector of points to be manipulated
     */
    private static void adaptiveMidpoints(List<Point> points) {
        int i = 0;
        while (i < points.size() - 1) {
            Point a = points.get(i++);
            Point b = points.get(i);
            if (Math.abs(a.x - b.x) > 1 || Math.abs(a.y - b.y) > 1) {
                if (Math.abs(a.x - b.x) != 0) {
                    double slope = (double) (a.y - b.y)
                            / (double) (a.x - b.x);
                    double incpt = a.y - slope * a.x;
                    for (int c = a.x < b.x ? a.x + 1 : b.x - 1; a.x < b.x ? c < b.x
                            : c > a.x; c += a.x < b.x ? 1 : -1) {
                        points.add(i++, new Point(c, (int) Math.round(incpt
                                + slope * c)));
                    }
                } else {
                    for (int c = a.y < b.y ? a.y + 1 : b.y - 1; a.y < b.y ? c < b.y
                            : c > a.y; c += a.y < b.y ? 1 : -1) {
                        points.add(i++, new Point(a.x, c));
                    }
                }
            }
        }
    }

    /**
     * Omits points along the spline in order to move in steps rather then pixel by pixel
     *
     * @param spline    The pixel by pixel spline
     * @param msForMove The ammount of time taken to traverse the spline. should be a value from
     * @param msPerMove The ammount of time per each move
     * @return The stepped spline
     */
    private Point[] applyDynamism(Point[] spline,
                                  int msForMove, int msPerMove) {
        int numPoints = spline.length;
        double msPerPoint = (double) msForMove / (double) numPoints;
        double undistStep = msPerMove / msPerPoint;
        int steps = (int) Math.floor(numPoints / undistStep);
        Point[] result = new Point[steps];
        double[] gaussValues = gaussTable(result.length);
        double currentPercent = 0;
        for (int i = 0; i < steps; i++) {
            currentPercent += gaussValues[i];
            int nextIndex = (int) Math.floor(numPoints * currentPercent);
            if (nextIndex < numPoints) {
                result[i] = spline[nextIndex];
            } else {
                result[i] = spline[numPoints - 1];
            }
        }
        if (currentPercent < 1D) {
            result[steps - 1] = spline[numPoints - 1];
        }
        return result;
    }

    /**
     * Returns an array of gaussian values that add up to 1 for the number of steps Solves the problem of having using
     * an intergral to distribute values
     *
     * @param steps Number of steps in the distribution
     * @return An array of values that contains the percents of the distribution
     */
    private double[] gaussTable(int steps) {
        double[] table = new double[steps];
        double step = 1D / steps;
        double sum = 0;
        for (int i = 0; i < steps; i++) {
            sum += gaussian(i * step);
        }
        for (int i = 0; i < steps; i++) {
            table[i] = gaussian(i * step) / sum;
        }
        return table;
    }

    /**
     * Satisfies Integral[gaussian(t),t,0,1] == 1D Therefore can distribute a value as a bell curve over the intervel 0
     * to 1
     *
     * @param t = A value, 0 to 1, representing a percent along the curve
     * @return The value of the gaussian curve at this position
     */
    private double gaussian(double t) {
        t = 10D * t - 5D;
        return 1D / (Math.sqrt(5D) * Math.sqrt(2D * Math.PI))
                * Math.exp(-t * t / 20D);
    }

    /**
     * Creates random control points for a spline. Written by Benland100
     *
     * @param sx           Begining X position
     * @param sy           Begining Y position
     * @param ex           Begining X position
     * @param ey           Begining Y position
     * @param ctrlSpacing  Distance between control origins
     * @param ctrlVariance Max X or Y variance of each control point from its origin
     * @return An array of Points that represents the control points of the spline
     */
    private Point[] generateControls(int sx, int sy,
                                     int ex, int ey, int ctrlSpacing, int ctrlVariance) {
        double dist = Math.sqrt((sx - ex) * (sx - ex) + (sy - ey)
                * (sy - ey));
        double angle = Math.atan2(ey - sy, ex - sx);
        int ctrlPoints = (int) Math.floor(dist / ctrlSpacing);
        ctrlPoints = ctrlPoints * ctrlSpacing == dist ? ctrlPoints - 1
                : ctrlPoints;
        if (ctrlPoints <= 1) {
            ctrlPoints = 2;
            ctrlSpacing = (int) dist / 3;
            ctrlVariance = (int) dist / 2;
        }
        Point[] result = new Point[ctrlPoints + 2];
        result[0] = new Point(sx, sy);
        for (int i = 1; i < ctrlPoints + 1; i++) {
            double radius = ctrlSpacing * i;
            Point cur = new Point((int) (sx + radius * Math.cos(angle)),
                    (int) (sy + radius * Math.sin(angle)));
            double percent = 1D - (double) (i - 1) / (double) ctrlPoints;
            percent = percent > 0.5 ? percent - 0.5 : percent;
            percent += 0.25;
            int curVariance = (int) (ctrlVariance * percent);
            cur.x = (int) (cur.x + curVariance * 2
                    * random.nextDouble() - curVariance);
            cur.y = (int) (cur.y + curVariance * 2
                    * random.nextDouble() - curVariance);
            result[i] = cur;
        }
        result[ctrlPoints + 1] = new Point(ex, ey);
        return result;
    }

    /**
     * Generates a spline that moves no more then one pixel at a time TIP: For most movements, this spline is not good,
     * use <code>applyDynamism</code>
     *
     * @param controls An array of control points
     * @return An array of Points that represents the spline
     */
    private Point[] generateSpline(Point[] controls) {
        double degree = controls.length - 1;
        List<Point> spline = new ArrayList<Point>();
        boolean lastFlag = false;
        for (double theta = 0; theta <= 1; theta += 0.01) {
            double x = 0;
            double y = 0;
            for (double index = 0; index <= degree; index++) {
                double probPoly = nCk((int) degree,
                        (int) index)
                        * Math.pow(theta, index)
                        * Math.pow(1D - theta, degree - index);
                x += probPoly * controls[(int) index].x;
                y += probPoly * controls[(int) index].y;
            }
            Point temp = new Point((int) x, (int) y);
            try {
                if (!temp.equals(spline.get(spline.size() - 1))) {
                    spline.add(temp);
                }
            } catch (Exception e) {
                spline.add(temp);
            }
            lastFlag = theta != 1.0;
        }
        if (lastFlag) {
            spline.add(new Point(controls[(int) degree].x,
                    controls[(int) degree].y));
        }
        adaptiveMidpoints(spline);
        return spline.toArray(new Point[spline.size()]);
    }

    /**
     * Binomial Coefficient "n choose k"
     */
    private double nCk(int n, int k) {
        return fact(n)
                / (fact(k) * fact(n - k));
    }

    /**
     * Factorial "n!"
     */
    private double fact(int n) {
        double result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
