package com.spartronics4915.frc2019.auto.modes;

import com.spartronics4915.frc2019.auto.AutoModeBase;
import com.spartronics4915.frc2019.auto.AutoModeEndedException;
import com.spartronics4915.frc2019.auto.actions.ActuateHarvesterAction;
import com.spartronics4915.frc2019.auto.actions.DrivePathAction;
import com.spartronics4915.frc2019.auto.actions.ParallelAction;
import com.spartronics4915.frc2019.auto.actions.ParallelSingleWaitAction;
import com.spartronics4915.frc2019.auto.actions.ResetPoseFromPathAction;
import com.spartronics4915.frc2019.auto.actions.SeriesAction;
import com.spartronics4915.frc2019.auto.actions.WaitAction;
import com.spartronics4915.frc2019.paths.DriveToCloseSwitchFromAPath;
import com.spartronics4915.frc2019.paths.DriveToFarSwitchFromAPath;
import com.spartronics4915.frc2019.paths.PathContainer;
import com.spartronics4915.frc2019.subsystems.Harvester;
import com.spartronics4915.lib.util.Util;

public class PlaceSwitchFromAMode extends AutoModeBase
{

    private PathContainer mClosePath = new DriveToCloseSwitchFromAPath();
    private PathContainer mFarPath = new DriveToFarSwitchFromAPath();

    @Override
    protected void routine() throws AutoModeEndedException
    {
        runAction(new ActuateHarvesterAction(Harvester.WantedState.GRAB));
        PathContainer path;
        double timeout;
        if (Util.getGameSpecificMessage().charAt(0) == 'L')
        {
            path = mClosePath;
            timeout = PowerupHelper.kSideSwitchCloseTimeout;
        }
        else
        {
            path = mFarPath;
            timeout = PowerupHelper.kSideSwitchFarTimeout;
        }
        runAction(new ResetPoseFromPathAction(path));
        runAction(PowerupHelper.getDriveSwitchActionWithTimeout(path, timeout));
        runAction(new ActuateHarvesterAction(Harvester.WantedState.SLIDE_DROP));
        runAction(new ActuateHarvesterAction(Harvester.WantedState.OPEN));
    }

}
