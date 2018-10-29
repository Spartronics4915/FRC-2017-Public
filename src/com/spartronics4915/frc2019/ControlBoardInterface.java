package com.spartronics4915.frc2019;

/**
 * A basic framework for robot controls that other controller classes implement
 */
public interface ControlBoardInterface
{

    // DRIVER CONTROLS
    double getThrottle();

    double getTurn();

    boolean getQuickTurn();

    boolean getSlowDrive();

}
