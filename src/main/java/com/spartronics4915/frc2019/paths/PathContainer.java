package com.spartronics4915.frc2019.paths;

import java.util.List;

import com.spartronics4915.frc2019.paths.PathBuilder.Waypoint;
import com.spartronics4915.lib.control.Path;
import com.spartronics4915.lib.math.Pose2d;

/**
 * Interface containing all information necessary for a path including the Path
 * itself, the Path's starting pose, and
 * whether or not the robot should drive in reverse along the path.
 */
public interface PathContainer
{

    Path buildPath();

    List<Waypoint> getWaypoints();
    
    Pose2d getStartPose();

    boolean isReversed();
}
