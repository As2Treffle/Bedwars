package fr.bedwars.commands;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import fr.bedwars.BedwarsMain;

public class BedWarsCommands implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args)
    {
        if(sender instanceof Player)
        {
            Player p = (Player)sender;
            FileConfiguration bedwars = BedwarsMain.instance.getGameSettings();
            
            if(args.length > 0)
            {
                if(p.isOp())
                {
                    if(args[0].equalsIgnoreCase("setLobby"))
                    {
                        bedwars.set("Game.Lobby.world_name", p.getWorld().getName());
                        bedwars.set("Game.Lobby.x", p.getLocation().getX());
                        bedwars.set("Game.Lobby.y", p.getLocation().getY());
                        bedwars.set("Game.Lobby.z", p.getLocation().getZ());
                        
                        p.sendMessage("§7[§bBed§6Wars§7] §aLobby location saved at §5" + (Math.round(p.getLocation().getX() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getY() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getZ() * 100) / 100));
                        
                        this.saveGameSettings();
                    }
                    else if(args[0].equalsIgnoreCase("setWaitingRoom"))
                    {
                        bedwars.set("Game.Waiting_room.world_name", p.getWorld().getName());
                        bedwars.set("Game.Waiting_room.x", p.getLocation().getX());
                        bedwars.set("Game.Waiting_room.y", p.getLocation().getY());
                        bedwars.set("Game.Waiting_room.z", p.getLocation().getZ());
                        
                        p.sendMessage("§7[§bBed§6Wars§7] §aWaiting room location saved at §5" + (Math.round(p.getLocation().getX() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getY() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getZ() * 100) / 100));
                        
                        this.saveGameSettings();
                    }
                    else if(args[0].equalsIgnoreCase("setSpawn") && args.length == 2)
                    {
                        String team = args[1];
                        
                        bedwars.set("Game.spawn" + team.toUpperCase() + ".world_name", p.getWorld().getName());
                        bedwars.set("Game.spawn" + team.toUpperCase() + ".x", p.getLocation().getX());
                        bedwars.set("Game.spawn" + team.toUpperCase() + ".y", p.getLocation().getY());
                        bedwars.set("Game.spawn" + team.toUpperCase() + ".z", p.getLocation().getZ());
                        
                        p.sendMessage("§7[§bBed§6Wars§7]§a " + team.toUpperCase() + "'s spawn location saved at §5" + (Math.round(p.getLocation().getX() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getY() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getZ() * 100) / 100));
                        
                        this.saveGameSettings();
                    }
                    else if(args[0].equalsIgnoreCase("setBed") && args.length == 2)
                    {
                        String team = args[1];
                        
                        bedwars.set("Game.bed" + team.toUpperCase() + ".world_name", p.getWorld().getName());
                        bedwars.set("Game.bed" + team.toUpperCase() + ".x", p.getLocation().getBlockX());
                        bedwars.set("Game.bed" + team.toUpperCase() + ".y", p.getLocation().getBlockY());
                        bedwars.set("Game.bed" + team.toUpperCase() + ".z", p.getLocation().getBlockZ());
                        
                        p.sendMessage("§7[§bBed§6Wars§7]§a " + team.toUpperCase() + "'s bed location saved at §5" + (Math.round(p.getLocation().getX() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getY() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getZ() * 100) / 100));
                        
                        this.saveGameSettings();
                    } 
                    else if(args[0].equalsIgnoreCase("setMaxPlayers") && args.length == 2)
                    {
                        String max = args[1];
                        
                        bedwars.set("Game.max_players", Integer.decode(max));
                        
                        p.sendMessage("§7[§bBed§6Wars§7]§a Max players set to §5" + max);
                        
                        this.saveGameSettings();
                    } 
                    else if(args[0].equalsIgnoreCase("setMinPlayers") && args.length == 2)
                    {
                        String min = args[1];
                        
                        bedwars.set("Game.min_players", Integer.decode(min));
                        
                        p.sendMessage("§7[§bBed§6Wars§7]§a Min players set to §5" + min);
                        
                        this.saveGameSettings();
                    } 
                    else if(args[0].equalsIgnoreCase("setMaxPlayersInTeam") && args.length == 2)
                    {
                        String max = args[1];
                        
                        bedwars.set("Game.max_players_teams", Integer.decode(max));
                        
                        p.sendMessage("§7[§bBed§6Wars§7]§a Max players in team set to §5" + max);
                        
                        this.saveGameSettings();
                    }  
                    else if(args[0].equalsIgnoreCase("setTeams") && args.length == 2)
                    {
                        String teams = args[1];
                        
                        bedwars.set("Game.nb_teams", Integer.decode(teams));
                        
                        p.sendMessage("§7[§bBed§6Wars§7]§a Teams set to §5" + teams);
                        
                        this.saveGameSettings();
                    }
                    else if(args[0].equalsIgnoreCase("setVillager1") && args.length == 2)
                    {
                        bedwars.set("Game.v1" + args[1].toUpperCase() + ".world_name", p.getWorld().getName());
                        bedwars.set("Game.v1" + args[1].toUpperCase() + ".x", p.getLocation().getX());
                        bedwars.set("Game.v1" + args[1].toUpperCase() + ".y", p.getLocation().getY());
                        bedwars.set("Game.v1" + args[1].toUpperCase() + ".z", p.getLocation().getZ());
                        
                        this.saveGameSettings();
                        
                        p.sendMessage("§7[§bBed§6Wars§7]§a Villager 1 location saved at §5" + (Math.round(p.getLocation().getX() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getY() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getZ() * 100) / 100));
                    }
                    else if(args[0].equalsIgnoreCase("setVillager2"))
                    {
                        bedwars.set("Game.v2.world_name", p.getWorld().getName());
                        bedwars.set("Game.v2.x", p.getLocation().getX());
                        bedwars.set("Game.v2.y", p.getLocation().getY());
                        bedwars.set("Game.v2.z", p.getLocation().getZ());
                        
                        this.saveGameSettings();
                        
                        p.sendMessage("§7[§bBed§6Wars§7]§a Villager 2 location saved at §5" + (Math.round(p.getLocation().getX() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getY() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getZ() * 100) / 100));
                    }
                    else if(args[0].equalsIgnoreCase("setVillager3"))
                    {
                        bedwars.set("Game.v3.world_name", p.getWorld().getName());
                        bedwars.set("Game.v3.x", p.getLocation().getX());
                        bedwars.set("Game.v3.y", p.getLocation().getY());
                        bedwars.set("Game.v3.z", p.getLocation().getZ());
                        
                        this.saveGameSettings();
                        
                        p.sendMessage("§7[§bBed§6Wars§7]§a Villager 3 location saved at §5" + (Math.round(p.getLocation().getX() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getY() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getZ() * 100) / 100));
                    }
                    else if(args[0].equalsIgnoreCase("setDiamond") && args.length == 2)
                    {
                        int nb = Integer.parseInt(args[1]);
                        
                        bedwars.set("Game.diamond" + nb + ".world_name", p.getWorld().getName());
                        bedwars.set("Game.diamond" + nb + ".x", p.getLocation().getX());
                        bedwars.set("Game.diamond" + nb + ".y", p.getLocation().getY());
                        bedwars.set("Game.diamond" + nb + ".z", p.getLocation().getZ());
                        
                        this.saveGameSettings();
                        
                        p.sendMessage("§7[§bBed§6Wars§7]§a Diamond generator's location saved at §5" + (Math.round(p.getLocation().getX() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getY() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getZ() * 100) / 100));
                    }
                    else if(args[0].equalsIgnoreCase("setEmerald") && args.length == 2)
                    {
                        int nb = Integer.parseInt(args[1]);
                        
                        bedwars.set("Game.emerald" + nb + ".world_name", p.getWorld().getName());
                        bedwars.set("Game.emerald" + nb + ".x", p.getLocation().getX());
                        bedwars.set("Game.emerald" + nb + ".y", p.getLocation().getY());
                        bedwars.set("Game.emerald" + nb + ".z", p.getLocation().getZ());
                        
                        this.saveGameSettings();
                        
                        p.sendMessage("§7[§bBed§6Wars§7]§a Emerald generator's location saved at §5" + (Math.round(p.getLocation().getX() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getY() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getZ() * 100) / 100));
                    }
                    else if(args[0].equalsIgnoreCase("setRessource") && args.length == 2)
                    {
                        String team = args[1];
                        
                        bedwars.set("Game.ressource" + team + ".world_name", p.getWorld().getName());
                        bedwars.set("Game.ressource" + team + ".x", p.getLocation().getX());
                        bedwars.set("Game.ressource" + team + ".y", p.getLocation().getY());
                        bedwars.set("Game.ressource" + team + ".z", p.getLocation().getZ());
                        bedwars.set("Game.ressource" + team + ".iron_sec", 5);
                        bedwars.set("Game.ressource" + team + ".gold_sec", 25);
                        
                        this.saveGameSettings();
                        
                        p.sendMessage("§7[§bBed§6Wars§7]§a Ressource generator's " + team.toUpperCase() + " location saved at §5" + (Math.round(p.getLocation().getX() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getY() * 100) / 100) + "§a, §5" + (Math.round(p.getLocation().getZ() * 100) / 100));
                    }
                    else if(args[0].equalsIgnoreCase("setNbDiamond") && args.length == 2)
                    {
                        int nb = Integer.parseInt(args[1]);
                        
                        bedwars.set("Game.nb_diamond", nb);
                        bedwars.set("Game.diamond_sec", 30);
                        
                        this.saveGameSettings();
                        
                        p.sendMessage("§7[§bBed§6Wars§7]§a Diamond generator's number saved");
                    }
                    else if(args[0].equalsIgnoreCase("setNbEmerald") && args.length == 2)
                    {
                        int nb = Integer.parseInt(args[1]);
                        
                        bedwars.set("Game.nb_emerald", nb);
                        bedwars.set("Game.emerald_sec", 60);
                        
                        this.saveGameSettings();
                        
                        p.sendMessage("§7[§bBed§6Wars§7]§a Emerald generator's number saved");
                    }
                }
            }
        }
        
        return true;
    }
    
    private void saveGameSettings()
    {
        FileConfiguration bedwars_file = BedwarsMain.instance.getGameSettings();
        
        try
        {
            bedwars_file.save(BedwarsMain.instance.getFile());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
        bedwars_file = YamlConfiguration.loadConfiguration(BedwarsMain.instance.getFile());
    }
}
