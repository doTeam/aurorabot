package ms.aurora.loader;

public enum Language {
    ENGLISH(0), GERMAN(1), FRENCH(2), PORTUGUESE(3);

    private final int code;

    Language(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return (getCode() >= 1 ? "/l=" + getCode() : "");
    }
}