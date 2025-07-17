package ru.shun.arasakafabric.modules;
import ru.shun.arasakafabric.modules.imple.combat.CrystalExplode;
import ru.shun.arasakafabric.modules.imple.movement.AutoSprint;
import ru.shun.arasakafabric.modules.imple.movement.Speed;
import ru.shun.arasakafabric.modules.imple.player.AntiAim;
import ru.shun.arasakafabric.modules.imple.render.Gamma;
import ru.shun.arasakafabric.modules.imple.render.Hud;
import ru.shun.arasakafabric.modules.imple.render.NameTags;
import ru.shun.arasakafabric.modules.imple.render.TestMultiSetting;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
public class ModuleManager {
    private static List<Module> modules;
    public static void moduleRegister() {
        modules = new ArrayList<>();
        link(new AutoSprint());
        link(new Gamma());
        link(new Hud());
        link(new AntiAim());
        link(new Speed());
        link(new TestMultiSetting());
        link(new NameTags());
        link(new CrystalExplode());
    }
    public static void link(Module moduleLink) {
        modules.add(moduleLink);
    }
    public static List<Module> getModules() {
        return modules;
    }
    public static Module getByName(String name) {
        return modules.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
    public static List<Module> getByCategory(Category cat) {
        return modules.stream()
                .filter(m -> m.getCategory() == cat)
                .collect(Collectors.toList());
    }
    @SuppressWarnings("unchecked")
    public static <T extends Module> T getModule(Class<T> clazz) {
        for (Module module : modules) {
            if (module.getClass() == clazz) {
                return (T) module;
            }
        }
        return null;
    }
}

