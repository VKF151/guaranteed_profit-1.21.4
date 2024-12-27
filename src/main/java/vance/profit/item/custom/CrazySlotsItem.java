package vance.profit.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import vance.profit.item.ModItems;

import java.util.Random;


public class CrazySlotsItem extends Item {
    public CrazySlotsItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {

        if (!world.isClient()) {
            Random random = new Random();
            int randomNumber = random.nextInt(2) + 1;
            ItemStack newItem = getWeaponForNumber(randomNumber);

            player.setStackInHand(hand, newItem);
            if (randomNumber == 3) {
                player.giveItemStack(new ItemStack(Items.SPECTRAL_ARROW));
            }
            world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 0.5f, 1.0f);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private ItemStack getWeaponForNumber(int number) {
        return switch (number) {
            case 1 -> new ItemStack(ModItems.CRAZY_SCYTHE);
            case 2 -> new ItemStack(ModItems.CRAZY_MACE);
            case 3 -> new ItemStack(Items.MACE);
            case 4 -> new ItemStack(Items.TRIDENT);
            case 5 -> new ItemStack(Items.CROSSBOW);
            case 6 -> new ItemStack(Items.BOW);
            case 7 -> new ItemStack(Items.DIAMOND_SWORD);
            case 8 -> new ItemStack(Items.GOLDEN_SWORD);
            case 9 -> new ItemStack(Items.IRON_SWORD);
            default -> ItemStack.EMPTY;
        };
    }


}
