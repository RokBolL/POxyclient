package ru.shun.arasakafabric.ui.imple;
import ru.shun.arasakafabric.ui.ISetting;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class MultiSetting implements ISetting {
    private final String name;
    private final Map<String, Boolean> options = new HashMap<>();
    private final List<String> optionNames = new ArrayList<>();
    private static final Gson gson = new Gson();
    public MultiSetting(String name, String... options) {
        this.name = name;
        for (String option : options) {
            this.optionNames.add(option);
            this.options.put(option, false);
        }
    }
    @Override
    public String getName() {
        return name;
    }
    public List<String> getOptions() {
        return optionNames;
    }
    public boolean isEnabled(String option) {
        return options.getOrDefault(option, false);
    }
    public void toggle(String option) {
        if (options.containsKey(option)) {
            options.put(option, !options.get(option));
        }
    }
    public void setEnabled(String option, boolean enabled) {
        if (options.containsKey(option)) {
            options.put(option, enabled);
        }
    }
    public int getOptionsCount() {
        return optionNames.size();
    }
    public String getOptionName(int index) {
        if (index >= 0 && index < optionNames.size()) {
            return optionNames.get(index);
        }
        return "";
    }
    @Override
    public Object getValue() {
        return gson.toJson(options);
    }
    @Override
    public void fromString(String value) {
        if (value != null && !value.isEmpty()) {
            try {
                Map<String, Boolean> loadedOptions = gson.fromJson(value, new TypeToken<Map<String, Boolean>>(){}.getType());
                for (String option : options.keySet()) {
                    options.put(option, false);
                }
                for (Map.Entry<String, Boolean> entry : loadedOptions.entrySet()) {
                    if (options.containsKey(entry.getKey())) {
                        options.put(entry.getKey(), entry.getValue());
                    }
                }
            } catch (Exception e) {
                System.out.println("[MultiSetting] Ошибка при загрузке значений: " + e.getMessage());
            }
        }
    }
} 
