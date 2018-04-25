package com.gamebuster19901.inventory.decrapifier.common;

import java.util.List;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public abstract class CommonDecrapifier{
	
	public static final EntityItem getEntityItemPlayerIsLookingAt(EntityPlayer p, int distance){
		int dis = distance;
		List<EntityItem> Items = p.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(p.posX - dis, p.posY - dis, p.posZ - dis, p.posX + dis, p.posY + dis, p.posZ + dis));
		double calcdist = dis;
		Vec3d pos = p.getPositionEyes(0);
		Vec3d lookvec = p.getLookVec();
		Vec3d var8 = pos.addVector(lookvec.x * dis, lookvec.y * dis, lookvec.z * dis);
		Vec3d var = pos.addVector(lookvec.x * 2, lookvec.y * 2, lookvec.z * 2);
		EntityItem pointedEntity = null;
		for (EntityItem entity : Items){
			float bordersize = entity.getCollisionBorderSize();
			AxisAlignedBB aabb = new AxisAlignedBB(entity.posX - entity.width / 2, entity.posY,
					entity.posZ - entity.width / 2, entity.posX + entity.width / 2,
					entity.posY + entity.height, entity.posZ + entity.width / 2);
			aabb.expand(bordersize, bordersize, bordersize);
			double d = calcdist;
			RayTraceResult mop0 = aabb.calculateIntercept(pos, var8);
			if (aabb.contains(pos)) {
				if (0.0D < d || d == 0.0D) {
					pointedEntity = entity;
					d = 0.0D;
				}
			} else if (mop0 != null) {
				double d1 = pos.distanceTo(mop0.hitVec);

				if (d1 < d || d == 0.0D) {
					pointedEntity = entity;
					d = d1;
				}
			}
		}
		return pointedEntity;
	}
	
}