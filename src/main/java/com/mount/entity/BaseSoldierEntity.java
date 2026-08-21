package com.mount.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import com.mount.util.NameGenerator;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public abstract class BaseSoldierEntity extends Monster {


    private static final EntityDataAccessor<Integer> DATA_LEVEL = SynchedEntityData.defineId(BaseSoldierEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_XP_PROGRESS = SynchedEntityData.defineId(BaseSoldierEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_XP_TOTAL = SynchedEntityData.defineId(BaseSoldierEntity.class, EntityDataSerializers.INT);
    private static final ResourceLocation LEVEL_HEALTH_MODIFIER = ResourceLocation.fromNamespaceAndPath("riding-the-storm-war-of-kings-2", "level_health");
    private static final ResourceLocation LEVEL_ATTACK_MODIFIER = ResourceLocation.fromNamespaceAndPath("riding-the-storm-war-of-kings-2", "level_attack");
    private static final ResourceLocation LEVEL_SPEED_MODIFIER = ResourceLocation.fromNamespaceAndPath("riding-the-storm-war-of-kings-2", "level_speed");
    private static final ResourceLocation LEVEL_FOLLOW_MODIFIER = ResourceLocation.fromNamespaceAndPath("riding-the-storm-war-of-kings-2", "level_follow");
    private static final EntityDataAccessor<String> DATA_NAME = SynchedEntityData.defineId(BaseSoldierEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_FACTION_ID = SynchedEntityData.defineId(BaseSoldierEntity.class, EntityDataSerializers.STRING);

    @Nullable
    private UUID ownerUUID;

    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTick = 0;

    private int mountEntityId = -1;
    private boolean isRidingHorse = false;

    protected BaseSoldierEntity(EntityType<? extends BaseSoldierEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_LEVEL, 1);
        builder.define(DATA_XP_PROGRESS, 0.0F);
        builder.define(DATA_XP_TOTAL, 0);
        builder.define(DATA_NAME, "");
        builder.define(DATA_FACTION_ID, "");
    }

    // ----- 等级系统（保持不变）-----
    public int getLevel() { return this.entityData.get(DATA_LEVEL); }
    public float getXpProgress() { return this.entityData.get(DATA_XP_PROGRESS); }
    public int getXpTotal() { return this.entityData.get(DATA_XP_TOTAL); }
    public int getXpNeededForNextLevel() { int level = getLevel(); return level * 7 + 3; }

    public boolean addXp(int amount) {
        if (this.level().isClientSide()) return false;
        int xpTotal = getXpTotal() + amount;
        int xpNeeded = getXpNeededForNextLevel();
        if (xpTotal >= xpNeeded) {
            int newLevel = getLevel() + 1;
            int remainingXp = xpTotal - xpNeeded;
            this.entityData.set(DATA_LEVEL, newLevel);
            this.entityData.set(DATA_XP_TOTAL, remainingXp);
            this.entityData.set(DATA_XP_PROGRESS, (float) remainingXp / getXpNeededForNextLevel());
            applyLevelModifiers(newLevel);
            return true;
        } else {
            this.entityData.set(DATA_XP_TOTAL, xpTotal);
            this.entityData.set(DATA_XP_PROGRESS, (float) xpTotal / xpNeeded);
            return false;
        }
    }

    private void applyLevelModifiers(int level) {
        removeLevelModifiers();
        AttributeInstance healthAttr = this.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.addPermanentModifier(new AttributeModifier(LEVEL_HEALTH_MODIFIER, (double) level, AttributeModifier.Operation.ADD_VALUE));
        }
        AttributeInstance attackAttr = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr != null) {
            attackAttr.addPermanentModifier(new AttributeModifier(LEVEL_ATTACK_MODIFIER, (double) level, AttributeModifier.Operation.ADD_VALUE));
        }
        AttributeInstance speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.addPermanentModifier(new AttributeModifier(LEVEL_SPEED_MODIFIER, level * 0.01, AttributeModifier.Operation.ADD_VALUE));
        }
        AttributeInstance followAttr = this.getAttribute(Attributes.FOLLOW_RANGE);
        if (followAttr != null) {
            followAttr.addPermanentModifier(new AttributeModifier(LEVEL_FOLLOW_MODIFIER, (double) level, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private void removeLevelModifiers() {
        AttributeInstance healthAttr = this.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) healthAttr.removeModifier(LEVEL_HEALTH_MODIFIER);
        AttributeInstance attackAttr = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr != null) attackAttr.removeModifier(LEVEL_ATTACK_MODIFIER);
        AttributeInstance speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) speedAttr.removeModifier(LEVEL_SPEED_MODIFIER);
        AttributeInstance followAttr = this.getAttribute(Attributes.FOLLOW_RANGE);
        if (followAttr != null) followAttr.removeModifier(LEVEL_FOLLOW_MODIFIER);
    }

    public void setLevel(int level) {
        this.entityData.set(DATA_LEVEL, Math.max(1, level));
        applyLevelModifiers(level);
    }

    public void levelUp() { addXp(getXpNeededForNextLevel()); }

    // ----- 名字系统（保持不变）-----
    public String getSoldierName() { return this.entityData.get(DATA_NAME); }
    public void setSoldierName(String name) { this.entityData.set(DATA_NAME, name); }

    // ----- 阵营系统（保持不变）-----
    public String getFactionId() { return this.entityData.get(DATA_FACTION_ID); }
    public void setFactionId(String factionId) { this.entityData.set(DATA_FACTION_ID, factionId == null ? "" : factionId); }
    public boolean isSameFaction(BaseSoldierEntity other) {
        String myFaction = getFactionId();
        String otherFaction = other.getFactionId();
        return myFaction != null && !myFaction.isEmpty() && otherFaction != null && !otherFaction.isEmpty() && myFaction.equals(otherFaction);
    }

    private String generateRandomName() { return NameGenerator.generateName(this.random); }

    @Override
    public Component getCustomName() {
        String name = getSoldierName();
        if (name == null || name.isEmpty()) {
            return Component.literal("Lv." + getLevel());
        }
        return Component.literal(name + " Lv." + getLevel());
    }
    @Override
    public boolean hasCustomName() { return true; }

    // ----- 抽象方法（保持不变）-----
    protected static AttributeSupplier.Builder createBaseSoldierAttributes() { return createBaseAttributes(); }
    public abstract String getDisplayNameKey();
    public abstract String getEntityName();

    public static AttributeSupplier.Builder createBaseAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.FOLLOW_RANGE, 35.0);
    }

    // ----- ownerUUID 相关方法 -----
    @Nullable
    public UUID getOwnerUUID() { return ownerUUID; }

    /**
     * 设置所有者 UUID（仅在服务端调用，否则保存无效）
     */
    public void setOwnerUUID(@Nullable UUID uuid) {
        this.ownerUUID = uuid;
    }

    /**
     * 便捷方法：设置所有者为指定玩家
     */
    public void setOwner(Player player) {
        if (!this.level().isClientSide() && player != null) {
            setOwnerUUID(player.getUUID());
            // 可选：发送数据包更新客户端（如果需要客户端显示）
        }
    }

    public boolean hasOwner() { return ownerUUID != null; }

    @Override
    public boolean canAttack(LivingEntity target) {
        // 如果目标是另一个士兵且拥有相同的主人，则不能攻击
        if (target instanceof BaseSoldierEntity other) {
            if (this.ownerUUID != null && this.ownerUUID.equals(other.ownerUUID)) {
                return false;  // 同一主人，不攻击
            }
            // 也可保留阵营ID判断
            if (this.isSameFaction(other)) {
                return false;
            }
        }
        // 否则调用父类逻辑
        return super.canAttack(target);
    }

    // ----- 跟随系统（保持不变）-----
    @Nullable
    public Player getFollowTarget() {
        if (ownerUUID == null || this.level().isClientSide()) return null;
        return this.level().getPlayerByUUID(ownerUUID);
    }

    private boolean followEnabled = false;
    private static final double FOLLOW_DISTANCE = 4.0;
    private static final double STOP_FOLLOW_DISTANCE = 2.0;

    public void setFollowEnabled(boolean enabled) { this.followEnabled = enabled; }
    public boolean isFollowEnabled() { return followEnabled; }

    protected void followOwnerTick() {
        if (!followEnabled || ownerUUID == null) return;
        Player owner = getFollowTarget();
        if (owner == null) return;
        double distSq = this.distanceToSqr(owner);
        if (distSq > FOLLOW_DISTANCE * FOLLOW_DISTANCE) {
            double dx = owner.getX() - this.getX();
            double dy = owner.getY() - this.getY();
            double dz = owner.getZ() - this.getZ();
            double dist = Math.sqrt(distSq);
            this.setDeltaMovement(
                    this.getDeltaMovement().x + dx / dist * 0.1,
                    this.getDeltaMovement().y + dy / dist * 0.1,
                    this.getDeltaMovement().z + dz / dist * 0.1
            );
        } else if (distSq < STOP_FOLLOW_DISTANCE * STOP_FOLLOW_DISTANCE) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 1.0, 0.5));
        }
    }

    // ----- 动画 tick（保持不变）-----
    @Override
    public void tick() {
        super.tick();
        if (getAttackAnim(1.0F) > 0.0F) {
            attackAnimationState.startIfStopped(tickCount);
        }
        if (!this.walkAnimation.isMoving()) {
            if (idleAnimationTick <= 3 && this.walkAnimation.speed() == 0.0F) {
                idleAnimationState.startIfStopped(tickCount);
            } else if (idleAnimationTick > 4 && this.walkAnimation.speed() == 0.0F) {
                idleAnimationState.stop();
            }
        } else {
            idleAnimationState.stop();
        }
        idleAnimationTick++;
    }

    // ***** 修改点 1：序列化保存（正确使用 ValueOutput）保持不变 *****
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        if (ownerUUID != null) {
            output.putLong("OwnerMost", ownerUUID.getMostSignificantBits());
            output.putLong("OwnerLeast", ownerUUID.getLeastSignificantBits());
        }
        String name = getSoldierName();
        if (name != null && !name.isEmpty()) {
            output.putString("SoldierName", name);
        }
        String factionId = getFactionId();
        if (factionId != null && !factionId.isEmpty()) {
            output.putString("FactionId", factionId);
        }
        output.putInt("Level", getLevel());
        output.putFloat("XpProgress", getXpProgress());
        output.putInt("XpTotal", getXpTotal());
    }

    // ***** 修改点 2：序列化读取 - 修复 getLong 用法 *****
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        // 修复：使用 getLong 返回 OptionalLong，用 getAsLong() 获取值
        var mostBits = input.getLong("OwnerMost");
        var leastBits = input.getLong("OwnerLeast");
        if (mostBits.isPresent() && leastBits.isPresent()) {
            ownerUUID = new UUID(mostBits.get(), leastBits.get()); // 注意是 getAsLong()
        }

        var nameOpt = input.getString("SoldierName");
        if (nameOpt.isPresent()) {
            setSoldierName(nameOpt.get());
        }
        var factionOpt = input.getString("FactionId");
        if (factionOpt.isPresent()) {
            setFactionId(factionOpt.get());
        }
        var levelOpt = input.getInt("Level");
        if (levelOpt.isPresent()) {
            setLevel(levelOpt.get());
        }
        this.entityData.set(DATA_XP_PROGRESS, input.getFloatOr("XpProgress", 0.0F));
        var xpTotalOpt = input.getInt("XpTotal");
        if (xpTotalOpt.isPresent()) {
            this.entityData.set(DATA_XP_TOTAL, xpTotalOpt.get());
        }
    }

    // ----- 骑马预留（保持不变）-----
    public int getMountEntityId() { return mountEntityId; }
    public void setMountEntityId(int entityId) { this.mountEntityId = entityId; }
    public boolean isRidingHorse() { return isRidingHorse; }
    public void setRidingHorse(boolean riding) { this.isRidingHorse = riding; }

    // 交互逻辑-  增加绑定玩家的功能
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);

        // 如果玩家手持空手且处于潜行状态，则绑定该玩家（或解绑）
        if (heldItem.isEmpty() && player.isShiftKeyDown()) {
            if (!this.level().isClientSide()) {
                // 如果已经有主人且主人就是当前玩家，则解绑
                if (this.ownerUUID != null && this.ownerUUID.equals(player.getUUID())) {
                    this.setOwnerUUID(null);
                    player.displayClientMessage(Component.literal("已解绑该士兵"), false);
                } else {
                    this.setOwner(player);
                    player.displayClientMessage(Component.literal("已绑定该士兵"), false);
                }
            }
            return InteractionResult.SUCCESS;
        }

        // 原有的背包打开逻辑（手持空手但不潜行）
        if (heldItem.isEmpty()) {
            if (!this.level().isClientSide()) {
                player.openMenu(new SoldierMenuProvider(this));
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    // ----- 初始化（保持不变）-----
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            EntitySpawnReason spawnReason,
            @Nullable SpawnGroupData spawnData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnReason, spawnData);
        if (getSoldierName().isEmpty()) {
            setSoldierName(generateRandomName());
        }
        return data;
    }



    // ----- Dummy 子类（保持不变）-----
    public static class DummySoldier extends BaseSoldierEntity {
        public DummySoldier(EntityType<? extends BaseSoldierEntity> entityType, Level level) {
            super(entityType, level);
        }
        @Override
        public String getDisplayNameKey() {
            return "entity.riding-the-storm-war-of-kings-2.dummy_soldier";
        }
        @Override
        public String getEntityName() {
            return "Dummy Soldier";
        }
    }
}