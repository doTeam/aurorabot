package ms.aurora.core.model;

import javax.persistence.*;
import java.util.List;

/**
 * @author rvbiljouw
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "scriptSource.getBySource", query = "select s from ScriptSource s where s.source = :source"),
        @NamedQuery(name = "scriptSource.getAll", query = "select s from ScriptSource s")
})
public final class ScriptSource extends AbstractModel {

    @Id
    @GeneratedValue
    private Long id;
    private String source;
    private boolean devMode;

    public ScriptSource(String source, boolean devMode) {
        this.source = source;
        this.devMode = devMode;
    }

    public ScriptSource() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isDevMode() {
        return devMode;
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    public static List<ScriptSource> getAll() {
        TypedQuery<ScriptSource> query = getEm().createNamedQuery("scriptSource.getAll", ScriptSource.class);
        return query.getResultList();
    }

    public static List<ScriptSource> getBySource(String source) {
        TypedQuery<ScriptSource> query = getEm().createNamedQuery("scriptSource.getBySource",
                ScriptSource.class).setParameter("source", source);
        return query.getResultList();
    }
}