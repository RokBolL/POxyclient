package ru.shun.arasakafabric.client;
import net.fabricmc.api.ClientModInitializer;
import ru.shun.arasakafabric.modules.ModuleManager;
public class ArasakaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModuleManager.moduleRegister();
        System.out.println("Shkolnik Client Mod initialized!");
    }
} 
