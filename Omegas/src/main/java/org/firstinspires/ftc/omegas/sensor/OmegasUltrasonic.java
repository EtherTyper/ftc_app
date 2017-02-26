package org.firstinspires.ftc.omegas.sensor;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.omegas.HardwareOmegas;

/**
 * Created by ethertyper on 2/6/17.
 */
@Autonomous(name = "Omegas: Ultrasonic Test", group = "Tests")
public class OmegasUltrasonic extends LinearOpMode {

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    HardwareOmegas Ω = null;

    @Override
    public void runOpMode() throws InterruptedException {
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        Ω = new HardwareOmegas() {
            @Override
            public void init() {
                initAppContext(hardwareMap);
                initUltrasonicSensor(hardwareMap);
                initDriveMotors(hardwareMap);
                initLightSensor(hardwareMap);
                initTelemetry(telemetry);
                initAudio();

                getLightSensor().enableLed(true);
                sayMessage();
            }
        };

        double light = 0.0;
        boolean wallDetected = false;
        double ultrasonicLevel = 0.0;

        // Seek the white line.
        while (opModeIsActive() && light < 0.3 ) {
            light = Ω.getLightSensor().getLightDetected();
            telemetry.addData("Data", "Light amount: " + light);
            telemetry.update();
            for (DcMotor motor: Ω.getMotors()) {
                motor.setPower(0.25);
            }
        }
        Ω.getLightSensor().enableLed(false);

        // Rest momentarily.
        Thread.sleep(200);
        Ω.stopDriving();

        // Rotate robot.
        telemetry.addData("Data", "Turning Robot...");
        telemetry.update();
        Ω.rotate(Math.PI * 4 / 9, true);

        // Seek the wall.
        while (opModeIsActive() && !wallDetected) {
            double newUltrasonicLevel = Ω.getUltrasonicSensor().getUltrasonicLevel();
            ultrasonicLevel = newUltrasonicLevel != 0 && newUltrasonicLevel != 255 ? newUltrasonicLevel : ultrasonicLevel;

            if (ultrasonicLevel < 15.0) {
                telemetry.addData("Data", "WALL DETECTED, Ultrasonic levels: " + ultrasonicLevel);
                telemetry.update();
                wallDetected = true;
            } else {
                telemetry.addData("Data", "NOT DETECTED, Ultrasonic levels: " + ultrasonicLevel);
                telemetry.update();
                for (DcMotor motor: Ω.getMotors()) {
                    motor.setPower(0.25);
                }
            }
        }

        // Done!
        Ω.stopDriving();
    }
}
