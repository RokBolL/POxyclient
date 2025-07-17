package ru.shun.arasakafabric;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import ru.shun.arasakafabric.client.AltManager;
import ru.shun.arasakafabric.client.FriendManager;
import ru.shun.arasakafabric.command.CommandManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class Arasaka implements ModInitializer {
    public static final String MOD_ID = "arasakafabric";
    public static final Identifier BACKGROUND_TEXTURE = new Identifier(MOD_ID, "textures/gui/background.png");
    private static Arasaka instance;
    private CommandManager commandManager;
    public Arasaka() {
        instance = this;
        commandManager = new CommandManager();
    }
    public static Arasaka getInstance() {
        return instance;
    }
    public CommandManager getCommandManager() {
        return commandManager;
    }
    @Override
    public void onInitialize() {
        System.out.println("[ArasakaFabric] Инициализация мода");
        commandManager.init();
        System.out.println("[ArasakaFabric] Система команд инициализирована");
        AltManager.getInstance();
        System.out.println("[ArasakaFabric] AltManager инициализирован");
        FriendManager.getInstance();
        System.out.println("[ArasakaFabric] FriendManager инициализирован");
    }
}

