package org.usfirst.frc.team4525.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;

public class XboxController extends GenericHID {

	private DriverStation m_ds;
	private final int m_port;

	/**
	 * Constructor
	 * 
	 * @param port
	 *            USB Port on DriverStation
	 */
	public XboxController(int port) {
		super();
		m_port = port;
		m_ds = DriverStation.getInstance();
	}

	/**
	 * Get Value from an Axis
	 * 
	 * @param axis
	 *            Axis Number
	 * @return Value from Axis (-1 to 1)
	 */
	public double getRawAxis(int axis) {
		return m_ds.getStickAxis(m_port, axis);
	}

	/**
	 * Get Value from an Axis
	 * 
	 * @param axis
	 *            AxisType
	 * @return
	 */
	public double getAxis(AxisType axis) {
		return getRawAxis(axis.value);
	}

	/**
	 * Retrieve value for X axis
	 * 
	 * @param hand
	 *            Hand associated with the Joystick
	 * @return Value of Axis (-1 to 1)
	 */
	public double getX(Hand hand) {
		if (hand.value == Hand.kRight.value) {
			return getAxis(AxisType.kRightX);
		} else if (hand.value == Hand.kLeft.value) {
			return getAxis(AxisType.kLeftX);
		} else {
			return 0;
		}
	}

	/**
	 * Retrieve value for Y axis
	 * 
	 * @param hand
	 *            Hand associated with the Joystick
	 * @return Value of Axis (-1 to 1)
	 */
	public double getY(Hand hand) {
		if (hand.value == Hand.kRight.value) {
			return getAxis(AxisType.kRightY);
		} else if (hand.value == Hand.kLeft.value) {
			return getAxis(AxisType.kLeftY);
		} else {
			return 0;
		}
	}

	/**
	 * Gets Value from D-Pad Left and Right Axis
	 * 
	 * @return Axis Value (-1 to 1)
	 */
	public double getTwist() {
		return getAxis(AxisType.kDLeftRight);
	}

	/**
	 * Gets value from a button
	 * 
	 * @param button
	 *            number of the button
	 * @return State of the button
	 */
	public boolean getRawButton(int button) {
		return ((0x1 << (button - 1)) & m_ds.getStickButtons(m_port)) != 0;
	}

	/**
	 * Get Value from a button
	 * 
	 * @param button
	 *            Button Type
	 * @return
	 */
	public boolean getButton(ButtonType button) {
		return getRawButton(button.value);
	}

	/**
	 * Get Trigger Value as Button
	 * 
	 * @param hand
	 *            Hand associated with button
	 * @return false
	 */
	public boolean getTrigger(AxisType trigger) {
		return getAxis(trigger) >= 0.8;
	}

	/**
	 * Get Trigger value us double
	 */
	public double getRawTrigger(AxisType trigger) {
		return getAxis(trigger);
	}

	/**
	 * Get Value from Back buttons
	 * 
	 * @param hand
	 *            hand associated with the button
	 * @return state of left or right
	 */
	public boolean getBumper(Hand hand) {
		if (hand == Hand.kRight) {
			return getButton(ButtonType.kR);
		} else if (hand == Hand.kLeft) {
			return getButton(ButtonType.kL);
		} else {
			return false;
		}
	}

	/**
	 * Get State of Select Button
	 * 
	 * @return State of button
	 */
	public boolean getStart() {
		return getButton(ButtonType.kStart);
	}

	/**
	 * Get State of Back Button
	 * 
	 * @return State of button
	 */
	public boolean getBack() {
		return getButton(ButtonType.kBack);
	}

	/**
	 * Get State of A Button
	 * 
	 * @return State of button
	 */
	public boolean getAButton() {
		return getButton(ButtonType.kA);
	}

	/**
	 * Get State of B Button
	 * 
	 * @return State of button
	 */
	public boolean getBButton() {
		return getButton(ButtonType.kB);
	}

	/**
	 * Get State of X Button
	 * 
	 * @return State of button
	 */
	public boolean getXButton() {
		return getButton(ButtonType.kX);
	}

