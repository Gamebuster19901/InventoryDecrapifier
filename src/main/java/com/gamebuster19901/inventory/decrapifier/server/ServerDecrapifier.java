package com.gamebuster19901.inventory.decrapifier.server;

import java.util.LinkedHashSet;

import org.apache.logging.log4j.Level;

import com.gamebuster19901.inventory.decrapifier.Main;
import com.gamebuster19901.inventory.decrapifier.common.CommonDecrapifier;
import com.gamebuster19901.inventory.decrapifier.common.PlayerPickupQueue;
import com.gamebuster19901.inventory.decrapifier.common.events.packets.ServerAskPickupItemPacket;
import com.gamebuster19901.inventory.decrapifier.common.events.packets.ServerHasModPacket;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ServerDecrapifier extends CommonDecrapifier{
	public static final LinkedHashSet<ConnectedClient> clients = new LinkedHashSet<ConnectedClient>();
	private AllPlayerPickupsHashSet pickups = new AllPlayerPickupsHashSet();
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	protected void ServerTickEvent(TickEvent.ServerTickEvent e){
		if(e.phase == TickEvent.Phase.END){
			pickups.clear();
		}
		for(PlayerPickupQueue o : pickups) {
			for(EntityItem entity : o) {
				System.out.println(entity.getName());
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	protected void onPickup(EntityItemPickupEvent e) {
		if(!e.getEntity().getEntityWorld().isRemote) {
			Main.Proxy.NETWORK.sendTo(new ServerAskPickupItemPacket(e.getItem().getUniqueID(), e.getItem().getItem()), (EntityPlayerMP)e.getEntityPlayer());
			if(getClient(e.getEntityPlayer()).hasMod){
				e.setCanceled(true);
				PlayerPickupQueue set = pickups.get(e.getEntityPlayer());
				if(set != null){
					if(set.contains(e.getItem())){
						e.setCanceled(false);
					}
				}
			}
		}
	}
	
	public void addPickup(EntityPlayer p, EntityItem e){
		PlayerPickupQueue set = pickups.get(p);
		if (set == null){
			set = new PlayerPickupQueue(p);
			pickups.add(set);
		}
		if (!set.contains(e)){
			set.add(e);
		}
	}
	
	@SubscribeEvent
	public void onPlayerJoin(PlayerLoggedInEvent e){
		Main.Proxy.NETWORK.sendTo(new ServerHasModPacket(), (EntityPlayerMP) e.player);
		new ConnectedClient(e.player);
	}
	
	@SubscribeEvent
	public void onPlayerLeave(PlayerLoggedOutEvent e){
		try{
			clients.remove(getClient(e.player));
		}
		catch(IllegalStateException e2){
			Main.LOGGER.log(Level.ERROR, "Player logged out that has not joined");
		}
	}
	
	public final ConnectedClient getClient(EntityPlayer p){
		for(ConnectedClient c : clients){
			if (c.getPlayer().equals(p)){
				return c;
			}
		}
		throw new IllegalStateException("No such client connected: " + p);
	}
	
	public static final class ConnectedClient{
		private EntityPlayer player;
		private boolean hasMod;
		
		public ConnectedClient(EntityPlayer player){
			this.player = player;
			ServerDecrapifier.clients.add(this);
		}
		
		public boolean hasMod(){
			return false;
		}
		
		public void setHasMod(boolean value){
			this.hasMod = value;
		}
		
		public EntityPlayer getPlayer(){
			return player;
		}
	}
}
