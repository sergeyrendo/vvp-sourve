package tech.vvp.vvp.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.radar.IRadarVehicle;

import java.util.*;

public class RadarHud {
    public static boolean hudEnabled = true;

    public enum Position { TOP_LEFT, TOP_RIGHT, MID_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT, MID_LEFT }
    public static Position position = Position.TOP_LEFT;

    public static List<Vec3> radarTargets = new ArrayList<>();

    private static final ResourceLocation RADAR_BACKGROUND = new ResourceLocation(VVP.MOD_ID, "textures/gui/radar_bg.png");
    private static final ResourceLocation RADAR_TARGET     = new ResourceLocation(VVP.MOD_ID, "textures/gui/radar_target.png");

    private static final int RADAR_SIZE = 96;
    private static final int MARGIN     = 10;

    // Fade-out памяти точки
    private static final long HOLD_TIME_MS = 500;
    private static final long FADE_TIME_MS = 800;
    private static final float MIN_ALPHA   = 0.06f;

    // Мигание: 1.5s OFF, 2.5s ON. Плавные края BLINK_FADE_MS.
    private static final long BLINK_OFF_MS  = 1500L;
    private static final long BLINK_ON_MS   = 2500L;
    private static final long BLINK_TOTAL   = BLINK_OFF_MS + BLINK_ON_MS;
    private static final long BLINK_FADE_MS = 350L; // время сглаживания краёв

    private static final Map<Long, Long> lastSeen = new HashMap<>();
    private static final Map<Long, Vec3> lastPos  = new HashMap<>();

    // Вызывай из S2C-пакета
    public static void onServerTargetsUpdate(List<Vec3> targetsFromServer) {
        radarTargets = targetsFromServer;
        long now = System.currentTimeMillis();
        for (Vec3 pos : targetsFromServer) {
            long key = cellKey(pos);
            lastSeen.put(key, now);
            lastPos.put(key, pos);
        }
    }

