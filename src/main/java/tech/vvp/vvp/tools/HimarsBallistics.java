package tech.vvp.vvp.tools;

public class HimarsBallistics {
    // Константа подобрана так, чтобы при 60° дальность была 1500,
    // но она также работает корректно, если максимум сделать 45°.
    private static final double V2_OVER_G = 1732.0508075688772;

    // Лимиты
    private static final double MIN_PITCH = 18.0;
    private static final double MAX_PITCH = 45.0; // ← ТЕПЕРЬ максимум 45°

    /**
     * Возвращает корректный pitch (в градусах) для дистанции R.
     */
    public static double computePitch(double range) {
        if (range <= 0) return MIN_PITCH;

        double val = range / V2_OVER_G;
        if (val > 1.0) {
            // Вне дальности — ставим максимум
            return MAX_PITCH;
        }

        // Баллистический низкий угол (в радианах → в градусы)
        double lowAngleRad = 0.5 * Math.asin(val);
        double lowAngleDeg = Math.toDegrees(lowAngleRad);

        // Плавное повышение угла — от 0 до 1 при росте дистанции 0–1000
        double smoothFactor = Math.min(1.0, range / 1000.0);

        // Плавный переход от 18° → lowAngleDeg
        double pitch = MIN_PITCH + (lowAngleDeg - MIN_PITCH) * smoothFactor;

        // Ограничиваем
        if (pitch < MIN_PITCH) pitch = MIN_PITCH;
        if (pitch > MAX_PITCH) pitch = MAX_PITCH;

        return pitch;
    }
}
