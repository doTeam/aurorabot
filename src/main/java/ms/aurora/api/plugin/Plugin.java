package ms.aurora.api.plugin;

import javafx.scene.control.Menu;
import ms.aurora.api.Context;
import org.apache.log4j.Logger;

/**
 * @author Rick
 */
public abstract class Plugin extends Context {
    private final Logger logger = Logger.getLogger(getClass());

    public Plugin() {

    }

    public final void info(String message) {
        logger.info(message);
    }

    public final void debug(String message) {
        logger.debug(message);
    }

    public final void error(String message) {
        logger.error(message);
    }

    public final void error(String message, Throwable t) {
        logger.error(message, t);
    }

    public final void setState(PluginState state) {
        switch (state) {
            case INIT:
                init();
                startup();
                break;

            case ACTIVE:
                execute();
                break;

            case STOPPED:
                cleanup();
                shutdown();
                break;
        }
    }

    private void init() {
        getSession().getEventBus().register(this);
    }

    private void shutdown() {
        getSession().getEventBus().deregister(this);
    }

    public abstract void startup();

    public abstract void execute();

    public abstract void cleanup();

    public final boolean validate() {
        return getManifest() != null;
    }

    public final PluginManifest getManifest() {
        return getClass().getAnnotation(PluginManifest.class);
    }

    public void registerMenu(Menu menu) {
        getSession().getUI().registerMenu(menu);
    }

    public void deregisterMenu(Menu menu) {
        getSession().getUI().deregisterMenu(menu);
    }
}
