package org.usfirst.frc.team4525.robot;

import edu.wpi.first.wpilibj.Solenoid;

// Piston Method

public class Piston {
    private final Solenoid extend, retract;
    private boolean isExtended;
    private int count;

    // Constructor
    public Piston(int extendPos, int retractPos, boolean isExtended) {
        extend = new Solenoid(extendPos);
        retract = new Solenoid(retractPos);

        this.isExtended = isExtended;

        count = 0;
    }

    // Setters/Getters
    public void setExtended(boolean isExtended) {
        this.isExtended = isExtended;
    }

    public void extend() {
        if (!isExtended()) {
            retract.set(false);
            extend.set(true);
            isExtended = true;

            this.count = 0;
        }
    }

    public void retract() {
        if (!isRetracted()) {
            extend.set(false);
            retract.set(true);
            this.isExtended = false;

            this.count = 0;
        }
    }

    public void off() {
        extend.set(false);
        retract.set(false);
    }

    public boolean isExtended() {
        return isExtended;
    }

    public boolean isRetracted() {
        return !isExtended;
    }

    public void countTime() {
        if (count >= 20) {
            off();
        } else {
            count++;
        }
    }
}
