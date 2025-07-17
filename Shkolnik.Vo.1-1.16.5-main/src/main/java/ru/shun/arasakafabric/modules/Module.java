package ru.shun.arasakafabric.modules;
import net.minecraft.client.MinecraftClient;
import ru.shun.arasakafabric.modules.notify.NotificationRenderer;
import ru.shun.arasakafabric.ui.ISetting;
import java.util.ArrayList;
import java.util.List;
public abstract class Module {
    private final String name;
    private final Category category;
    private final String desc;
    private int bind;
    private boolean enabled;
    protected List<ISetting> settings = new ArrayList<>();
    public List<ISetting> getSettings() {
        return settings;
    }
    public void addSetting(ISetting setting) {
        settings.add(setting);
    }
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public Module() {
        ModuleInform moduleInform = getClass().getAnnotation(ModuleInform.class);
        this.name = moduleInform.name();
        this.category = moduleInform.category();
        this.desc = moduleInform.description();
        this.bind = moduleInform.keybind();
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) onEnable(); else onDisable();
        }
    }
    public void toggled() {
        setEnabled(!enabled);
        NotificationRenderer.add(getName() + " " + (enabled ? "включен" : "выключен"));
    }
    public String getName() {
        return name;
    }
    public Category getCategory() {
        return category;
    }
    public String getDesc() {
        return desc;
    }
    public int getBind() {
        return bind;
    }
    public void setBind(int bind) {
        this.bind = bind;
    }
    public void onEnable() {
    }
    public void onDisable() {
    }
    public abstract void onTick();
}

