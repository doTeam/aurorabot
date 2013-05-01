package ms.aurora.api.util;

import static java.lang.Thread.currentThread;

/**
 * @author tobiewarburton
 */
public final class Utilities {

    private Utilities() {
    }

    public static int random(int min, int max) {
        return (int) (min + (Math.random() * max));
    }

    public static double random(double min, double max) {
        return (min + (Math.random() * max));
    }

    public static void sleepNoException(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
            currentThread().interrupt();
        }
    }

    public static void sleepNoException(int min, int max) {
        try {
            Thread.sleep(random(min, max));
        } catch (InterruptedException e) {
            e.printStackTrace();
            currentThread().interrupt();
        }
    }

    /**
     * Sleeps until the passed predicate returns true.
     * @param predicate predicate
     */
    @Deprecated
    public static void sleepUntil(StatePredicate predicate) {
        while(!predicate.apply() && !currentThread().isInterrupted()) {
            sleepNoException(random(10, 20)); // Prevent it from slerping CPU.
        }
    }

    /**
     * Sleeps until the passed predicate returns true or sleeping for longer than the timeout.
     * @param predicate predicate
     * @param timeOut millis time out
     */
    public static boolean sleepUntil(StatePredicate predicate, long timeOut) {
        Timer timer = new Timer(timeOut);
        boolean success;
        while((success = !predicate.apply()) && !currentThread().isInterrupted() && !timer.finished()) {
            sleepNoException(random(10, 20)); // Prevent it from slerping CPU.
        }
        return !success;
    }

    /**
     * Sleeps until the passed predicate returns false.
     * @param predicate predicate
     */
    @Deprecated
    public static void sleepWhile(StatePredicate predicate) {
        while(predicate.apply() && !currentThread().isInterrupted()) {
            sleepNoException(random(10, 20)); // Prevent it from slerping CPU.
        }
    }

    /**
     * Sleeps until the passed predicate returns false.
     * @param predicate predicate
     * @param timeOut millis time out
     */
    public static boolean sleepWhile(StatePredicate predicate, long timeOut) {
        Timer timer = new Timer(timeOut);
        boolean success;
        while((success = predicate.apply()) && !currentThread().isInterrupted() && !timer.finished()) {
            sleepNoException(random(10, 20)); // Prevent it from slerping CPU.
        }
        return !success;
    }

}