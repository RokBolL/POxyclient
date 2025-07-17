package ru.shun.arasakafabric.client.ui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;
import ru.shun.arasakafabric.client.util.DrawHelper;
import ru.shun.arasakafabric.client.util.RenderUtil;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
public class CustomTextField implements Element, Drawable {
    private final TextRenderer textRenderer;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private String text = "";
    private String suggestion = "";
    private int maxLength = 32;
    private int cursorCounter;
    private boolean focused;
    private boolean visible = true;
    private boolean editable = true;
    private int cursorPosition;
    private int selectionEnd;
    private int firstCharacterIndex;
    private Predicate<String> textPredicate;
    private Consumer<String> changeListener;
    private Consumer<String> suggestionUpdater;
    private Runnable focusLostCallback;
    private final Color backgroundColor = new Color(10, 20, 45, 230);
    private final Color focusedBackgroundColor = new Color(15, 30, 65, 230);
    private final Color borderColor = new Color(30, 80, 200, 255);
    private final Color textColor = new Color(220, 220, 220, 255);
    private final Color suggestionColor = new Color(150, 150, 150, 150);
    private final Color selectionColor = new Color(30, 80, 200, 150);
    private final Color cursorColor = new Color(220, 220, 220, 255);
    private final Color glowColor = new Color(50, 100, 255, 120);
    public CustomTextField(TextRenderer textRenderer, int x, int y, int width, int height) {
        this.textRenderer = textRenderer;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public CustomTextField(TextRenderer textRenderer, int x, int y, int width, int height, String suggestion) {
        this(textRenderer, x, y, width, height);
        this.suggestion = suggestion;
    }
    public void setTextPredicate(Predicate<String> textPredicate) {
        this.textPredicate = textPredicate;
    }
    public void setChangeListener(Consumer<String> changeListener) {
        this.changeListener = changeListener;
    }
    public void setSuggestionUpdater(Consumer<String> suggestionUpdater) {
        this.suggestionUpdater = suggestionUpdater;
    }
    public void setFocusLostCallback(Runnable focusLostCallback) {
        this.focusLostCallback = focusLostCallback;
    }
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
    public String getText() {
        return this.text;
    }
    public void setText(String text) {
        if (this.textPredicate == null || this.textPredicate.test(text)) {
            if (text.length() > this.maxLength) {
                this.text = text.substring(0, this.maxLength);
            } else {
                this.text = text;
            }
            this.setCursorToEnd();
            this.setSelectionEnd(this.cursorPosition);
            this.onChanged(text);
        }
    }
    public String getSelectedText() {
        int i = Math.min(this.cursorPosition, this.selectionEnd);
        int j = Math.max(this.cursorPosition, this.selectionEnd);
        return this.text.substring(i, j);
    }
    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
    public void write(String text) {
        String string2;
        int i = Math.min(this.cursorPosition, this.selectionEnd);
        int j = Math.max(this.cursorPosition, this.selectionEnd);
        int k = this.maxLength - this.text.length() - (i - j);
        if (k <= 0) {
            return;
        }
        if (text.length() > k) {
            string2 = text.substring(0, k);
        } else {
            string2 = text;
        }
        String newText = new StringBuilder(this.text).replace(i, j, string2).toString();
        if (this.textPredicate == null || this.textPredicate.test(newText)) {
            this.text = newText;
            this.setSelectionStart(i + string2.length());
            this.setSelectionEnd(this.cursorPosition);
            this.onChanged(this.text);
        }
    }
    private void onChanged(String newText) {
        if (this.changeListener != null) {
            this.changeListener.accept(newText);
        }
        if (this.suggestionUpdater != null) {
            this.suggestionUpdater.accept(newText);
        }
    }
    private void erase(int offset) {
        if (this.text.isEmpty()) return;
        if (this.selectionEnd != this.cursorPosition) {
            this.write("");
        } else {
            String newText;
            if (offset < 0) {
                int start = Math.max(0, this.cursorPosition + offset);
                newText = new StringBuilder(this.text).delete(start, this.cursorPosition).toString();
                this.setSelectionStart(start);
            } else {
                int end = Math.min(this.text.length(), this.cursorPosition + offset);
                newText = new StringBuilder(this.text).delete(this.cursorPosition, end).toString();
            }
            if (this.textPredicate == null || this.textPredicate.test(newText)) {
                this.text = newText;
                this.onChanged(this.text);
            }
        }
    }
    public void setSelectionStart(int cursor) {
        this.cursorPosition = Math.max(0, Math.min(cursor, this.text.length()));
    }
    public void setSelectionEnd(int index) {
        this.selectionEnd = Math.max(0, Math.min(index, this.text.length()));
    }
    public void setCursorToStart() {
        this.setSelectionStart(0);
    }
    public void setCursorToEnd() {
        this.setSelectionStart(this.text.length());
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.isVisible() || !this.editable) {
            return false;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.setFocused(false);
            return true;
        }
        switch (keyCode) {
            case GLFW.GLFW_KEY_BACKSPACE:
                this.erase(-1);
                return true;
            case GLFW.GLFW_KEY_DELETE:
                this.erase(1);
                return true;
            case GLFW.GLFW_KEY_RIGHT:
                if ((modifiers & GLFW.GLFW_MOD_SHIFT) != 0) {
                    this.setSelectionEnd(this.selectionEnd + 1);
                } else {
                    this.setSelectionStart(this.cursorPosition + 1);
                    this.setSelectionEnd(this.cursorPosition);
                }
                return true;
            case GLFW.GLFW_KEY_LEFT:
                if ((modifiers & GLFW.GLFW_MOD_SHIFT) != 0) {
                    this.setSelectionEnd(this.selectionEnd - 1);
                } else {
                    this.setSelectionStart(this.cursorPosition - 1);
                    this.setSelectionEnd(this.cursorPosition);
                }
                return true;
            case GLFW.GLFW_KEY_HOME:
                if ((modifiers & GLFW.GLFW_MOD_SHIFT) != 0) {
                    this.setSelectionEnd(0);
                } else {
                    this.setCursorToStart();
                    this.setSelectionEnd(this.cursorPosition);
                }
                return true;
            case GLFW.GLFW_KEY_END:
                if ((modifiers & GLFW.GLFW_MOD_SHIFT) != 0) {
                    this.setSelectionEnd(this.text.length());
                } else {
                    this.setCursorToEnd();
                    this.setSelectionEnd(this.cursorPosition);
                }
                return true;
            case GLFW.GLFW_KEY_V:
                if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
                    MinecraftClient client = MinecraftClient.getInstance();
                    this.write(client.keyboard.getClipboard());
                    return true;
                }
                break;
            case GLFW.GLFW_KEY_C:
                if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
                    MinecraftClient client = MinecraftClient.getInstance();
                    client.keyboard.setClipboard(this.getSelectedText());
                    return true;
                }
                break;
            case GLFW.GLFW_KEY_X:
                if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
                    MinecraftClient client = MinecraftClient.getInstance();
                    client.keyboard.setClipboard(this.getSelectedText());
                    this.write("");
                    return true;
                }
                break;
            case GLFW.GLFW_KEY_A:
                if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
                    this.setCursorToEnd();
                    this.setSelectionEnd(0);
                    return true;
                }
                break;
        }
        return false;
    }
    @Override
    public boolean charTyped(char c, int modifiers) {
        if (!this.isVisible() || !this.editable) {
            return false;
        }
        if (isAllowedCharacter(c)) {
            this.write(Character.toString(c));
            return true;
        }
        return false;
    }
    private static boolean isAllowedCharacter(char c) {
        return c != 167 && c >= 32 && c != 127;
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isVisible()) {
            return false;
        }
        boolean wasMouseOver = this.isMouseOver(mouseX, mouseY);
        if (wasMouseOver && button == 0) {
            int i = (int)(mouseX - this.x);
            String visibleText = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
            this.setSelectionStart(this.textRenderer.trimToWidth(visibleText, i).length() + this.firstCharacterIndex);
            this.setSelectionEnd(this.cursorPosition);
            return this.setFocused(true);
        } else {
            return this.isFocused() && button == 0 && !wasMouseOver ? this.setFocused(false) : false;
        }
    }
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!this.isVisible()) {
            return;
        }
        this.cursorCounter++;
        Color bgColor = this.isFocused() ? focusedBackgroundColor : backgroundColor;
        if (this.isFocused()) {
            RenderUtil.drawRoundedRectWithGlow(
                this.x,
                this.y,
                this.width,
                this.height,
                3, 
                5, 
                bgColor,
                glowColor
            );
        } else {
            DrawHelper.drawRoundedRect(
                this.x,
                this.y + this.height, 
                this.width,
                this.height,
                3, 
                bgColor
            );
        }
        DrawHelper.drawRoundedRectOutline(
            this.x,
            this.y + this.height, 
            this.width,
            this.height,
            3, 
            1f, 
            borderColor
        );
        int i = this.cursorPosition - this.firstCharacterIndex;
        int j = this.selectionEnd - this.firstCharacterIndex;
        String visibleText = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
        boolean cursorVisible = i >= 0 && i <= visibleText.length();
        int textX = this.x + 4;
        int textY = this.y + (this.height - 8) / 2;
        int l = textX;
        if (visibleText.isEmpty() && !this.suggestion.isEmpty() && !this.isFocused()) {
            this.textRenderer.drawWithShadow(matrices, this.suggestion, l, textY, suggestionColor.getRGB());
        }
        if (j > visibleText.length()) {
            j = visibleText.length();
        }
        if (!visibleText.isEmpty()) {
            String before = visibleText.substring(0, cursorVisible ? i : 0);
            l = this.textRenderer.drawWithShadow(matrices, this.text.substring(this.firstCharacterIndex, this.firstCharacterIndex + before.length()), l, textY, textColor.getRGB());
        }
        int cursorX = l;
        if (this.cursorPosition < this.text.length() && !visibleText.isEmpty() && i < visibleText.length()) {
            int selStart = Math.min(i, j);
            int selEnd = Math.max(i, j);
            if (selStart != selEnd) {
                int selWidth = this.textRenderer.getWidth(visibleText.substring(selStart, selEnd));
                int selX = textX + this.textRenderer.getWidth(visibleText.substring(0, selStart));
                RenderUtil.drawRect(selX, textY - 1, selWidth, 10, selectionColor.getRGB());
            }
            this.textRenderer.drawWithShadow(matrices, visibleText, textX, textY, textColor.getRGB());
        }
        boolean shouldRenderCursor = this.isFocused() && this.cursorCounter / 12 % 2 == 0;
        if (shouldRenderCursor && cursorVisible) {
            if (this.cursorPosition < this.text.length()) {
                RenderUtil.drawRect(cursorX, textY - 1, 1, 10, cursorColor.getRGB());
            } else {
                this.textRenderer.drawWithShadow(matrices, "_", cursorX, textY, cursorColor.getRGB());
            }
        }
    }
    private int getInnerWidth() {
        return this.width - 8;
    }
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;
    }
    public boolean isFocused() {
        return this.focused;
    }
    public boolean setFocused(boolean focused) {
        if (focused != this.focused) {
            this.focused = focused;
            if (!focused && this.focusLostCallback != null) {
                this.focusLostCallback.run();
            }
        }
        return focused;
    }
    public boolean isVisible() {
        return this.visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
} 
