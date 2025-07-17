package ru.shun.arasakafabric.ui;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import ru.shun.arasakafabric.client.util.DrawHelper;
import ru.shun.arasakafabric.client.util.RenderUtil;
import ru.shun.arasakafabric.modules.Category;
import ru.shun.arasakafabric.modules.Module;
import ru.shun.arasakafabric.modules.ModuleManager;
import ru.shun.arasakafabric.ui.imple.BooleanSetting;
import ru.shun.arasakafabric.ui.imple.NumberSetting;
import ru.shun.arasakafabric.ui.imple.MultiSetting;
import ru.shun.arasakafabric.client.util.KeyboardUtils;
public class ClickGuiMain extends Screen {
    private static final int CATEGORY_HEIGHT = 18;
    private static final int CATEGORY_WIDTH = 120;
    private static final int CATEGORY_SPACING = 20;
    private static final int PANEL_HEIGHT = 200;
    private static int categoryStartX;
    private static int categoryStartY; 
    private final Map<Category, Integer> scrollOffsets = new HashMap<>();
    private Module selectedModuleForSettings = null;
    private NumberSetting draggingSlider = null;
    private float settingsAnimationProgress = 0.0f;
    private long lastSettingsToggleTime = 0;
    private static final float ANIMATION_DURATION = 250.0f; 
    private Module previousSelectedModule = null; 
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final Color darkBlue = new Color(5, 10, 25, 240);       
    private final Color mediumBlue = new Color(10, 20, 45, 230);    
    private final Color brightBlue = new Color(30, 80, 200, 255);   
    private final Color lightBlue = new Color(100, 150, 255, 255);  
    private final Color glowColor = new Color(50, 100, 255, 120);   
    private final Color activeModuleColor = new Color(19, 21, 83, 215); 
    private final Color inactiveModuleColor = new Color(5, 15, 30, 240); 
    private Module waitingForBind = null;
    private boolean isAltPressed = false;
    private long lastKeyboardLayoutCheckTime = 0;
    private static final long KEYBOARD_CHECK_INTERVAL = 1000; 
    public ClickGuiMain() {
        super(Text.of("ClickGui"));
        for (Category category : Category.values()) {
            scrollOffsets.put(category, 0);
        }
        KeyboardUtils.updateKeyboardLayout();
    }
    private int calculateCenterX() {
        int categoryCount = Category.values().length;
        int totalWidth = categoryCount * CATEGORY_WIDTH + (categoryCount - 1) * CATEGORY_SPACING;
        return (this.width - totalWidth) / 2;
    }
    private int calculateCenterY() {
        int totalHeight = PANEL_HEIGHT + CATEGORY_HEIGHT;
        return (this.height - totalHeight) / 2;
    }
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        isAltPressed = InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_ALT) || 
                      InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_ALT);
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastKeyboardLayoutCheckTime > KEYBOARD_CHECK_INTERVAL) {
            KeyboardUtils.updateKeyboardLayout();
            lastKeyboardLayoutCheckTime = currentTime;
        }
        categoryStartX = calculateCenterX();
        categoryStartY = calculateCenterY();
        int x = categoryStartX;
        if (selectedModuleForSettings != null && settingsAnimationProgress < 1.0f) {
            long animTime1 = System.currentTimeMillis();
            float timeElapsed = animTime1 - lastSettingsToggleTime;
            settingsAnimationProgress = Math.min(1.0f, timeElapsed / ANIMATION_DURATION);
        } else if (selectedModuleForSettings == null && settingsAnimationProgress > 0.0f) {
            long animTime2 = System.currentTimeMillis();
            float timeElapsed = animTime2 - lastSettingsToggleTime;
            settingsAnimationProgress = Math.max(0.0f, 1.0f - (timeElapsed / ANIMATION_DURATION));
            if (settingsAnimationProgress <= 0.0f) {
                previousSelectedModule = null;
            }
        }
        for (Category category : Category.values()) {
            java.util.List<Module> categoryModules = ModuleManager.getByCategory(category);
            int totalPanelHeight = PANEL_HEIGHT + CATEGORY_HEIGHT;
            DrawHelper.drawRoundedRect(
                x, 
                categoryStartY+214,
                CATEGORY_WIDTH, 
                totalPanelHeight, 
                5, 
                darkBlue 
            );
            float textWidth = mc.textRenderer.getWidth(category.name());
            float categoryTextX = x + (CATEGORY_WIDTH - textWidth) / 2;
            mc.textRenderer.draw(matrices, category.name(), categoryTextX, categoryStartY + 5, -1);
            if (!categoryModules.isEmpty()) {
                int panelY = categoryStartY + CATEGORY_HEIGHT;
                int totalContentHeight = categoryModules.size() * (CATEGORY_HEIGHT + 2);
                int maxScroll = Math.max(0, totalContentHeight - PANEL_HEIGHT + 10); 
                scrollOffsets.put(category, 0);
                int currentScroll = 0;
                int moduleY = panelY + 5 - currentScroll;
                for (Module module : categoryModules) {
                    if (module.isEnabled()) {
                        DrawHelper.drawRoundedRect(
                                x + 5,
                                moduleY + CATEGORY_HEIGHT,
                                CATEGORY_WIDTH - 8,
                                CATEGORY_HEIGHT,
                                4, 
                                activeModuleColor
                        );
                    } else {
                        DrawHelper.drawRoundedRect(
                            x + 5,
                            moduleY + 18,
                            CATEGORY_WIDTH - 8, 
                            CATEGORY_HEIGHT, 
                            4, 
                            inactiveModuleColor
                        );
                    }
                    String displayText = module.getName();
                    if (waitingForBind == module) {
                        displayText = "Нажмите клавишу...";
                    } else if (isAltPressed && module.getBind() != -1) {
                        String keyName = getKeyName(module.getBind());
                        displayText = displayText + " | " + keyName;
                    }
                    float moduleTextX = x + 8;
                    float textY = moduleY + (CATEGORY_HEIGHT - mc.textRenderer.fontHeight) / 2.0f;
                    mc.textRenderer.draw(matrices, displayText, moduleTextX, textY, -1);
                    moduleY += CATEGORY_HEIGHT + 2;
                }
            }
            x += CATEGORY_WIDTH + CATEGORY_SPACING;
        }
        if (selectedModuleForSettings != null || settingsAnimationProgress > 0.0f) {
            Module displayModule = selectedModuleForSettings != null ? 
                                   selectedModuleForSettings : previousSelectedModule;
            if (displayModule == null) {
                return;
            }
            int settingsTargetX = categoryStartX - 170; 
            if (settingsTargetX < 10) {
                settingsTargetX = 10;
            }
            int settingsY = categoryStartY;
            int lastCategoryX = categoryStartX;
            int categoryCount = Category.values().length;
            if (categoryCount > 0) {
                lastCategoryX = categoryStartX + (categoryCount - 1) * (CATEGORY_WIDTH + CATEGORY_SPACING);
            }
            int settingsX = settingsTargetX;
            if (settingsAnimationProgress < 1.0f) {
                settingsX = lastCategoryX - (int)((lastCategoryX - settingsTargetX) * settingsAnimationProgress);
            }
            int alpha = (int)(settingsAnimationProgress * 255);
            Color fadeMediumBlue = new Color(mediumBlue.getRed(), mediumBlue.getGreen(), 
                                         mediumBlue.getBlue(), Math.min(mediumBlue.getAlpha(), alpha));
            Color fadeDarkBlue = new Color(darkBlue.getRed(), darkBlue.getGreen(), 
                                       darkBlue.getBlue(), Math.min(darkBlue.getAlpha(), alpha));
            Color fadeGlowColor = new Color(glowColor.getRed(), glowColor.getGreen(), 
                                        glowColor.getBlue(), Math.min(glowColor.getAlpha(), alpha));
            Color fadeBrightBlue = new Color(brightBlue.getRed(), brightBlue.getGreen(), 
                                         brightBlue.getBlue(), Math.min(brightBlue.getAlpha(), alpha));
            RenderUtil.drawRoundedRectWithGlow(
                settingsX, 
                settingsY, 
                160, 
                CATEGORY_HEIGHT, 
                5, 
                6, 
                fadeMediumBlue, 
                fadeGlowColor
            );
            String settingsTitle = "Настройки: " + displayModule.getName();
            mc.textRenderer.drawWithShadow(matrices, settingsTitle, 
                    settingsX + 5, settingsY + 5, 
                    new Color(255, 255, 255, alpha).getRGB());
            int settingsPanelY = settingsY + CATEGORY_HEIGHT + 5;
            RenderUtil.drawRoundedRectWithGlow(
                settingsX, 
                settingsPanelY, 
                160, 
                PANEL_HEIGHT, 
                5, 
                8, 
                fadeDarkBlue, 
                fadeGlowColor
            );
            int currentSettingY = settingsPanelY + 10;
            for (int j = 0; j < displayModule.getSettings().size(); j++) {
                ISetting setting = displayModule.getSettings().get(j);
                if (setting instanceof BooleanSetting) {
                    BooleanSetting bs = (BooleanSetting) setting;
                    Color buttonColor = bs.isEnabled() ? 
                        new Color(brightBlue.getRed(), brightBlue.getGreen(), brightBlue.getBlue(), 
                                  Math.min(brightBlue.getAlpha(), alpha)) :
                        new Color(mediumBlue.getRed(), mediumBlue.getGreen(), mediumBlue.getBlue(), 
                                  Math.min(mediumBlue.getAlpha(), alpha));
                    RenderUtil.drawRoundedRectWithGlow(
                        settingsX + 5, 
                        currentSettingY, 
                        150, 
                        18, 
                        4, 
                        5, 
                        buttonColor, 
                        fadeGlowColor
                    );
                    mc.textRenderer.drawWithShadow(matrices,
                            bs.getName() + ": " + (bs.isEnabled() ? "ВКЛ" : "ВЫКЛ"),
                            settingsX + 10, currentSettingY + 5, 
                            new Color(255, 255, 255, alpha).getRGB());
                    currentSettingY += 22;
                }
                if (setting instanceof NumberSetting) {
                    NumberSetting ns = (NumberSetting) setting;
                    int sliderWidth = 140;
                    int sliderHeight = 15; 
                    int sliderX = settingsX + 10;
                    int sliderY = currentSettingY + 20; 
                    double percent = (ns.getDoubleValue() - ns.getMin()) / (ns.getMax() - ns.getMin());
                    int filledWidth = (int) (percent * sliderWidth);
                    DrawHelper.drawRoundedRect(
                        sliderX, 
                        sliderY, 
                        sliderWidth, 
                        sliderHeight, 
                        4,
                        fadeMediumBlue
                    );
                    if (filledWidth > 0) {
                        DrawHelper.drawRoundedRect(
                            sliderX, 
                            sliderY, 
                            filledWidth, 
                            sliderHeight, 
                            4, 
                            fadeBrightBlue
                        );
                    }
                    String valueText = String.format("%.2f", ns.getDoubleValue());
                    String fullText = ns.getName() + ": " + valueText;
                    mc.textRenderer.drawWithShadow(matrices,
                            fullText,
                            settingsX + 11, sliderY + sliderHeight + -9,
                            new Color(255, 255, 255, alpha).getRGB());
                    currentSettingY += 40; 
                }
                if (setting instanceof MultiSetting) {
                    MultiSetting ms = (MultiSetting) setting;
                    mc.textRenderer.drawWithShadow(matrices,
                            ms.getName() + ":",
                            settingsX + 5, currentSettingY + 1,
                            new Color(255, 255, 255, alpha).getRGB());
                    currentSettingY += 15;
                    int totalOptions = ms.getOptionsCount();
                    int leftColumnSize = (totalOptions + 1) / 2; 
                    for (int i = 0; i < leftColumnSize; i++) {
                        String leftOption = ms.getOptionName(i);
                        boolean leftEnabled = ms.isEnabled(leftOption);
                        Color leftButtonColor = leftEnabled ? 
                            new Color(brightBlue.getRed(), brightBlue.getGreen(), brightBlue.getBlue(), 
                                      Math.min(brightBlue.getAlpha(), alpha)) :
                            new Color(mediumBlue.getRed(), mediumBlue.getGreen(), mediumBlue.getBlue(), 
                                      Math.min(mediumBlue.getAlpha(), alpha));
                        RenderUtil.drawRoundedRectWithGlow(
                            settingsX + 5, 
                            currentSettingY, 
                            70, 
                            18, 
                            4, 
                            5, 
                            leftButtonColor, 
                            fadeGlowColor
                        );
                        mc.textRenderer.drawWithShadow(matrices,
                                leftOption,
                                settingsX + 8, currentSettingY + 5, 
                                new Color(255, 255, 255, alpha).getRGB());
                        if (i + leftColumnSize < totalOptions) {
                            String rightOption = ms.getOptionName(i + leftColumnSize);
                            boolean rightEnabled = ms.isEnabled(rightOption);
                            Color rightButtonColor = rightEnabled ? 
                                new Color(brightBlue.getRed(), brightBlue.getGreen(), brightBlue.getBlue(), 
                                          Math.min(brightBlue.getAlpha(), alpha)) :
                                new Color(mediumBlue.getRed(), mediumBlue.getGreen(), mediumBlue.getBlue(), 
                                          Math.min(mediumBlue.getAlpha(), alpha));
                            RenderUtil.drawRoundedRectWithGlow(
                                settingsX + 85, 
                                currentSettingY, 
                                70, 
                                18, 
                                4, 
                                5, 
                                rightButtonColor, 
                                fadeGlowColor
                            );
                            mc.textRenderer.drawWithShadow(matrices,
                                    rightOption,
                                    settingsX + 88, currentSettingY + 5, 
                                    new Color(255, 255, 255, alpha).getRGB());
                        }
                        currentSettingY += 22;
                    }
                    currentSettingY += 5; 
                }
            }
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = categoryStartX;
        for (Category category : Category.values()) {
            int totalPanelHeight = PANEL_HEIGHT + CATEGORY_HEIGHT;
            if (mouseX >= x && mouseX <= x + CATEGORY_WIDTH && 
                mouseY >= categoryStartY && mouseY <= categoryStartY + totalPanelHeight) {
                int panelY = categoryStartY + CATEGORY_HEIGHT;
                if (mouseY >= categoryStartY && mouseY <= panelY) {
                    return true; 
                }
                java.util.List<Module> categoryModules = ModuleManager.getByCategory(category);
                if (!categoryModules.isEmpty()) {
                    int currentScroll = 0;
                    int moduleY = panelY + 5 - currentScroll;
                    for (Module module : categoryModules) {
                        if (mouseX >= x + 4 && mouseX <= x + CATEGORY_WIDTH - 4 &&
                            mouseY >= moduleY && mouseY <= moduleY + CATEGORY_HEIGHT) {
                            if (button == 0) {
                                module.toggled();
                            } else if (button == 1) {
                                if (selectedModuleForSettings != module) {
                                    lastSettingsToggleTime = System.currentTimeMillis();
                                    settingsAnimationProgress = 0.0f;
                                }
                                if (selectedModuleForSettings == module) {
                                    previousSelectedModule = selectedModuleForSettings;
                                    lastSettingsToggleTime = System.currentTimeMillis();
                                    selectedModuleForSettings = null;
                                } else {
                                    selectedModuleForSettings = module;
                                }
                                return true;
                            } else if (button == 2) {
                                waitingForBind = module;
                                return true;
                            }
                        }
                        moduleY += CATEGORY_HEIGHT + 2;
                    }
                }
                return true;
            }
            x += CATEGORY_WIDTH + CATEGORY_SPACING;
        }
        if ((selectedModuleForSettings != null || previousSelectedModule != null) && settingsAnimationProgress >= 0.8f) {
            Module displayModule = selectedModuleForSettings != null ? 
                                  selectedModuleForSettings : previousSelectedModule;
            int settingsX = categoryStartX - 170;
            if (settingsX < 10) {
                settingsX = 10;
            }
            int settingsY = categoryStartY + CATEGORY_HEIGHT + 5; 
            int currentSettingY = settingsY + 10; 
            for (int j = 0; j < displayModule.getSettings().size(); j++) {
                ISetting setting = displayModule.getSettings().get(j);
                if (setting instanceof BooleanSetting) {
                    if (mouseX >= settingsX + 5 && mouseX <= settingsX + 155 &&
                            mouseY >= currentSettingY && mouseY <= currentSettingY + 18) {
                        BooleanSetting bs = (BooleanSetting) setting;
                        bs.setEnabled(!bs.isEnabled());
                        return true;
                    }
                    currentSettingY += 22;
                }
                if (setting instanceof NumberSetting) {
                    NumberSetting ns = (NumberSetting) setting;
                    int sliderWidth = 140;
                    int sliderX = settingsX + 19;
                    int clickAreaY = currentSettingY; 
                    int clickAreaHeight = 40; 
                    if (mouseX >= sliderX && mouseX <= sliderX + sliderWidth &&
                            mouseY >= clickAreaY && mouseY <= clickAreaY + clickAreaHeight) {
                        draggingSlider = ns;
                        updateSliderValue(ns, mouseX, sliderX, sliderWidth);
                        return true;
                    }
                    currentSettingY += 40;
                }
                if (setting instanceof MultiSetting) {
                    MultiSetting ms = (MultiSetting) setting;
                    currentSettingY += 15;
                    int totalOptions = ms.getOptionsCount();
                    int leftColumnSize = (totalOptions + 1) / 2; 
                    for (int i = 0; i < leftColumnSize; i++) {
                        if (i < totalOptions) {
                            String leftOption = ms.getOptionName(i);
                            if (mouseX >= settingsX + 5 && mouseX <= settingsX + 75 &&
                                    mouseY >= currentSettingY && mouseY <= currentSettingY + 18) {
                                ms.toggle(leftOption);
                                return true;
                            }
                        }
                        if (i + leftColumnSize < totalOptions) {
                            String rightOption = ms.getOptionName(i + leftColumnSize);
                            if (mouseX >= settingsX + 85 && mouseX <= settingsX + 155 &&
                                    mouseY >= currentSettingY && mouseY <= currentSettingY + 18) {
                                ms.toggle(rightOption);
                                return true;
                            }
                        }
                        currentSettingY += 22;
                    }
                    currentSettingY += 5; 
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingSlider = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingSlider != null) {
            Module displayModule = selectedModuleForSettings != null ? 
                                 selectedModuleForSettings : previousSelectedModule;
            if (displayModule == null) {
                return false;
            }
            int settingsX = categoryStartX - 170;
            if (settingsX < 10) {
                settingsX = 10;
            }
            int sliderX = settingsX + 10;
            int sliderWidth = 140;
            updateSliderValue(draggingSlider, mouseX, sliderX, sliderWidth);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    private void updateSliderValue(NumberSetting slider, double mouseX, int sliderX, int sliderWidth) {
        double percent = Math.max(0.0, Math.min(1.0, (mouseX - sliderX) / sliderWidth));
        double range = slider.getMax() - slider.getMin();
        double newValue = slider.getMin() + (percent * range);
        double increment = slider.getIncrement();
        double rounded = Math.round(newValue / increment) * increment;
        rounded = Math.max(slider.getMin(), Math.min(slider.getMax(), rounded));
        slider.setValue(rounded);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (waitingForBind != null) {
            if (keyCode == 256) {
                waitingForBind.setBind(-1); 
                waitingForBind = null;
                return true;
            }
            waitingForBind.setBind(keyCode);
            String keyName = KeyboardUtils.getKeyName(keyCode);
            ru.shun.arasakafabric.modules.notify.NotificationRenderer.add(
                    waitingForBind.getName() + " привязан к " + keyName);
            waitingForBind = null;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
    private String getKeyName(int keyCode) {
        return KeyboardUtils.getKeyName(keyCode);
    }
}

