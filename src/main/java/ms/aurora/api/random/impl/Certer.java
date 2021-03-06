package ms.aurora.api.random.impl;

import ms.aurora.api.methods.*;
import ms.aurora.api.methods.Predicates;
import ms.aurora.api.random.AfterLogin;
import ms.aurora.api.random.Random;
import ms.aurora.api.random.RandomManifest;
import ms.aurora.api.util.Utilities;
import ms.aurora.api.wrappers.GameObject;
import ms.aurora.api.wrappers.NPC;
import ms.aurora.api.wrappers.Tile;
import ms.aurora.api.wrappers.Widget;

/**
 * @author tobiewarburton
 */
@AfterLogin
@RandomManifest(name = "Certer", version = 1.0)
public class Certer extends Random {
    private final int[] MODEL_IDS = {2807, 8828, 8829, 8832, 8833, 8834, 8835, 8836, 8837};
    private final int[] bookPiles = {42352, 42354};
    private final String[] ITEM_NAMES = {"bowl", "battleaxe", "fish", "shield", "helmet", "ring", "shears", "sword", "spade"};

    private boolean readyToLeave = false;
    private int failCount = 0;

    @Override
    public boolean activate() {
        return Objects.get(Predicates.OBJECT_ID(bookPiles)) != null;
    }

    @Override
    public int loop() {
        if (!activate() && readyToLeave) {
            readyToLeave = false;
            failCount = 0;
            return -1;
        }

        if (Widgets.getWidget(241, 4).getText().contains("Ahem, ")) {
            readyToLeave = false;
        }

        if (Widgets.getWidget(241, 4).getText().contains("Correct.") || Widgets.getWidget(241, 4).getText().contains("You can go now.")) {
            readyToLeave = true;
        }

        if (readyToLeave) {
            int PORTAL_ID = 11368;
            final GameObject portal = Objects.get(Predicates.OBJECT_ID(PORTAL_ID));
            if (portal != null) {
                final Tile portalLocation = portal.getLocation();
                if (Calculations.distance(Players.getLocal().getLocation(), portal.getLocation()) < 4) {
                    portal.applyAction("Enter");
                    return Utilities.random(3000, 4000);
                } else {
                    Walking.clickOnMap(new Tile(portalLocation.getX() - 1, portalLocation.getY()));
                    return Utilities.random(6000, 8000);
                }
            }
        }

        if (Widgets.getWidget(184, 0) != null) {
            final int modelID = Widgets.getWidget(184, 8).getChildren()[3].getModelId();
            String itemName = null;
            for (int i = 0; i < MODEL_IDS.length; i++) {
                if (MODEL_IDS[i] == modelID) {
                    itemName = ITEM_NAMES[i];
                }
            }

            if (itemName == null) {
                return Utilities.random(1000, 2000);
            }

            for (int j = 0; j < 3; j++) {
                final Widget iface = Widgets.getWidget(184, 8).getChildren()[j];
                if (iface.getText().contains(itemName)) {
                    iface.click(true);
                    return Utilities.random(3000, 5000);
                }
            }
        }

        if (Widgets.canContinue()) {
            Widgets.clickContinue();
            return Utilities.random(3000, 4000);
        }

        final NPC certer = Npcs.get(Predicates.NPC_NAME("Niles", "Miles", "Giles"));
        if (certer != null) {
            if (Calculations.distance(Players.getLocal().getLocation(), certer.getLocation()) < 4) {
                certer.applyAction("Talk-to");
                return Utilities.random(4000, 5000);
            } else {
                Tile certerLocation = certer.getLocation();
                Walking.clickOnMap(new Tile(certerLocation.getX() + 2, certerLocation.getY()));
                return Utilities.random(6000, 8000);
            }
        }
        return Utilities.random(1000, 2000);
    }
}
