package ru.shun.arasakafabric.ui;
import java.util.ArrayList;
import java.util.List;
public class Setting {
    public static final List<ISetting> SETTINGS = new ArrayList<>();
    public static void register(ISetting setting) {
        SETTINGS.add(setting);
    }
    public static List<ISetting> getAll() {
        return SETTINGS;
    }
}

