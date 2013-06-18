package ms.aurora.api.plugin.internal;

import ms.aurora.api.methods.Objects;
import ms.aurora.api.methods.Players;
import ms.aurora.api.util.Predicate;
import ms.aurora.api.wrappers.RSObject;
import ms.aurora.event.EventBus;

import java.awt.*;

/**
 * @author rvbiljouw
 */
public class GroundObjectPaint {

    @EventBus.EventHandler
    public void onRepaint(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        RSObject[] objects = Objects.getAll(RSOBJECT_PREDICATE);
        for (RSObject object : objects) {
            Point loc = object.getScreenLocation();
            graphics.setColor(Color.GREEN);
            graphics.drawString(String.valueOf(object.getId()), loc.x, loc.y);
            try {
                /*graphics.setColor(Color.RED);
                graphics.draw(RSModel.scaleHull(hull, 0.5));*/
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
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
