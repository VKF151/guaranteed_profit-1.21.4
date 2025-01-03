package vance.profit.item.custom;

import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import vance.profit.item.ModItems;

public class CrazyTridentitem extends TridentItem {
    public CrazyTridentitem(Item.Settings settings) {
        super(settings);
    }
    boolean canTransform = false;

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, EquipmentSlot.MAINHAND);
        canTransform = true;

    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (!world.isClient() && user.isSneaking() && !user.isSwimming()) {
            if (canTransform) {
                ItemStack originalItem = new ItemStack(ModItems.CRAZY_SLOTS);

                user.setStackInHand(hand, originalItem);
                world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_ANVIL_USE, SoundCategory.PLAYERS, 0.35f, 1.0f);
                canTransform = false;
                return ActionResult.SUCCESS;
            }

        }
        if (itemStack.willBreakNextUse()) {
            return ActionResult.FAIL;
        } else if (!user.isTouchingWaterOrRain()) {
            return ActionResult.FAIL;
        } else {
            user.setCurrentHand(hand);
            return ActionResult.CONSUME;
        }
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
            if (i < 10) {
                return false;
            } else {
                if (!playerEntity.isTouchingWaterOrRain()) {
                    return false;
                } else if (stack.willBreakNextUse()) {
                    return false;
                } else {
                    RegistryEntry<SoundEvent> registryEntry = (RegistryEntry<SoundEvent>)EnchantmentHelper.getEffect(stack, EnchantmentEffectComponentTypes.TRIDENT_SOUND)
                            .orElse(SoundEvents.ITEM_TRIDENT_RIPTIDE_2);
                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));

                    float g = playerEntity.getYaw();
                    float h = playerEntity.getPitch();
                    float j = -MathHelper.sin(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0));
                    float k = -MathHelper.sin(h * (float) (Math.PI / 180.0));
                    float l = MathHelper.cos(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0));
                    float m = MathHelper.sqrt(j * j + k * k + l * l);
                    j *= 3 / m;
                    k *= 3 / m;
                    l *= 3 / m;
                    playerEntity.addVelocity((double)j, (double)k, (double)l);
                    playerEntity.useRiptide(20, 8.0F, stack);
                    if (playerEntity.isOnGround()) {
                        float n = 1.1999999F;
                        playerEntity.move(MovementType.SELF, new Vec3d(0.0, 1.1999999F, 0.0));
                    }

                    world.playSoundFromEntity(null, playerEntity, registryEntry.value(), SoundCategory.PLAYERS, 1.0F, 1.0F);
                    return true;

                }
            }
        } else {
            return false;
        }
    }
}
