package ru.shun.arasakafabric.mixin;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.shun.arasakafabric.Arasaka;
import ru.shun.arasakafabric.command.CommandManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
@Mixin(ChatScreen.class)
public class MixinChatScreen {
    @Shadow
    protected TextFieldWidget chatField;
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (keyCode == 257 || keyCode == 335) { 
            String message = this.chatField.getText();
            if (!message.isEmpty() && Arasaka.getInstance().getCommandManager().handleMessage(message)) {
                this.chatField.setText("");
                cir.setReturnValue(true);
            }
        }
    }
    @Inject(method = "onChatFieldUpdate", at = @At("RETURN"))
    private void onChatFieldUpdate(String text, CallbackInfo ci) {
        if (text.startsWith(CommandManager.PREFIX)) {
            String inputWithoutPrefix = text.substring(CommandManager.PREFIX.length());
            String[] parts = inputWithoutPrefix.split(" ");
            List<String> suggestions = new ArrayList<>();
            CommandManager commandManager = Arasaka.getInstance().getCommandManager();
            if (parts.length <= 1) {
                suggestions = getCommandSuggestions(inputWithoutPrefix);
            } 
            else {
                String commandName = parts[0].toLowerCase();
                String[] args;
                if (text.endsWith(" ")) {
                    args = new String[parts.length]; 
                    System.arraycopy(parts, 1, args, 0, parts.length - 1);
                    args[args.length - 1] = ""; 
                } else {
                    args = Arrays.copyOfRange(parts, 1, parts.length);
                }
                suggestions = commandManager.getCommandArgSuggestions(commandName, args);
            }
            if (!suggestions.isEmpty()) {
                showSuggestions(text, suggestions);
            }
        }
    }
    private void showSuggestions(String text, List<String> suggestions) {
        try {
            int cursorPos = chatField.getCursor();
            int lastSpacePos = text.lastIndexOf(' ');
            int startPos = lastSpacePos == -1 ? CommandManager.PREFIX.length() : lastSpacePos + 1;
            SuggestionsBuilder builder = new SuggestionsBuilder(text, startPos);
            if (lastSpacePos == -1) {
                for (String suggestion : suggestions) {
                    builder.suggest(CommandManager.PREFIX + suggestion);
                }
            } else {
                String prefix = text.substring(0, lastSpacePos + 1);
                for (String suggestion : suggestions) {
                    builder.suggest(prefix + suggestion);
                }
            }
            Suggestions suggestionsList = builder.build();
            ChatScreen screen = (ChatScreen)(Object)this;
            java.lang.reflect.Field suggestionsField = ChatScreen.class.getDeclaredField("suggestions");
            suggestionsField.setAccessible(true);
            CompletableFuture<Suggestions> future = CompletableFuture.completedFuture(suggestionsList);
            suggestionsField.set(screen, future);
            java.lang.reflect.Method showSuggestionsMethod = ChatScreen.class.getDeclaredMethod("showSuggestions");
            showSuggestionsMethod.setAccessible(true);
            showSuggestionsMethod.invoke(screen);
        } catch (Exception e) {
            System.err.println("Ошибка при установке подсказок: " + e.getMessage());
        }
    }
    private List<String> getCommandSuggestions(String prefix) {
        CommandManager commandManager = Arasaka.getInstance().getCommandManager();
        List<String> commands = commandManager.getCommandNames();
        if (prefix.isEmpty()) {
            return commands;
        } else {
            List<String> filtered = new ArrayList<>();
            for (String cmd : commands) {
                if (cmd.toLowerCase().startsWith(prefix.toLowerCase())) {
                    filtered.add(cmd);
                }
            }
            return filtered;
        }
    }
} 
