import ms.aurora.api.Players;
import ms.aurora.api.Projection;
import ms.aurora.api.wrappers.RSPlayer;
import ms.aurora.event.listeners.PaintListener;

import java.awt.*;

/**
 * @author rvbiljouw
 */
public class PositionPaint implements PaintListener {
    @Override
    public void onRepaint(Graphics graphics) {
        RSPlayer player = Players.getLocal();
        if (player != null) {
            Point loc = player.getScreenLocation();
            graphics.drawString(player.getLocation().toString(),
                    loc.x, loc.y);
            Point minimapLoc = Projection.worldToMinimap(player.getLocalX(), player.getLocalY());
            graphics.drawOval(minimapLoc.x - 1, minimapLoc.y - 1, 3, 3);
        }
    }
}
