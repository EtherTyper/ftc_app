package org.firstinspires.ftc.omegas;

import android.content.Context;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;

/**
 * This is NOT an opmode.
 * <p>
 * This class can be used to define all the specific hardware for a single robot.
 * It will be updated to accurately reflect the peripherals and components
 * available on our finished FTC Velocity Vortex robot.
 * <p>
 * This hardware class assumes the following device names have been configured on the robot:
 * Note:  All names are lower case and some have single spaces between words.
 * <p>
 * Motor channel:  Left  drive motor:                "left_drive"
 * Motor channel:  Right drive motor:                "right_drive"
 * Servo channel:  Servo to push left beaconator:    "left_beaconator"
 * Servo channel:  Servo to open right beaconator:   "right_beaconator"
 * Color sensor:  Color sensor for left beaconator:  "left_color_sensor"
 * Color sensor:  Color sensor for right beaconator: "right_color_sensor"
 * <p>
 * (The "beaconators" are our colloquial term for beacon pushers.)
 */
public abstract class HardwareOmegas {
    /* Public OpMode members. */
    private LightSensor lineSensor;
    private DcMotor leftFrontMotor;
    private DcMotor leftBackMotor;
    private DcMotor rightFrontMotor;
    private DcMotor rightBackMotor;
    private Servo leftBeaconator;
    private Servo rightBeaconator;

    private ArrayList<DcMotor> motors;
    private Context appContext;

    private static final double MS_PER_RADIAN = 159.15;
    private static boolean isExtending = false;

    /* local OpMode members. */
    private ElapsedTime period = new ElapsedTime();

    protected HardwareOmegas() {
        init();
    }

    public abstract void init();

    /* Initialize Drive Motor interfaces */
    protected void initDriveMotors(HardwareMap hwMap) {
        // Define and Initialize Motors
        leftFrontMotor = hwMap.dcMotor.get("left_front");
        leftBackMotor = hwMap.dcMotor.get("left_back");
        rightFrontMotor = hwMap.dcMotor.get("right_front");
        rightBackMotor = hwMap.dcMotor.get("right_back");

        motors = new ArrayList<DcMotor>() {
            {
                add(getLeftFrontMotor());
                add(getLeftBackMotor());
                add(getRightFrontMotor());
                add(getRightBackMotor());
            }
        };

        // Set all motors to zero power, and to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        for (DcMotor motor : getMotors()) {
            motor.setPower(0.0);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        getLeftFrontMotor().setDirection(DcMotor.Direction.FORWARD);  // Set to REVERSE if using AndyMark motors
        getLeftBackMotor().setDirection(DcMotor.Direction.FORWARD);   // Set to REVERSE if using AndyMark motors
        getRightFrontMotor().setDirection(DcMotor.Direction.REVERSE); // Set to FORWARD if using AndyMark motors
        getRightBackMotor().setDirection(DcMotor.Direction.REVERSE);  // Set to FORWARD if using AndyMark motors

    }

    /* Initialize Beaconator interfaces */
    protected void initBeaconators(HardwareMap hwMap) {
        // Connect to servo (Assume PushBot Left Hand)
        // Change the text in quotes to match any servo name on your robot.
        leftBeaconator = hwMap.servo.get("left_beaconator");
        rightBeaconator = hwMap.servo.get("right_beaconator");
    }

    /* Initialize LightSensor interfaces */
    protected void initLightSensor(HardwareMap hwMap) {
        lineSensor = hwMap.lightSensor.get("light_sensor");
        getLineSensor().enableLed(true);
    }

    protected void initAppContext(HardwareMap hwMap) {
        appContext = hwMap.appContext;
    }

    /**
     * waitForTick implements a periodic delay. However, this acts like a metronome with a regular
     * periodic tick.  This is used to compensate for varying processing times for each cycle.
     * The function looks at the elapsed cycle time, and sleeps for the remaining time interval.
     *
     * @param periodMs Length of wait cycle in mSec.
     */
    public void waitForTick(long periodMs) {
        long remaining = periodMs - (long) period.milliseconds();

        // sleep for the remaining portion of the regular cycle period.
        if (remaining > 0) {
            try {
                Thread.sleep(remaining);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Reset the cycle clock for the next pass.
        period.reset();
    }

    /**
     * rotate implements a robot rotation using on-robot motors.
     *
     * @param radians Angle moved in radians.
     */
    public void rotate(double radians) {
        ElapsedTime timePushed = new ElapsedTime();

        while (timePushed.milliseconds() < radians * MS_PER_RADIAN) {
            getLeftBackMotor().setPower(1.0);
            getLeftFrontMotor().setPower(1.0);
            getRightBackMotor().setPower(-1.0);
            getRightFrontMotor().setPower(-1.0);
        }
    }

    public void rightBeaconatorSequence(Servo beaconator, long milliseconds) {
        ElapsedTime timePushed = new ElapsedTime();
        if (isExtending) return;
        else isExtending = true;

        while (true) {
            if (timePushed.milliseconds() < milliseconds) {
                positionServo(beaconator, 0.0);
            } else if (timePushed.milliseconds() < milliseconds * 2) {
                positionServo(beaconator, 1.0);
            } else {
                positionServo(beaconator, 1.0);
                isExtending = false;
                return;
            }
        }
    }

    public void leftBeaconatorSequence(Servo beaconator, long milliseconds) {
        ElapsedTime timePushed = new ElapsedTime();
        if (isExtending) return;
        else isExtending = true;

        while (true) {
            if (timePushed.milliseconds() < milliseconds) {
                positionServo(beaconator, 1.0);
            } else if (timePushed.milliseconds() < milliseconds * 2) {
                positionServo(beaconator, 0.0);
            } else {
                positionServo(beaconator, 0.0);
                isExtending = false;
                return;
            }
        }
    }

    public void positionServo(Servo beaconator, double pos) {
        beaconator.setPosition(Math.abs(pos));
    }

    public LightSensor getLineSensor() {
        return lineSensor;
    }

    public DcMotor getLeftFrontMotor() {
        return leftFrontMotor;
    }

    public DcMotor getLeftBackMotor() {
        return leftBackMotor;
    }

    public DcMotor getRightFrontMotor() {
        return rightFrontMotor;
    }

    public DcMotor getRightBackMotor() {
        return rightBackMotor;
    }

    public Servo getLeftBeaconator() {
        return leftBeaconator;
    }

    public Servo getRightBeaconator() {
        return rightBeaconator;
    }

    public ArrayList<DcMotor> getMotors() {
        return motors;
    }

    public Context getAppContext() {
        return appContext;
    }
}

