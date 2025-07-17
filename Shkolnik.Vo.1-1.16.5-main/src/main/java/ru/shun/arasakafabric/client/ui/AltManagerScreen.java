package ru.shun.arasakafabric.client.ui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import ru.shun.arasakafabric.client.AltManager;
import ru.shun.arasakafabric.client.util.DrawHelper;
import ru.shun.arasakafabric.client.util.RenderUtil;
import java.awt.*;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
public class AltManagerScreen extends Screen {
    private Screen parent;
    private CustomTextField nicknameField;
    private int selectedAlt = -1;
    private String status = "";
    private long statusTime = 0;
    private int scrollOffset = 0; 
    private final int panelWidth = 500;
    private final int panelHeight = 400;
    private final int buttonHeight = 24;
    private final int buttonWidth = 140;
    private final int buttonSpacing = 12;
    private final Color darkBg = new Color(5, 10, 25, 240);       
    private final Color panelBg = new Color(10, 20, 45, 230);     
    private final Color accentColor = new Color(30, 80, 200, 255); 
    private final Color lightAccent = new Color(100, 150, 255, 255); 
    private final Color glowColor = new Color(50, 100, 255, 120);  
    private final Color activeModule = new Color(19, 21, 83, 215); 
    private final Color inactiveModule = new Color(5, 15, 30, 240); 
    private final Color textColor = new Color(220, 220, 220, 255); 
    public AltManagerScreen(Screen parent) {
        super(Text.of("Аккаунты"));
        this.parent = parent;
    }
    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        this.nicknameField = new CustomTextField(
                this.textRenderer,
                centerX - 180,
                centerY - panelHeight / 2 + 60,
                360,
                25,
                "Введите никнейм и нажмите Enter..."
        );
        this.nicknameField.setMaxLength(16);
        this.nicknameField.setFocusLostCallback(() -> {
            tryAddNickname();
        });
        this.children.add(this.nicknameField);
        addClickGuiButton(
                centerX - 180,
                centerY + panelHeight / 2 - 40,
                110,
                buttonHeight,
                "Случайный",
                button -> {
                    String randomName = "Player, Pesnya, Penis, pensil, bober, pensil_bobra" + (int)(Math.random() * 10000);
                    nicknameField.setText(randomName);
                }
        );
        addClickGuiButton(
                centerX + 70,
                centerY + panelHeight / 2 - 40,
                110,
                buttonHeight,
                "Очистить",
                button -> {
                    List<AltManager.Alt> alts = AltManager.getInstance().getAlts();
                    for (int i = alts.size() - 1; i >= 0; i--) {
                        AltManager.getInstance().removeAlt(alts.get(i));
                    }
                    selectedAlt = -1;
                    scrollOffset = 0; 
                    setStatus("Все альты удалены");
                }
        );
    }
    private void tryAddNickname() {
        String nickname = nicknameField.getText();
        if (nickname != null && !nickname.isEmpty()) {
            AltManager.getInstance().addAlt(nickname);
            setStatus("Добавлен никнейм: " + nickname);
            nicknameField.setText("");
        } else {
            setStatus("Введите никнейм");
        }
    }
    private void addClickGuiButton(int x, int y, int width, int height, String text, ButtonPressAction action) {
        this.addButton(new ClickGuiButton(x, y, width, height, text, action));
    }
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        RenderUtil.drawRoundedRectWithGlow(
                centerX - panelWidth / 2, 
                centerY - panelHeight / 2,
                panelWidth, 
                panelHeight, 
                5, 
                8, 
                darkBg, 
                glowColor
        );
        this.textRenderer.drawWithShadow(
                matrices, 
                "Аккаунты", 
                centerX - this.textRenderer.getWidth("Аккаунты") / 2, 
                centerY - panelHeight / 2 + 20, 
                accentColor.getRGB()
        );
        String subTitle = "Нажмите на любой, для входа в аккаунт (Текущий: " + MinecraftClient.getInstance().getSession().getUsername() + ")";
        this.textRenderer.drawWithShadow(
                matrices, 
                subTitle, 
                centerX - this.textRenderer.getWidth(subTitle) / 2,
                centerY - panelHeight / 2 + 40, 
                new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 180).getRGB()
        );
        String hintText = "Введите никнейм и нажмите Enter для добавления. ESC - выход";
        this.textRenderer.drawWithShadow(
                matrices, 
                hintText, 
                centerX - this.textRenderer.getWidth(hintText) / 2,
                centerY + panelHeight / 2 - -140,
                new Color(lightAccent.getRed(), lightAccent.getGreen(), lightAccent.getBlue(), 180).getRGB()
        );
        drawAltsList(matrices, centerX, centerY, mouseX, mouseY);
        if (System.currentTimeMillis() - statusTime < 3000) {
            this.textRenderer.drawWithShadow(
                    matrices, 
                    status, 
                    centerX - this.textRenderer.getWidth(status) / 2, 
                    centerY + panelHeight / 2 - 55, 
                    textColor.getRGB()
            );
        }
        this.nicknameField.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }
    private void drawAltsList(MatrixStack matrices, int centerX, int centerY, int mouseX, int mouseY) {
        int listX = centerX - panelWidth / 2 + 50;
        int listY = centerY - 100; 
        int listWidth = panelWidth - 100;
        int listHeight = 240;
        RenderUtil.drawRoundedRectWithGlow(
                listX, 
                listY, 
                listWidth, 
                listHeight, 
                4, 
                3, 
                panelBg, 
                glowColor
        );
        List<AltManager.Alt> alts = AltManager.getInstance().getAlts();
        if (alts.isEmpty()) {
            this.textRenderer.drawWithShadow(
                    matrices, 
                    "Нет сохраненных альтов", 
                    centerX - this.textRenderer.getWidth("Нет сохраненных альтов") / 2, 
                    centerY, 
                    new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 180).getRGB()
            );
            return;
        }
        int maxVisibleRows = 3; 
        int columns = 2;
        int altWidth = (listWidth - 20) / columns;
        int altHeight = 70;
        int spacing = 5;
        int maxVisibleAlts = maxVisibleRows * columns;
        int maxRows = (int) Math.ceil((double) alts.size() / columns);
        int maxScrollOffset = Math.max(0, maxRows - maxVisibleRows);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));
        if (maxScrollOffset > 0) {
            String scrollHint = "Используйте колесо мыши для прокрутки";
            this.textRenderer.drawWithShadow(
                    matrices,
                    scrollHint,
                    listX + listWidth - this.textRenderer.getWidth(scrollHint) - 10,
                    listY - 12,
                    new Color(lightAccent.getRed(), lightAccent.getGreen(), lightAccent.getBlue(), 180).getRGB()
            );
        }
        if (maxScrollOffset > 0) {
            float scrollPercentage = (float) scrollOffset / maxScrollOffset;
            int scrollBarHeight = 120; 
            int scrollBarY = listY + 10 + (int) (scrollPercentage * (listHeight - 20 - scrollBarHeight));
            DrawHelper.drawRoundedRect(
                    listX + listWidth - 7,
                    listY + listHeight - 10,
                    4,
                    listHeight - 20,
                    2,
                    new Color(20, 30, 50, 100)
            );
            DrawHelper.drawRoundedRect(
                    listX + listWidth - 7,
                    scrollBarY + scrollBarHeight,
                    4,
                    scrollBarHeight,
                    2,
                    accentColor
            );
        }
        for (int i = scrollOffset * columns; i < alts.size() && i < (scrollOffset + maxVisibleRows) * columns; i++) {
            AltManager.Alt alt = alts.get(i);
            int visibleIndex = i - scrollOffset * columns;
            int column = visibleIndex % columns;
            int row = visibleIndex / columns;
            int altX = listX + 10 + column * (altWidth + spacing);
            int altY = listY + 10 + row * (altHeight + spacing);
            Color moduleColor = (i == selectedAlt) ? activeModule : inactiveModule;
            Color hoverColor = new Color(moduleColor.getRed(), moduleColor.getGreen(), moduleColor.getBlue(), 180);
            boolean hovered = mouseX >= altX && mouseX <= altX + altWidth &&
                     mouseY >= altY && mouseY <= altY + altHeight;
            if (hovered || i == selectedAlt) {
                RenderUtil.drawRoundedRectWithGlow(
                        altX, 
                        altY, 
                        altWidth, 
                        altHeight, 
                        4, 
                        6, 
                        moduleColor, 
                        glowColor
                );
            } else {
                DrawHelper.drawRoundedRect(
                        altX, 
                        altY + altHeight, 
                        altWidth, 
                        altHeight, 
                        4, 
                        hovered ? hoverColor : moduleColor
                );
            }
            DrawHelper.drawRoundedRect(
                    altX + 10, 
                    altY + 10 + 32, 
                    32, 
                    32, 
                    4, 
                    new Color(200, 160, 80)
            );
            this.textRenderer.drawWithShadow(
                    matrices, 
                    alt.getUsername(), 
                    altX + 50, 
                    altY + 12, 
                    textColor.getRGB()
            );
            String dateText = "Дата создания";
            this.textRenderer.drawWithShadow(
                    matrices, 
                    dateText, 
                    altX + 50, 
                    altY + 30, 
                    new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 150).getRGB()
            );
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            String date = dateFormat.format(new Date(alt.getCreationDate()));
            this.textRenderer.drawWithShadow(
                    matrices, 
                    date, 
                    altX + 50, 
                    altY + 42, 
                    new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 150).getRGB()
            );
            boolean deleteHovered = mouseX >= altX + altWidth - 25 && mouseX <= altX + altWidth - 5 &&
                            mouseY >= altY + 5 && mouseY <= altY + 22;
            Color deleteButtonColor = deleteHovered ? 
                    new Color(50, 100, 200, 230) : 
                    new Color(30, 60, 150, 200);   
            RenderUtil.drawRoundedRectWithGlow(
                    altX + altWidth - 25,
                    altY + 5,
                    20,
                    17,
                    3, 
                    deleteHovered ? 4 : 2, 
                    deleteButtonColor,
                    glowColor 
            );
            this.textRenderer.drawWithShadow(
                    matrices, 
                    "✕", 
                    altX + altWidth - 17, 
                    altY + 9, 
                    Color.WHITE.getRGB()
            );
        }
        int remainingAlts = alts.size() - ((scrollOffset + maxVisibleRows) * columns);
        if (remainingAlts > 0) {
            this.textRenderer.drawWithShadow(
                    matrices, 
                    "И еще " + remainingAlts + " альтов...", 
                    listX + 10, 
                    listY + listHeight - 15, 
                    new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 180).getRGB()
            );
        }
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        List<AltManager.Alt> alts = AltManager.getInstance().getAlts();
        if (!alts.isEmpty()) {
            int centerX = this.width / 2;
            int centerY = this.height / 2;
            int listX = centerX - panelWidth / 2 + 50;
            int listY = centerY - 100; 
            int listWidth = panelWidth - 100;
            int listHeight = 240;
            int columns = 2;
            int altWidth = (listWidth - 20) / columns;
            int altHeight = 70;
            int spacing = 5;
            int maxVisibleRows = 3; 
            int maxVisibleAlts = maxVisibleRows * columns;
            boolean isInListArea = mouseX >= listX && mouseX <= listX + listWidth && 
                                mouseY >= listY && mouseY <= listY + listHeight;
            if (isInListArea) {
                for (int i = scrollOffset * columns; i < alts.size() && i < (scrollOffset + maxVisibleRows) * columns; i++) {
                    int visibleIndex = i - scrollOffset * columns;
                    int column = visibleIndex % columns;
                    int row = visibleIndex / columns;
                    int altX = listX + 10 + column * (altWidth + spacing);
                    int altY = listY + 10 + row * (altHeight + spacing);
                    if (mouseX >= altX + altWidth - 25 && mouseX <= altX + altWidth - 5 &&
                        mouseY >= altY + 5 && mouseY <= altY + 22) {
                        AltManager.Alt alt = alts.get(i);
                        AltManager.getInstance().removeAlt(alt);
                        if (selectedAlt == i) {
                            selectedAlt = -1;
                        } else if (selectedAlt > i) {
                            selectedAlt--;
                        }
                        int maxRows = (int) Math.ceil((double) (alts.size() - 1) / columns);
                        int maxScrollOffset = Math.max(0, maxRows - maxVisibleRows);
                        if (scrollOffset > maxScrollOffset) {
                            scrollOffset = maxScrollOffset;
                        }
                        setStatus("Альт удален: " + alt.getUsername());
                        return true;
                    }
                    if (mouseX >= altX && mouseX <= altX + altWidth - 26 &&
                        mouseY >= altY && mouseY <= altY + altHeight) {
                        selectedAlt = i;
                        AltManager.Alt alt = alts.get(i);
                        AltManager.getInstance().changeNickname(alt.getUsername());
                        nicknameField.setText(alt.getUsername());
                        setStatus("Выбран и установлен никнейм: " + alt.getUsername());
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        List<AltManager.Alt> alts = AltManager.getInstance().getAlts();
        if (!alts.isEmpty()) {
            int centerX = this.width / 2;
            int centerY = this.height / 2;
            int listX = centerX - panelWidth / 2 + 50;
            int listY = centerY - 100;
            int listWidth = panelWidth - 100;
            int listHeight = 240;
            boolean isInListArea = mouseX >= listX && mouseX <= listX + listWidth && 
                                mouseY >= listY && mouseY <= listY + listHeight;
            if (isInListArea) {
                int columns = 2;
                int maxVisibleRows = 3; 
                int maxRows = (int) Math.ceil((double) alts.size() / columns);
                int maxScrollOffset = Math.max(0, maxRows - maxVisibleRows);
                if (amount > 0) {
                    scrollOffset = Math.max(0, scrollOffset - 1);
                } else {
                    scrollOffset = Math.min(maxScrollOffset, scrollOffset + 1);
                }
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.openScreen(this.parent);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            if (this.nicknameField.isFocused()) {
                tryAddNickname();
                return true;
            }
        }
        List<AltManager.Alt> alts = AltManager.getInstance().getAlts();
        if (!alts.isEmpty()) {
            int columns = 2;
            int maxVisibleRows = 3; 
            if (keyCode == GLFW.GLFW_KEY_UP) {
                scrollOffset = Math.max(0, scrollOffset - 1);
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
                int maxRows = (int) Math.ceil((double) alts.size() / columns);
                int maxScrollOffset = Math.max(0, maxRows - maxVisibleRows);
                scrollOffset = Math.min(maxScrollOffset, scrollOffset + 1);
                return true;
            }
        }
        if (this.nicknameField.isFocused()) {
            return this.nicknameField.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (this.nicknameField.isFocused()) {
            return this.nicknameField.charTyped(chr, modifiers);
        }
        return false;
    }
    private void setStatus(String message) {
        this.status = message;
        this.statusTime = System.currentTimeMillis();
    }
    interface ButtonPressAction {
        void onPress(ClickGuiButton button);
    }
    private class ClickGuiButton extends net.minecraft.client.gui.widget.ButtonWidget {
        private final Color normalColor = inactiveModule;
        private final Color hoverColor = new Color(30, 40, 70, 230);
        private final Color pressColor = activeModule;
        private final Color textColor = new Color(220, 220, 220, 255);
        private boolean isPressed = false;
        public ClickGuiButton(int x, int y, int width, int height, String text, ButtonPressAction action) {
            super(x, y, width, height, Text.of(text), button -> action.onPress((ClickGuiButton)button));
        }
        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            Color bgColor = this.isHovered() ? (isPressed ? pressColor : hoverColor) : normalColor;
            if (this.isHovered()) {
                RenderUtil.drawRoundedRectWithGlow(
                        this.x,
                        this.y,
                        this.width,
                        this.height,
                        4, 
                        6, 
                        bgColor,
                        glowColor
                );
            } else {
                DrawHelper.drawRoundedRect(
                        this.x,
                        this.y + this.height, 
                        this.width,
                        this.height,
                        4, 
                        bgColor
                );
            }
            int textWidth = AltManagerScreen.this.textRenderer.getWidth(this.getMessage().getString());
            AltManagerScreen.this.textRenderer.drawWithShadow(
                    matrices,
                    this.getMessage(),
                    this.x + (this.width - textWidth) / 2,
                    this.y + (this.height - 8) / 2,
                    textColor.getRGB()
            );
        }
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.isMouseOver(mouseX, mouseY)) {
                this.isPressed = true;
                return super.mouseClicked(mouseX, mouseY, button);
            }
            return false;
        }
        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            this.isPressed = false;
            return super.mouseReleased(mouseX, mouseY, button);
        }
    }
} 
