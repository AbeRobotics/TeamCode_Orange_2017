package org.firstinspires.ftc.teamcode;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.detectors.*;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.io.IOException;
import java.util.LinkedList;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.OPModeConstants;


/**
 * Created by Akanksha.Joshi on 23-Dec-2017.
 */
//Forward, turn right, move forward, turn left, deposit glyph
@Autonomous(name="Blue Team Left", group="Autonomous")
public class Abe_BlueTeam_Left_Autonomous extends LinearOpMode{

    private OPModeConstants opModeConstants = null;
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException {
        opModeConstants = OPModeConstants.getInstance();
        telemetry.setAutoClear(false);
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        opModeConstants.setSelectedTeam(OPModeConstants.SelectedTeam.BLUE_TEAM);
        opModeConstants.setOrientation(OPModeConstants.Orientation.FRONT_FACING);
        OPModeConstants.DEBUG = false;

        // Get the camera going

        Task_JewelOrder jewelOrder = new Task_JewelOrder(hardwareMap);
        jewelOrder.Init();

        waitForStart();
        resetStartTime();

        Task_GlyphClaw pickGlyph = new Task_GlyphClaw(hardwareMap, OPModeConstants.GlyphClawPosition.CLOSE);
        pickGlyph.Init();
        while(pickGlyph.GetTaskStatus() == false){
            pickGlyph.PerformTask(telemetry, getRuntime());
            sleep(100);
        }
        pickGlyph.Reset();

        Task_LiftGlyph liftGlyph = new Task_LiftGlyph(hardwareMap);
        liftGlyph.Init();
        while(liftGlyph.GetTaskStatus() ==false)
        {
            liftGlyph.PerformTask(telemetry,getRuntime());
            sleep(200);
        }
        liftGlyph.Reset();

        while(jewelOrder.GetTaskStatus()==false) {
            jewelOrder.PerformTask(telemetry, getRuntime());
            sleep(100);
        }
        telemetry.addData("Jewel Order ",opModeConstants.getDetectedOrder().toString());
        telemetry.update();
        Task_GetCryptoKey getCryptoKey = new Task_GetCryptoKey(hardwareMap,telemetry);
        getCryptoKey.Init();
        while(getCryptoKey.GetTaskStatus() == false)
        {
            getCryptoKey.PerformTask(telemetry, getRuntime());
            sleep(100);
        }
        telemetry.addData("Crypto Location ",opModeConstants.getCryptoLocation());
        telemetry.update();

        Task_JewelArm jewelArm = new Task_JewelArm(hardwareMap, OPModeConstants.jewelKickerArmPosition.ACTION);
        jewelArm.Init();
        while(jewelArm.GetTaskStatus()==false)
        {
            jewelArm.PerformTask(telemetry, getRuntime());
            sleep(100);
        }
        telemetry.addData("Fire Sequence ",opModeConstants.getFireSequence().toString());
        telemetry.update();

        Task_JewelRemove jewelRemove = new Task_JewelRemove(hardwareMap);
        jewelRemove.Init();
        while(jewelRemove.GetTaskStatus() == false) {
            jewelRemove.PerformTask(telemetry, getRuntime());
            sleep(100);
        }

        jewelArm = new Task_JewelArm(hardwareMap, OPModeConstants.jewelKickerArmPosition.REST);
        jewelArm.Init();
        while(jewelArm.GetTaskStatus()==false)
        {
            jewelArm.PerformTask(telemetry, getRuntime());
            sleep(100);
        }

        /*start of manually calling gyro method (This would be our glyph maneuver)*/
        OPModeDriveHelper driveHelper = OPModeDriveHelper.getInstance();
        driveHelper.Init(telemetry,hardwareMap);
        //Comment for competition
        opModeConstants.setAutoSpeed(OPModeConstants.AutonomousSpeed.SLOW);
        driveHelper.MoveForward(40.0d);
        driveHelper.gyroTurn(0.5,-90);
        switch (opModeConstants.getCryptoLocation()){
            case LEFT:
                driveHelper.MoveForward(0.0d);
                break;
            case CENTER:
                driveHelper.MoveForward(7.63d);
                break;
            case RIGHT:
                driveHelper.MoveForward(15.26d);
                break;
            default:
                driveHelper.MoveForward(7.63d);
                break;
        }
        driveHelper.gyroTurn(0.5,0);
        //ends here///////////////////////////////////////

        Task_GlyphClaw glyphClaw = new Task_GlyphClaw(hardwareMap, OPModeConstants.GlyphClawPosition.OPEN);
        glyphClaw.Init();
        while(glyphClaw.GetTaskStatus() == false){
            glyphClaw.PerformTask(telemetry, getRuntime());
            sleep(100);
        }

        Task_LowerGlyph lowerGlyph = new Task_LowerGlyph(hardwareMap);
        lowerGlyph.Init();
        while(lowerGlyph.GetTaskStatus() == false){
            lowerGlyph.PerformTask(telemetry, getRuntime());
            sleep(100);
        }

        driveHelper.MoveForward(6.0d);
        driveHelper.MoveBackward(3.0d);

        telemetry.addData("Tasks Completed In ", getRuntime());
        telemetry.update();
        sleep((30 - (int)getRuntime())*1000);

        Task_ResetAll resetAll = new Task_ResetAll(hardwareMap);
        resetAll.Init();
        resetAll.PerformTask(telemetry,0);

        //TODO -- Make sure to set motor power to 0 and encoder values to "DO NOT USE ENCODERS"
    }
}


