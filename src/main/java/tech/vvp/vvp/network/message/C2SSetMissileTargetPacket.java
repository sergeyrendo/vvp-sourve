package tech.vvp.vvp.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import tech.vvp.vvp.entity.vehicle.M142HimarsEntity;

import java.util.function.Supplier;

public class C2SSetMissileTargetPacket {
    private final int entityId;
    private final double x, y, z;

    public C2SSetMissileTargetPacket(int entityId, double x, double y, double z) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public C2SSetMissileTargetPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            Level level = player.level();
            Entity entity = level.getEntity(entityId);

            if (entity instanceof M142HimarsEntity himars) {
                himars.setTargetCoordinates(player, new Vec3(x, y, z));
            }
        });
        ctx.setPacketHandled(true);
        return true;
    }
}
