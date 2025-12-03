package tech.vvp.vvp.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.M142HimarsEntity;
import tech.vvp.vvp.init.CoordinateTargetVehicle;
import tech.vvp.vvp.network.message.C2SSetMissileTargetPacket;

import java.util.HashMap;
import java.util.Map;

public class CoordinateInputScreen extends Screen {
    private final CoordinateTargetVehicle vehicle;
    private EditBox xInput, yInput, zInput;
    private Button submitButton, currentPosButton;
    private static final Map<Integer, Vec3> savedPositions = new HashMap<>();

    private static final int MAP_SIZE = 180;
    private static final int MAP_SCALE = 12;
    private Vec3 mapCenter;
    private Vec3 targetPos;

    private static final Map<Long, Integer> terrainCache = new HashMap<>();
    private static long lastCacheClean = 0;
    private static final int MAX_CACHE_SIZE = 10000;

    private static final int PANEL_COLOR = 0xD0202020;
    private static final int BORDER_COLOR = 0xFF00FF00;
    private static final int HIGHLIGHT_COLOR = 0xFF00FF00;

    public CoordinateInputScreen(CoordinateTargetVehicle vehicle) {
        super(Component.literal("Artillery Fire Control"));
        this.vehicle = vehicle;
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            this.mapCenter = player.position();
            boolean hasGuidance = false;
            if (vehicle instanceof M142HimarsEntity himars) {
                hasGuidance = himars.hasGuidanceData();
            }
            if (!hasGuidance) {
                savedPositions.remove(vehicle.getId());
            }
            Vec3 saved = savedPositions.getOrDefault(vehicle.getId(), player.position());
            this.targetPos = saved;
        }
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        Player player = Minecraft.getInstance().player;

        boolean hasGuidance = false;
        if (vehicle instanceof M142HimarsEntity himars) {
            hasGuidance = himars.hasGuidanceData();
        }

        Vec3 saved;
        if (!hasGuidance) {
            saved = player != null ? player.position() : new Vec3(0, 64, 0);
        } else {
            saved = savedPositions.getOrDefault(vehicle.getId(), player != null ? player.position() : new Vec3(0, 64, 0));
        }

        int inputY = centerY - 60;

        xInput = new EditBox(font, centerX - 150, inputY, 100, 20, Component.literal("X"));
        xInput.setValue(hasGuidance ? String.format(java.util.Locale.US, "%.0f", saved.x) : "");
        xInput.setResponder(this::onCoordinateChange);

        yInput = new EditBox(font, centerX - 150, inputY + 30, 100, 20, Component.literal("Y"));
        yInput.setValue(hasGuidance ? String.format(java.util.Locale.US, "%.0f", saved.y) : "");
        yInput.setResponder(this::onCoordinateChange);

        zInput = new EditBox(font, centerX - 150, inputY + 60, 100, 20, Component.literal("Z"));
        zInput.setValue(hasGuidance ? String.format(java.util.Locale.US, "%.0f", saved.z) : "");
        zInput.setResponder(this::onCoordinateChange);

        this.addRenderableWidget(xInput);
        this.addRenderableWidget(yInput);
        this.addRenderableWidget(zInput);

        currentPosButton = Button.builder(Component.literal("Current"), btn -> {
            if (player != null) {
                Vec3 pos = player.position();
                xInput.setValue(String.format(java.util.Locale.US, "%.0f", pos.x));
                yInput.setValue(String.format(java.util.Locale.US, "%.0f", pos.y));
                zInput.setValue(String.format(java.util.Locale.US, "%.0f", pos.z));
                onCoordinateChange("");
            }
        }).bounds(centerX - 150, inputY + 90, 100, 20).build();
        this.addRenderableWidget(currentPosButton);

