package ru.shun.arasakafabric.ui.imple;
import ru.shun.arasakafabric.ui.ISetting;
public class NumberSetting implements ISetting {
    private final String name;
    private double value, min, max, increment;
    public NumberSetting(String name, double value, double min, double max, double increment) {
        this.name = name;
        this.value = value;
        this.min = min;
        this.max = max;
        this.increment = increment;
    }
    @Override
    public String getName() {
        return name;
    }
    public double getDoubleValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = Math.max(min, Math.min(max, value));
    }
    public void increase() {
        setValue(value + increment);
    }
    public void decrease() {
        setValue(value - increment);
    }
    public double getMin() {
        return min;
    }
    public double getMax() {
        return max;
    }
    public double getIncrement() {
        return increment;
    }
    @Override
    public Object getValue() {
        return value;
    }
    @Override
    public void fromString(String stringValue) {
        if (stringValue != null) {
            try {
                setValue(Double.parseDouble(stringValue));
            } catch (NumberFormatException e) {
            }
        }
    }
}