	/**
	 * Get State of Y Button
	 * 
	 * @return State of button
	 */
	public boolean getYButton() {
		return getButton(ButtonType.kY);
	}

	/**
	 * Stick Pushed
	 */
	public boolean getStickPushed(ButtonType stick) {
		return getButton(stick);
	}

	// Bumpers
	public boolean getLeftBumperPushed() {
		return getButton(ButtonType.kB);
	}

	public boolean getRightBumperPushed() {
		return getButton(ButtonType.kR);
	}

	// Buttons
	public static class AxisType {
		// Axis types on the Xbox controller
		// IE Left Stick, Right Stick, Etc
		public final int value;
		private static final int kLeftX_val = 0;
		private static final int kLeftY_val = 1;
		private static final int kLTrigger_val = 2;
		private static final int kRTrigger_val = 3;
		private static final int kRightX_val = 4;
		private static final int kRightY_val = 5;
		private static final int kDLeftRight_val = 6;
		private static final int kDUpDown_val = 7;

		private AxisType(int value) {
			this.value = value;
		}

		/**
		 * Axis: Left X
		 */
		public static final AxisType kLeftX = new AxisType(kLeftX_val);

		/**
		 * Axis: Left Y
		 */
		public static final AxisType kLeftY = new AxisType(kLeftY_val);

		/**
		 * Axis: Triggers
		 */
		public static final AxisType kTriggerL = new AxisType(kLTrigger_val);
		public static final AxisType kTriggerR = new AxisType(kRTrigger_val);
		/**
		 * Axis: Right X
		 */
		public static final AxisType kRightX = new AxisType(kRightX_val);

		/**
		 * Axis: Right Y
		 */
		public static final AxisType kRightY = new AxisType(kRightY_val);

		/**
		 * Axis: D-Pad Left-Right
		 */
		public static final AxisType kDLeftRight = new AxisType(kDLeftRight_val);

		/**
		 * Axis: D-Pad Up Down - DK
		 */
		public static final AxisType kDUpDown = new AxisType(kDUpDown_val);
	}

	/**
	 * Represents a digital button on a joystick.
	 */
	public static class ButtonType {

		/**
		 * The integer value representing this enumeration
		 */
		public final int value;
		private static final int kA_val = 1;
		private static final int kB_val = 2;
		private static final int kX_val = 3;
		private static final int kY_val = 4;
		private static final int kBL_val = 5;
		private static final int kBR_val = 6;
		private static final int kBack_val = 7;
		private static final int kStart_val = 8;
		private static final int kLeftPushedDown = 9;
		private static final int kRightPushedDown = 10;

		private ButtonType(int value) {
			this.value = value;
		}

		/**
		 * Button: X
		 */
		public static final ButtonType kX = new ButtonType(kX_val);

		/**
		 * Button: Y
		 */
		public static final ButtonType kY = new ButtonType(kY_val);

		/**
		 * Button: A
		 */
		public static final ButtonType kA = new ButtonType(kA_val);

		/**
		 * Button: B
		 */
		public static final ButtonType kB = new ButtonType(kB_val);

		/**
		 * Button: R1
		 */
		public static final ButtonType kR = new ButtonType(kBR_val);

		/**
		 * Button: L1
		 */
		public static final ButtonType kL = new ButtonType(kBL_val);

		/**
		 * Button: Select
		 */
		public static final ButtonType kStart = new ButtonType(kStart_val);

		/**
		 * Button: Start
		 */
		public static final ButtonType kBack = new ButtonType(kBack_val);
		// Stick push down
		public static final ButtonType kLStickPush = new ButtonType(
				kLeftPushedDown);
		public static final ButtonType kRStickPush = new ButtonType(
				kRightPushedDown);
	}

	/*
	 * Everything below here pisses me off.
	 */
	@Override
	public double getZ(Hand hand) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getThrottle() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getTrigger(Hand hand) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getTop(Hand hand) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getPOV(int pov) {
		// TODO Auto-generated method stub
		return 0;
	}
}
