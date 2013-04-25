package ms.aurora.api.plugin.internal;

import ms.aurora.api.methods.Objects;
import ms.aurora.api.methods.Players;
import ms.aurora.api.util.Predicate;
import ms.aurora.api.wrappers.RSModel;
import ms.aurora.api.wrappers.RSObject;
import ms.aurora.event.listeners.PaintListener;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * @author rvbiljouw
 */
public class GroundObjectPaint implements PaintListener {

    @Override
    public void onRepaint(Graphics graphics) {
        RSObject[] objects = Objects.getAll(RSOBJECT_PREDICATE);
        for (RSObject object : objects) {
            Point loc = object.getScreenLocation();
            graphics.setColor(Color.GREEN);
            graphics.drawString(String.valueOf(object.getId()), loc.x, loc.y);
            try {
                Polygon hull = object.getModel().getHull();
                graphics.drawPolygon(hull);
                graphics.setColor(Color.RED);
                Point2D centroid = RSModel.centroid(hull);
                System.out.println(centroid);
                graphics.drawRect((int) centroid.getX(), (int) centroid.getY(), 1, 1);
                graphics.drawPolygon(RSModel.scaleHull(hull, 0.5));
            } catch (Exception ignored) {}
        }
    }

    private final static Predicate<RSObject> RSOBJECT_PREDICATE = new Predicate<RSObject>() {
        @Override
        public boolean apply(RSObject object) {
            return object.distance(Players.getLocal()) < 7
                    && object.getObjectType().equals(RSObject.ObjectType.GROUND_DECORATION);
        }
    };
}
