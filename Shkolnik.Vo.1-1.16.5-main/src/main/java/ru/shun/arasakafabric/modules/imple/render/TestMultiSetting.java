package ru.shun.arasakafabric.modules.imple.render;
import ru.shun.arasakafabric.modules.Category;
import ru.shun.arasakafabric.modules.Module;
import ru.shun.arasakafabric.modules.ModuleInform;
import ru.shun.arasakafabric.ui.imple.MultiSetting;
@ModuleInform(
        name = "ТестМульти",
        description = "Тестовый модуль для демонстрации MultiSetting",
        category = Category.RENDER
)
public class TestMultiSetting extends Module {
    private final MultiSetting entityTypes;
    private final MultiSetting effectTypes;
    public TestMultiSetting() {
        entityTypes = new MultiSetting("Существа", 
                "Игроки", 
                "Мобы", 
                "Животные", 
                "Предметы");
        effectTypes = new MultiSetting("Эффекты",
                "Свечение",
                "Контур",
                "Имя",
                "Здоровье");
        entityTypes.setEnabled("Игроки", true);
        effectTypes.setEnabled("Свечение", true);
        addSetting(entityTypes);
        addSetting(effectTypes);
    }
    @Override
    public void onEnable() {
        super.onEnable();
    }
    @Override
    public void onDisable() {
        super.onDisable();
    }
    @Override
    public void onTick() {
    }
} 
