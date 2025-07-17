package ru.shun.arasakafabric.client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.MinecraftClient;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
public class FriendManager {
    private static FriendManager instance;
    private final List<String> friends = new ArrayList<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path savePath = Paths.get(MinecraftClient.getInstance().runDirectory.getPath(), "Arasaka", "Friends", "friends.json");
    private FriendManager() {
        loadFriends();
    }
    public static FriendManager getInstance() {
        if (instance == null) {
            instance = new FriendManager();
        }
        return instance;
    }
    public boolean addFriend(String name) {
        if (!isFriend(name)) {
            friends.add(name);
            saveFriends();
            return true;
        }
        return false;
    }
    public boolean removeFriend(String name) {
        if (isFriend(name)) {
            friends.remove(name);
            saveFriends();
            return true;
        }
        return false;
    }
    public boolean isFriend(String name) {
        return friends.stream().anyMatch(friend -> friend.equalsIgnoreCase(name));
    }
    public List<String> getFriends() {
        return new ArrayList<>(friends);
    }
    private void saveFriends() {
        try {
            File saveDir = savePath.getParent().toFile();
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            String json = gson.toJson(friends);
            try (FileWriter writer = new FileWriter(savePath.toFile())) {
                writer.write(json);
            }
        } catch (IOException e) {
            System.out.println("[ArasakaFabric] Ошибка при сохранении списка друзей: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void loadFriends() {
        try {
            if (Files.exists(savePath)) {
                String json = new String(Files.readAllBytes(savePath));
                List<String> loadedFriends = gson.fromJson(json, new TypeToken<List<String>>(){}.getType());
                if (loadedFriends != null) {
                    friends.clear();
                    friends.addAll(loadedFriends);
                }
            }
        } catch (IOException e) {
            System.out.println("[ArasakaFabric] Ошибка при загрузке списка друзей: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 
