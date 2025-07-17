package ru.shun.arasakafabric.client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class AltManager {
    private static final AltManager INSTANCE = new AltManager();
    private final List<Alt> alts = new ArrayList<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File altsDirectory;
    private File altsFile;
    public static AltManager getInstance() {
        return INSTANCE;
    }
    public AltManager() {
        try {
            File gameDir = MinecraftClient.getInstance().runDirectory;
            System.out.println("[AltManager] Директория игры: " + gameDir.getAbsolutePath());
            altsDirectory = new File(gameDir, "Arasaka/Alts");
            if (!altsDirectory.exists()) {
                boolean created = altsDirectory.mkdirs();
                System.out.println("[AltManager] Создание директории " + altsDirectory.getAbsolutePath() + ": " + (created ? "успешно" : "не удалось"));
            } else {
                System.out.println("[AltManager] Директория уже существует: " + altsDirectory.getAbsolutePath());
            }
            altsFile = new File(altsDirectory, "alts.json");
            System.out.println("[AltManager] Файл альтов будет: " + altsFile.getAbsolutePath());
            loadAlts();
        } catch (Exception e) {
            System.out.println("[AltManager] Ошибка при инициализации: " + e.getMessage());
            e.printStackTrace();
            altsDirectory = new File("Arasaka/Alts");
            if (!altsDirectory.exists()) {
                altsDirectory.mkdirs();
            }
            altsFile = new File(altsDirectory, "alts.json");
        }
    }
    public void addAlt(String username) {
        Alt alt = new Alt(username);
        if (!alts.contains(alt)) {
            alts.add(alt);
            saveAlts();
        }
    }
    public void removeAlt(Alt alt) {
        alts.remove(alt);
        saveAlts();
    }
    public List<Alt> getAlts() {
        return alts;
    }
    public void changeNickname(String nickname) {
        try {
            System.out.println("[AltManager] Попытка изменить никнейм на: " + nickname);
            MinecraftClient mc = MinecraftClient.getInstance();
            Class<?> mcClass = MinecraftClient.class;
            Field sessionField = null;
            try {
                sessionField = mcClass.getDeclaredField("session");
            } catch (NoSuchFieldException e) {
                for (Field field : mcClass.getDeclaredFields()) {
                    if (field.getType().equals(Session.class)) {
                        sessionField = field;
                        System.out.println("[AltManager] Найдено поле сессии: " + field.getName());
                        break;
                    }
                }
                if (sessionField == null) {
                    System.out.println("[AltManager] Не удалось найти поле сессии");
                    return;
                }
            }
            sessionField.setAccessible(true);
            Session currentSession = mc.getSession();
            System.out.println("[AltManager] Текущая сессия: " + currentSession.getUsername());
            try {
                Field usernameField = null;
                try {
                    usernameField = Session.class.getDeclaredField("username");
                } catch (NoSuchFieldException e) {
                    for (Field field : Session.class.getDeclaredFields()) {
                        if (field.getType().equals(String.class)) {
                            field.setAccessible(true);
                            Object value = field.get(currentSession);
                            if (value != null && value.equals(currentSession.getUsername())) {
                                usernameField = field;
                                System.out.println("[AltManager] Найдено поле имени: " + field.getName());
                                break;
                            }
                        }
                    }
                }
                if (usernameField != null) {
                    usernameField.setAccessible(true);
                    usernameField.set(currentSession, nickname);
                    System.out.println("[AltManager] Никнейм изменен прямым способом на: " + nickname);
                    addAlt(nickname);
                    return;
                } else {
                    System.out.println("[AltManager] Не удалось найти поле username в классе Session");
                }
            } catch (Exception e) {
                System.out.println("[AltManager] Ошибка при прямом изменении имени: " + e.getMessage());
            }
            try {
                System.out.println("[AltManager] Пробуем создать новую сессию...");
                String uuid = currentSession.getUuid();
                String accessToken = currentSession.getAccessToken();
                String accountType = "mojang"; 
                System.out.println("[AltManager] UUID: " + uuid);
                System.out.println("[AltManager] AccessToken длина: " + (accessToken != null ? accessToken.length() : "null"));
                Session newSession = new Session(nickname, uuid, accessToken, accountType);
                System.out.println("[AltManager] Новая сессия создана: " + newSession.getUsername());
                sessionField.set(mc, newSession);
                System.out.println("[AltManager] Новая сессия установлена");
                addAlt(nickname);
                Session currentSessionAfter = mc.getSession();
                System.out.println("[AltManager] Сессия после изменения: " + currentSessionAfter.getUsername());
                if (!currentSessionAfter.getUsername().equals(nickname)) {
                    System.out.println("[AltManager] ВНИМАНИЕ: Имя в сессии не изменилось!");
                }
            } catch (Exception e) {
                System.out.println("[AltManager] Ошибка при создании новой сессии: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("[AltManager] Критическая ошибка при изменении никнейма: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void saveAlts() {
        try {
            if (!altsDirectory.exists()) {
                altsDirectory.mkdirs();
            }
            try (Writer writer = new FileWriter(altsFile)) {
                gson.toJson(alts, writer);
            }
            System.out.println("[AltManager] Альты успешно сохранены в: " + altsFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("[AltManager] Ошибка при сохранении альтов: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void loadAlts() {
        if (!altsFile.exists()) {
            System.out.println("[AltManager] Файл альтов не найден, будет создан новый при сохранении.");
            return;
        }
        try (Reader reader = new FileReader(altsFile)) {
            Type type = new TypeToken<List<Alt>>(){}.getType();
            List<Alt> loadedAlts = gson.fromJson(reader, type);
            if (loadedAlts != null) {
                alts.clear();
                alts.addAll(loadedAlts);
                System.out.println("[AltManager] Загружено " + alts.size() + " альтов из: " + altsFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("[AltManager] Ошибка при загрузке альтов: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public String getAltsFilePath() {
        return altsFile.getAbsolutePath();
    }
    public static class Alt {
        private String username;
        private long creationDate; 
        public Alt(String username) {
            this.username = username;
            this.creationDate = System.currentTimeMillis();
        }
        public String getUsername() {
            return username;
        }
        public long getCreationDate() {
            return creationDate;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Alt alt = (Alt) obj;
            return username.equals(alt.username);
        }
        @Override
        public int hashCode() {
            return username.hashCode();
        }
    }
} 
