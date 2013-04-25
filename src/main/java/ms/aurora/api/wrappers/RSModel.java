package ms.aurora.api.wrappers;

import ms.aurora.api.Context;
import ms.aurora.api.methods.Viewport;
import ms.aurora.api.util.GrahamScan;
import ms.aurora.rt3.Model;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import static ms.aurora.api.util.Utilities.random;

/**
 * @author Rick
 */
public final class RSModel {
    private final Context ctx;

    private int[] trianglesX,
            trianglesY,
            trianglesZ,
            verticesX,
            verticesY,
            verticesZ,
            originalX,
            originalZ;
    private int localX;
    private int localY;
    private int orientation;


    public RSModel(Context ctx, Model wrapped, int localX, int localY, int orientation) {
        this.ctx = ctx;
        this.trianglesX = wrapped.getTrianglesX().clone();
        this.trianglesY = wrapped.getTrianglesY().clone();
        this.trianglesZ = wrapped.getTrianglesZ().clone();
        this.verticesX = wrapped.getVerticesX().clone();
        this.verticesY = wrapped.getVerticesY().clone();
        this.verticesZ = wrapped.getVerticesZ().clone();
        this.localX = localX;
        this.localY = localY;
        this.orientation = orientation;
        this.originalX = this.verticesX.clone();
        this.originalZ = this.verticesZ.clone();

        setRotation((64 * 128) & 0x3fff);
        if (orientation != 0) {
            setRotation(orientation & 0x3fff);
        }
    }

    public Polygon[] getPolygons() {
        setRotation((64 * 128) & 0x3fff);
        if (orientation != 0) {
            setRotation(orientation & 0x3fff);
        }

        ArrayList<Polygon> polys = new ArrayList<Polygon>();
        for (int i = 0; i < trianglesX.length; i++) {
            if (i >= trianglesY.length && i >= trianglesZ.length) return null;
            Point x = Viewport.convert(new RSTile(localX, localY), verticesX[trianglesX[i]], verticesZ[trianglesX[i]], -verticesY[trianglesX[i]]);
            Point y = Viewport.convert(new RSTile(localX, localY), verticesX[trianglesY[i]], verticesZ[trianglesY[i]], -verticesY[trianglesY[i]]);
            Point z = Viewport.convert(new RSTile(localX, localY), verticesX[trianglesZ[i]], verticesZ[trianglesZ[i]], -verticesY[trianglesZ[i]]);
            if (x.x > 0 && x.y > 0 && y.x > 0 && y.y > 0 && z.x > 0 && z.y > 0) {
                int xx[] = {
                        x.x, y.x, z.x
                };
                int yy[] = {
                        x.y, y.y, z.y
                };
                polys.add(new Polygon(xx, yy, 3));
            }
        }
        return polys.toArray(new Polygon[polys.size()]);
    }

    protected void setRotation(int orientation) {
        int sin = SIN_TABLE[orientation];
        int cos = COS_TABLE[orientation];
        for (int i = 0; i < this.originalX.length; ++i) {
            this.verticesX[i] = this.originalX[i] * cos + this.originalZ[i] * sin >> 15;
            this.verticesZ[i] = this.originalZ[i] * cos - this.originalX[i] * sin >> 15;
        }

    }

    public Polygon getHull() {
        ArrayList<Point> modelVertices = new ArrayList<Point>();
        for (int i = 0; i < trianglesX.length; i++) {
            if (i >= trianglesY.length && i >= trianglesZ.length) return null;
            Point x = Viewport.convert(new RSTile(localX, localY), verticesX[trianglesX[i]], verticesZ[trianglesX[i]], -verticesY[trianglesX[i]]);
            Point y = Viewport.convert(new RSTile(localX, localY), verticesX[trianglesY[i]], verticesZ[trianglesY[i]], -verticesY[trianglesY[i]]);
            Point z = Viewport.convert(new RSTile(localX, localY), verticesX[trianglesZ[i]], verticesZ[trianglesZ[i]], -verticesY[trianglesZ[i]]);
            if (x.x > 0 && x.y > 0 && y.x > 0 && y.y > 0 && z.x > 0 && z.y > 0) {
                modelVertices.add(x);
                modelVertices.add(y);
                modelVertices.add(z);
            }
        }
        Polygon hull = new Polygon();
        java.util.List<Point> points = GrahamScan.getConvexHull(modelVertices);
        if (points == null) return hull;
        for (Point p : points) {
            hull.addPoint(p.x, p.y);
        }
        return hull;
    }

