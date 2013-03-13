package ms.aurora.api.wrappers;

import ms.aurora.api.ClientContext;
import ms.aurora.api.Menu;
import ms.aurora.api.Projection;
import ms.aurora.api.util.Predicate;

import java.awt.*;

/**
 * @author rvbiljouw
 */
public class RSCharacter extends RSRenderable {
    private final ms.aurora.api.rt3.Character wrapped;

    public RSCharacter(ClientContext clientContext,
                       ms.aurora.api.rt3.Character wrapped) {
        super(clientContext, wrapped);
        this.wrapped = wrapped;
    }

    public Point getScreenLocation() {
        return Projection.worldToScreen(getRegionalLocation());
    }

    public RSTile getLocation() {
        return new RSTile(getX(), getY());
    }

    public RSTile getRegionalLocation() {
        int x = getLocalX();
        int z = getLocalY();
        return new RSTile(x, z, getHeight());
    }

    public int distance(RSCharacter other) {
        return (int) Point.distance(getX(), getY(), other.getX(), other.getY());
    }

    public int getX() {
        return context.getClient().getBaseX() + (getLocalX() >> 7);
    }

    public int getY() {
        return context.getClient().getBaseY() + (getLocalY() >> 7);
    }

    public int getLocalX() {
        return wrapped.getLocalX();
    }

    public int getLocalY() {
        return wrapped.getLocalY();
    }

    /**
     * @return the character that the current character is interacting with, or
     *         null
     */
    public RSCharacter getInteracting() {
        int interacting = getInteractingEntity();
        if (interacting == -1)
            return null;
        if (interacting < 32767) {
            return new RSNPC(context,
                    context.getClient().getAllNpcs()[interacting]);
        } else if (interacting >= 32767) {
            int index = (interacting - 32767);
            return new RSPlayer(context,
                    context.getClient().getAllPlayers()[index]);
        }
        return null;
    }

    /**
     * @return if the current character is in combat
     */
    public boolean isInCombat() {
        return context.getClient().getLoopCycle() < getLoopCycleStatus();
    }

    /**
     * @param actionName
     * @return
     */
    public boolean applyAction(String actionName) {
        if (!Projection.tileOnScreen(getRegionalLocation()))
            return false;
        Point screen = getScreenLocation();
        context.input.getMouse().moveMouse(screen.x, screen.y);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Menu.click(actionName);
    }

    public int getHitsLoopCycle() {
        return wrapped.getHitsLoopCycle();
    }

    public int getAnimation() {
        return wrapped.getAnimation();
    }

    public int getCurrentHealth() {
        return wrapped.getCurrentHealth();
    }

    public int getMaxHealth() {
        return wrapped.getMaxHealth();
    }

    public int getLoopCycleStatus() {
        return wrapped.getLoopCycleStatus();
    }

    public int getTurnDirection() {
        return wrapped.getTurnDirection();
    }

    public String getMessage() {
        return wrapped.getMessage();
    }

    public int getInteractingEntity() {
        return wrapped.getInteractingEntity();
    }

    public static final Predicate<RSCharacter> IDLE = new Predicate<RSCharacter>() {
        @Override
        public boolean apply(RSCharacter object) {
            return !object.isInCombat();
        }
    };
}