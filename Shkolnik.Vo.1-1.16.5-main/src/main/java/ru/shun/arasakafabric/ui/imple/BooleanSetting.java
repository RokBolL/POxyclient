package ru.shun.arasakafabric.ui.imple;
import ru.shun.arasakafabric.ui.ISetting;
public class BooleanSetting implements ISetting {
    private final String name;
    private boolean enabled;
    public BooleanSetting(String name, boolean defaultValue) {
        this.name = name;
        this.enabled = defaultValue;
    }
    @Override
    public String getName() {
        return name;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void toggle() {
        enabled = !enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    @Override
    public Object getValue() {
        return enabled;
    }
    @Override
    public void fromString(String value) {
        if (value != null) {
            this.enabled = Boolean.parseBoolean(value);
        }
    }
}
