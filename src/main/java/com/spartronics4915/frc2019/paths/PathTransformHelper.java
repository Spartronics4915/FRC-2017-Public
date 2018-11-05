package com.spartronics4915.frc2019.paths;

import java.util.ArrayList;

import com.spartronics4915.frc2019.Constants;
import com.spartronics4915.frc2019.paths.PathBuilder.Waypoint;
import com.spartronics4915.lib.math.Pose2d;
import com.spartronics4915.lib.math.Translation2d;

public class PathTransformHelper
{
    public static ArrayList<Waypoint> mirrorWaypointsAboutAxis(ArrayList<Waypoint> waypoints, boolean mirrorX, boolean mirrorY)
    {
        for (Waypoint w : waypoints)
        {
            w.position = mirrorTranslationAboutAxis(w.position, mirrorX, mirrorY);
        }
        return waypoints;
    }
    
    public static Pose2d mirrorPoseAboutAxis(Pose2d rt, boolean mirrorX, boolean mirrorY)
    {
        rt = new Pose2d(mirrorTranslationAboutAxis(rt.getTranslation(), mirrorX, mirrorY), rt.getRotation());
        return rt;
    }
    
    public static Translation2d mirrorTranslationAboutAxis(Translation2d translation, boolean mirrorX, boolean mirrorY)
    {
        if (mirrorX)
            translation = new Translation2d(Constants.kFieldDimensionTranslation.x() - translation.x(), translation.y());
        
        if (mirrorY)
            translation = new Translation2d(translation.x(), Constants.kFieldDimensionTranslation.y() - translation.y());
        
        return translation;
    }
}
