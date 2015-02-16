package org.usfirst.frc.team4525.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;

public class ArmMovements implements Runnable {

	private Talon mech;
	private double time, speed, delay;
	private boolean timeBased;
	private boolean ready;
	private DigitalInput limitSwitch;

	// Contructor Sensor Based
	public ArmMovements(Talon mech, double speed, DigitalInput limitSwitch) {
		delay = 0;
		this.mech = mech;
		this.limitSwitch = limitSwitch;
		this.speed = speed;
		timeBased = false; 
		ready = false;
	}
	
	// With Start Delay
	public ArmMovements(Talon mech, double speed, DigitalInput limitSwitch, double delay) {
		this.delay = delay;
		this.mech = mech;
		this.limitSwitch = limitSwitch;
		this.speed = speed;
		timeBased = false; 
		ready = false;
	}

	// Constructor Time Based
	public ArmMovements(Talon mech, double speed, double time) {
		delay = 0;
		this.mech = mech;
		this.speed = speed;
		this.time = time;
		timeBased = true; // needs to be true here
		ready = false;
	}

	public boolean isReady() {
		return ready;
	}

	public void run() {
		Timer.delay(delay);
		if (timeBased) {
			mech.set(speed);
			Timer.delay(time);
		} else {
			mech.set(speed);
			while(limitSwitch.get());
		}
		mech.set(0);
		ready = true;
	}
}
