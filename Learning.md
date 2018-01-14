## Learning Team254's FRC-2017-Public code

### OI
* How do UI events trigger robot actions… are network tables involved?
* If a subsystems wants access to a joystick, how is this obtained?
* At what point is field position (alliance) detected for autonomous behavior?
* Where does joystick remapping occur? what remapping functions are applied?
* Which ControlBoardInterface is actually employed?
* Who owns the responsibility for inverting the sense of joystick directions?
* How/when/where is the smart dashboard updated?
* Is there a single location/database for all button and joystick identifiers?

### Threads
* what is a Runnable(),  CrashTrackingRunnable()?
* what does a synchronized method imply?
* what guarantees does Thread.sleep() offer?  How many invocations of this method are there in the entire codebase?

### Subsystem
* how many subsystems are there? Is there a base class that establishes
  all the interface conventions for subsystems?
* is there a single database of motor identifiers, gpio pins, etc?
* can subsystems modify the state of other subsystems?
* what does registerEnabledLoops do?  Who calls it?
* LED
	* when/how does the LED subsystem determine what blinking pattern to emit?  
	* How does it make a blinking pattern?
* Superstructure
	* how is this subsystem different from others?
* Drive
	* how many internal states does the Drive subsystem have?
	* how many actuators, how many sensors?
	* are MotorSafety settings in play?  If so, how do we ensure no
		“Robots Don’t Die” messages?
	* how are encoders handled?  how are encoder ticks converted to user-units?
	* what’s the relationship between Drive and RobotStateEstimator?
	* how easy will it be to replace NavX support with our IMU code?
	* in teleop, are we operating in setVelocity or in pctVBus mode?
	* when do they use positionControlMode?
	* how many follower-mode motors are there?
	* what units do they use in velocity control mode?
	* does their OI allow driver to enter auto-like modes during teleop?
	* what’s the difference between aimToGoal and driveTowardsGoal?

### Vision
    (todo)

### Commands, Scheduler
* how is the autonomous mode selected?  How does the selected mode
receive cycles (updates)?
* how does the autonomous mode control subsystems?
* what’s the difference between an auto mode and and auto action?
* what is the frequency of the scheduler loops, where is this embodied?
* how many threads are running at the same time in auto and tele?
* what is the Looper? how many instances are there? what is the roll
  of its Notifier?  Where is it started?
* what is a “wantedState” as specified in Robot::teleopPeriodic?  
* How are the multiple states of each subsystem coordinated?
* What is the relationship between code here and in the Superstructure
	class?
* What is the difference between WantedState and SystemState?

### Paths
* what is the output format of the cheesypath webapp?  	
* How are these paths brought into the robot code?

### Roborio configuration
* what does the adb installer script actually do?  Is this wise?
* where are crashlogs found?  How did they get there?

### References

https://github.com/Spartronics4915/FRC-2017-Public

DesignNotes.md
