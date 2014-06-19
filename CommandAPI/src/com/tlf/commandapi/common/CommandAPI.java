package com.tlf.commandapi.common;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

import net.minecraft.command.ICommand;

import java.util.HashSet;
import java.util.Set;

@Mod(modid = CommandAPI.MODID, name = CommandAPI.NAME, version = CommandAPI.VERSION)
public class CommandAPI
{
	public static final String MODID = "commandapi";
	public static final String NAME = "CommandAPI";
	public static final String VERSION = "1.0.0";
	
	@Instance(CommandAPI.MODID)
	public static CommandAPI instance;
	
	/** The set of commands that all players can use */
	private static Set<String> playerCommands = new HashSet<String>();
	
	/** The set of commands that only ops can use */
	private static Set<String> opCommands = new HashSet<String>();
	
	/** The set of ICommands to add on server start */
	private static ICommand[] commands = new ICommand[]{};
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		ModMetadata meta = event.getModMetadata();
		
		meta.description = "An API for adding commands to the game";
	}
	
	@EventHandler
	public void onInit(FMLInitializationEvent event)
	{
		addExistingPlayerCommand("tell");
		addExistingPlayerCommand("help");
		addExistingPlayerCommand("me");
	}
	
	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		registerCommands(event);
	}
	
	/**
	 * Adds a command to the {@link #playerCommands} and {@link #opCommands} sets
	 * @param command The ICommand class to add
	 */
	public static void addPlayerCommand(ICommand command)
	{
		playerCommands.add(command.getCommandName());
		addOpCommand(command);
	}
	
	/**
	 * Adds a command to the {@link #opCommands} set
	 * @param command The ICommand class to add
	 */
	public static void addOpCommand(ICommand command)
	{
		opCommands.add(command.getCommandName());
		
		ICommand[] temp = new ICommand[commands.length + 1];
		for (int i = 0; i < commands.length; i++)
		{
			temp[i] = commands[i];
		}
		
		temp[temp.length-1] = command;
		
		commands = temp;
	}
	
	
	/**
	 * called by the {@link CommandAPI main class} on postInit - adds the existing commands (that can be used by all players) to the {@link #playerCommands} and {@link #opCommands} sets
	 * @param command The command String to add
	 */
	protected static void addExistingPlayerCommand(String command)
	{
		playerCommands.add(command);
		addExistingOpCommand(command);
	}
	
	/**
	 * called by the {@link CommandAPI main class} on postInit - adds the existing op-only commands to the {@link #opCommands} set
	 * @param command The command String to add
	 */
	protected static void addExistingOpCommand(String command)
	{
		opCommands.add(command);
	}
	
	/**
	 * Called during the ServerStarting event
	 */
	public static void registerCommands(FMLServerStartingEvent event)
	{
		for (int i = 0; i < commands.length; i++)
		{
			event.registerServerCommand(commands[i]);
		}
	}
	
	/**
	 * @return {@link #playerCommands}
	 */
	public static Set<String> getPlayerCommands()
	{
		return playerCommands;
	}
	
	/**
	 * @return {@link #opCommands}
	 */
	public static Set<String> getOpCommands()
	{
		return opCommands;
	}
	
	/**
	 * Returns true if the command is in the set {@link #playerCommands}
	 * @param s The command to search for
	 */
	public static boolean isPlayerCommand(String s)
	{
		return playerCommands.contains(s);
	}
	
	/**
	 * Returns true if the command is in the set {@link #opCommands}
	 * @param s The command to search for
	 */
	public static boolean isOpCommand(String s)
	{
		return opCommands.contains(s);
	}
	
	/**
	 * Returns true if the command exists in either of the sets. ({@link #playerCommands} or {@link #opCommands})
	 * @param s The command to search for
	 */
	public static boolean isCommand(String s)
	{
		return playerCommands.contains(s) || opCommands.contains(s);
	}
}
