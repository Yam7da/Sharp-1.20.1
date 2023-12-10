package net.yam7da.sharpmod.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


public class BattleAxeItem extends AxeItem {
    public BattleAxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            Vec3d lookVec = user.getRotationVector();
            BlockPos startPos = user.getBlockPos();

            for (int i = 1; i <= 8; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        BlockPos targetPos = startPos.offset(); //I'm not really sure what to use in the offset method

                        if (world.isAir(targetPos)) {
                            FallingBlockEntity fallingBlock = new FallingBlockEntity(EntityType.FALLING_BLOCK, world);
                            fallingBlock.setVelocity(lookVec.add(0, 0.4, 0)); // adjusting velocity
                            world.spawnEntity(fallingBlock);
                        }

                        // check for entities and apply effects
                        world.getEntitiesByClass(LivingEntity.class, new Box(targetPos), entity -> entity != user).forEach(entity -> {
                            entity.setVelocity(entity.getVelocity().add(0, 1, 0)); // launch into the air
                            entity.damage(world.getDamageSources().fallingBlock(null), 10f); // apply damage
                        });

                    }
                }
                user.getItemCooldownManager().set(this, 100); // add cooldown for the ability
            }
        }

        return TypedActionResult.success(user.getActiveItem());
    }

    // Additional methods and logic as needed
}

