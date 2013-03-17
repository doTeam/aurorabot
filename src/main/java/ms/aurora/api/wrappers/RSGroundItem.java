package ms.aurora.api.wrappers;

import ms.aurora.api.ClientContext;
import ms.aurora.api.Projection;
import ms.aurora.api.rt3.Item;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: tobiewarburton
 * Date: 16/03/2013
 * Time: 01:11
 * To change this template use File | Settings | File Templates.
 */
public class RSGroundItem implements Locatable {
    private ClientContext context;
    private Item item;
    private int localX;
    private int localY;
    private int z;

    public RSGroundItem(ClientContext context, Item item, int localX, int localY, int z) {
        this.context = context;
        this.item = item;
        this.localX = localX;
        this.localY = localY;
        this.z = z;
    }

    public int getId() {
        return item.getId();
    }

    public int getStackSize() {
        return item.getStackSize();
    }

    public Point getScreenLocation() {
        return Projection.worldToScreen(new RSTile(getLocalX() * 128 + 64, getLocalY() * 128 + 64, z));
    }

    public RSTile getLocation() {
        return new RSTile(getX(), getY(), z);
    }

    public RSTile getRegionalLocation() {
        return new RSTile(getLocalX(), getLocalY(), z);
    }

    public int distance(Locatable other) {
        return (int) Point.distance(getX(), getY(), other.getX(), other.getY());
    }

    public int getX() {
        return getLocalX() + context.getClient().getBaseX();
    }

    public int getY() {
        return getLocalY() + context.getClient().getBaseY();
    }

    public int getLocalX() {
        return localX;
    }

    public int getLocalY() {
        return localY;
    }

    /**
     * @param actionName
     * @return
     */
    public boolean applyAction(String actionName) {
        Point screen = getScreenLocation();
        if (screen.x == -1 && screen.y == -1) return false;
        context.input.getMouse().moveMouse(screen.x, screen.y);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ms.aurora.api.Menu.click(actionName);
    }

    public boolean hover() {
        Point screen = getScreenLocation();
        if (screen.x == -1 && screen.y == -1) return false;
        context.input.getMouse().moveMouse(screen.x, screen.y);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }
}