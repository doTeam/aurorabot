package ms.aurora.api.plugin.internal;

import ms.aurora.api.methods.Players;
import ms.aurora.event.EventBus;
import ms.aurora.api.wrappers.Player;

import java.awt.*;

/**
 * @author rvbiljouw
 */
public class AnimationPaint {

    @EventBus.EventHandler
    public void onRepaint(Graphics graphics) {
        Player player = Players.getLocal();
        if (player != null) {
            Point loc = player.getScreenLocation();
            graphics.drawString(String.valueOf(player.getAnimation()),
                    loc.x, loc.y);
        }
    }
}
