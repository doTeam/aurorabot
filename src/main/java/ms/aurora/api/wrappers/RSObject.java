package ms.aurora.api.wrappers;

import ms.aurora.api.Context;
import ms.aurora.api.methods.Minimap;
import ms.aurora.api.methods.Objects;
import ms.aurora.api.methods.Viewport;
import ms.aurora.api.methods.Walking;
import ms.aurora.api.pathfinding.Path;
import ms.aurora.api.pathfinding.impl.RSMapPathFinder;
import ms.aurora.input.VirtualMouse;
import ms.aurora.rt3.*;

import java.awt.*;

import static ms.aurora.api.Context.getProperty;

/**
 * @author Rick
 */
public final class RSObject implements Locatable, Interactable {
    private final Context ctx;
    private final GameObject wrapped;

    private ObjectType objectType = ObjectType.NULL;
    private int localX;
    private int localY;

    public static enum ObjectType {GROUND_DECORATION, WALL_DECORATION, WALL_OBJECT, ANIMABLE, NULL}

    public RSObject(Context ctx, GameObject wrapped, int localX, int localY) {
        this.ctx = ctx;
        this.wrapped = wrapped;
        this.localX = localX;
        this.localY = localY;
        if (wrapped instanceof GroundDecoration) {
            objectType = ObjectType.GROUND_DECORATION;
        } else if (wrapped instanceof WallDecoration) {
            objectType = ObjectType.WALL_DECORATION;
        } else if (wrapped instanceof WallObject) {
            objectType = ObjectType.WALL_OBJECT;
        } else if (wrapped instanceof AnimableObject) {
            objectType = ObjectType.ANIMABLE;
        }
    }

    public final int getId() {
        return wrapped.getHash() >> 14 & 32767;
    }

    public final int getLocalX() {
        return wrapped.getX();
    }

    public final int getLocalY() {
        return wrapped.getY();
    }

    public final int getX() {
        return (getLocalX() >> 7) + Context.getClient().getBaseX();
    }

    public final int getY() {
        return (getLocalY() >> 7) + Context.getClient().getBaseY();
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public final Point getScreenLocation() {
        return Viewport.convertLocal(getRegionalLocation());
    }

    public final boolean isOnScreen() {
        Point screenLocation = this.getScreenLocation();
        return screenLocation.x != -1 && screenLocation.y != -1;
    }

    public boolean canReach() {
        RSMapPathFinder pf = new RSMapPathFinder();
        Path path = pf.getPath(getX(), getY(), RSMapPathFinder.FULL);
        return path != null && path.getLength() > 0;
    }

    public final RSTile getLocation() {
        return new RSTile(getX(), getY(), 0);
    }

    public final RSTile getRegionalLocation() {
        int x = getLocalX();
        int y = getLocalY();
        return new RSTile(x, y, 0);
    }

    public final int distance(Locatable other) {
        return (int) Point.distance(getX(), getY(), other.getX(), other.getY());
    }

    /**
     * @param actionName
     * @return
     */
    public final boolean applyAction(String actionName) {
        if (!Viewport.tileOnScreen(getLocation())) {
            return false;
        }
        Point click = getClickLocation();
        for (int attempt = 0; attempt < 10; attempt++) {
            VirtualMouse.moveMouse(click.x, click.y);
            if (ms.aurora.api.methods.Menu.getIndex(actionName) != -1) {
                return ms.aurora.api.methods.Menu.click(actionName);
            }
        }
        return false;
    }

    public final boolean hover() {
        if (!Viewport.tileOnScreen(getLocation())) {
            return false;
        }
        Point screen = getScreenLocation();
        VirtualMouse.moveMouse(screen.x, screen.y);
        return true;
    }

    @Override
    public final boolean click(boolean left) {
        if (!Viewport.tileOnScreen(getLocation())) {
            return false;
        }
        Point screen = getClickLocation();
        VirtualMouse.clickMouse(screen.x, screen.y, left);
        return true;
    }

    private Point getClickLocation() {
        try {
            if (getModel() != null) {
                return getModel().getRandomPoint();
            }
        } catch (Exception ignored) {}
        return getScreenLocation();
    }

    public RSModel getModel() {
        if (wrapped.getModel() != null && wrapped.getModel() instanceof Model) {
            return new RSModel(ctx, (Model) wrapped.getModel(), getLocalX(), getLocalY(), 0);
        }
        return null;
    }

    public boolean isValid() {
        RSObject[] objectsAtPos = Objects.getObjectsAtLocal(localX, localY).toArray(new RSObject[0]);
        for (RSObject object : objectsAtPos) {
            if (object.getId() == getId()) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return getId() + " | " + (wrapped instanceof GroundDecoration ? 0 : wrapped.getOrientation());
    }
}
