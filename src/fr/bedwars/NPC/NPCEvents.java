package fr.bedwars.NPC;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.bedwars.PacketReader;
import net.minecraft.server.v1_15_R1.EntityPlayer;

public class NPCEvents implements Listener
{ 
    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        PacketReader reader = new PacketReader();
        reader.inject(e.getPlayer());
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e)
    {
        PacketReader reader = new PacketReader();
        reader.uninject(e.getPlayer());
    }
    
    @EventHandler
    public void onClickNPC(RightClickNPCEvent e)
    {
        Player p = e.getPlayer();
        EntityPlayer npc = e.getNPC();
        
        if(npc.getName().equalsIgnoreCase("Achats"))
        {
            p.sendMessage("OK");
        }
    }
}
