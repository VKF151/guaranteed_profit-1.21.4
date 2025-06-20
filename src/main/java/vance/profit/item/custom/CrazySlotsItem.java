package vance.profit.item.custom;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import vance.profit.item.ModItems;

import java.util.Map;
import java.util.Random;



public class CrazySlotsItem extends Item {
    public CrazySlotsItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {

        if (!world.isClient()) {
            Random random = new Random();
            int randomNumber = random.nextInt(6) + 1;
            ItemStack newItem = getWeaponForNumber(randomNumber);
            player.setStackInHand(hand, newItem);

            if (randomNumber == 3) {
                player.giveItemStack(new ItemStack(Items.SPECTRAL_ARROW));
                CrazyCrossbowItem.canTransform = false;
            } else if (randomNumber == 6 && isHealthLow(player)) {
                if (player.getOffHandStack().isEmpty()) {
                    player.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.TOTEM_OF_UNDYING));
                } else player.giveItemStack(new ItemStack(Items.TOTEM_OF_UNDYING));
            }

            world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 0.25f, 1.0f);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private ItemStack getWeaponForNumber(int number) {
        return switch (number) {
            case 1 -> new ItemStack(ModItems.CRAZY_SCYTHE);
            case 2 -> new ItemStack(ModItems.CRAZY_MACE);
            case 3 -> new ItemStack(ModItems.CRAZY_CROSSBOW);
            case 4 -> new ItemStack(ModItems.CRAZY_TRIDENT);
            case 5 -> new ItemStack(ModItems.CRAZY_AXE);
            case 6 -> new ItemStack(ModItems.CRAZY_SWORD);
            default -> ItemStack.EMPTY;
        };
    }

    private Boolean isHealthLow(PlayerEntity player) {
        return player.getHealth() <= 8.0F;
    }
}




