package br.com.syrxcraft.betterskyblock.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

import br.com.syrxcraft.betterskyblock.BetterSkyBlock;
import com.flowpowered.math.vector.Vector3i;
import com.griefdefender.api.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.biome.BiomeReplace;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.biome.BaseBiome;
import com.sk89q.worldedit.world.biome.Biomes;

public class Utils {

	public static UUID toUUID(byte[] bytes) {

	    if (bytes.length != 16) {
	        throw new IllegalArgumentException();
	    }

	    int i = 0;
	    long msl = 0;
	    for (; i < 8; i++) {
	        msl = (msl << 8) | (bytes[i] & 0xFF);
	    }

	    long lsl = 0;
	    for (; i < 16; i++) {
	        lsl = (lsl << 8) | (bytes[i] & 0xFF);
	    }

	    return new UUID(msl, lsl);
	}
	
	public static String UUIDtoHexString(UUID uuid) {

		if (uuid == null) return "0x0";

		return "0x" + org.apache.commons.lang.StringUtils.leftPad(Long.toHexString(uuid.getMostSignificantBits()), 16, "0")+org.apache.commons.lang.StringUtils.leftPad(Long.toHexString(uuid.getLeastSignificantBits()), 16, "0");
	}
	
	public static boolean isFakePlayer(Player player) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if(player == p) {
				return false;
			}
		}
		return true;
	}
	
	public static String sanitizeSql(String string) {
		return string.replace("\\", "\\\\").replace("\"", "\\\"");
	}
	
	public static boolean regen(Location center, int blockRadius) {
		com.sk89q.worldedit.world.World world = fromBukkitToWorldEditWorld(center.getWorld());
		EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, 1000000);
		Region region = new CuboidRegion(new Vector(center.getBlockX()-blockRadius,0,center.getBlockZ()-blockRadius), new Vector(center.getBlockX()+blockRadius,255,center.getBlockZ()+blockRadius));
		
		
		return world.regenerate(region, editSession);
	}
	
	public static void setBiome(Location location, int radius, String biomeName) {
		com.sk89q.worldedit.world.World world = fromBukkitToWorldEditWorld(location.getWorld());
		EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession((com.sk89q.worldedit.world.World) world, 1000000);
		world.getWorldData().getBiomeRegistry().getBiomes();
		BaseBiome biome = Biomes.findBiomeByName(world.getWorldData().getBiomeRegistry().getBiomes(), biomeName, world.getWorldData().getBiomeRegistry());
		if (biome == null) {
			throw new IllegalStateException("Biome not found");
		}
		
		BiomeReplace biomeReplace = new BiomeReplace(editSession, biome);
		try {
			for (int x = location.getBlockX()-radius; x<=location.getBlockX()+radius; x++) {
				for (int z = location.getBlockZ()-radius; z<=location.getBlockZ()+radius; z++) {
					biomeReplace.apply(new Vector2D(x, z));
				}
			}
		} catch (WorldEditException e) {
			e.printStackTrace();
		}
	}
	
	public static Biome matchAllowedBiome(String biomeName) {
		biomeName = biomeName.toLowerCase();
		for (Biome biome : BetterSkyBlock.getInstance().config().getAllowedBiomes()) {
			if (biome.toString().replace("_", "").toLowerCase().equals(biomeName)) {
				return biome;
			}
		}
		return null;
	}

	public static String fromSnakeToCamelCase(String string) {
		StringBuilder sb = new StringBuilder();
		
		for (String s : string.split("_")) {
			sb.append((""+s.charAt(0)).toUpperCase());
			sb.append(s.substring(1).toLowerCase());
		}
		
		return sb.toString();
	}

	public static com.sk89q.worldedit.world.World fromBukkitToWorldEditWorld(org.bukkit.World world) {
		for (com.sk89q.worldedit.world.World w : WorldEdit.getInstance().getServer().getWorlds()) {
			if (world.getName().equals(w.getName())) {
				return w;
			}
		}
		return null;
	}
	
	public static Vector toVector(Location loc) {
		return new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	public static String toString(Location location) {
		return "[World: " + location.getWorld().getName() + ", X: " + location.getBlockX() + ", Y: " + location.getBlockY() + ", Z: " + location.getBlockZ()+"]";
	}

	public static int validate(int Min, int Max, int value){

		if(value < Min){
			return Min;
		}else if(value > Max){
			return Max;
		}

		return value;
	}

	public static int validate(int Min, int Max, int defaultValue, int value){

		if(value < Min || value > Max) return defaultValue;

		return value;
	}

	public static Biome getBiome(String name){

		for(Biome biome : Biome.values()){
			if(biome.name().equalsIgnoreCase(name)){
				return biome;
			}
		}

		return null;
	}

	public static World worldFromUUID(UUID uuid){
		return Bukkit.getWorld(uuid);
	}

	public static void bukkitSync(Runnable runnable){
		Bukkit.getScheduler().runTask(BetterSkyBlock.getInstance(), runnable);
	}

	public static Player asBukkitPlayer(UUID uuid){
		return Bukkit.getPlayer(uuid);
	}

	public static Player asBukkitPlayer(User user){
		return asBukkitPlayer(user.getUniqueId());
	}
	public static OfflinePlayer asBukkitOfflinePlayer(UUID uuid){
		return Bukkit.getOfflinePlayer(uuid);
	}

	public static OfflinePlayer asBukkitOfflinePlayer(User user){
		return asBukkitOfflinePlayer(user.getUniqueId());
	}

	public static Vector3i locationToVector(Location location){
		return new Vector3i(location.getBlockX(),location.getBlockY(),location.getBlockZ());
	}

	public static Vector3i[] orderPositions(Vector3i[] positions){

		int minX = positions[0].getX();
		int minY = positions[0].getY();
		int minZ = positions[0].getZ();

		int maxX = minX;
		int maxY = minY;
		int maxZ = minZ;

		for (Vector3i pos : positions) {

			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			if (x < minX) minX = x;
			if (y < minY) minY = y;
			if (z < minZ) minZ = z;

			if (x > maxX) maxX = x;
			if (y > maxY) maxY = y;
			if (z > maxZ) maxZ = z;
		}

		return new Vector3i[]{
				new Vector3i(minX, minY, minZ),
				new Vector3i(maxX, maxY, maxZ)
		};
	}

	public static String getFormatedDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'às' HH:mm:ss");
		return formatter.format(date);
	}

	public static String getFormatedDate(Instant instant) {
		Date date = Date.from(instant);
		return getFormatedDate(date);
	}

	public static List<OfflinePlayer> getPlayersByUUID(List<UUID> uuidList) {
		List<OfflinePlayer> players = new ArrayList<>();
		for (UUID uuid : uuidList) {
			players.add(asBukkitOfflinePlayer(uuid));
		}
		return players;
	}
	public static List<String> getPlayersNameByUUID(List<UUID> uuidList) {
		List<String> players = new ArrayList<>();
		for (UUID uuid : uuidList) {
			players.add(asBukkitOfflinePlayer(uuid).getName());
		}
		return players;
	}
	public static List<String> getPlayersNameByUUID(Set<UUID> uuidList) {
		List<String> players = new ArrayList<>();
		for (UUID uuid : uuidList) {
			players.add(asBukkitOfflinePlayer(uuid).getName());
		}
		return players;
	}
}
