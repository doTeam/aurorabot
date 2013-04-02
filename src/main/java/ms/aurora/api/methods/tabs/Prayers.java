package ms.aurora.api.methods.tabs;

import ms.aurora.api.methods.Widgets;
import ms.aurora.api.wrappers.RSWidget;
import ms.aurora.api.wrappers.RSWidgetGroup;

/**
 * @author Matty Cov / A_C
 * @author rvbiljouw
 */
public class Prayers {

    public interface Prayer {

        /**
         * @return returns child interface of the prayer
         */
        public int getInterface();

        /**
         * @return Level required to activate prayer
         */
        public int getRequiredLvl();

        /**
         * @return Name of prayer as String
         */
        public String getName();

    }

    public enum Modern implements Prayer {

        THICK_SKIN(0, 1, "(Thick skin|Lederhaut|Peau dure)"),
        BURST_OF_STRENGTH(1, 4, "(Burst of strength|Kraftschub|Vigueur)"),
        CLARITY_OF_THOUGHT(2, 7, "(Clarity of thought|Schnelle Reflexe|Lucidité)"),
        SHARP_EYE(3, 8, "(Sharp eye|Weitsicht|\u0152il de loup)"),
        MYSTIC_WILL(4, 9, "(Mystic will|Magischer Schub|Volonté mystique)"),
        ROCK_SKIN(5, 10, "(Rock skin|Steinhaut|Peau de pierre)"),
        SUPERHUMAN_STRENGTH(6, 13, "(Superhuman strength|Übernatürliche Kraft|Force surhumaine)"),
        IMPROVED_REFLEXES(7, 16, "(Improved reflexes|Verbesserte Reflexe|Réflexes améliorés)"),
        RAPID_RESTORE(8, 19, "(Rapid restore|Rasante Wiederherstellung|Récupération rapide)"),
        RAPID_HEAL(9, 22, "(Rapid heal|Rasante Heilung| Guérison rapide)"),
        PROTECT_ITEM_REGULAR(10, 25, "(Protect item|Gegenstandsschutz|Protection d'objet)"),
        HAWK_EYE(11, 26, "(Hawk eye|Falkenauge|\u0152il de faucon)"),
        MYSTIC_LORE(12, 27, "(Mystic lore|Magische Kunde|Savoir mystique)"),
        STEEL_SKIN(13, 28, "(Steel skin|Stahlhaut|Peau d'acier)"),
        ULTIMATE_STRENGTH(14, 31, "(Ultimate strength|Ultimative Kraft|Force ultime)"),
        INCREDIBLE_REFLEXES(15, 34, "(Incredible reflexes|Unglaubliche Reflexe|Réflexes extraordinaires)"),
        PROTECT_FROM_SUMMONING(16, 35, "(Protect from summoning|Beschwörungs-Schutz|Anti-invocation)"),
        PROTECT_FROM_MAGIC(17, 37, "(Protect from magic|Magie-Schutz|Anti-magie)"),
        PROTECT_FROM_MISSILES(18, 40, "(Protect from missiles|Fernkampf-Schutz|Anti-projectiles)"),
        PROTECT_FROM_MELEE(19, 43, "(Protect from melee|Nahkampf-Schutz|Anti-mêlée)"),
        EAGLE_EYE(20, 44, "(Eagle eye|Adlerauge|\u0152il de lynx)"),
        MYSTIC_MIGHT(21, 45, "(Mystic might|Magische Macht|Force mystique)"),
        RETRIBUTION(22, 46, "(Retribution|Vergeltung|Châtiment)"),
        REDEMPTION(23, 49, "(Redemption|Erlösung|Rédemption)"),
        SMITE(24, 52, "(Smite|Bestrafung|Expiation)"),
        CHIVALRY(25, 60, "(Chivalry|Ritterlichkeit|Chevalerie)"),
        RAPID_RENEWAL(26, 65, "(Rapid renewal|Erneuerung|Regain rapide)"),
        PIETY(27, 70, "(Piety|Frömmigkeit|Piété)"),
        RIGOUR(28, 74, "(Rigour|Dynamik|Rigueur)"),
        AUGURY(29, 77, "(Augury|Omen|Augure)");

        private int index;
        private int reqLvl;
        private String name;

        /**
         * Gets the modern book.
         *
         * @param index  Prayer index.
         * @param reqLvl Required level.
         * @param name   Prayer name.
         */
        private Modern(int index, int reqLvl, String name) {
            this.index = index;
            this.reqLvl = reqLvl;
            this.name = name;
        }

        /**
         * Gets the Interface.
         *
         * @return index.
         */
        public int getInterface() {
            return this.index;
        }

        /**
         * Gets the required level.
         *
         * @return Required level.
         */
        public int getRequiredLvl() {
            return this.reqLvl;
        }

        /**
         * Gets the name.
         *
         * @return Name.
         */
        public String getName() {
            return this.name;
        }
    }

    /**
     * Gets a prayer book.
     *
     * @return widget
     */
    public static RSWidget getBook() {
        return Widgets.getWidget(Constants.PRAYER_TAB, Constants.PRAYER_INTERFACE);
    }

    /**
     * Gets a prayer in the prayer book
     * @param p prayer
     * @return toggle widget
     */
    public static RSWidget getPrayer(Prayer p) {
        return getBook().getChildren()[p.getInterface()];
    }

    /**
     * @param p Activates prayer.
     */
    public static void activate(Prayer p) {
        if (!Tabs.isOpen(Tabs.Tab.PRAYER)) {
            Tabs.openTab(Tabs.Tab.PRAYER);
        }
        getPrayer(p).applyAction("Activate");
    }

    /**
     * @param p Deactivates prayer.
     */
    public static void deactivate(Prayer p) {
        if (!Tabs.isOpen(Tabs.Tab.PRAYER)) {
            Tabs.openTab(Tabs.Tab.PRAYER);
        }
        getPrayer(p).applyAction("Deactivate");
    }

    /**
     * @param p Prayer to check
     * @return True - prayer is active else false
     */
    public static boolean isActive(Prayer p) {
        return getPrayer(p).getActions()[0].equals("Deactivate");
    }

    public class Constants {
        public static final int PRAYER_TAB = 271;
        public static final int PRAYER_INTERFACE = 8;
    }
}