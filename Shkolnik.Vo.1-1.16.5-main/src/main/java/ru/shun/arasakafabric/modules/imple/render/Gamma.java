package ru.shun.arasakafabric.modules.imple.render;
import ru.shun.arasakafabric.event.EventBus;
import ru.shun.arasakafabric.event.impl.EventTick;
import ru.shun.arasakafabric.modules.Category;
import ru.shun.arasakafabric.modules.Module;
import ru.shun.arasakafabric.modules.ModuleInform;
@ModuleInform(
        name = "FullBright",
        description = "All Rights",
        category = Category.RENDER
)
public class Gamma extends Module {
    private final float MAX_GAMMA = 2000f;
    private final float MIN_GAMMA = 0.1f;
    public Gamma() {
    }
    @Override
    public void onEnable() {
        EventBus.subscribe(EventTick.class, eventTick -> {
            if (isEnabled()) {
                mc.options.gamma = MAX_GAMMA;
            } else {
                mc.options.gamma = MIN_GAMMA;
            }
        });
    }
    @Override
    public void onDisable() {
        mc.options.gamma = MIN_GAMMA;
    }
    @Override
    public void onTick() {
    }
} 
