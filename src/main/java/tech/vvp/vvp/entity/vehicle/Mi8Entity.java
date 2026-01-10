package tech.vvp.vvp.entity.vehicle;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.lang.reflect.Field;

public class Mi8Entity extends CamoVehicleBase {

    private static final EntityDataAccessor<Boolean> LEFT_DOOR_OPEN = SynchedEntityData.defineId(Mi8Entity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RIGHT_DOOR_OPEN = SynchedEntityData.defineId(Mi8Entity.class, EntityDataSerializers.BOOLEAN);

    // Door animation progress (0 = closed, 1 = open)
    private float leftDoorProgress = 0f;
    private float leftDoorProgressO = 0f;
    private float rightDoorProgress = 0f;
    private float rightDoorProgressO = 0f;

    private static final ResourceLocation[] CAMO_TEXTURES = {
        new ResourceLocation("vvp", "textures/entity/mi8_default.png"),
        new ResourceLocation("vvp", "textures/entity/mi8_pepeshneyna.png")
    };

    private static final String[] CAMO_NAMES = {"Default", "Pepeshneyna"};

    private static Field propellerRotField;
    private static Field propellerRotOField;

    static {
        try {
            Class<?> vehicleClass = Class.forName("com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity");
            propellerRotField = vehicleClass.getDeclaredField("propellerRot");
            propellerRotField.setAccessible(true);
            propellerRotOField = vehicleClass.getDeclaredField("propellerRotO");
            propellerRotOField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Mi8Entity(EntityType<Mi8Entity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LEFT_DOOR_OPEN, false);
        this.entityData.define(RIGHT_DOOR_OPEN, false);
    }

    @Override
    public ResourceLocation[] getCamoTextures() {
        return CAMO_TEXTURES;
    }

    @Override
    public String[] getCamoNames() {
        return CAMO_NAMES;
    }

    // Door state methods
    public boolean isLeftDoorOpen() {
        return this.entityData.get(LEFT_DOOR_OPEN);
    }

    public void setLeftDoorOpen(boolean open) {
        this.entityData.set(LEFT_DOOR_OPEN, open);
    }

    public boolean isRightDoorOpen() {
        return this.entityData.get(RIGHT_DOOR_OPEN);
    }

    public void setRightDoorOpen(boolean open) {
        this.entityData.set(RIGHT_DOOR_OPEN, open);
    }

    // Door animation progress getters
    public float getLeftDoorProgress(float partialTick) {
        return Mth.lerp(partialTick, leftDoorProgressO, leftDoorProgress);
    }

    public float getRightDoorProgress(float partialTick) {
        return Mth.lerp(partialTick, rightDoorProgressO, rightDoorProgress);
    }

    @Override
    public void tick() {
        super.tick();
        
        // Auto-close doors when taking off (propeller spinning fast or off ground)
        if (!this.level().isClientSide) {
            float propellerSpeed = Math.abs(getPropellerRot() - getPropellerRotO());
            boolean isFlying = !this.onGround() || propellerSpeed > 0.5f;
            
            if (isFlying) {
                if (isLeftDoorOpen()) {
                    setLeftDoorOpen(false);
                }
                if (isRightDoorOpen()) {
                    setRightDoorOpen(false);
                }
            }
        }
        
        // Update door animation progress
        leftDoorProgressO = leftDoorProgress;
        rightDoorProgressO = rightDoorProgress;
        
        float doorSpeed = 0.1f; // Animation speed
        
        if (isLeftDoorOpen()) {
            leftDoorProgress = Math.min(1f, leftDoorProgress + doorSpeed);
        } else {
            leftDoorProgress = Math.max(0f, leftDoorProgress - doorSpeed);
        }
        
        if (isRightDoorOpen()) {
            rightDoorProgress = Math.min(1f, rightDoorProgress + doorSpeed);
        } else {
            rightDoorProgress = Math.max(0f, rightDoorProgress - doorSpeed);
        }
    }

    public float getPropellerRot() {
        try {
            return propellerRotField != null ? (float) propellerRotField.get(this) : 0f;
        } catch (Exception e) {
            return 0f;
        }
    }

    public float getPropellerRotO() {
        try {
            return propellerRotOField != null ? (float) propellerRotOField.get(this) : 0f;
        } catch (Exception e) {
            return 0f;
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        // Check if player clicked on a door (empty hand, not riding) - ПРОВЕРЯЕМ ДВЕРИ ПЕРВЫМИ
        if (player.getItemInHand(hand).isEmpty() && !player.isPassenger()) {
            // Determine which door based on player position relative to helicopter
            double relativeX = player.getX() - this.getX();
            double relativeZ = player.getZ() - this.getZ();
            
            // Rotate by helicopter yaw to get local coordinates
            float yaw = (float) Math.toRadians(-this.getYRot());
            double localX = relativeX * Math.cos(yaw) - relativeZ * Math.sin(yaw);
            double localZ = relativeX * Math.sin(yaw) + relativeZ * Math.cos(yaw);
            
            // Door zone - sides of helicopter in middle section
            boolean onLeftSide = localX > 1.0;
            boolean onRightSide = localX < -1.0;
            boolean inMiddleSection = localZ > -1.0 && localZ < 2.0;
            
            // Shift+Click = toggle door, Click = board (if door open)
            if (inMiddleSection && (onLeftSide || onRightSide)) {
                if (player.isShiftKeyDown()) {
                    // Shift+Click - toggle door (НЕ вызываем super!)
                    if (onLeftSide) {
                        if (!this.level().isClientSide) {
                            setLeftDoorOpen(!isLeftDoorOpen());
                            player.displayClientMessage(
                                Component.literal(isLeftDoorOpen() ? "Left door opened" : "Left door closed")
                                    .withStyle(ChatFormatting.YELLOW),
                                true
                            );
                            this.level().playSound(null, this.blockPosition(),
                                SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
                        player.swing(hand);
                        return InteractionResult.SUCCESS;
                    } else if (onRightSide) {
                        if (!this.level().isClientSide) {
                            setRightDoorOpen(!isRightDoorOpen());
                            player.displayClientMessage(
                                Component.literal(isRightDoorOpen() ? "Right door opened" : "Right door closed")
                                    .withStyle(ChatFormatting.YELLOW),
                                true
                            );
                            this.level().playSound(null, this.blockPosition(),
                                SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
                        player.swing(hand);
                        return InteractionResult.SUCCESS;
                    }
                } else {
                    // Normal click - try to board if door is open
                    if (onLeftSide && !isLeftDoorOpen()) {
                        if (!this.level().isClientSide) {
                            player.displayClientMessage(
                                Component.literal("Open the door first! (Shift+RMB)").withStyle(ChatFormatting.RED),
                                true
                            );
                        }
                        return InteractionResult.FAIL;
                    }
                    if (onRightSide && !isRightDoorOpen()) {
                        if (!this.level().isClientSide) {
                            player.displayClientMessage(
                                Component.literal("Open the door first! (Shift+RMB)").withStyle(ChatFormatting.RED),
                                true
                            );
                        }
                        return InteractionResult.FAIL;
                    }
                    // Door is open - allow boarding
                    return super.interact(player, hand);
                }
            }
            
            // Block boarding if doors are closed
            if (!isLeftDoorOpen() && !isRightDoorOpen()) {
                if (!this.level().isClientSide) {
                    player.displayClientMessage(
                        Component.literal("Open the door first!").withStyle(ChatFormatting.RED),
                        true
                    );
                }
                return InteractionResult.FAIL;
            }
        }
        
        // Проверяем спрей и стандартное поведение (инвентарь, посадка)
        return super.interact(player, hand);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("LeftDoorOpen", isLeftDoorOpen());
        compound.putBoolean("RightDoorOpen", isRightDoorOpen());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("LeftDoorOpen")) {
            setLeftDoorOpen(compound.getBoolean("LeftDoorOpen"));
        }
        if (compound.contains("RightDoorOpen")) {
            setRightDoorOpen(compound.getBoolean("RightDoorOpen"));
        }
    }
}
