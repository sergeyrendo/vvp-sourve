package tech.vvp.vvp.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.init.CoordinateTargetVehicle;
import tech.vvp.vvp.network.SetMissileTargetPacket;

import java.util.HashMap;
import java.util.Map;

public class CoordinateInputScreen extends Screen {
    private final CoordinateTargetVehicle vehicle;
    private EditBox xInput, yInput, zInput;
    private Button submitButton;
    private static final Map<Integer, Vec3> savedPositions = new HashMap<>();

    private EditBox pasteInput;

    public CoordinateInputScreen(CoordinateTargetVehicle vehicle) {
        super(Component.literal("Enter Coordinates"));
        this.vehicle = vehicle;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        Vec3 saved = savedPositions.getOrDefault(vehicle.getId(), new Vec3(0, 0, 0));

        xInput = new EditBox(font, centerX - 50, centerY - 40, 100, 20, Component.literal("X"));
        xInput.setValue(String.format(java.util.Locale.US, "%.2f", saved.x));
        yInput = new EditBox(font, centerX - 50, centerY - 10, 100, 20, Component.literal("Y"));
        yInput.setValue(String.format(java.util.Locale.US, "%.2f", saved.y));
        zInput = new EditBox(font, centerX - 50, centerY + 20, 100, 20, Component.literal("Z"));
        zInput.setValue(String.format(java.util.Locale.US, "%.2f", saved.z));

        this.addRenderableWidget(xInput);
        this.addRenderableWidget(yInput);
        this.addRenderableWidget(zInput);

        // Поле для вставки координат одной строкой
        pasteInput = new EditBox(font, centerX - 100, centerY + 55, 200, 20, Component.literal("Paste XYZ"));
        pasteInput.setMaxLength(256);
        pasteInput.setResponder(this::onPasteInput);
        this.addRenderableWidget(pasteInput);

        submitButton = Button.builder(Component.literal("Fire"), btn -> {
            try {
                double x = Double.parseDouble(xInput.getValue());
                double y = Double.parseDouble(yInput.getValue());
                double z = Double.parseDouble(zInput.getValue());

                savedPositions.put(vehicle.getId(), new Vec3(x, y, z));

                VVP.PACKET_HANDLER.sendToServer(new SetMissileTargetPacket(vehicle.getId(), x, y, z));
                Minecraft.getInstance().setScreen(null);
            } catch (NumberFormatException e) {
                if (Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.sendSystemMessage(Component.literal("§cInvalid coordinates format!"));
                }
            }
        }).bounds(centerX - 40, centerY + 85, 80, 20).build();

        this.addRenderableWidget(submitButton);
    }

    private void onPasteInput(String text) {
        if (text.isEmpty()) return;
        try {
            // Пытаемся распарсить строку вида "x y z"
            String[] parts = text.trim().split("\\s+");
            if (parts.length >= 3) {
                double x = Double.parseDouble(parts[0]);
                double y = Double.parseDouble(parts[1]);
                double z = Double.parseDouble(parts[2]);

                xInput.setValue(String.format(java.util.Locale.US, "%.2f", x));
                yInput.setValue(String.format(java.util.Locale.US, "%.2f", y));
                zInput.setValue(String.format(java.util.Locale.US, "%.2f", z));
            }
        } catch (NumberFormatException ignored) {
            // Игнорируем ошибки парсинга пока пользователь вводит
        }
    }

    @Override
    public void render(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Подписи X, Y, Z
        guiGraphics.drawString(font, "x", centerX - 65, centerY - 34, 0xFFFFFF);
        guiGraphics.drawString(font, "y", centerX - 65, centerY - 4, 0xFFFFFF);
        guiGraphics.drawString(font, "z", centerX - 65, centerY + 26, 0xFFFFFF);

        // Подпись для поля вставки
        guiGraphics.drawCenteredString(font, "xyz:", centerX, centerY + 45, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
