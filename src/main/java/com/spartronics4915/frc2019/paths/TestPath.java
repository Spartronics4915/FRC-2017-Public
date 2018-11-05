package com.spartronics4915.frc2019.paths;

import java.util.ArrayList;
import java.util.List;

import com.spartronics4915.frc2019.paths.PathBuilder.Waypoint;
import com.spartronics4915.lib.control.Path;
import com.spartronics4915.lib.math.Pose2d;
import com.spartronics4915.lib.math.Rotation2d;
import com.spartronics4915.lib.math.Translation2d;

public class TestPath implements PathContainer {

    ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();

    public TestPath()
    {
        sWaypoints.add(new Waypoint(50,50,0,0));
        sWaypoints.add(new Waypoint(170,50,0,10));
//        sWaypoints.add(new Waypoint(182,110,0,10));

    }

    @Override
    public Path buildPath()
    {
        return PathBuilder.buildPathFromWaypoints(sWaypoints);
    }

    @Override
    public List<Waypoint> getWaypoints()
    {
        return sWaypoints;
    }

    @Override
    public Pose2d getStartPose()
    {
        return new Pose2d(new Translation2d(50, 50), Rotation2d.fromDegrees(90.0));
    }

    @Override
    public boolean isReversed()
    {
        return false;
    }
    // WAYPOINT_DATA: [{"position":{"x":50,"y":50},"speed":0,"radius":0,"comment":""},{"position":{"x":182,"y":50},"speed":10,"radius":30,"comment":""},{"position":{"x":182,"y":110},"speed":10,"radius":0,"comment":""}]
    // IS_REVERSED: false
    // FILE_NAME: TestPath
}