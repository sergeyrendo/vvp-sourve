package tech.vvp.vvp.tools;

public class HimarsBallistics {
    private static final double V2_OVER_G = 1732.0508075688772;
    private static final double MIN_PITCH = 18.0;
    private static final double MAX_PITCH = 45.0;

    public static double computePitch(double range) {
        if (range <= 0) return MIN_PITCH;

        double val = range / V2_OVER_G;
        if (val > 1.0) {
            return MAX_PITCH;
        }

        double lowAngleRad = 0.5 * Math.asin(val);
        double lowAngleDeg = Math.toDegrees(lowAngleRad);

        double smoothFactor = Math.min(1.0, range / 1000.0);
        double pitch = MIN_PITCH + (lowAngleDeg - MIN_PITCH) * smoothFactor;

        if (pitch < MIN_PITCH) pitch = MIN_PITCH;
        if (pitch > MAX_PITCH) pitch = MAX_PITCH;

        return pitch;
    }
}
