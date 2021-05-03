package fr.bedwars;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.bedwars.NPC.NPC;
import fr.bedwars.NPC.NPCEvents;
import fr.bedwars.commands.BedWarsCommands;
import fr.bedwars.events.BedwarsEvents;

public class BedwarsMain extends JavaPlugin
{
    private File bedwars;
    private FileConfiguration bedwars_file;
    public static BedwarsMain instance;
    
    public GameState state;

    @Override
    public void onEnable()
    {
        getCommand("bw").setExecutor(new BedWarsCommands());
        getServer().getPluginManager().registerEvents(new BedwarsEvents(), this);
        getServer().getPluginManager().registerEvents(new NPCEvents(), this);
        
        if(!Bukkit.getOnlinePlayers().isEmpty())
        {
            for(Player player : Bukkit.getOnlinePlayers())
            {
                PacketReader reader = new PacketReader();
                reader.inject(player);
            }
        }
        
        instance = this;
        
        if(!getDataFolder().exists())
        {
            getDataFolder().mkdir();
        }
        
        this.bedwars = new File(getDataFolder() + File.separator + "game_settings.yml");
        
        if(!bedwars.exists())
        {
            try {
                bedwars.createNewFile();
                
            } catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        
        this.bedwars_file = YamlConfiguration.loadConfiguration(this.bedwars);
        
        if(!bedwars_file.contains("Game.RED"))
        {
            for(String color : this.getColorsTeam(bedwars_file.getInt("Game.max_players_teams")))
            {
                bedwars_file.set("Game." + color, new ArrayList<String>());
            }
            
            bedwars_file.set("Game.waiting_players", new ArrayList<String>());
        }
        
        this.setState(GameState.WAITING);
    }
    
    @Override
    public void onDisable()
    {
        for(Player player : Bukkit.getOnlinePlayers())
        {
            PacketReader reader = new PacketReader();
            reader.uninject(player);
        }
    }
    
    public FileConfiguration getGameSettings()
    {
        return this.bedwars_file;
    }
    
    public File getFile()
    {
        return this.bedwars;
    }
    
    public GameState getGameState()
    {
        return this.state;
    }
    
    public void setState(GameState state)
    {
        this.state = state;
    }
    
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
    
    @SuppressWarnings("unchecked")
    public void addPlayer(Player player, String color)
    {
        FileConfiguration bedwars = BedwarsMain.instance.getGameSettings();
        
        UUID uuid = player.getUniqueId();
        ArrayList<String> team = (ArrayList<String>) bedwars.getList("Game." + color.toUpperCase());
        team.add(uuid.toString());
        bedwars.set("Game." + color.toUpperCase(), team);
        this.saveGameSettings();
    }
    
    @SuppressWarnings("unchecked")
    public void addPlayer(Player player)
    {
        FileConfiguration bedwars = BedwarsMain.instance.getGameSettings();
        
        UUID uuid = player.getUniqueId();
        ArrayList<String> team = (ArrayList<String>) bedwars.getList("Game.waiting_players");
        team.add(uuid.toString());
        bedwars.set("Game.waiting_players", team);
        this.saveGameSettings();
    }
    
    @SuppressWarnings("unchecked")
    public void removePlayer(Player player, String color)
    {
        FileConfiguration bedwars = BedwarsMain.instance.getGameSettings();
        
        UUID uuid = player.getUniqueId();
        ArrayList<String> team = (ArrayList<String>) bedwars.getList("Game." + color.toUpperCase());
        team.remove(uuid.toString());
        bedwars.set("Game." + color.toUpperCase(), team);
        this.saveGameSettings();
    }
    
    @SuppressWarnings("unchecked")
    public void removePlayer(Player player)
    {
        FileConfiguration bedwars = BedwarsMain.instance.getGameSettings();
        
        UUID uuid = player.getUniqueId();
        ArrayList<String> team = (ArrayList<String>) bedwars.getList("Game.waiting_players");
        team.remove(uuid.toString());
        bedwars.set("Game.waiting_players", team);
        this.saveGameSettings();
    }
    
    @SuppressWarnings("unchecked")
    public String getTeamColor(Player player)
    {
        FileConfiguration bedwars = BedwarsMain.instance.getGameSettings();
        
        int max_players_team = bedwars.getInt("Game.max_players_teams");
        ArrayList<String> colors_teams = this.getColorsTeam(max_players_team);
        
        for(String color : colors_teams)
        {
            ArrayList<String> team = (ArrayList<String> )bedwars.getList("Game." + color.toUpperCase());
            
            if(team.contains(player.getUniqueId().toString()))
            {
                return color;
            }
            
            team.clear();
        }
        
        return "";
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<String> getTeam(String color)
    {
        FileConfiguration bedwars = BedwarsMain.instance.getGameSettings();
        
        ArrayList<String> team = (ArrayList<String>) bedwars.getList("Game." + color.toUpperCase());
        return team;
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
    
    public void timer()
    {
        FileConfiguration bedwars_file = BedwarsMain.instance.getGameSettings();
        
        new BukkitRunnable()
        {       
            int diamond = bedwars_file.getInt("Game.diamond_sec");
            int emerald = bedwars_file.getInt("Game.emerald_sec");
            int start = 15;
            HashMap<String, Integer> team_iron = new HashMap<>();
            HashMap<String, Integer> team_gold = new HashMap<>();
            
            @SuppressWarnings("deprecation")
            @Override
            public void run()
            {
                if(start >= 0)
                {
                    for(Player p : Bukkit.getOnlinePlayers())
                    {
                        if(start == 10)
                        {
                            p.sendTitle("§eLancement dans", "§a10 §eseconde(s)");
                        }
                        else if(start <= 5 && start != 0)
                        {
                            p.sendTitle("§eLancement dans", "§c" + start + " §eseconde(s)");
                        }
                        else if(start == 0)
                        {
                            p.sendTitle("§eLet's Go", "");
                            
                            for(String color :  BedwarsMain.instance.getColorsTeam(bedwars_file.getInt("Game.max_players_teams")))
                            {
                                Location loc = new Location(Bukkit.getWorld("world"), bedwars_file.getDouble("Game.v1" + color + ".x"), bedwars_file.getDouble("Game.v1" + color + ".y"), bedwars_file.getDouble("Game.v1" + color + ".z"));
                                NPC.createNPC("§b§lAchats", loc);
                            }
                        }
                    }
                }
                else if(start == -1)
                {
                    for(String color : BedwarsMain.instance.getColorsTeam(bedwars_file.getInt("Game.max_players_teams")))
                    {
                        ArrayList<String> team = BedwarsMain.instance.getTeam(color);
                        Location loc = new Location(Bukkit.getWorld(bedwars_file.getString("Game.spawn" + color.toUpperCase() + ".world_name")), bedwars_file.getDouble("Game.spawn" + color.toUpperCase() + ".x"), (bedwars_file.getDouble("Game.spawn" + color.toUpperCase() + ".y") + 1), bedwars_file.getDouble("Game.spawn" + color.toUpperCase() + ".z"));
                        
                        for(String s : team)
                        {
                            BedwarsMain.instance.setState(GameState.PLAYING);
                            Player p = Bukkit.getPlayer(UUID.fromString(s));
                            p.teleport(loc);
                            p.setGameMode(GameMode.SURVIVAL);
                            p.getInventory().clear();
                            p.setHealth(20.0);
                            p.setExp(0);
                            p.setFoodLevel(10);
                            ArrayList<Integer> RGBCode = BedwarsMain.instance.getRGBCode(color.toUpperCase());
                            BedwarsMain.instance.setLeatherArmor(p, RGBCode.get(0), RGBCode.get(1), RGBCode.get(2));
                        }
                    }
                }
                
                start--;
                
                if(diamond == 0)
                {
                    int nb = bedwars_file.getInt("Game.nb_diamond");
                    
                    for(int i = 1; i <= nb; i++)
                    {  
                        ItemStack diamond = new ItemStack(Material.DIAMOND);
                        Location loc = new Location(Bukkit.getWorld(bedwars_file.getString("Game.diamond" + i + ".world_name")), bedwars_file.getDouble("Game.diamond" + i + ".x"), (bedwars_file.getDouble("Game.diamond" + i + ".y") + 1), bedwars_file.getDouble("Game.diamond" + i + ".z"));
                        Item stack =  Bukkit.getWorld(bedwars_file.getString("Game.diamond" + i + ".world_name")).dropItem(loc, diamond);
                        stack.setPickupDelay(0);
                        stack.setVelocity(new Vector(0, 0, 0));
                        stack.setTicksLived(Integer.MAX_VALUE);
                    }
                    
                    diamond = 10;
                }
                
                if(emerald == 0)
                {
                    int nb = bedwars_file.getInt("Game.nb_emerald");
                    
                    for(int i = 1; i <= nb; i++)
                    {  
                        ItemStack emerald = new ItemStack(Material.EMERALD);
                        Location loc = new Location(Bukkit.getWorld(bedwars_file.getString("Game.emerald" + i + ".world_name")), bedwars_file.getDouble("Game.emerald" + i + ".x"), (bedwars_file.getDouble("Game.emerald" + i + ".y") + 1), bedwars_file.getDouble("Game.emerald" + i + ".z"));
                        Item stack =  Bukkit.getWorld(bedwars_file.getString("Game.emerald" + i + ".world_name")).dropItem(loc, emerald);
                        stack.setPickupDelay(0);
                        stack.setVelocity(new Vector(0, 0, 0));
                        stack.setTicksLived(Integer.MAX_VALUE);
                    }
                    
                    emerald = 10;
                }
                
                if(start < 0)
                {
                    diamond--;
                    emerald--;
                    
                    for(String color : BedwarsMain.instance.getColorsTeam(bedwars_file.getInt("Game.max_players_teams")))
                    {
                        if(!BedwarsMain.instance.getTeam(color).isEmpty())
                        {       
                            if(!team_iron.containsKey(color))
                            {
                                team_iron.put(color, bedwars_file.getInt("Game.ressource" + color.toUpperCase() + ".iron_sec") - 1);
                                team_gold.put(color, bedwars_file.getInt("Game.ressource" + color.toUpperCase() + ".gold_sec") - 1);
                            }
                            else
                            {
                                team_iron.put(color, (team_iron.get(color) - 1));
                                team_gold.put(color, (team_gold.get(color) - 1));
                                
                                if(team_iron.get(color) == 0)
                                {
                                    ItemStack iron = new ItemStack(Material.IRON_INGOT);
                                    Location loc = new Location(Bukkit.getWorld(bedwars_file.getString("Game.ressource" + color + ".world_name")), bedwars_file.getDouble("Game.ressource" + color + ".x"), (bedwars_file.getDouble("Game.ressource" + color + ".y") + 1), bedwars_file.getDouble("Game.ressource" + color + ".z"));
                                    Item stack =  Bukkit.getWorld(bedwars_file.getString("Game.ressource" + color + ".world_name")).dropItem(loc, iron);
                                    stack.setPickupDelay(0);
                                    stack.setVelocity(new Vector(0, 0, 0));
                                    stack.setTicksLived(Integer.MAX_VALUE);
                                    
                                    team_iron.put(color, bedwars_file.getInt("Game.ressource" + color.toUpperCase() + ".iron_sec"));
                                }
                                
                                if(team_gold.get(color) == 0)
                                {
                                    ItemStack gold = new ItemStack(Material.GOLD_INGOT);
                                    Location loc = new Location(Bukkit.getWorld(bedwars_file.getString("Game.ressource" + color + ".world_name")), bedwars_file.getDouble("Game.ressource" + color + ".x"), (bedwars_file.getDouble("Game.ressource" + color + ".y") + 1), bedwars_file.getDouble("Game.ressource" + color + ".z"));
                                    Item stack =  Bukkit.getWorld(bedwars_file.getString("Game.ressource" + color + ".world_name")).dropItem(loc, gold);
                                    stack.setPickupDelay(0);
                                    stack.setVelocity(new Vector(0, 0, 0));
                                    stack.setTicksLived(Integer.MAX_VALUE);
                                    
                                    team_gold.put(color, bedwars_file.getInt("Game.ressource" + color.toUpperCase() + ".gold_sec"));
                                }
                            }
                        }
                    }
                }
            }
            
        }.runTaskTimer(instance, 0, 20);
    }
    
    public void setLeatherArmor(Player p, int red, int green, int blue)
    {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta im_helmet = (LeatherArmorMeta) helmet.getItemMeta();
        im_helmet.setColor(Color.fromRGB(red, green, blue));
        im_helmet.setUnbreakable(true);
        helmet.setItemMeta(im_helmet);
        
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta im_chestplate = (LeatherArmorMeta) chestplate.getItemMeta();
        im_chestplate.setColor(Color.fromRGB(red, green, blue));
        im_chestplate.setUnbreakable(true);
        chestplate.setItemMeta(im_chestplate);
        
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta im_leggings = (LeatherArmorMeta) leggings.getItemMeta();
        im_leggings.setColor(Color.fromRGB(red, green, blue));
        im_leggings.setUnbreakable(true);
        leggings.setItemMeta(im_leggings);
        
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta im_boots = (LeatherArmorMeta) boots.getItemMeta();
        im_boots.setColor(Color.fromRGB(red, green, blue));
        im_boots.setUnbreakable(true);
        boots.setItemMeta(im_boots);
        
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
        p.updateInventory();
    }
    
    public ArrayList<Integer> getRGBCode(String color)
    {
        ArrayList<Integer> code = new ArrayList<>();
        
        switch (color)
        {
            case "RED":
                code.add(176);
                code.add(46);
                code.add(38);
            case "BLUE":
                code.add(60);
                code.add(68);
                code.add(170);
            case "LIME":
                code.add(128);
                code.add(199);
                code.add(31);
            case "YELLOW":
                code.add(254);
                code.add(216);
                code.add(61);
            case "CYAN":
                code.add(22);
                code.add(156);
                code.add(156);
            case "PINK":
                code.add(243);
                code.add(139);
                code.add(170);
            case "ORANGE":
                code.add(248);
                code.add(128);
                code.add(29);
            case "PURPLE":
                code.add(137);
                code.add(50);
                code.add(184);
        }
        
        return code;
    }
    
    public void spawnVillagers()
    {
        
    }
}