        submitButton = Button.builder(Component.literal("§a§lSET"), btn -> {
            try {
                double x = Double.parseDouble(xInput.getValue());
                double y = Double.parseDouble(yInput.getValue());
                double z = Double.parseDouble(zInput.getValue());
                savedPositions.put(vehicle.getId(), new Vec3(x, y, z));
                VVP.PACKET_HANDLER.sendToServer(new C2SSetMissileTargetPacket(vehicle.getId(), x, y, z));
                Minecraft.getInstance().setScreen(null);
            } catch (NumberFormatException e) {
                if (player != null) {
                    player.sendSystemMessage(Component.literal("§cInvalid coordinates!"));
                }
            }
        }).bounds(centerX - 150, inputY + 120, 100, 25).build();
        this.addRenderableWidget(submitButton);
    }

    private void onCoordinateChange(String text) {
        try {
            double x = Double.parseDouble(xInput.getValue());
            double y = Double.parseDouble(yInput.getValue());
            double z = Double.parseDouble(zInput.getValue());
            targetPos = new Vec3(x, y, z);
        } catch (NumberFormatException ignored) {}
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int panelTop = centerY - 100;

        drawPanel(guiGraphics, centerX - 170, panelTop, 340, 220);

        String title = "§l§aFIRE CONTROL SYSTEM";
        guiGraphics.drawCenteredString(font, title, centerX, panelTop + 10, HIGHLIGHT_COLOR);

        int inputY = centerY - 60;
        guiGraphics.drawString(font, "§aX:", centerX - 164, inputY + 6, 0xFFFFFF);
        guiGraphics.drawString(font, "§aY:", centerX - 164, inputY + 36, 0xFFFFFF);
        guiGraphics.drawString(font, "§aZ:", centerX - 164, inputY + 66, 0xFFFFFF);

        Player player = Minecraft.getInstance().player;
        if (player != null && targetPos != null) {
            double distance = player.position().distanceTo(targetPos);
            String distText = String.format("Range: §e%.0fm", distance);
            guiGraphics.drawString(font, distText, centerX - 164, inputY + 153, 0xFFAAAAAA);

            if (distance < 200 || distance > 1500) {
                guiGraphics.drawString(font, "§c⚠ Out of range!", centerX - 164, inputY + 166, 0xFFFF0000);
            } else {
                guiGraphics.drawString(font, "§a✓ Ready", centerX - 164, inputY + 166, HIGHLIGHT_COLOR);
            }
        }

        renderMiniMap(guiGraphics, centerX - 24, panelTop + 30, mouseX, mouseY);

        if (vehicle instanceof M142HimarsEntity himars && himars.hasGuidanceData()) {
            int infoX = centerX + 74;
            int infoY = panelTop + 5;
            guiGraphics.drawString(font, String.format("Req Yaw: %.1f°", -himars.getGuidanceYaw()), infoX, infoY, 0xFFFFFFFF);
            guiGraphics.drawString(font, String.format("Req Pitch: %.1f°", -himars.getGuidancePitch()), infoX, infoY + 12, 0xFFFFFFFF);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderMiniMap(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        guiGraphics.fill(x - 2, y - 2, x + MAP_SIZE + 2, y + MAP_SIZE + 2, BORDER_COLOR);
        guiGraphics.fill(x, y, x + MAP_SIZE, y + MAP_SIZE, 0xFF1A1A1A);

        Player player = Minecraft.getInstance().player;
        if (player == null || mapCenter == null) return;

        Level level = player.level();
        cleanCacheIfNeeded();

        for (int px = 0; px < MAP_SIZE; px += 2) {
            for (int pz = 0; pz < MAP_SIZE; pz += 2) {
                int worldX = (int) (mapCenter.x + (px - MAP_SIZE / 2) * MAP_SCALE);
                int worldZ = (int) (mapCenter.z + (pz - MAP_SIZE / 2) * MAP_SCALE);
                int color = getTerrainColorCached(level, worldX, worldZ);
                guiGraphics.fill(x + px, y + pz, x + px + 2, y + pz + 2, color);
            }
        }

        for (int i = 0; i <= MAP_SIZE; i += 45) {
            guiGraphics.fill(x + i, y, x + i + 1, y + MAP_SIZE, 0x30FFFFFF);
            guiGraphics.fill(x, y + i, x + MAP_SIZE, y + i + 1, 0x30FFFFFF);
        }

        int centerMapX = x + MAP_SIZE / 2;
        int centerMapY = y + MAP_SIZE / 2;
        drawRangeCircleClipped(guiGraphics, centerMapX, centerMapY, (int)(200.0 / MAP_SCALE), 0x80FFFF00, x, y, MAP_SIZE);
        drawRangeCircleClipped(guiGraphics, centerMapX, centerMapY, (int)(1500.0 / MAP_SCALE), 0x80FF0000, x, y, MAP_SIZE);

        int playerX = x + MAP_SIZE / 2;
        int playerZ = y + MAP_SIZE / 2;

        if (vehicle instanceof M142HimarsEntity himars) {
            float turretYaw = himars.getTurretYRot() + himars.getYRot();
            drawTurretDirection(guiGraphics, playerX, playerZ, turretYaw, 0xFF00FF00);
        }

        drawCross(guiGraphics, playerX, playerZ, 5, 0xFF00FF00);

        if (targetPos != null) {
            int targetX = x + MAP_SIZE / 2 + (int) ((targetPos.x - mapCenter.x) / MAP_SCALE);
            int targetZ = y + MAP_SIZE / 2 + (int) ((targetPos.z - mapCenter.z) / MAP_SCALE);

            if (targetX >= x && targetX <= x + MAP_SIZE && targetZ >= y && targetZ <= y + MAP_SIZE) {
                drawCross(guiGraphics, targetX, targetZ, 6, 0xFFFF0000);
                drawLine(guiGraphics, playerX, playerZ, targetX, targetZ, 0x60FF0000);
                double dist = Math.sqrt(Math.pow(targetPos.x - mapCenter.x, 2) + Math.pow(targetPos.z - mapCenter.z, 2));
                guiGraphics.drawString(font, String.format("§e%.0fm", dist), targetX + 8, targetZ - 4, 0xFFFFFFFF);
            }
        }

        guiGraphics.drawString(font, "§7Map (1.5km range)", x - 5, y - 12, 0xFFAAAAAA);
    }

    private void drawTurretDirection(GuiGraphics guiGraphics, int x, int y, float yaw, int color) {
        double angle = Math.toRadians(-yaw + 180);
        int length = 12;
        int endX = x - (int)(Math.sin(angle) * length);
        int endY = y - (int)(Math.cos(angle) * length);
        drawLine(guiGraphics, x, y, endX, endY, color);

        double arrowAngle1 = angle + Math.toRadians(150);
        double arrowAngle2 = angle - Math.toRadians(150);
        int arrowLen = 5;
        int arrow1X = endX - (int)(Math.sin(arrowAngle1) * arrowLen);
        int arrow1Y = endY - (int)(Math.cos(arrowAngle1) * arrowLen);
        int arrow2X = endX - (int)(Math.sin(arrowAngle2) * arrowLen);
        int arrow2Y = endY - (int)(Math.cos(arrowAngle2) * arrowLen);
        drawLine(guiGraphics, endX, endY, arrow1X, arrow1Y, color);
        drawLine(guiGraphics, endX, endY, arrow2X, arrow2Y, color);
    }

    private void drawRangeCircleClipped(GuiGraphics guiGraphics, int centerX, int centerY, int radius, int color, int clipX, int clipY, int clipSize) {
        int points = 64;
        for (int i = 0; i < points; i++) {
            double angle1 = 2 * Math.PI * i / points;
            double angle2 = 2 * Math.PI * (i + 1) / points;
            int x1 = centerX + (int)(Math.cos(angle1) * radius);
            int y1 = centerY + (int)(Math.sin(angle1) * radius);
            int x2 = centerX + (int)(Math.cos(angle2) * radius);
            int y2 = centerY + (int)(Math.sin(angle2) * radius);
            if (isInsideMap(x1, y1, clipX, clipY, clipSize) && isInsideMap(x2, y2, clipX, clipY, clipSize)) {
                drawLine(guiGraphics, x1, y1, x2, y2, color);
            }
        }
    }

    private boolean isInsideMap(int x, int y, int mapX, int mapY, int mapSize) {
        return x >= mapX && x <= mapX + mapSize && y >= mapY && y <= mapY + mapSize;
    }

    private void cleanCacheIfNeeded() {
        long now = System.currentTimeMillis();
        if (terrainCache.size() > MAX_CACHE_SIZE || now - lastCacheClean > 30000) {
            terrainCache.clear();
            lastCacheClean = now;
        }
    }

    private int getTerrainColorCached(Level level, int x, int z) {
        long key = ((long)x << 32) | (z & 0xFFFFFFFFL);
        return terrainCache.computeIfAbsent(key, k -> getTerrainColor(level, x, z));
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int panelTop = centerY - 100;
        int mapX = centerX - 24;
        int mapY = panelTop + 30;

        if (mouseX >= mapX && mouseX <= mapX + MAP_SIZE && mouseY >= mapY && mouseY <= mapY + MAP_SIZE) {
            Player player = Minecraft.getInstance().player;
            if (player != null && mapCenter != null) {
                int clickX = (int) (mapCenter.x + (mouseX - mapX - MAP_SIZE / 2) * MAP_SCALE);
                int clickZ = (int) (mapCenter.z + (mouseY - mapY - MAP_SIZE / 2) * MAP_SCALE);
                BlockPos groundPos = player.level().getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, new BlockPos(clickX, 0, clickZ));
                int clickY = groundPos.getY() + 1;
                xInput.setValue(String.valueOf(clickX));
                yInput.setValue(String.valueOf(clickY));
                zInput.setValue(String.valueOf(clickZ));
                onCoordinateChange("");
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @SuppressWarnings("deprecation")
    private int getTerrainColor(Level level, int x, int z) {
        try {
            BlockPos pos = new BlockPos(x, 0, z);
            if (!level.hasChunkAt(pos)) {
                return 0xFF0A0A0A;
            }
            BlockPos surfacePos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, pos);
            BlockState state = level.getBlockState(surfacePos);
            if (state.isAir() && surfacePos.getY() > level.getMinBuildHeight()) {
                state = level.getBlockState(surfacePos.below());
            }
            if (state.is(Blocks.WATER)) return 0xFF0066CC;
            if (state.is(Blocks.LAVA)) return 0xFFFF4400;
            if (state.is(Blocks.GRASS_BLOCK)) return 0xFF55AA22;
            if (state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT)) return 0xFF8B4513;
            if (state.is(Blocks.SAND)) return 0xFFEEDD88;
            if (state.is(Blocks.SNOW) || state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.ICE)) return 0xFFEEFFFF;
            if (state.is(Blocks.STONE) || state.is(Blocks.DEEPSLATE)) return 0xFF777777;
            if (state.is(Blocks.GRAVEL)) return 0xFF888888;
            if (!state.isAir()) return 0xFF555555;
        } catch (Exception e) {}
        return 0xFF0A0A0A;
    }

    private void drawPanel(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        guiGraphics.fill(x, y, x + width, y + height, PANEL_COLOR);
        guiGraphics.renderOutline(x, y, width, height, BORDER_COLOR);
    }

    private void drawCross(GuiGraphics guiGraphics, int x, int y, int size, int color) {
        guiGraphics.fill(x, y - size, x + 1, y + size, color);
        guiGraphics.fill(x - size, y, x + size, y + 1, color);
    }

    private void drawLine(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        while (true) {
            guiGraphics.fill(x1, y1, x1 + 1, y1 + 1, color);
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x1 += sx; }
            if (e2 < dx) { err += dx; y1 += sy; }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
