package com.massivecraft.factions.task;

import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.ModuloRepeatTask;
import com.massivecraft.massivecore.util.TimeUnit;

public class TaskEconLandReward extends ModuloRepeatTask
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TaskEconLandReward i = new TaskEconLandReward();
	public static TaskEconLandReward get() { return i; }
	
	@Override
	public long getDelayMillis()
	{
		return (long) (MConf.get().taskEconLandRewardMinutes * TimeUnit.MILLIS_PER_MINUTE);
	}
	
	@Override
	public void setDelayMillis(long delayMillis)
	{
		MConf.get().taskEconLandRewardMinutes = delayMillis / (double) TimeUnit.MILLIS_PER_MINUTE;
	}
	
	@Override
	public void invoke(long now)
	{
		if ( ! MassiveCore.isTaskServer()) return;
		FactionColl.get().econLandRewardRoutine();
	}
	
}
