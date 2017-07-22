package com.lothrazar.cyclicmagic.fluid;
import java.awt.Color;
import com.lothrazar.cyclicmagic.data.Const;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
/**
 * I learned how to do this thanks to @elucent https://github.com/RootsTeam/Embers/blob/6e75e7c5c19e6dc6f9eb91a75f56c938b64a9898/src/main/java/teamroots/embers/fluid/FluidMoltenIron.java
 * @author Sam
 *
 */
public class FluidExp extends Fluid {
  // TODO: link this to the Experience Pylon, convert that to Fluid GUI
  public FluidExp() {
    super("xpjuice", new ResourceLocation(Const.MODID , "blocks/fluid_xpjuice_base"), new ResourceLocation(Const.MODID, "blocks/fluid_xpjuice_flowing"));
    setViscosity(1200);//water is 1000, lava is 6000
    setDensity(1200);//water is 1000, lava is 3000
    setUnlocalizedName("xpjuice");
  }
  @Override
  public int getColor() {
    return Color.GREEN.getRGB();
  }
}