    /**
     * wot lolol
     *
     * @return
     */
    public Point getRandomPoint() {
        return getRandomHullPoint();
    }

    public Point getRandomHullPoint() {
        Polygon hull = this.getHull();
        Rectangle bounds = hull.getBounds();
        Point p = new Point(-1, -1);
        do {
            Point temp = new Point(random((int)bounds.getCenterX() - (bounds.width / 4),
                    (int)bounds.getCenterX() + (bounds.width / 4)),
                    random((int)bounds.getCenterY() - (bounds.height/ 4),
                            (int)bounds.getCenterY() + (bounds.height / 4)));
            if (hull.contains(temp)) {
                p = temp;
            }
        } while (p.x == -1 || p.y == -1);
        return p;
    }

    public static Polygon scaleHull(Polygon hull, double scale) {
        int[] x = hull.xpoints;
        int[] y = hull.ypoints;
        int[] tx = new int[x.length];
        int[] ty = new int[y.length];

        /*Rectangle2D bounds = hull.getBounds2D();
        double scaledWidth = bounds.getWidth() * scale;
        double scaledHeight = bounds.getHeight() * scale;
        double translateX = (bounds.getWidth() - scaledWidth) / 2;
        double translateY = (bounds.getHeight() - scaledHeight) / 2;

        double scaleX = x[0] + translateX;
        double scaleY = y[0] + translateY;*/

        Point2D centroid = centroid(hull);

        final AffineTransform affineTransform =
                AffineTransform.getTranslateInstance((1 - scale) * centroid.getX(),
                        (1 - scale) * centroid.getY());
        affineTransform.scale(scale, scale);

        for (int i = 0; i < hull.npoints; i++) {
            Point2D p = new Point2D.Double(x[i], y[i]);

            affineTransform.transform(p, p);

            tx[i] = (int) p.getX();
            ty[i] = (int) p.getY();
        }
        return new Polygon(tx, ty, hull.npoints);
    }

    public static Point2D[] getPoints(Polygon hull) {
        int[] x = hull.xpoints;
        int[] y = hull.ypoints;
        Point2D[] points = new Point2D[hull.npoints];
        for (int i = 0; i < hull.npoints; i++) {
            points[i] = new Point2D.Double(x[i], y[i]);
        }
        return points;
    }

    // return area of polygon
    public static double area(Polygon hull) { return Math.abs(signedArea(hull)); }

    // return signed area of polygon
    public static double signedArea(Polygon hull) {
        Point2D[] points = getPoints(hull);
        double sum = 0.0;
        for (int i = 0; i < points.length; i++) {
            sum = sum + (points[i].getX() * points[i+1].getX()) - (points[i].getX() * points[i+1].getX());
        }
        return 0.5 * sum;
    }

    // return the centroid of the polygon
    public static Point2D centroid(Polygon hull) {
        Point2D[] points = getPoints(hull);
        double cx = 0.0, cy = 0.0;
        for (int i = 0; i < points.length; i++) {
            cx = cx + (points[i].getX() + points[i+1].getX()) * (points[i].getY() * points[i+1].getX() - points[i].getX() * points[i+1].getY());
            cy = cy + (points[i].getY() + points[i+1].getX()) * (points[i].getY() * points[i+1].getX() - points[i].getX() * points[i+1].getY());
        }
        cx /= (6 * area(hull));
        cy /= (6 * area(hull));
        return new Point2D.Double(cx, cy);
    }

    private static final int[] SIN_TABLE = new int[16384];
    private static final int[] COS_TABLE = new int[16384];

    static {
        final double d = 0.00038349519697141029D;
        for (int i = 0; i < 16384; i++) {
            SIN_TABLE[i] = (int) (32768D * Math.sin(i * d));
            COS_TABLE[i] = (int) (32768D * Math.cos(i * d));
        }
    }

}
