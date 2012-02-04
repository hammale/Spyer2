package me.hammale.Spyer;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ServerConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

public class PacketHand
  implements PacketListener
{
  private final SpyerPlayerListener spyer;

  public PacketHand(SpyerPlayerListener spyer)
  {
    this.spyer = spyer;
  }

  public boolean checkPacket(Player player, MCPacket packet)
  {
    if (player == null) {
      System.out.println("[SPYER] Hey Spout, why are you making me check a packet addressed at nobody? I don't know what to do!");
    }
    return true;
  }

  public String name(MCPacket packet)
  {
    try {
      Field f = ((MCCraftPacket)packet).getPacket().getClass()
        .getField("a");
      if (f == null) {
        return "{!?}Null!{!?}";
      }
      Integer i = (Integer)f.get(((MCCraftPacket)packet).getPacket());
      for (Iterator localIterator = ((CraftServer)Bukkit.getServer()).getHandle().players.iterator(); localIterator.hasNext(); ) { Object e = localIterator.next();
        EntityPlayer ep = (EntityPlayer)e;
        if (ep.id == i.intValue()) {
          return ep.name;
        }
      }
      return "{!?}Notaplayer!{!?}";
    }
    catch (Exception e1) {
      e1.printStackTrace();
    }
    return "{!?}Error!{!?}";
  }
}