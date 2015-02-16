package org.usfirst.frc.team4525.robot;

import edu.wpi.first.wpilibj.Victor;

public class DriveTrain {

	private Victor left1, left2;
	private Victor right1, right2;
	//
	private boolean direction;
	private boolean canSprint;
	private boolean reverseSkim;
	private double upTime;
	private double moveSensitivity = 1;
	private double restriction, minRestriction;
	private double turnSens;
	private double motorPower;

	// Constructor
	public DriveTrain(Victor left1, Victor left2, Victor right1, Victor right2) {
		direction = true;
		upTime = 1;
		minRestriction = 0.75;
		this.left1 = left1;
		this.left2 = left2;
		this.right1 = right1;
		this.right2 = right2;
		reverseSkim = true;
	}

	public void setSkimReverse(boolean setting) {
		reverseSkim = setting;
	}

	// Controls
	public void setControlSensitivity(double Sensitivity) {
		moveSensitivity = Sensitivity;
	}

	public void setTurnSensitivity(double turnSens) {
		this.turnSens = turnSens; // For ArcadeDrive only.
	}

	public void speedSensitivity(double powerSens) {
		minRestriction = powerSens;
	}

	// Sprint
	public boolean sprintAllowed() {
		return canSprint;
	}

	public void setSprint(boolean bool) {
		if (bool && canSprint) {
			restriction = 0.99;
		} else {
			restriction = minRestriction;
		}
	}

	// Motor Setter
	private void setLeft(double power) {
		left1.set(power);
		left2.set(power);
	}

	private void setRight(double power) {
		right1.set(power);
		right2.set(power);
	}

	// Speed Controllers
	public double getRawSpeed() {
		return motorPower;
	}

	// Power Skimmer
	private double skimPower(double power) {
		if (power < 0 && reverseSkim)
			power = power * 0.5;
		if ((power < 0.1 && power > 0) || (power > -0.1 && power < 0))
			return 0;
		if (moveSensitivity == 1) {
			return power;
		}

		boolean currentDir = false;
		if (power > 0) {
			currentDir = true;
		}
		if ((power < 0.025 && power > 0) || (power < 0 && power > -0.025)
				|| currentDir != direction) {
			upTime = moveSensitivity;
			direction = currentDir;
			canSprint = false;
		} else if (upTime > 1) {
			upTime -= 0.1;
		} else {
			canSprint = true;
		}
		return power / upTime;
	}

	// Stopping
	public void stop() {
		setLeft(0);
		setRight(0);
	}

	// Drive stuff
	public void arcadeDrive(double power, double offset) {
		motorPower = skimPower(power);
		offset = offset * turnSens;
		//
		double leftSpeed, rightSpeed;
		//
		if (power > 0) {
			if (offset > 0) {
				leftSpeed = power - offset;
				rightSpeed = Math.max(power, offset);
			} else {
				leftSpeed = Math.max(power, -offset);
				rightSpeed = power + offset; // could overflow 1
			}
		} else {
			if (offset > 0) {
				leftSpeed = -Math.max(-power, offset);
				rightSpeed = power + offset;
			} else {
				leftSpeed = power - offset;
				rightSpeed = -Math.max(-power, -offset);
			}
		}
		setLeft(-leftSpeed * restriction);
		setRight(-rightSpeed * restriction);
	}

	public void manualDrive(double left, double right) {
		setLeft(left);
		setRight(right);
	}

}
