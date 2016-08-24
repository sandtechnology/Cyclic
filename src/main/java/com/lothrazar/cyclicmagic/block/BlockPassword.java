package com.lothrazar.cyclicmagic.block;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.lothrazar.cyclicmagic.ModMain;
import com.lothrazar.cyclicmagic.block.tileentity.TileEntityPassword;
import com.lothrazar.cyclicmagic.gui.ModGuiHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockPassword extends Block {
  public static final PropertyBool POWERED = PropertyBool.create("powered");
  public BlockPassword() {
    super(Material.ROCK);
  }
  @Override
  public TileEntity createTileEntity(World worldIn, IBlockState state) {
    return new TileEntityPassword();
  }
  @Override
  public boolean hasTileEntity() {
    return true;
  }
  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

    if (player.isSneaking()) { return false; } 
    if (world.isRemote) { return true; }
    int x = pos.getX(), y = pos.getY(), z = pos.getZ();
    player.openGui(ModMain.instance, ModGuiHandler.GUI_INDEX_PASSWORD, world, x, y, z);
    return true;
  }
  @Override
  public boolean hasTileEntity(IBlockState state) {
    return hasTileEntity();
  }
  public IBlockState getStateFromMeta(int meta) {
    return this.getDefaultState().withProperty(POWERED, meta == 1 ? true : false);
  }
  @Override
  public int getMetaFromState(IBlockState state) {
    return (state.getValue(POWERED)) ? 1 : 0;
  }
  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { POWERED });
  }
  @Override
  public boolean canProvidePower(IBlockState state) {
    return true;
  }
  @Override
  public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
    return blockState.getValue(POWERED) ? 15 : 0;
  }
  @Override
  public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
    return blockState.getValue(POWERED) ? 15 : 0;
  }
  @SubscribeEvent
  public void chatEvent(ServerChatEvent event) {//forge event
    World world = event.getPlayer().getEntityWorld();
    //for each loop hits a // oops : java.util.ConcurrentModificationException, so we need iterator
    Iterator<TileEntityPassword> iterator = TileEntityPassword.listeningBlocks.iterator();
    Map<BlockPos, Boolean> updates = new HashMap<BlockPos, Boolean>();
    List<TileEntityPassword> toRemove = new ArrayList<TileEntityPassword>();
    //TileEntityPassword current;
    while (iterator.hasNext()) {
      TileEntityPassword current = iterator.next();
      if (current.isInvalid() == false) {
        if (event.getMessage().equals(current.getMyPassword())) {
          IBlockState blockState = current.getWorld().getBlockState(current.getPos());
          boolean hasPowerHere = this.getStrongPower(blockState, current.getWorld(), current.getPos(), EnumFacing.UP) > 0;
          System.out.println("password activated by " + event.getUsername() + " hasPowerHere = " + hasPowerHere);
          updates.put(current.getPos(), !hasPowerHere);
          //current.getWorld().setBlockState(current.getPos(), this.getDefaultState().withProperty(BlockPassword.POWERED, !hasPowerHere));
        }
        //else password was wrong
      }
      else {
        toRemove.add(current);///is invalid
      }
    }
    //even with iterator we were getting ConcurrentModificationException on the iterator.next() line
    for (TileEntityPassword rm : toRemove) {
      TileEntityPassword.listeningBlocks.remove(rm);
    }
    for (Map.Entry<BlockPos, Boolean> entry : updates.entrySet()) {
      world.setBlockState(entry.getKey(), this.getDefaultState().withProperty(BlockPassword.POWERED, entry.getValue()));
    }
  }
}
