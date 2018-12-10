package jackyy.dimensionaledibles.block;

import jackyy.dimensionaledibles.DimensionalEdibles;
import jackyy.dimensionaledibles.registry.ModConfig;
import jackyy.dimensionaledibles.util.TeleporterHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockOverworldCake extends BlockCakeBase {

    public BlockOverworldCake() {
        super();
        setRegistryName(DimensionalEdibles.MODID + ":overworld_cake");
        setTranslationKey(DimensionalEdibles.MODID + ".overworld_cake");
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        int meta = getMetaFromState(world.getBlockState(pos)) - 1;
        ItemStack stack = player.getHeldItem(hand);

        if (player.capabilities.isCreativeMode) {
            if (!stack.isEmpty() && stack.getItem() == Item.REGISTRY.getObject(new ResourceLocation(ModConfig.tweaks.overworldCake.fuel))) {
                world.setBlockState(pos, getStateFromMeta(0), 2);
                return true;
            }
            else {
                if (world.provider.getDimension() != 0) {
                    if (!world.isRemote) {
                        EntityPlayerMP playerMP = (EntityPlayerMP) player;
                        BlockPos coords;
                        if (ModConfig.tweaks.overworldCake.useCustomCoords) {
                            coords = new BlockPos(ModConfig.tweaks.overworldCake.customCoords.x, ModConfig.tweaks.overworldCake.customCoords.y, ModConfig.tweaks.overworldCake.customCoords.z);
                        } else {
                            coords = world.getSpawnPoint();
                            while (world.getBlockState(world.getSpawnPoint()).isFullCube()) {
                                coords = coords.up(2);
                            }
                        }
                        TeleporterHandler.teleport(playerMP, 0, coords.getX(), coords.getY(), coords.getZ(), playerMP.server.getPlayerList());
                    }
                }
                return true;
            }
        }
        else {
            if (!stack.isEmpty() && stack.getItem() == Item.REGISTRY.getObject(new ResourceLocation(ModConfig.tweaks.overworldCake.fuel))) {
                if (meta >= 0) {
                    world.setBlockState(pos, getStateFromMeta(meta), 2);
                    stack.shrink(1);
                    return true;
                }
            }
            else {
                if (world.provider.getDimension() != 0) {
                    if (!world.isRemote) {
                        consumeCake(world, pos, player);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void consumeCake(World world, BlockPos pos, EntityPlayer player) {
        if (player.canEat(true)) {
            int l = world.getBlockState(pos).getValue(BITES);

            if (l < 6) {
                player.getFoodStats().addStats(2, 0.1F);
                world.setBlockState(pos, world.getBlockState(pos).withProperty(BITES, l + 1), 3);
                EntityPlayerMP playerMP = (EntityPlayerMP) player;
                BlockPos coords;
                if (ModConfig.tweaks.overworldCake.useCustomCoords) {
                    coords = new BlockPos(ModConfig.tweaks.overworldCake.customCoords.x, ModConfig.tweaks.overworldCake.customCoords.y, ModConfig.tweaks.overworldCake.customCoords.z);
                } else {
                    coords = world.getSpawnPoint();
                    while (world.getBlockState(world.getSpawnPoint()).isFullCube()) {
                        coords = coords.up(2);
                    }
                }
                TeleporterHandler.teleport(playerMP, 0, coords.getX(), coords.getY(), coords.getZ(), playerMP.server.getPlayerList());
            }
        }
    }

    @Override @SuppressWarnings("deprecation")
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return ModConfig.tweaks.overworldCake.preFueled ? getStateFromMeta(0) : getStateFromMeta(6);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (ModConfig.general.overworldCake)
            list.add(new ItemStack(this));
    }

}
