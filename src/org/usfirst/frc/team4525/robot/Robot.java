/* This is the revised code that has been polished, heavily tested and set for the competition.
 * THIS IS NOT FOR MODIFICATION UNLESS YOU HAVE MADE A COPY PRIOR TO THE COMPETITION.
 * MODIFYING THIS AT THE COMPETITION THE NIGHT BEFORE WILL RESULT IN A SEVERE ASS KICKING. DON'T DO IT.
 * Let's not repeat last year. 
 */

package org.usfirst.frc.team4525.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SampleRobot {
	/*
	 * IMPORTANT NOTES: We do not have D-PAD (POV) functionality presently with
	 * the XboxController class.
	 */

	// Control Systems | Mechanisms
	private XboxController xboxDrive, xboxMech;
	Victor one, two, three, four;
	private DriveTrain driveSys;

	public Talon winch, boom;

	// Sensors
	private Encoder leftEncoder, rightEncoder;
	private final double encoderPulses = 0.051;
	private DigitalInput boomBackSwitch;
	private DigitalInput boomFrontSwitch;
	private DigitalInput robotFrontTouch;
	private Gyro gyro;
	CameraServer cam;
	// private final double gyroSensitivity = 0.071;

	// Pneumatic Systems
	private Compressor compressor;
	private Piston claw, tilt;

	// Control Selection | Autonomous Selection
	private SendableChooser driveModeChooser;

	enum driveMode {
		ONE_DRIVER, TWO_DRIVER, TEST
	}

	private SendableChooser autoModeChooser;

	enum autoMode {
		FULL_OUT_FAR, FULL_OUT_CLOSE, GET_TOTE, NOTHING, KICK_THE_ROBOT, INIT_ERROR_FIX
	}

	enum Winch {
		UP(1.0), DOWN(-1.0), OFF(0.0);

		private final double value;

		Winch(double value) {
			this.value = value;
		}

		double get() {
			return this.value;
		}
	}

	enum Boom {
		FORWARD(-1.0), BACKWARD(1.0), OFF(0.0);

		private final double value;

		Boom(double value) {
			this.value = value;
		}

		double get() {
			return this.value;
		}
	}

	/*
	 * ROBOT CODE:
	 */

	// Initiation
	public Robot() {
		// Control Systems
		xboxDrive = new XboxController(0);
		xboxMech = new XboxController(1);
		one = new Victor(0);
		two = new Victor(1);
		three = new Victor(2);
		four = new Victor(3);
		driveSys = new DriveTrain(one, two, three, four);
		driveSys.setTurnSensitivity(0.75);
		driveSys.speedSensitivity(1.0);
		// = 'Mechanismsyui
		winch = new Talon(4);
		boom = new Talon(5);

		// Sensors
		leftEncoder = new Encoder(0, 1, false, EncodingType.k4X);
		rightEncoder = new Encoder(2, 3, true, EncodingType.k4X);
		boomBackSwitch = new DigitalInput(9); // End of boom
		boomFrontSwitch = new DigitalInput(8); // Front of boom
		robotFrontTouch = new DigitalInput(5); // Switch on front
		leftEncoder.setDistancePerPulse(encoderPulses);
		rightEncoder.setDistancePerPulse(encoderPulses);
		// Gyro Setup
		gyro = new Gyro(0);
		// Camera Setup
		// cam = CameraServer.getInstance();
		// cam.setQuality(30);
		// cam.startAutomaticCapture("cam0");

		// Pneumatics
		compressor = new Compressor(0);
		tilt = new Piston(0, 1, false); // Moves up down
		claw = new Piston(2, 3, true); // Grips/Ungrips

		// SmartDashboard | Data | Input
		driveModeChooser = new SendableChooser();
		driveModeChooser.addDefault("Dual Operators", driveMode.TWO_DRIVER);
		driveModeChooser.addObject("One Operator", driveMode.ONE_DRIVER);
		driveModeChooser.addObject("Testing", driveMode.TEST);
		SmartDashboard.putData("Driver Mode", driveModeChooser);

		autoModeChooser = new SendableChooser();
		autoModeChooser.addDefault("Bin Grab - Far Bump", autoMode.FULL_OUT_FAR);
		autoModeChooser.addDefault("Bin Grab - Close Bump", autoMode.FULL_OUT_CLOSE);
		autoModeChooser.addDefault("Tote Grabber", autoMode.GET_TOTE);
		autoModeChooser.addObject("Do Nothing", autoMode.NOTHING);
		autoModeChooser.addObject("Kick The Robot Game", autoMode.KICK_THE_ROBOT);
		autoModeChooser.addObject("Error fix", autoMode.INIT_ERROR_FIX);
		SmartDashboard.putData("Automiton Mode", autoModeChooser);

	}

	public void autonomous() {
		compressor.start();
		driveSys.setControlSensitivity(1);
		driveSys.speedSensitivity(1);
		driveSys.setSkimReverse(false);

		//

		//
		autoMode mode = (autoMode) autoModeChooser.getSelected();
		if (mode == autoMode.INIT_ERROR_FIX) {
			while (isEnabled()) {
				double val = 0.5;
				one.set(val);
				two.set(val);
				three.set(-val);
				four.set(-val);
			}
		} else if (mode == autoMode.GET_TOTE) {
			tilt.retract();
			claw.extend();
		} else if (mode == autoMode.NOTHING) {
			// Nothing Mode
			claw.extend();
			winch.set(Winch.UP.get());
		} else if (mode == autoMode.KICK_THE_ROBOT) { // Gyro Test || Kick The
			// Robot
			while (isEnabled()) {
				driveDistance(75, 0.3, false);
				driveDistance(-75, -0.3, false);
			}
		} else if (mode == autoMode.FULL_OUT_CLOSE) { // Close Bump
			claw.retract();
			tilt.extend();
			gyro.reset();

			driveDistance(22, 0.35, true);
			// Boom Forward
			ArmMovements boomForward = new ArmMovements(boom, Boom.FORWARD.get(), boomBackSwitch,
					0.5);
			Thread boomForwardThread = new Thread(boomForward);
			boomForwardThread.start();
			// Drop Claw
			ArmMovements dropClaw = new ArmMovements(winch, Winch.DOWN.get(), 3);
			Thread dropClawThread = new Thread(dropClaw);
			dropClawThread.start();
			// Go forwards
			driveDistanceOrLimit(86, 0.5, robotFrontTouch, false);
			while (!boomForward.isReady())
				;
			// Grab bin
			claw.extend();
			Timer.delay(0.5);
			//
			winch.set(Winch.UP.get());
			Timer.delay(3);
			;
			// Back Arm off
			winch.set(Winch.OFF.get());

		} else { // Full out Far
			claw.retract();
			tilt.extend();
			gyro.reset();

			// Move Arm Forwards
			ArmMovements boomForward = new ArmMovements(boom, Boom.FORWARD.get(), boomBackSwitch,
					0.5);
			Thread boomForwardThread = new Thread(boomForward);
			boomForwardThread.start();
			// Drive To
			driveDistance(50, 0.5, false);
			// Drop Arms
			ArmMovements dropClaw = new ArmMovements(winch, Winch.DOWN.get(), 3);
			Thread dropClawThread = new Thread(dropClaw);
			dropClawThread.start();
			// Go to bin
			driveDistanceOrLimit(53, 0.35, robotFrontTouch, true);
			// Extend Boom
			while (!boomForward.isReady())
				;
			// Grab Bin
			claw.extend();
			Timer.delay(0.5);
			// Raise Winch
			winch.set(Winch.UP.get());
			Timer.delay(3);
			// Back Arm off
			winch.set(Winch.OFF.get());
			// Back Arm off
			ArmMovements boomBackward = new ArmMovements(boom, Boom.BACKWARD.get(), boomFrontSwitch);
			Thread boomBackwardThread = new Thread(boomBackward);
			boomBackwardThread.start();
			// Hold up
			Timer.delay(0.25);
			// Back Off
			driveDistance(-15, -0.35, true);
			driveDistance(-20, -0.5, false);
			// Drop Winch
			ArmMovements winchDrop = new ArmMovements(winch, Winch.DOWN.get(), 3.5);
			Thread winchDropThread = new Thread(winchDrop);
			winchDropThread.start();
			// Back up more
			driveDistance(-11, 0.4, false);
			driveDistance(-4, 0.3, false);
			// Spin to 45 degrees
			Timer.delay(1.5);
			spinRobot(-30, 0.4);
			Timer.delay(0.5);
			driveDistance(-2, 0.35, true);
			// claw.retract();
		}
	}

	public void operatorControl() {
		compressor.start();
		driveSys.setControlSensitivity(6);
		driveSys.setSkimReverse(true);
		driveSys.speedSensitivity(0.75);
		//
		driveMode mode = (driveMode) driveModeChooser.getSelected();
		double power, offset;
		if (mode == driveMode.ONE_DRIVER) { // One Driver
			while (isOperatorControl() && isEnabled()) {
				// Mechanism Movement
				// Up/Down
				if (xboxDrive.getAButton()) {
					winch.set(Winch.DOWN.get()); // Winch down
				} else if (xboxDrive.getYButton()) {
					winch.set(Winch.UP.get()); // Winch up
				} else {
					winch.set(Winch.OFF.get());
				}
				// In/Out
				if (xboxDrive.getRawButton(6) && boomBackSwitch.get()) {
					boom.set(Boom.FORWARD.get()); // Boom Out
				} else if (xboxDrive.getRawButton(5) && boomFrontSwitch.get()) {
					boom.set(Boom.BACKWARD.get()); // Boom In
				} else {
					boom.set(Boom.OFF.get());
				}

				// Pneumatics
				// Gripper
				if (xboxDrive.getXButton()) {
					tilt.extend();
				} else if (xboxDrive.getBButton()) {
					tilt.retract();
				}
				// Claw
				if (xboxDrive.getTrigger(XboxController.AxisType.kTriggerR)) {
					claw.extend();
				} else if (xboxDrive.getTrigger(XboxController.AxisType.kTriggerL)) {
					claw.retract();
				}
				// Solenoid Use
				tilt.countTime();
				claw.countTime();

				// Drive Controls
				driveSys.setSprint(xboxDrive.getRawButton(9));

				power = -xboxDrive.getAxis(XboxController.AxisType.kLeftY);
				offset = -xboxDrive.getAxis(XboxController.AxisType.kRightX);
				driveSys.arcadeDrive(power, offset);
			}
		} else { // Dual Drivers
			double mechUD, mechLR;
			while (isOperatorControl() && isEnabled()) {
				// Mechanism Movement
				// Up/Down Movement
				mechUD = -xboxMech.getAxis(XboxController.AxisType.kLeftY);
				// Left/Right Movement
				mechLR = -xboxMech.getAxis(XboxController.AxisType.kRightX);
				// Modify Values

				if (mechUD > 0.6 && mechUD > 0) {
					mechUD = Winch.UP.get();
				} else if (mechUD < -0.6) {
					mechUD = Winch.DOWN.get();
				} else {
					mechUD = Winch.OFF.get();
				}
				if ((mechLR < 0.4 && mechLR > 0) || (mechLR > -0.4 && mechLR < 0))
					mechLR = 0;
				if (!boomBackSwitch.get() && mechLR < 0)
					mechLR = 0;
				if (!boomFrontSwitch.get() && mechLR > 0)
					mechLR = 0;
				// Set the mechanisms
				winch.set(mechUD);
				boom.set(mechLR);

				// Pneumatics
				// Gripper
				if (xboxMech.getXButton()) {
					tilt.extend();
				} else if (xboxMech.getBButton()) {
					tilt.retract();
				}
				// Claw
				if (xboxMech.getTrigger(XboxController.AxisType.kTriggerR)) {
					claw.extend();
				} else if (xboxMech.getTrigger(XboxController.AxisType.kTriggerL)) {
					claw.retract();
				}
				// Solenoid Use
				tilt.countTime();
				claw.countTime();

				// Drive Controls
				driveSys.setSprint(xboxDrive.getRawButton(9));

				power = -xboxDrive.getAxis(XboxController.AxisType.kLeftY);
				offset = -xboxDrive.getAxis(XboxController.AxisType.kRightX);
				driveSys.arcadeDrive(power, offset);
			}
		}

	}

	/**
	 * Runs during test mode
	 */
	public void test() {
	}

	// Our Own Methods
	private double encoderAverage(double e1, double e2) {
		return (e1 + e2) / 2;
	}

	private void driveStraitEncoders(double speed, double le, double re) {
		double offset = (le - re) * 0.05;
		if (offset > 0.04)
			offset = 0.04;
		if (offset < -0.04)
			offset = -0.04;
		driveSys.arcadeDrive(speed, offset);
	}

	private void driveStraitGyro(double speed) {
		double offset = gyro.getAngle() * 0.15;
		if (offset > 0.15 && offset > 0)
			offset = 0.15;
		if (offset < 0 && offset < -0.15)
			offset = -0.15;
		driveSys.arcadeDrive(speed, offset);
	}

	private void driveDistanceOrLimit(double distance, double speed, DigitalInput limitswitch,
			boolean useEncoders) {
		leftEncoder.reset();
		rightEncoder.reset();
		double le, re, count;
		count = 0;
		//
		if ((distance > 0 && speed < 0) || (distance < 0 && speed > 0))
			speed = speed * -1;
		if (distance > 0) {
			while (limitswitch.get() && isEnabled() && count < distance) {
				le = leftEncoder.getDistance();
				re = rightEncoder.getDistance();
				count = encoderAverage(le, re);
				SmartDashboard.putNumber("Encoders_DOL", count);
				if (!useEncoders) {
					driveStraitGyro(speed);
				} else {
					driveStraitEncoders(speed, le, re);
				}
			}
		} else {
			while (limitswitch.get() && isEnabled() && count > distance) {
				le = leftEncoder.getDistance();
				re = rightEncoder.getDistance();
				count = encoderAverage(le, re);
				SmartDashboard.putNumber("Encoders_DOL", count);
				if (!useEncoders) {
					driveStraitGyro(speed);
				} else {
					driveStraitEncoders(speed, le, re);
				}
			}
		}
		driveSys.stop();
	}

	private void driveDistance(double distance, double speed, boolean useEncoders) {
		double count = 0;
		double le, re;
		leftEncoder.reset();
		rightEncoder.reset();
		count = 0;
		if (distance > 0) {
			if (speed < 0)
				speed = speed * -1;
			while (isEnabled() && count < distance) {
				le = leftEncoder.getDistance();
				re = rightEncoder.getDistance();
				count = encoderAverage(le, re);
				if (useEncoders) {
					driveStraitEncoders(speed, le, re);
				} else {
					driveStraitGyro(speed);
				}
			}
			driveSys.stop();
		} else if (distance < 0) {
			if (speed > 0)
				speed = speed * -1;
			while (isEnabled() && count > distance) {
				le = leftEncoder.getDistance();
				re = rightEncoder.getDistance();
				count = encoderAverage(le, re);
				if (useEncoders) {
					driveStraitEncoders(speed, le, re);
				} else {
					driveStraitGyro(speed);
				}
			}
			driveSys.stop();
		}
	}

	public void spinRobot(double degree, double speed) {
		double count = 0;
		double l = 0;
		double r = 0;
		gyro.reset();
		if (degree > 0 && speed < 0)
			speed = speed * -1;
		if (degree < 0) {
			r = -speed;
			l = speed;
		} else if (degree > 0) {
			l = -speed;
			r = speed;
		}
		if (speed > 0.3)
			degree = degree - 20;
		while (isEnabled() && (degree > 0 && count < degree) || (degree < 0 && count > degree)) {
			count = gyro.getAngle();
			driveSys.manualDrive(l, r);
		}
		driveSys.stop();
	}

}
