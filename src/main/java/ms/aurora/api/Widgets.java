package ms.aurora.api;

import com.google.common.base.Function;
import ms.aurora.api.rt3.Widget;
import ms.aurora.api.wrappers.RSWidget;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newArrayList;
import static ms.aurora.api.ClientContext.context;

/**
 * @author tobiewarburton
 * @author rvbiljouw
 */
public final class Widgets {

    private Widgets() {
    }

    public static RSWidget getWidget(int parent, int child) {
        Widget[][] cache = context.get().getClient().getWidgetCache();
        return new RSWidget(context.get(), cache[parent][child]);
    }

    public static RSWidget[] getWidgets(int parent) {
        Widget[][] cache = context.get().getClient().getWidgetCache();
        return transform(newArrayList(cache[parent]), transform).toArray(new RSWidget[0]);
    }

    /**
     * @param predicate the string to search for
     * @return a list of all the RSWidget that contain the specified text
     */
    public static RSWidget[] getWidgetsWithText(String predicate) {
        List<RSWidget> satisfied = new ArrayList<RSWidget>();
        for (Widget[] parents : context.get().getClient().getWidgetCache()) {
            for (RSWidget child : transform(newArrayList(parents), transform).toArray(new RSWidget[0])) {
                if (child.getText() != null && child.getText().contains(predicate)) {
                    satisfied.add(child);
                }
            }
        }
        return satisfied.toArray(new RSWidget[0]);
    }

    private static final Function<Widget, RSWidget> transform = new Function<Widget, RSWidget>() {
        @Override
        public RSWidget apply(Widget widget) {
            if (widget != null) {
                return new RSWidget(context.get(), widget);
            }
            return null;
        }
    };

}