package ru.shun.arasakafabric.handler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import java.util.ArrayList;
import java.util.List;
public class InventoryActionHandler {
    private static final List<PendingAction> pendingActions = new ArrayList<>();
    private static boolean processingActions = false;
    public static boolean hasPendingActions() {
        return !pendingActions.isEmpty();
    }
    public static void executePendingActions() {
        if (pendingActions.isEmpty()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerInteractionManager interactionManager = mc.interactionManager;
        ClientPlayerEntity player = mc.player;
        if (interactionManager != null && player != null) {
            for (PendingAction action : pendingActions) {
                action.execute(interactionManager, player);
            }
            pendingActions.clear();
            processingActions = false;
            System.out.println("[InventoryActionHandler] Выполнены все отложенные действия: " + pendingActions.size());
        }
    }
    public static void addPendingAction(int syncId, int slotId, int button, SlotActionType actionType, ItemStack itemStack) {
        pendingActions.add(new PendingAction(syncId, slotId, button, actionType, itemStack));
        System.out.println("[InventoryActionHandler] Добавлено отложенное действие, всего: " + pendingActions.size());
    }
    public static void clearPendingActions() {
        pendingActions.clear();
        processingActions = false;
        System.out.println("[InventoryActionHandler] Очищены все отложенные действия");
    }
    public static void setProcessingActions(boolean processing) {
        processingActions = processing;
    }
    public static boolean isProcessingActions() {
        return processingActions;
    }
    private static class PendingAction {
        private final int syncId;
        private final int slotId;
        private final int button;
        private final SlotActionType actionType;
        private final ItemStack itemStack;
        public PendingAction(int syncId, int slotId, int button, SlotActionType actionType, ItemStack itemStack) {
            this.syncId = syncId;
            this.slotId = slotId;
            this.button = button;
            this.actionType = actionType;
            this.itemStack = itemStack;
        }
        public void execute(ClientPlayerInteractionManager interactionManager, ClientPlayerEntity player) {
            interactionManager.clickSlot(syncId, slotId, button, actionType, player);
        }
    }
} 
