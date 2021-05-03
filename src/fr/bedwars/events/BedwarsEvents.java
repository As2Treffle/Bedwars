package fr.bedwars.events;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.bedwars.BedwarsMain;
import fr.bedwars.GameState;

public class BedwarsEvents implements Listener
{
    @EventHandler
    public void onIteract(PlayerInteractEvent e)
    {
        Player p = e.getPlayer();
        ItemStack stack = e.getItem();
        
        if(stack != null)
        {
            if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_AIR)
            {
                if(stack.hasItemMeta())
                {
                    ItemMeta meta = stack.getItemMeta();
                    
                    if(stack.getType() == Material.MAGMA_CREAM && meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE))
                    {
                        e.setCancelled(true);
                        
                        FileConfiguration bedwars_file = BedwarsMain.instance.getGameSettings();
                        
                        Location loc = new Location(Bukkit.getWorld(bedwars_file.getString("Game.Lobby.world_name")), bedwars_file.getDouble("Game.Lobby.x"), bedwars_file.getDouble("Game.Lobby.y"), bedwars_file.getDouble("Game.Lobby.z"));
                        p.teleport(loc);
                        
                    }
                    else if(stack.getType() == Material.NETHER_STAR && meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE))
                    {
                        e.setCancelled(true);
                        this.openTeam(p);
                    }
                }
            }
            else if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
            {
                Material block = e.getClickedBlock().getType();
                
                if(block == Material.FURNACE || block == Material.CRAFTING_TABLE || block == Material.RED_BED)
                {
                    e.setCancelled(true);
                }
            }
        }
        else if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            Material block = e.getClickedBlock().getType();
            
            if(block == Material.FURNACE || block == Material.CRAFTING_TABLE || block == Material.RED_BED)
            {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent e)
    {
        Player p = (Player)e.getWhoClicked();
        ItemStack stack = e.getCurrentItem();
        InventoryView view = e.getView();
        FileConfiguration bedwars = BedwarsMain.instance.getGameSettings();
        
        if(view.getTitle() == "§5§8Choisir une équipe")
        {
            if(stack != null)
            {
                Material color = stack.getType();
                e.setCancelled(true);
                
                ArrayList<String> team = BedwarsMain.instance.getTeam(color.toString().replace("_WOOL", ""));
                
                if(team.size() != bedwars.getInt("Game.max_players_teams"))
                {
                    BedwarsMain.instance.addPlayer(p, color.toString().replace("_WOOL", ""));
                    p.setPlayerListName(this.getColorCode(color.toString().replace("_WOOL", "")) + " §l" + color.toString().charAt(0) + " " + p.getDisplayName() + " ");
                    p.setDisplayName(this.getColorCode(color.toString().replace("_WOOL", "")) + " §l" + color.toString().charAt(0) + " " + p.getDisplayName() + " ");
                    BedwarsMain.instance.removePlayer(p);
                    ArrayList<String> colors = this.getColorTeam();
                    colors.remove(color.toString());
                    
                    for(String s : colors)
                    {
                        BedwarsMain.instance.removePlayer(p, s.replace("_WOOL", ""));
                    }
                    
                    p.sendMessage("§aVous avez rejoin une nouvelle équipe");
                    this.openTeam(p);
                }
                else
                {
                    p.sendMessage("§cCette équipe est déjà complète");
                }
            }
        }
        
    }
    
    public void openTeam(Player player)
    {
        Inventory inv = Bukkit.createInventory(null, 9, "§5§8Choisir une équipe");
        FileConfiguration bedwars = BedwarsMain.instance.getGameSettings();
        
        for(String color: this.getColorTeam())
        {
            ArrayList<String> lore = new ArrayList<>();;
            
            ItemStack stack = new ItemStack(Material.valueOf(color));
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName("§f" + color.replace("_WOOL", ""));
            
            ArrayList<String> team = BedwarsMain.instance.getTeam(color.toString().replace("_WOOL", ""));
            
            for(String uuid : team)
            {
                lore.add("§8 - " + this.getColorCode(color.replace("_WOOL", "")) + Bukkit.getPlayer(UUID.fromString(uuid)).getDisplayName());
            }
            
            if(team.size() == bedwars.getInt("Game.max_players_teams"))
            {
                lore.add("§8Cette équipe est complète");
            }
            
            meta.setLore(lore);
            stack.setItemMeta(meta);
            lore.clear();
            
            inv.addItem(stack);
        }
        
        player.openInventory(inv);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        FileConfiguration bedwars_file = BedwarsMain.instance.getGameSettings(); 
        
        int players = bedwars_file.getInt("Game.nb_teams") * bedwars_file.getInt("Game.max_players_teams");
        
        e.setJoinMessage("§8[§a+§8]§6 " + p.getDisplayName() + " §6joined §8[§a" + Bukkit.getOnlinePlayers().size() + "§7/§a" + players + "§8]");
        
        ItemStack lobby = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta im_lobby = lobby.getItemMeta();
        im_lobby.setDisplayName("§8Retour au §aLobby");
        im_lobby.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        lobby.setItemMeta(im_lobby);
        
        ItemStack team = new ItemStack(Material.NETHER_STAR);
        ItemMeta im_team = team.getItemMeta();
        im_team.setDisplayName("§8Choisir une §aéquipe");
        im_team.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        team.setItemMeta(im_team);
        
        p.getInventory().clear();
        p.getInventory().setItem(4, team);
        p.getInventory().setItem(8, lobby);
        p.updateInventory();
        p.setHealth(20.0);
        p.setFoodLevel(20);
        p.setGameMode(GameMode.ADVENTURE);
        
        Location loc = new Location(Bukkit.getWorld(bedwars_file.getString("Game.Waiting_room.world_name")), bedwars_file.getDouble("Game.Waiting_room.x"), bedwars_file.getDouble("Game.Waiting_room.y"), bedwars_file.getDouble("Game.Waiting_room.z"));
        p.teleport(loc);
        
        BedwarsMain.instance.addPlayer(p);
        
        if(Bukkit.getOnlinePlayers().size() >= bedwars_file.getInt("Game.min_players"))
        {
            BedwarsMain.instance.setState(GameState.START);
            BedwarsMain.instance.timer();
        }
        
    }
    
    @SuppressWarnings("deprecation")
    @EventHandler
    public void breakBed(BlockBreakEvent e)
    {
        Player p = e.getPlayer();
        Block block = e.getBlock();
        
        if(BedwarsMain.instance.getGameState() == GameState.PLAYING)
        {
            if(block.getType() == Material.RED_BED)
            {
                Location loc = block.getLocation();
                
                if(this.getBedLocations().contains(loc))
                {
                    String team = this.getBedColor(loc, BedwarsMain.instance.getTeamColor(p));
                    
                    if(!team.equals("error"))
                    {
                        for(String p1 : BedwarsMain.instance.getTeam(team))
                        {
                            Player p_team = Bukkit.getPlayer(UUID.fromString(p1));
                            p_team.sendTitle("§cLit Détruit", "");
                        }
                        
                        for(Player p2 : Bukkit.getOnlinePlayers())
                        {
                            p2.sendMessage("");
                            p2.sendMessage("Lit de l'équipe " + this.getColorCode(team) + "§l" + team + " §rdétruit par " + this.getColorCode(BedwarsMain.instance.getTeamColor(p)) + p.getName());
                            p2.sendMessage("");
                        }
                    }
                    else
                    {
                        e.setCancelled(true);
                        p.sendMessage("§cVous ne pouvez pas casser votre lit !");
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        
        for(String s : this.getColorTeam())
        {
            BedwarsMain.instance.removePlayer(p, s.replace("_WOOL", ""));
        }
        
        BedwarsMain.instance.removePlayer(p);
        FileConfiguration bedwars_file = BedwarsMain.instance.getGameSettings();
        
        int players = bedwars_file.getInt("Game.nb_teams") * bedwars_file.getInt("Game.max_players_teams");
        
        e.setQuitMessage("§8[§c-§8]§6 " + p.getDisplayName() + " §6disconnected §8[§a" + Bukkit.getOnlinePlayers().size() + "§7/§a" + players + "§8]");
    }
    
    private ArrayList<String> getColorTeam()
    {
        ArrayList<String> colors = new ArrayList<String>();
        
        colors.add("RED_WOOL");
        colors.add("BLUE_WOOL");
        colors.add("YELLOW_WOOL");
        colors.add("LIME_WOOL");
        colors.add("CYAN_WOOL");
        colors.add("PINK_WOOL");
        colors.add("ORANGE_WOOL");
        colors.add("PURPLE_WOOL");
        
        return colors;
    }
    
    private String getColorCode(String color)
    {
        switch (color)
        {
            case "RED":
                return "§4";
            case "BLUE":
                return "§1";
            case "YELLOW":
                return "§e";
            case "LIME":
                return "§a";
            case "CYAN":
                return "§3";
            case "PINK":
                return "§d";
            case "ORANGE":
                return "§6";
            case "PURPLE":
                return "§5";
        }
        
        return "";
    }
    
    /*
    @SuppressWarnings("unchecked")
    private void setTeam()
    {
        FileConfiguration bedwars_file = BedwarsMain.instance.getGameSettings(); 
        ArrayList<String> waiting_players = (ArrayList<String>) bedwars_file.getList("Game.waiting_players");
        ArrayList<String> colors = this.getColorsTeam(bedwars_file.getInt("Game.max_players_teams"));
        
        for(String player : waiting_players)
        {
            for(String color : colors)
            {
                if(BedwarsMain.instance.getTeam(color).size() != bedwars_file.getInt("Game.max_players_teams"))
                {
                    BedwarsMain.instance.addPlayer(Bukkit.getPlayer(UUID.fromString(player)), color);
                    BedwarsMain.instance.removePlayer(Bukkit.getPlayer(UUID.fromString(player)));
                }
            }
        }
    }*/
    
    private ArrayList<String> getColorsTeam(int max_players)
    {
        ArrayList<String> colors = new ArrayList<>();
        
        if(max_players == 1 || max_players == 2)
        {
            colors.add("RED");
            colors.add("BLUE");
            colors.add("YELLOW");
            colors.add("LIME");
            colors.add("CYAN");
            colors.add("PINK");
            colors.add("ORANGE");
            colors.add("PURPLE");
        }
        else
        {
            colors.add("RED");
            colors.add("BLUE");
            colors.add("YELLOW");
            colors.add("LIME");
        }
        
        return colors;
    }
    
    private ArrayList<Location> getBedLocations()
    {
        FileConfiguration bedwars_file = BedwarsMain.instance.getGameSettings();   
        ArrayList<Location> locs = new ArrayList<>();
        
        for(String color : this.getColorsTeam(bedwars_file.getInt("Game.max_players_teams")))
        {
            Location loc = new Location(Bukkit.getWorld("world"), bedwars_file.getDouble("Game.bed" + color + ".x"), bedwars_file.getDouble("Game.bed" + color + ".y"), bedwars_file.getDouble("Game.bed" + color + ".z"));
            locs.add(loc);
        }
        
        return locs;
    }
    
    private String getBedColor(Location loc_bed, String color1)
    {
        FileConfiguration bedwars_file = BedwarsMain.instance.getGameSettings();   
        
        for(String color : this.getColorsTeam(bedwars_file.getInt("Game.max_players_teams")))
        {
            Location loc = new Location(Bukkit.getWorld("world"), bedwars_file.getDouble("Game.bed" + color + ".x"), bedwars_file.getDouble("Game.bed" + color + ".y"), bedwars_file.getDouble("Game.bed" + color + ".z"));
            
            if(loc_bed == loc && color != color1)
            {
                return color;
            }
            else
            {
                return "error";
            }
        }
        
        return "";
    }
}