    public static final IGuiOverlay HUD_RADAR = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || !player.isAlive() || mc.level == null) return;

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof IRadarVehicle rv)) return;
        if (!hudEnabled) return;

        int radarX, radarY;
        switch (position) {
            case TOP_RIGHT   -> { radarX = screenWidth - RADAR_SIZE - MARGIN; radarY = MARGIN; }
            case MID_RIGHT   -> { radarX = screenWidth - RADAR_SIZE - MARGIN; radarY = (screenHeight - RADAR_SIZE) / 2; }
            case BOTTOM_RIGHT-> { radarX = screenWidth - RADAR_SIZE - MARGIN; radarY = screenHeight - RADAR_SIZE - MARGIN; }
            case BOTTOM_LEFT -> { radarX = MARGIN; radarY = screenHeight - RADAR_SIZE - MARGIN; }
            case MID_LEFT    -> { radarX = MARGIN; radarY = (screenHeight - RADAR_SIZE) / 2; }
            default          -> { radarX = MARGIN; radarY = MARGIN; }
        }
        int radarCenterX = radarX + RADAR_SIZE / 2;
        int radarCenterY = radarY + RADAR_SIZE / 2;

        int   radarRange        = Math.max(rv.getRadarRange(), 1);
        float radarDisplayRange = radarRange * 2.0f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Фон
        guiGraphics.blit(RADAR_BACKGROUND, radarX, radarY, 0, 0, RADAR_SIZE, RADAR_SIZE, RADAR_SIZE, RADAR_SIZE);

        long now = System.currentTimeMillis();

        // Чистим полностью погасшие
        lastSeen.entrySet().removeIf(e -> now - e.getValue() > HOLD_TIME_MS + FADE_TIME_MS);
        lastPos.keySet().retainAll(lastSeen.keySet());

        // Мягкое мигание: общий множитель прозрачности для всех меток
        float blink = computeBlinkAlpha(now);
        if (blink <= 0.01f) {
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
            return; // в глухой OFF-фазе
        }

        // Подготовка проекции
        Vec3 origin = vehicle.position();
        double yawRad = Math.toRadians(vehicle.getYRot());

        for (Map.Entry<Long, Vec3> entry : lastPos.entrySet()) {
            Vec3 pos = entry.getValue();
            long seen = lastSeen.getOrDefault(entry.getKey(), now);

            // Альфа памяти точки
            float alpha;
            long dt = now - seen;
            if (dt <= HOLD_TIME_MS) alpha = 1.0f;
            else {
                float k = Math.min(1f, (float)(dt - HOLD_TIME_MS) / (float)FADE_TIME_MS);
                alpha = 1.0f - (1.0f - MIN_ALPHA) * k;
            }

            // Относительный вектор
            Vec3 rel = pos.subtract(origin);
            double dx = rel.x, dz = rel.z;
            double distance = Math.sqrt(dx*dx + dz*dz);
            if (distance < 1e-6 || distance > radarRange) continue;

            // В локальные оси техники (без зеркалирования)
            double localX = -(dx * Math.cos(yawRad) + dz * Math.sin(yawRad)); // вправо +
            double localZ =  (-dx * Math.sin(yawRad) + dz * Math.cos(yawRad)); // вперёд +

            // Экранные координаты
            double displayDist = Math.min((distance / radarDisplayRange) * (RADAR_SIZE / 2.0 - 4), RADAR_SIZE / 2.0 - 4);
            double nx = localX / distance, nz = localZ / distance;

            int targetX = radarCenterX + (int)(nx * displayDist);
            int targetY = radarCenterY - (int)(nz * displayDist);

            // Итоговая прозрачность с учётом мигания
            float finalAlpha = alpha * blink;
            RenderSystem.setShaderColor(1f, 1f, 1f, finalAlpha);
            int s = 3;
            guiGraphics.blit(RADAR_TARGET, targetX - (s / 2), targetY - (s / 2), 0, 0, s, s, s, s);
        }

        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
    };

    public static Position cyclePosition() {
        switch (position) {
            case TOP_LEFT   -> position = Position.TOP_RIGHT;
            case TOP_RIGHT  -> position = Position.MID_RIGHT;
            case MID_RIGHT  -> position = Position.BOTTOM_RIGHT;
            case BOTTOM_RIGHT-> position = Position.BOTTOM_LEFT;
            case BOTTOM_LEFT-> position = Position.MID_LEFT;
            case MID_LEFT   -> position = Position.TOP_LEFT;
        }
        return position;
    }

    public static String positionId(Position p) {
        return switch (p) {
            case TOP_LEFT -> "top_left";
            case TOP_RIGHT -> "top_right";
            case MID_RIGHT -> "mid_right";
            case BOTTOM_RIGHT -> "bottom_right";
            case BOTTOM_LEFT -> "bottom_left";
            case MID_LEFT -> "mid_left";
        };
    }

    private static float normalize360(float angle) {
        angle %= 360f;
        if (angle < 0f) angle += 360f;
        return angle;
    }

    private static long cellKey(Vec3 pos) {
        int cx = Mth.floor(pos.x * 2.0);
        int cz = Mth.floor(pos.z * 2.0);
        return ((long) cx << 32) ^ (cz & 0xffffffffL);
    }

    // Мягкий коэффициент мигания [0..1] с плавными краями (smoothstep)
    private static float computeBlinkAlpha(long nowMs) {
        long phase = nowMs % BLINK_TOTAL;

        // OFF зона с fade-in в конце
        long offHard = Math.max(0, (int)(BLINK_OFF_MS - BLINK_FADE_MS));
        if (phase < offHard) return 0f;
        if (phase < BLINK_OFF_MS) {
            float x = (float)(phase - offHard) / (float)BLINK_FADE_MS; // 0..1
            return smooth01(x); // плавный вход
        }

        // ON зона с fade-out в конце
        long onPhase = phase - BLINK_OFF_MS;
        long onHard = Math.max(0, (int)(BLINK_ON_MS - BLINK_FADE_MS));
        if (onPhase < onHard) return 1f;
        if (onPhase < BLINK_ON_MS) {
            float x = 1f - (float)(onPhase - onHard) / (float)BLINK_FADE_MS; // 1..0
            return smooth01(x); // плавный выход
        }
        return 0f;
    }

    // Smoothstep (0..1) -> (0..1) с S‑кривой
    private static float smooth01(float x) {
        x = Mth.clamp(x, 0f, 1f);
        return x * x * (3f - 2f * x);
    }
}