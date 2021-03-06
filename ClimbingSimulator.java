package com.craigcode.climbingSimulator;


//Copyright Craig Parker 2016

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.applet.Applet;
import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class ClimbingSimulator extends JFrame {

	private static final long serialVersionUID = 1L;

	public ClimbingSimulator (String string) {
	
	}

	public static void main (String[] args) {	
		SwingUtilities.invokeLater(new Runnable () {
			public void run() {
				createAndShowWall();
			}
		});
	}
	
	private static void createAndShowWall () {
		ClimbingSimulator cS = new ClimbingSimulator("Climbing Wall Simulator");
		cS.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		ClimbingWall cw = new ClimbingWall();
		
		cS.add(cw);
		cS.pack();
		cS.setVisible(true);
		cw.requestFocusInWindow();

	}

	static class constants {
		
		static final float Pi = (float) Math.PI;
		
		//overall scale adjuster
		static final float SizeFactor = 1f;
		
		//variables for wall dimensions, hold size, and rope thickness
		static final int WallWidth = 650;	
		
		static final float HoldDiameter = 10.0f;
		static final float RopeDiameter = 2.0f;
		
		//introduction screen parameters
		static final float WidthOfBufferZoneForRandomHoldGeneration = 100.0f;
		static final float RadianLimitOfCentralDirectionforRandomHoldGeneration = Pi/4;
		static final float DistanceLimitOfWholeSegmentOfCentralDirectionForRandomHoldGeneration = 100.0f;
		static final float DistanceLimitOfPartialSegmentOfCentralDirectionForRandomHoldGeneration = 30.0f;
		static final float DistanceLimitOfSegmentUsedToPlaceHoldPerpendicularToCentralDirectionForRandomHoldGeneration = 35.0f;
		
		static final int numberOfHoldsToDisplayPerSecondOnIntroductionScreen = 3;
				
		//button parameters
		static final int XValueForStartButtonLeftBorder = 260;
		static final int YValueForStartButtonUpperBorder = 525;
		static final int WidthOfStartButton = 145;
		static final int HeightOfStartButton = 43;	
		
		static final int XValueForResetButtonLeftBorder = 30;
		static final int YValueForResetButtonUpperBorder = 20;
		static final int WidthOfResetButton = 90;
		static final int HeightOfResetButton = 27;	
		
		//timing constants
		//value is cycles per second (hertz)
		static final int DisplayTickRate = 60;
		//value is in milliseconds
		static final int MovementPromptPauseLength = 5;
		
		//movement constants
		static final float LoweringTickDistance = 3.0f;
		static final float ToppingOutDistance = 1.0f;
		static final float LimbMovementIncrement = 1.0f;
		static final float TorsoMovementIncrement = .5f;
		
		static final float DegreesOfLegReachHeightGainedAsFootMovesAwayFromUpperOuterHipBoxPoint = 20.0f;
		static final float DegreesOfLegReachWidthGainedAsFootMovesAwayFromInseam = 7.5f;
		
		static final float MaximumOverallExtension = 0.95f;
		
		//structural positioning dimensions
		static final float ShoulderSocketSpan = 40.0f;
		static final float HumerusLength = 34.0f;
		static final float ForearmLength = 28.0f;
		static final float HandRadius = 6.0f;
		static final float TorsoHeightFromTopOfTrapsToWaist = 56.0f;
		static final float BeltBuckleToCenterOfPelvisBetweenHipSocketsVerticalDistance = 10.0f;
		static final float HipSocketSpan = 20.0f;
		static final float FemurLength = 40f;
		static final float CalfLength = 36f;
		static final float FootLengthFromAnkle = 12.0f;
		static final float FootLength = 17.0f;
		static final float FootHeight = 6.0f;
		
		//dimensions for drawing
		static final float HeadRadius = 15.0f;
		static final float DistanceFromNeckBaseAtShoulderSocketLevelToBottomOfHeadCircle = 10.0f;
		static final float NeckThickness = 16.0f;
		
		static final float UpperArmWidth = 11.0f;
		static final float ElbowWidth = 9.0f;
		static final float WristWidth = 7.0f;
		
		static final float MiddleFingerLength = 18.0f;
		static final float FingerWidth = 1.8f;
		static final float ThumbLength = 11.8f;
		static final float ThumbWidth = 2.6f;

		static final float UpperThighWidth = 16.0f;
		static final float KneeWidth = 9.0f;
		static final float AnkleWidth = 7.0f;
		
		static final float WaistToInseamCentralVerticalDistance = 18.0f;
		static final float HipToOuterPelvisCreaseVerticalDistance = 4.0f;
		static final float InseamToInnerPelvisCreaseHorizontalDistance = 6.0f;
		
		//body dimension ratios
		static final float DistanceFromTopOfTrapsToTorsoCOGComparedToDistanceFromTopOfTrapsToWaistRatio = .8f;
		
		static final float RelativeDistanceFromHeelToBallOfFoot = 0.75f;
		
		static final float FactorForDeterminingHeightOfTrapsAgainstSideOfNeckDeterminedAsAFractionOfTheVerticalDistanceFromShoulderLevelToTheBottomOfTheHead = 0.6f;
		static final float FactorForDeterminingAdditionalVerticalDistanceOfNeckDrawingAreaDeterminedAsAFractionOfHeadRadius = 0.5f;
		static final float LatHorizontalCutInWidthAsRatioOfOverallShoulderSocket = 0.13f;
		static final float LatInsertionPointRatioOfOverallTorsoHeightStartingFromWaist = 0.44f;
		
		static final Color WallColor = new Color (220,220,220);
		static final Color ClearestTransparentWallColor = new Color (.859f, .859f, .859f, .2f);
		static final Color MediumTransparentWallColor = new Color (.859f, .859f, .859f, .5f);
		static final Color MostOpaqueTransparentWallColor = new Color (.859f, .859f, .859f, .75f);
		static final Color ShirtColor = new Color(18,150,165);
		static final Color PantsColor = new Color(150,93,80);
		static final Color SkinTone = new Color(229,174,93);
		static final Color HairColor = new Color(240,103,60);
		static final Color RopeColor = new Color(225,15,240);
		
		static final Color FinalizeHoldsConfirmationTextColor = new Color (26,120,150);
		static final Color OpaqueNeedCloserHoldsColor = new Color (0.95f, 0.15f, 0.35f, 1.0f);
		static final Color TransparentNeedCloserHoldsColor = new Color (0.95f, 0.15f, 0.35f, .35f);
		static final Color BoundaryDesignationColor = new Color(50,170,110);
		
		static final Color TransparentBlue = new Color(0.1f, 0.5f, 0.95f, 0.35f);
		static final Color TransparentRed = new Color(0.95f, 0.15f, 0.1f, 0.35f);
		
		static final float InterholdDistanceTooClose = 20.0f;
		
		static final float MinimumDistanceForProperSpacingInQuadrant = 25.0f;		
		static final float MaximumDistanceForProperSpacingInQuadrant = 75.0f;
		
		static final float InterQuadrantGapInDegrees = 10.0f;
		
		static final float HeightOfBottomOfWallZone = 40.0f;
		static final float HeightOfTopOfWallZone = 20.0f;
				
		static final int NumberOfRandomHoldsInRandomSpray = 5;
		static final float RandomHoldSprayRadius = 65.0f;
		
		static final float ThicknessOfTheBoundaryMarkerForTheZonesAtTheTopAndTheBottomOfTheWall = 4.0f;
	}
}

class CommonAlgorithms {
	
	static float Pi = ClimbingSimulator.constants.Pi;
	
	//I. BASIC CALCULATIONS
	
	static float findSquare (double a) {
		
		return (float) Math.pow(a, 2);
	}
	
	static Point roundPoint(float x, float y) {
		
		return new Point(Math.round(x),Math.round(y));
	}
	
	//II. METHODS TO DETERMINE POSITIONING INFORMATION
	
	static Point averageThePointsInAnArea(ClimbingWall.SetOfHolds<ClimbingWall.ClimbingHold> setOfHolds, Area containingArea) {
		
		int holdsInArea = 0;
		int totalX = 0;
		int totalY = 0;
		
		for (ClimbingWall.ClimbingHold h : setOfHolds) {
			
			if (containingArea.contains(h)) {
				
				totalX += h.x;
				totalY += h.y;
				holdsInArea++;
			}
		}
		
		if (holdsInArea > 0) {
			
			float averageX = totalX/holdsInArea;
			float averageY = totalY/holdsInArea;
			
			Point result = roundPoint(averageX, averageY);
			return result;
			
		} else {
			
			return null;
		}
	}
	
	static float findDistance(Point a,Point b) {
		
		return (float) Math.sqrt(findSquare(a.x - b.x)+findSquare(a.y - b.y));
	}
	
	static Point findMidpoint (Point a, Point b) {
		
		return roundPoint(((a.x+b.x)/2),((a.y+b.y)/2)); 
	}
	
	//calculate "slope" (change in y over change in x on a standard x,y graph) as a measure of radians relative to horizon
	//values range from -pi radians (-180 degrees) to pi radians (180 degrees) 
	static float findSlopeAsRadians (Point start, Point end) {
		
		float changeInYs = (float) (end.y-start.y);
		
		//adjust for orientation of y-axis in swing compared to trigonometric functions
		changeInYs = (-1.0f) * changeInYs;
		
		float changeInXs = (float) (end.x - start.x);
		
		float slopeAsRadians = (float) Math.atan2(changeInYs, changeInXs);
		
		return slopeAsRadians;
	}
	
	//determine angle between sides a and b of triangle with sides a, b, and c
	//side c is the side opposite the angle being calculated
	//values range from 0 radians (0 degrees) to pi radians (180 degrees)
	static float lawOfCosines (double a, double b, double c) {
		
		return (float) Math.acos((CommonAlgorithms.findSquare(a) + CommonAlgorithms.findSquare(b)-CommonAlgorithms.findSquare(c))/(2*a*b));
	}
	
	//calculate new point created by moving a certain length from starting point, either along or against a "slope" that is measured as a radial angle relative to horizon
	static Point shiftBySlopeAsRadians(Point start, float length, float slopeAsRadians, boolean withRadialSlope) {
		
		//reverse radial slope 180 degrees if movement is in opposite direction of radial slope
		if (!withRadialSlope) {
			
			slopeAsRadians = Pi + slopeAsRadians;
			
		}
		
		double xShifted = start.x + (Math.cos(slopeAsRadians) * length);
		
		//minus sign is due to flipped y-axis compared to trigonometric functions
		double yShifted = start.y - (Math.sin(slopeAsRadians) * length);
		
		Point result = new Point(Math.toIntExact(Math.round(xShifted)),Math.toIntExact(Math.round(yShifted)));
		
		return result;
	}
	
	//determine point that is a particular distance away from a starting point along a ray that is the result of pivoting a starting ray through a certain measure of radians, either positive (counterclockwise) or negative (clockwise)
	static Point shiftPointByShiftedOrientation (Point startingPoint, float distanceShifted, float startingRay, float changeInRadiansInCounterclockwiseDirection) {
		
		float shiftedRay = CommonAlgorithms.pivotRay(startingRay, changeInRadiansInCounterclockwiseDirection, true);
		
		return shiftBySlopeAsRadians(startingPoint, distanceShifted, shiftedRay, true);
	}
	
	//take a radial orientation and reverse it 180 degrees by adding pi
	//returns value ranging from -pi to pi 
	static float calculateOppositeRadialOrientation (float radialOrientation) {
		
		float result = radialOrientation + Pi;
		
		//ensure that result is within allowable range of values
		if (result > Pi) {
			result = result - (2 * Pi);
		}
		
		return result;
	}
	
	//find absolute value of convex radial pivot between two rays
	//value returned will range from 0 radians (0 degrees) to pi radians (180 degrees) 
	static float calculateConvexAngleBetweenTwoRays (float rayA, float rayB) {
		
		float result = Math.abs(rayA - rayB);
		
		if (result > Pi) {
			result = (2 * Pi) - result;
		}
		
		return result;
	}
	
	//calculate radian measure that results from moving a ray through a certain number of radians, either clockwise or counterclockwise
	//value returned will be between -pi radians (-180 degrees) and pi radians (180 degrees)
	static float pivotRay(float startingRay, float deltaRadians, boolean counterclockwise) {
		
		float adjuster = 1.0f;
		
		if (!counterclockwise) {
			
			adjuster = adjuster*(-1.0f);
		}
		
		float result = startingRay + (deltaRadians*adjuster);
		
		if (result > Pi) {
			
			result = result - (2 *Pi);		
		}
		
		if (result < -Pi) {
			
			result = result + (2 * Pi);
		}
	
		return result;
	}
	
	static Point farthestReachablePointPossibleInARadialDirection(float radialDirection, Point startingPoint, float reachDistance) {
		
		return shiftBySlopeAsRadians(startingPoint, reachDistance, radialDirection, true);
	}
	
	static Area createQuadrantOfProperHoldSpacingBasedOnReachableRange(ClimbingWall.ClimbingHold h, Direction dir, float interQuadrantGapFormattedAsDegreesInFloat) {			
		
		float outerRadius = ClimbingSimulator.constants.MaximumDistanceForProperSpacingInQuadrant * ClimbingSimulator.constants.SizeFactor;
		float innerRadius = ClimbingSimulator.constants.MinimumDistanceForProperSpacingInQuadrant * ClimbingSimulator.constants.SizeFactor;
		
		float arcStartingDegrees = 0;
		float arcExtent = 90 - (2 * interQuadrantGapFormattedAsDegreesInFloat);
			
		switch(dir) {
		
			case UPANDRIGHT:
				arcStartingDegrees = interQuadrantGapFormattedAsDegreesInFloat;
				break;
				
			case UPANDLEFT:
				arcStartingDegrees = 90 + interQuadrantGapFormattedAsDegreesInFloat;
				break;
				
			case DOWNANDLEFT:
				arcStartingDegrees = 180 + interQuadrantGapFormattedAsDegreesInFloat;
				break;
				
			case DOWNANDRIGHT:
				arcStartingDegrees = 270 + interQuadrantGapFormattedAsDegreesInFloat;
				break;
		
			default:
				System.out.println("I'm being given a Direction that doesn't make sense as a quadrant");
		}
		
		Rectangle2D enclosingOuterSquare = new Rectangle2D.Float(h.x - outerRadius, h.y - outerRadius, 2 * outerRadius, 2 * outerRadius);
		Rectangle2D enclosingInnerSquare = new Rectangle2D.Float(h.x - innerRadius, h.y - innerRadius, 2 * innerRadius, 2 * innerRadius);
		
		Area outerArc = new Area(new Arc2D.Float(enclosingOuterSquare, arcStartingDegrees, arcExtent, 2));
		Area innerArc = new Area(new Arc2D.Float(enclosingInnerSquare, arcStartingDegrees, arcExtent, 2));
		
		outerArc.subtract(innerArc);
		
		return outerArc;
	}
	
	static Area createQuadrantForDeterminingBorderHolds(ClimbingWall.ClimbingHold h, Direction dir) {			
		
		float radius = ClimbingSimulator.constants.MaximumDistanceForProperSpacingInQuadrant * ClimbingSimulator.constants.SizeFactor;
		
		float arcStartingDegrees = 0;
		
		switch(dir) {
		
			case UPANDRIGHT:
				arcStartingDegrees = 0;
				break;
				
			case UPANDLEFT:
				arcStartingDegrees = 90;
				break;
				
			case DOWNANDLEFT:
				arcStartingDegrees = 180;
				break;
				
			case DOWNANDRIGHT:
				arcStartingDegrees = 270;
				break;
		
			default:
				System.out.println("I'm being given a Direction that doesn't make sense as a quadrant");
		}
		
		Rectangle2D enclosingSquare = new Rectangle2D.Float(h.x - radius, h.y - radius, 2 * radius, 2 * radius);
		
		Area result = new Area(new Arc2D.Float(enclosingSquare, arcStartingDegrees, 90, 2));
		
		return result;
	}	
}

enum LimbIdentifier {
	LEFTARM, RIGHTARM, LEFTLEG, RIGHTLEG
}

enum Direction {
	UP, DOWN, RIGHT, LEFT, UPANDRIGHT, DOWNANDRIGHT, DOWNANDLEFT, UPANDLEFT
}

enum ConnectionAnalysisState {
	NOTYETCONNECTED, CONNECTING, CONNECTED;  
}

class LawOfCosinesException extends Exception {

	private static final long serialVersionUID = 1L;
	
}

class LimbMustMoveFirstException extends Exception {

		private static final long serialVersionUID = 1L;
	
	LimbIdentifier limbIdentifier;
	
	public LimbMustMoveFirstException(LimbIdentifier limbIdentifier) {
		
		this.limbIdentifier = limbIdentifier;
	}

	public LimbIdentifier getLimbIdentifier() {
		
		return this.limbIdentifier;
	}
}

class ImpossibleRouteException extends Exception {

	private static final long serialVersionUID = 1L;
	
}

class Climber extends Applet {
	
	private static final long serialVersionUID = 1L;
	
	private static float Pi = ClimbingSimulator.constants.Pi;
	
	private boolean iAmOnTheWall;
	private boolean iHaveLeftGroundAndConnectedToWallWithAllFourLimbs;
	private boolean iAmBacktracking;

	private Point mostRecentFourConnectionsPoint;
	private int difficultMoveTracker;
	private boolean hasReachedDestinationPointThatWasBeyondSeeminglyImpossiblePosition;
	private boolean iAmTryingToClimb;
	
	private ClimbingWall.SetOfHolds<ClimbingWall.ClimbingHold> finalizedSetOfUserEnteredHolds;
	
	private Point anchorPoint;
	
	private LeftArm leftArm;
	private RightArm rightArm;
	private LeftLeg leftLeg;
	private RightLeg rightLeg;
	
	private LimbsCoordinator<Limb> limbsCoordinator;
	
	private LinkedList<Integer> shortestPathThroughHoldPointsFromBottomToTopBasedOnQuadrantConnections;
	
	private LinkedList<Point> pathThroughEachSecondIterationAveragePointOfTheHoldPointsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections;

	private Point desiredDestinationForBeltBuckle;
	
	private float desiredRadialDirectionOfTravel;
	private Integer averagePathIndexIterator;
	
	private boolean hasReachedSeeminglyImpossiblePosition;
	private boolean hasReturnedSafely;
	private int indexOfAveragePathDestinationPointAtTheSeeminglyImpossiblePosition;
	
	private Area currentReachableAreaAggregate;
	
	//fundamental points for current climber positioning
	private Point torsoCOG = new Point();
	
	private Point leftShoulderSocketPos = new Point();
	private Point rightShoulderSocketPos = new Point();
	private Point headBasePos = new Point();
	private Point headTopPos = new Point();
	private Point neckBaseBetweenShoulderSocketsPos = new Point();
	private Point leftElbowPos = new Point();
	private Point rightElbowPos = new Point();
	private Point leftWristPos = new Point();
	private Point rightWristPos = new Point();
	private Point leftHandCenterPos = new Point();
	private Point rightHandCenterPos = new Point();
	
	private Point beltBucklePos = new Point();
	private Point pelvisCenterPos = new Point();
	private Point leftHipSocketPos = new Point();
	private Point rightHipSocketPos = new Point();
	
	private Point leftKneePos = new Point();
	private Point leftAnklePos = new Point();
	private Point rightKneePos = new Point();
	private Point rightAnklePos = new Point();
	private Point leftFootHoldContactPoint = new Point();
	private Point rightFootHoldContactPoint = new Point();
	
	private Area currentHipBox;
	private Area prospectiveHipBox;
	
	private Point leftUpperOuterHipBoxPos = new Point();
	private Point rightUpperOuterHipBoxPos = new Point();
	private Point prospectiveLeftUpperOuterHipBoxPos = new Point();
	private Point prospectiveRightUpperOuterHipBoxPos = new Point();
	
	private Point leftUnjammedFootWaypoint;
	private Point rightUnjammedFootWaypoint;
		
	//slopes of limb segments measured as rays relative to horizon with proximal joint as origin 
	private float leftHumerusOrientation;
	private float rightHumerusOrientation;
	private float leftThighOrientation;
	private float rightThighOrientation;
	private float leftForearmOrientation;
	private float rightForearmOrientation;
	private float leftCalfOrientation;
	private float rightCalfOrientation;
	private float leftWristPivot;
	private float rightWristPivot;

	//points and distances for testing prospective torso move
	private Point prospectiveLeftShoulderSocketPos = new Point();
	private Point prospectiveRightShoulderSocketPos = new Point();
	private Point prospectiveBeltBucklePos = new Point();
	private Point prospectiveTorsoCOG = new Point();
	private Point prospectivePelvisCenterPos = new Point();
	private Point prospectiveLeftHipSocketPos = new Point();
	private Point prospectiveRightHipSocketPos = new Point();
	
	//points related to painting the upper body
	//instantiated to (0,0) here because formulae work directly with x and y values, which therefore should not be null
	private Point leftWaistDrawingPoint = new Point();
	private Point rightWaistDrawingPoint = new Point();
	
	private Point leftLatTorsoInsertionPoint = new Point();
	private Point rightLatTorsoInsertionPoint = new Point();
	private Point leftTrapNeckIntersectionPoint = new Point();
	private Point rightTrapNeckIntersectionPoint = new Point();
	
	private Point lateralProximalLeftHumerus = new Point();
	private Point medialProximalLeftHumerus = new Point();
	private Point lateralDistalLeftHumerus = new Point();
	private Point medialDistalLeftHumerus = new Point();
	private Point lateralProximalRightHumerus = new Point();
	private Point medialProximalRightHumerus = new Point();
	private Point lateralDistalRightHumerus = new Point();
	private Point medialDistalRightHumerus = new Point();
	
	private Point lateralProximalLeftForearm = new Point();
	private Point medialProximalLeftForearm = new Point();
	private Point lateralDistalLeftForearm = new Point();
	private Point medialDistalLeftForearm = new Point();
	private Point lateralProximalRightForearm = new Point();
	private Point medialProximalRightForearm = new Point();
	private Point lateralDistalRightForearm = new Point();
	private Point medialDistalRightForearm = new Point();
	
	private Point leftShoulderDrawingPoint = new Point();
	private Point rightShoulderDrawingPoint = new Point();
	private Point leftElbowDrawingPoint = new Point();
	private Point rightElbowDrawingPoint = new Point();	
	private Point leftHandDrawingPoint = new Point();
	private Point rightHandDrawingPoint = new Point();	
	
	//points related to painting the lower body
	private Point lateralProximalLeftThigh = new Point();
	private Point medialProximalLeftThigh = new Point();
	private Point lateralDistalLeftThigh = new Point();
	private Point medialDistalLeftThigh = new Point();
	private Point lateralProximalRightThigh = new Point();
	private Point medialProximalRightThigh = new Point();
	private Point lateralDistalRightThigh = new Point();
	private Point medialDistalRightThigh = new Point();
	
	private Point lateralProximalLeftCalf = new Point();
	private Point medialProximalLeftCalf = new Point();
	private Point lateralDistalLeftCalf = new Point();
	private Point medialDistalLeftCalf = new Point();
	private Point lateralProximalRightCalf = new Point();
	private Point medialProximalRightCalf = new Point();
	private Point lateralDistalRightCalf = new Point();
	private Point medialDistalRightCalf = new Point();
		
	private Point inseam = new Point();
	
	private Point leftOuterPelvisHinge = new Point();
	private Point leftInnerPelvisHinge = new Point();
	private Point rightOuterPelvisHinge = new Point();
	private Point rightInnerPelvisHinge = new Point();
	
	private Point leftHipSocketDrawingPoint = new Point();
	private Point rightHipSocketDrawingPoint = new Point();
	private Point leftKneeDrawingPoint = new Point();
	private Point rightKneeDrawingPoint = new Point();	
	private Point leftFootDrawingPoint = new Point();
	private Point rightFootDrawingPoint = new Point();
	
	//points related to painting the head
	private Point headVisualCenter = new Point();
	private Point headDrawingPoint = new Point();
	private Point neckDrawingPoint = new Point();
	
	//body part dimensions
	private static float shoulderSocketSpan;
	private static float humerusLength;
	private static float forearmLength;
	private static float torsoHeightFromTopOfTrapsToWaist;
	private static float beltBuckleToCenterPelvisDistance;
	private static float hipSocketSpan;
	private static float femurLength;
	private static float calfLength;
	private static float footLengthHorizontalFromAnkleToToe;
	private static float footLength;
	private static float footHeight;
	private static float headRadius;
	private static float distanceFromNeckBaseAtShoulderSocketLevelToBottomOfHeadCircle;
	private static float neckThickness;
	private static float upperArmWidth;
	private static float elbowWidth;
	private static float wristWidth;
	private static float handRadius;
	private static float upperThighWidth;
	private static float kneeWidth;
	private static float ankleWidth;
	private static float waistToInseamCentralVerticalDistance;
	private static float hipToOuterPelvisCreaseVerticalDistance;
	private static float inseamToInnerPelvisCreaseHorizontalDistance;
	private static float fingerLength;
	private static float fingerWidth;
	private static float thumbLength;
	private static float thumbWidth;
	
	private float highestPossibleExtensionFromFootToHand;
	private float widestPossibleExtensionFromHandToHand;
	
	boolean wallHasDrawnTheLatestClimberMoves;
	
	Climber () {
		
		iAmOnTheWall = false;
		iHaveLeftGroundAndConnectedToWallWithAllFourLimbs = false;
		
		shoulderSocketSpan = ClimbingSimulator.constants.ShoulderSocketSpan * ClimbingSimulator.constants.SizeFactor;
		humerusLength = ClimbingSimulator.constants.HumerusLength * ClimbingSimulator.constants.SizeFactor;
		forearmLength = ClimbingSimulator.constants.ForearmLength * ClimbingSimulator.constants.SizeFactor;
		torsoHeightFromTopOfTrapsToWaist = ClimbingSimulator.constants.TorsoHeightFromTopOfTrapsToWaist * ClimbingSimulator.constants.SizeFactor;
		beltBuckleToCenterPelvisDistance = ClimbingSimulator.constants.BeltBuckleToCenterOfPelvisBetweenHipSocketsVerticalDistance * ClimbingSimulator.constants.SizeFactor;
		hipSocketSpan = ClimbingSimulator.constants.HipSocketSpan * ClimbingSimulator.constants.SizeFactor;
		femurLength = ClimbingSimulator.constants.FemurLength * ClimbingSimulator.constants.SizeFactor;
		calfLength = ClimbingSimulator.constants.CalfLength * ClimbingSimulator.constants.SizeFactor;
		footLengthHorizontalFromAnkleToToe = ClimbingSimulator.constants.FootLengthFromAnkle * ClimbingSimulator.constants.SizeFactor;
		footLength = ClimbingSimulator.constants.FootLength * ClimbingSimulator.constants.SizeFactor;
		footHeight = ClimbingSimulator.constants.FootHeight * ClimbingSimulator.constants.SizeFactor;
		headRadius = ClimbingSimulator.constants.HeadRadius * ClimbingSimulator.constants.SizeFactor;
		distanceFromNeckBaseAtShoulderSocketLevelToBottomOfHeadCircle = ClimbingSimulator.constants.DistanceFromNeckBaseAtShoulderSocketLevelToBottomOfHeadCircle * ClimbingSimulator.constants.SizeFactor;
		neckThickness = ClimbingSimulator.constants.NeckThickness * ClimbingSimulator.constants.SizeFactor;
		upperArmWidth = ClimbingSimulator.constants.UpperArmWidth * ClimbingSimulator.constants.SizeFactor;
		elbowWidth = ClimbingSimulator.constants.ElbowWidth * ClimbingSimulator.constants.SizeFactor;
		wristWidth = ClimbingSimulator.constants.WristWidth * ClimbingSimulator.constants.SizeFactor;
		handRadius = ClimbingSimulator.constants.HandRadius * ClimbingSimulator.constants.SizeFactor;		
		upperThighWidth = ClimbingSimulator.constants.UpperThighWidth * ClimbingSimulator.constants.SizeFactor;
		kneeWidth = ClimbingSimulator.constants.KneeWidth * ClimbingSimulator.constants.SizeFactor;
		ankleWidth = ClimbingSimulator.constants.AnkleWidth * ClimbingSimulator.constants.SizeFactor;
		waistToInseamCentralVerticalDistance = ClimbingSimulator.constants.WaistToInseamCentralVerticalDistance * ClimbingSimulator.constants.SizeFactor;
		hipToOuterPelvisCreaseVerticalDistance = ClimbingSimulator.constants.HipToOuterPelvisCreaseVerticalDistance * ClimbingSimulator.constants.SizeFactor;
		inseamToInnerPelvisCreaseHorizontalDistance = ClimbingSimulator.constants.InseamToInnerPelvisCreaseHorizontalDistance * ClimbingSimulator.constants.SizeFactor;
		fingerLength = ClimbingSimulator.constants.MiddleFingerLength * ClimbingSimulator.constants.SizeFactor;
		fingerWidth = ClimbingSimulator.constants.FingerWidth * ClimbingSimulator.constants.SizeFactor;
		thumbLength = ClimbingSimulator.constants.ThumbLength * ClimbingSimulator.constants.SizeFactor;
		thumbWidth = ClimbingSimulator.constants.ThumbWidth * ClimbingSimulator.constants.SizeFactor;
		
		highestPossibleExtensionFromFootToHand = footHeight + calfLength + femurLength + (torsoHeightFromTopOfTrapsToWaist - (distanceFromNeckBaseAtShoulderSocketLevelToBottomOfHeadCircle * ClimbingSimulator.constants.FactorForDeterminingHeightOfTrapsAgainstSideOfNeckDeterminedAsAFractionOfTheVerticalDistanceFromShoulderLevelToTheBottomOfTheHead)) + humerusLength + forearmLength + handRadius;
		widestPossibleExtensionFromHandToHand = shoulderSocketSpan + (2 * (humerusLength + forearmLength + handRadius)); 
		
		leftArm = new LeftArm();
		rightArm = new RightArm();
		leftLeg = new LeftLeg();
		rightLeg = new RightLeg();
		
		limbsCoordinator = new LimbsCoordinator<>();

		shortestPathThroughHoldPointsFromBottomToTopBasedOnQuadrantConnections = new LinkedList<>();
		pathThroughEachSecondIterationAveragePointOfTheHoldPointsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections = new LinkedList<>();
	}
	
	abstract class Limb {
		
		LimbIdentifier limbIdentifier;
		
		Area currentlyReachableArea;
		Area prospectiveMoveReachableArea;
		
		ArrayList<Point> allReachableHoldPoints;
				
		boolean isConnectedToSomething;
		boolean hasMadeFirstConnectionToTheWall;
		
		boolean limbHadToAbandonHoldBecauseOfAViolationOfReachableArea;
		boolean limbHasMovedThroughAnAreaWhereItHadNoOptions;
		
		float maximumReachLength;
		float maximumLimbExtensionLength;
		float overallExtensionRatio;
				
		abstract Point getWallConnectionBodyPartPoint(); //hand or foot		
		abstract void setWallConnectionBodyPartPoint(Point p);
		
		abstract Point getProximalStartPointForLimb(); //shoulder or pelvis socket
		abstract Point getProspectiveProximalStartPointForLimb();
		
		abstract Point getDistalEndpointOfDistalSegment(); //wrist or ankle	
		
		boolean isLeft() {
			return Left.class.isAssignableFrom(this.getClass());
		}
		
		boolean isArm() {
			return Arm.class.isAssignableFrom(this.getClass());
		}
		
		abstract float getProximalSegmentLength();
		abstract float getDistalSegmentLength();
		abstract float getReachDistanceContributedByExtremity();		
		
		void calculateLimbMaxEntensionValues() {
			
			maximumReachLength = getProximalSegmentLength() + getDistalSegmentLength() + getReachDistanceContributedByExtremity();
			
			maximumLimbExtensionLength = getProximalSegmentLength() + getDistalSegmentLength();
		}

		void updateOverallExtensionRatio() {
			
			float totalDistanceBetweenLimbEndpoints = CommonAlgorithms.findDistance(getProximalStartPointForLimb(), getDistalEndpointOfDistalSegment());
			overallExtensionRatio = totalDistanceBetweenLimbEndpoints/maximumLimbExtensionLength;
		}
		
		abstract void updateReachableAreaForCurrentPosition();
		
		abstract void updateProspectiveMoveReachableArea();
				
		boolean limbCanContinueToUseCurrentHoldIfTorsoMakesProspectiveMove() {
			
			updateProspectiveMoveReachableArea();
			
			return prospectiveMoveReachableArea.contains(getWallConnectionBodyPartPoint());
		}
		
		void updateReachableHoldPoints() {
		
			updateReachableAreaForCurrentPosition();
			
			ArrayList<Point> result = new ArrayList<Point>();
			
			for (Point p : finalizedSetOfUserEnteredHolds) {
				
				if (currentlyReachableArea.contains(p)) {
					
					result.add(p);
				}
			}
			
			allReachableHoldPoints = result;
		}
		
		void getSetToClimb() {
			
			updateReachableHoldPoints();
							
			if(allReachableHoldPoints.size() == 0) {
					
				if (isArm()) {
						
					moveToReadyPoint();
					return;
						
				} else {
						
					return; //leg remains on floor
				}
			}
			
			Point destinationHoldPoint = findPointThatIsMostExtremeInRadialDirectionWithinSuppliedSetOfPoints(desiredRadialDirectionOfTravel, allReachableHoldPoints);
			
			moveLimbToPointOverrideable(destinationHoldPoint, true);
			
			indexOfAveragePathDestinationPointAtTheSeeminglyImpossiblePosition = Integer.MAX_VALUE;
		}
		
		boolean receiveARequestToAdvanceLimbFromCurrentHoldToAReachableHoldThatIsMoreExtremeInDesiredDirectionOfTravelThanIsCurrentHoldAndConfirmIfAMoveWasMade() {
			
			if (limbCanBeAdvancedFromCurrentHoldToAReachableHoldThatIsMoreExtremeInDesiredDirectionOfTravel()) {
				
				advanceLimbAsMuchAsPossibleToAConfirmedHoldThatIsMoreExtremeInDesiredDirectionOfTravelThanIsTheCurrentHold();
				
				return true;
				
			} else {
				
				return false;
				
			}
		}
		
		boolean limbCanBeAdvancedFromCurrentHoldToAReachableHoldThatIsMoreExtremeInDesiredDirectionOfTravel() {
			
			updateReachableHoldPoints();
			
			Point reachableHoldThatIsMostExtremeInDesiredDirectionOfTravel = findPointThatIsMostExtremeInRadialDirectionWithinSuppliedSetOfPoints(desiredRadialDirectionOfTravel, allReachableHoldPoints);
			
			if ((getWallConnectionBodyPartPoint().x == reachableHoldThatIsMostExtremeInDesiredDirectionOfTravel.x) && (getWallConnectionBodyPartPoint().y == reachableHoldThatIsMostExtremeInDesiredDirectionOfTravel.y)) {
				
				return false;
				
			} else {
				
				return true;
				
			}
		}
		
		void advanceLimbAsMuchAsPossibleToAConfirmedHoldThatIsMoreExtremeInDesiredDirectionOfTravelThanIsTheCurrentHold() {
			
			updateReachableHoldPoints();
			
			Point reachableHoldThatIsMostExtremeAndMoreExtremeThanCurrentHoldInDesiredDirectionOfTravel = findPointThatIsMostExtremeInRadialDirectionWithinSuppliedSetOfPoints(desiredRadialDirectionOfTravel, allReachableHoldPoints);
			
			moveLimbToPointOverrideable(reachableHoldThatIsMostExtremeAndMoreExtremeThanCurrentHoldInDesiredDirectionOfTravel, true);
		}
		
		boolean receiveARequestToMoveLimbToTheHoldThatIsFurthestInDesiredDirectionOfTravelAmongReachableHoldsOtherThanHoldsThatHaveBeenUsedDuringEitherAscentOrBacktrackingAndConfirmIfAMoveWasMade() {
			
			if (limbHasAHoldAvailableOtherThanHoldsThatHaveBeenUsedDuringEitherAscentOrBacktracking()) {
				
				ArrayList<Point> unusedPoints = new ArrayList<>();
				
				if (!iAmBacktracking) {
					
					for (Point p : allReachableHoldPoints) {
						
						int indexOfHoldAtPointPWithinFinalizedSetOfUserEnteredHolds = finalizedSetOfUserEnteredHolds.indexOf(p);
						
						if (!finalizedSetOfUserEnteredHolds.get(indexOfHoldAtPointPWithinFinalizedSetOfUserEnteredHolds).limbsThatHaveUsedHoldDuringAscent.contains(limbIdentifier)) {
							
							unusedPoints.add(p);
							
						}
					}
					
				} else {
				
					for (Point p : allReachableHoldPoints) {
						
						int indexOfHoldAtPointPWithinFinalizedSetOfUserEnteredHolds = finalizedSetOfUserEnteredHolds.indexOf(p);
						
						if (!finalizedSetOfUserEnteredHolds.get(indexOfHoldAtPointPWithinFinalizedSetOfUserEnteredHolds).limbsThatHaveUsedHoldDuringBacktrackingSequence.contains(limbIdentifier)) {
							
							unusedPoints.add(p);
							
						}
					}
				}
				
				if (unusedPoints.size() == 0) {
					
					return false;
					
				}
				
				Point destinationHoldPoint = findPointThatIsMostExtremeInRadialDirectionWithinSuppliedSetOfPoints(desiredRadialDirectionOfTravel, unusedPoints);
				
				moveLimbToPointOverrideable(destinationHoldPoint, true);
				
				return true;
				
			} else {
				
				return false;
				
			}	
		}
		
		boolean limbHasAHoldAvailableOtherThanHoldsThatHaveBeenUsedDuringEitherAscentOrBacktracking() {
			
			updateReachableHoldPoints();
					
			ArrayList<Point> filteredPoints = new ArrayList<>();
						
			if (!iAmBacktracking) {
					
				for (Point p : allReachableHoldPoints) {
					
					int indexOfHoldAtPointPWithinFinalizedSetOfUserEnteredHolds = finalizedSetOfUserEnteredHolds.indexOf(p);
					
					if (!finalizedSetOfUserEnteredHolds.get(indexOfHoldAtPointPWithinFinalizedSetOfUserEnteredHolds).limbsThatHaveUsedHoldDuringAscent.contains(limbIdentifier)) {
						
						filteredPoints.add(p);
						
					}
				}
				
			} else {
			
				for (Point p : allReachableHoldPoints) {
					
					int indexOfHoldAtPointPWithinFinalizedSetOfUserEnteredHolds = finalizedSetOfUserEnteredHolds.indexOf(p);
					
					if (!finalizedSetOfUserEnteredHolds.get(indexOfHoldAtPointPWithinFinalizedSetOfUserEnteredHolds).limbsThatHaveUsedHoldDuringBacktrackingSequence.contains(limbIdentifier)) {
						
						filteredPoints.add(p);
						
					}
				}
			}
				
			if (filteredPoints.size() > 0) {
				
				return true;
				
			} else {
				
				return false;
				
			}
		}		
		
		void moveLimbToTheReachableHoldThatIsFurthestInDesiredDirectionOfTravelAmongAllReachableHolds() {
			
			updateReachableHoldPoints();
			
			Point reachableHoldThatIsMostExtremeInDesiredDirectionOfTravel = findPointThatIsMostExtremeInRadialDirectionWithinSuppliedSetOfPoints(desiredRadialDirectionOfTravel, allReachableHoldPoints);
			
			moveLimbToPointOverrideable(reachableHoldThatIsMostExtremeInDesiredDirectionOfTravel, true);
		}

		Point findPointThatIsMostExtremeInRadialDirectionWithinSuppliedSetOfPoints(float radialDirection, ArrayList<Point> setOfPoints) {
			
			Point result = null;
			
			Point targetPoint = CommonAlgorithms.farthestReachablePointPossibleInARadialDirection(radialDirection, getProximalStartPointForLimb(), maximumReachLength);
			
			float distanceOfClosestHoldThusfar = 2 * Float.MAX_VALUE;
			
			for (Point p: setOfPoints) {
				
				float interholdDistance = CommonAlgorithms.findDistance(p, targetPoint);
				
				if (interholdDistance < distanceOfClosestHoldThusfar) {
					distanceOfClosestHoldThusfar = interholdDistance;
					result = p;
				}
			}
			
			return result;
		}		
		
		void moveLimbToPointOverrideable(Point destinationPoint, boolean connectToWallAfterMakingMove) {
			
			if (isConnectedToSomething) {
				disconnectFromWall();
			}
			
			ArrayList<float[]> waypointsFormattedAsFloat = calculateWaypointFloatCoordinates(getWallConnectionBodyPartPoint(), destinationPoint, ClimbingSimulator.constants.LimbMovementIncrement * ClimbingSimulator.constants.SizeFactor);
			
			Point waypoint = getWallConnectionBodyPartPoint();
					
			for (float[] waypointCoordinatesInFloat : waypointsFormattedAsFloat) {
				waypoint.x = Math.round(waypointCoordinatesInFloat[0]);
				waypoint.y = Math.round(waypointCoordinatesInFloat[1]);
	
				setWallConnectionBodyPartPoint(waypoint);
				
				updateLimbPoints();
				pauseForAnimationTiming();
			}
			
			if (connectToWallAfterMakingMove) {
				connectToWall(destinationPoint);
			}
		}
		
		void moveLimbToPointBasic(Point destinationPoint, boolean connectToWallAfterMakingMove) {
			
			if (isConnectedToSomething) {
				disconnectFromWall();
			}
			
			ArrayList<float[]> waypointsFormattedAsFloat = calculateWaypointFloatCoordinates(getWallConnectionBodyPartPoint(), destinationPoint, ClimbingSimulator.constants.LimbMovementIncrement * ClimbingSimulator.constants.SizeFactor);
			
			Point waypoint = getWallConnectionBodyPartPoint();
					
			for (float[] waypointCoordinatesInFloat : waypointsFormattedAsFloat) {
				waypoint.x = Math.round(waypointCoordinatesInFloat[0]);
				waypoint.y = Math.round(waypointCoordinatesInFloat[1]);
	
				setWallConnectionBodyPartPoint(waypoint);
				
				updateLimbPoints();
				pauseForAnimationTiming();
			}
			
			if (connectToWallAfterMakingMove) {
				connectToWall(destinationPoint);
			}
		}
		
		abstract boolean wouldMoveGoThroughAreaThatJamsLimb(Point prospectiveDestinationPoint);
		abstract void moveLimbToUnjammedWaypointFirst(Point ultimateDestinationPoint, boolean connectToWallAfterMakingMove);
		
		abstract void moveToReadyPoint();
	
		abstract void updateLimbPoints();
		
		void disconnectFromWall() {
			
			isConnectedToSomething = false;
		}
			
		void connectToWall(Point connectionPoint) {
			
			hasMadeFirstConnectionToTheWall = true;
			
			limbHadToAbandonHoldBecauseOfAViolationOfReachableArea = false;
			limbHasMovedThroughAnAreaWhereItHadNoOptions = false;
			
			isConnectedToSomething = true;
			
			updateSetOfLimbIdentifiersForHoldThatClimberJustConnectedTo(connectionPoint);
		}
		
		void updateSetOfLimbIdentifiersForHoldThatClimberJustConnectedTo(Point connectionPoint) {
		
			int indexOfHoldThatLimbIsCurrentlyUsingInFinalizedSetOfUserEnteredHolds = finalizedSetOfUserEnteredHolds.indexOf(connectionPoint);
			
			if (!iAmBacktracking) {
				
				finalizedSetOfUserEnteredHolds.get(indexOfHoldThatLimbIsCurrentlyUsingInFinalizedSetOfUserEnteredHolds).limbsThatHaveUsedHoldDuringAscent.add(limbIdentifier);
				
			} else {
				
				finalizedSetOfUserEnteredHolds.get(indexOfHoldThatLimbIsCurrentlyUsingInFinalizedSetOfUserEnteredHolds).limbsThatHaveUsedHoldDuringBacktrackingSequence.add(limbIdentifier);
				
			}
		}
	}
	
	abstract class Arm extends Limb {
		
		@Override
		float getProximalSegmentLength() {
			return humerusLength;
		}

		@Override
		float getDistalSegmentLength() {
			return forearmLength;
		}

		@Override
		float getReachDistanceContributedByExtremity() {
			return handRadius;
		}
		
		@Override
		void moveToReadyPoint() {
			
			if(isLeft()) {

				moveLimbToPointOverrideable(CommonAlgorithms.roundPoint((getProximalStartPointForLimb().x - (1 * ClimbingSimulator.constants.SizeFactor)), (getProximalStartPointForLimb().y + (55 * ClimbingSimulator.constants.SizeFactor))), false);
				
			} else {

				moveLimbToPointOverrideable(CommonAlgorithms.roundPoint((getProximalStartPointForLimb().x + (1 * ClimbingSimulator.constants.SizeFactor)), (getProximalStartPointForLimb().y + (55 * ClimbingSimulator.constants.SizeFactor))), false);
			}
		}
		
		void updateReachableAreaForCurrentPosition() {
			
			currentlyReachableArea = calculateReachableAreaForArm(getProximalStartPointForLimb());
		}
	
		void updateProspectiveMoveReachableArea() {
			
			prospectiveMoveReachableArea = calculateReachableAreaForArm(getProspectiveProximalStartPointForLimb());
		}
		
		Area calculateReachableAreaForArm(Point shoulderSocket) {
			
			Area result;

			Shape armCircle = new Ellipse2D.Float(shoulderSocket.x - maximumReachLength, shoulderSocket.y - maximumReachLength, 2 * maximumReachLength, 2 * maximumReachLength);
			result = new Area(armCircle);
	
			Shape otherSideOfBody;
			
			if (isLeft()) {
				otherSideOfBody = new Rectangle2D.Float(shoulderSocket.x + (ClimbingSimulator.constants.ShoulderSocketSpan * ClimbingSimulator.constants.SizeFactor), shoulderSocket.y - maximumReachLength, maximumReachLength, 2 * maximumReachLength);
			} else {
				otherSideOfBody = new Rectangle2D.Float(shoulderSocket.x - ((ClimbingSimulator.constants.ShoulderSocketSpan * ClimbingSimulator.constants.SizeFactor) + maximumReachLength), shoulderSocket.y - maximumReachLength, maximumReachLength, 2 * maximumReachLength);
			}
	
			Area areaToSubtract = new Area(otherSideOfBody);
			
			result.subtract(areaToSubtract);
			
			return result;
		}
		
		boolean wouldMoveGoThroughAreaThatJamsLimb(Point prospectiveDestinationPoint) {
			
			return false;
		}
		
		void moveLimbToUnjammedWaypointFirst(Point ultimateDestinationPoint, boolean connectToWallAfterMakingMove) {
			
		}
	}
	
	abstract class Leg extends Limb {
		
		@Override
		float getProximalSegmentLength() {
			return femurLength;
		}

		@Override
		float getDistalSegmentLength() {
			return calfLength;
		}

		@Override
		float getReachDistanceContributedByExtremity() {
			return Math.min(footHeight + ((ClimbingSimulator.constants.HoldDiameter/2) * ClimbingSimulator.constants.SizeFactor), (footLength * ClimbingSimulator.constants.RelativeDistanceFromHeelToBallOfFoot) - (footLength - footLengthHorizontalFromAnkleToToe)); //the second part of this equation could be a bit longer if it took into account the distance between ankle and foot-hold contact point, but reaching that far tends to trigger cramps in the author's feet
		}
		
		@Override
		void moveToReadyPoint() {

			if(isLeft()) {
				
				moveLimbToPointOverrideable(CommonAlgorithms.roundPoint((getProximalStartPointForLimb().x - (5 * ClimbingSimulator.constants.SizeFactor)), (getProximalStartPointForLimb().y + (83 * ClimbingSimulator.constants.SizeFactor))), false);

			} else {
				
				moveLimbToPointOverrideable(CommonAlgorithms.roundPoint((getProximalStartPointForLimb().x + (5 * ClimbingSimulator.constants.SizeFactor)), (getProximalStartPointForLimb().y + (83 * ClimbingSimulator.constants.SizeFactor))), false);
			}
		}
		
		void updateReachableAreaForCurrentPosition() {
			
			updateCurrentHipBox();
			
			currentlyReachableArea = calculateReachableAreaForLeg(getProximalStartPointForLimb(), torsoCOG, pelvisCenterPos, currentHipBox, getCurrentUpperOuterHipBoxPoint());
		}
		
		void updateProspectiveMoveReachableArea() {
			
			updateProspectiveHipBox();
			
			prospectiveMoveReachableArea = calculateReachableAreaForLeg(getProspectiveProximalStartPointForLimb(), prospectiveTorsoCOG, prospectivePelvisCenterPos, prospectiveHipBox, getProspectiveUpperOuterHipBoxPoint());
		}
		
		Area calculateReachableAreaForLeg (Point hipSocket, Point torsoCOG, Point pelvisCenter, Area relevantHipBox, Point upperOuterHipBoxPoint) {
			
			Area result;

			Shape legCircle = new Ellipse2D.Float(hipSocket.x - maximumReachLength, hipSocket.y - maximumReachLength, 2 * maximumReachLength, 2 * maximumReachLength);
			result = new Area(legCircle);
			
			Area totalAreaToSubtract;
			
			Area areaTooHighForInflexibleClimber;
			Area areaOfLegReachArchThatRisesDistallyAwayFromUpperOuterHipBoxPoint;
			Area areaOnOtherSideOfBody;
			Area areaOfLegReachArchThatRunsDistallyAwayFromPelvisCenter;
			
			Shape tooHighForInflexibleClimber;
			Shape legReachArcThatRisesAwayFromTorso;
			Shape otherSideOfBody;
			Shape legReachArcThatRunsAwayFromPelvisCenter;
			
			tooHighForInflexibleClimber = new Rectangle2D.Float(torsoCOG.x - (maximumReachLength + footLength), torsoCOG.y - (maximumReachLength + ((ClimbingSimulator.constants.HoldDiameter/2) * ClimbingSimulator.constants.SizeFactor)), 2 * (maximumReachLength + footLength), maximumReachLength + ((ClimbingSimulator.constants.HoldDiameter/2) * ClimbingSimulator.constants.SizeFactor));
						
			if (isLeft()) {
				legReachArcThatRisesAwayFromTorso = new Arc2D.Float(upperOuterHipBoxPoint.x - (maximumReachLength + footLength), upperOuterHipBoxPoint.y - (maximumReachLength + footLength), 2 * (maximumReachLength + footLength), 2 * (maximumReachLength + footLength), (180.0f - ClimbingSimulator.constants.DegreesOfLegReachHeightGainedAsFootMovesAwayFromUpperOuterHipBoxPoint), ClimbingSimulator.constants.DegreesOfLegReachHeightGainedAsFootMovesAwayFromUpperOuterHipBoxPoint, Arc2D.PIE);
			} else {
				legReachArcThatRisesAwayFromTorso = new Arc2D.Float(upperOuterHipBoxPoint.x - (maximumReachLength + footLength), upperOuterHipBoxPoint.y - (maximumReachLength + footLength), 2 * (maximumReachLength + footLength), 2 * (maximumReachLength + footLength), 0, ClimbingSimulator.constants.DegreesOfLegReachHeightGainedAsFootMovesAwayFromUpperOuterHipBoxPoint, Arc2D.PIE);
			}
								
			if (isLeft()) {
				otherSideOfBody = new Rectangle2D.Float(pelvisCenter.x + ((footLength * ClimbingSimulator.constants.RelativeDistanceFromHeelToBallOfFoot) - (footLength - footLengthHorizontalFromAnkleToToe)), pelvisCenter.y - maximumReachLength, maximumReachLength + ((footLength * ClimbingSimulator.constants.RelativeDistanceFromHeelToBallOfFoot) - (footLength - footLengthHorizontalFromAnkleToToe)), 2 * (maximumReachLength + ((ClimbingSimulator.constants.HoldDiameter/2) * ClimbingSimulator.constants.SizeFactor)));
			} else {
				otherSideOfBody = new Rectangle2D.Float(pelvisCenter.x - maximumReachLength, pelvisCenter.y - maximumReachLength, maximumReachLength - ((footLength * ClimbingSimulator.constants.RelativeDistanceFromHeelToBallOfFoot) - (footLength - footLengthHorizontalFromAnkleToToe)), 2 * (maximumReachLength + ((ClimbingSimulator.constants.HoldDiameter/2) * ClimbingSimulator.constants.SizeFactor)));
			}
			
			if (isLeft()) {
				legReachArcThatRunsAwayFromPelvisCenter = new Arc2D.Float((pelvisCenter.x + ((footLength * ClimbingSimulator.constants.RelativeDistanceFromHeelToBallOfFoot) - (footLength - footLengthHorizontalFromAnkleToToe))) - (maximumReachLength + footLength), pelvisCenter.y - (maximumReachLength + footLength), 2 * (maximumReachLength + footLength), 2 * (maximumReachLength + footLength), 270.0f, ClimbingSimulator.constants.DegreesOfLegReachWidthGainedAsFootMovesAwayFromInseam, Arc2D.PIE);
			} else {
				legReachArcThatRunsAwayFromPelvisCenter = new Arc2D.Float((pelvisCenter.x - ((footLength * ClimbingSimulator.constants.RelativeDistanceFromHeelToBallOfFoot) - (footLength - footLengthHorizontalFromAnkleToToe))) - (maximumReachLength + footLength), pelvisCenter.y - (maximumReachLength + footLength), 2 * (maximumReachLength + footLength), 2 * (maximumReachLength + footLength), 270.0f - ClimbingSimulator.constants.DegreesOfLegReachWidthGainedAsFootMovesAwayFromInseam, ClimbingSimulator.constants.DegreesOfLegReachWidthGainedAsFootMovesAwayFromInseam, Arc2D.PIE);
			}
									
			areaTooHighForInflexibleClimber = new Area(tooHighForInflexibleClimber);
			areaOfLegReachArchThatRisesDistallyAwayFromUpperOuterHipBoxPoint = new Area(legReachArcThatRisesAwayFromTorso);
			areaTooHighForInflexibleClimber.subtract(areaOfLegReachArchThatRisesDistallyAwayFromUpperOuterHipBoxPoint);
			
			areaOnOtherSideOfBody = new Area(otherSideOfBody);
			areaOfLegReachArchThatRunsDistallyAwayFromPelvisCenter = new Area(legReachArcThatRunsAwayFromPelvisCenter);
			areaOnOtherSideOfBody.subtract(areaOfLegReachArchThatRunsDistallyAwayFromPelvisCenter);
			
			totalAreaToSubtract = new Area();
						
			totalAreaToSubtract.add(areaOnOtherSideOfBody);
			totalAreaToSubtract.add(areaTooHighForInflexibleClimber);
			totalAreaToSubtract.add(relevantHipBox);
			
			result.subtract(totalAreaToSubtract);
			
			return result;
		}
		
		@Override
		void moveLimbToPointOverrideable(Point p, boolean connectToWallAfterMakingMove) {
			
			if (wouldMoveGoThroughAreaThatJamsLimb(p)) {
				
				moveLimbToUnjammedWaypointFirst(p, connectToWallAfterMakingMove);
				
				return;
			}
			
			moveLimbToPointBasic(p, connectToWallAfterMakingMove);
		}
		
		abstract Point getCurrentUpperOuterHipBoxPoint();
		abstract Point getProspectiveUpperOuterHipBoxPoint();		
		
		abstract boolean wouldMoveGoThroughAreaThatJamsLimb(Point prospectiveDestinationPoint);
		
		abstract void moveLimbToUnjammedWaypointFirst(Point ultimateDestinationPoint, boolean connectToWallAfterMakingMove);
	}
	
	interface Left {

	}
	
	interface Right {

	}
	
	class LeftArm extends Arm implements Left {
		
		LeftArm() {
		
			limbIdentifier = LimbIdentifier.LEFTARM;
			
			calculateLimbMaxEntensionValues();
									
			isConnectedToSomething = true;
		}

		@Override
		Point getWallConnectionBodyPartPoint() {
			return leftHandCenterPos;
		}

		@Override
		void setWallConnectionBodyPartPoint(Point p) {
			leftHandCenterPos = p;
		}

		@Override
		Point getProximalStartPointForLimb() {
			return leftShoulderSocketPos;
		}

		@Override
		Point getProspectiveProximalStartPointForLimb() {
			return prospectiveLeftShoulderSocketPos;
		}
		
		@Override
		Point getDistalEndpointOfDistalSegment() {
			return leftWristPos;
		}

		@Override
		void updateLimbPoints() {
			
			try {
				updateLeftArmWireframeValues();
			} catch (Exception e) {
				System.out.println("I'm having trouble moving my left arm only");
			}
				
			updateLeftArmDrawingPoints();
		}
	}
	
	class RightArm extends Arm implements Right {
		
		RightArm() {
			
			limbIdentifier = LimbIdentifier.RIGHTARM;
			
			calculateLimbMaxEntensionValues();
			
			isConnectedToSomething = true;
		}

		@Override
		Point getWallConnectionBodyPartPoint() {
			return rightHandCenterPos;
		}

		@Override
		void setWallConnectionBodyPartPoint(Point p) {
			rightHandCenterPos = p;
		}

		@Override
		Point getProximalStartPointForLimb() {
			return rightShoulderSocketPos;
		}

		@Override
		Point getProspectiveProximalStartPointForLimb() {
			return prospectiveRightShoulderSocketPos;
		}
		
		@Override
		Point getDistalEndpointOfDistalSegment() {
			return rightWristPos;
		}

		@Override
		void updateLimbPoints() {
			
			try {
				updateRightArmWireframeValues();
			} catch (Exception e) {
				System.out.println("I'm having trouble moving my right arm only");
			}
				
			updateRightArmDrawingPoints();
		}
	}
	
	class LeftLeg extends Leg implements Left {
		
		LeftLeg() {
			
			limbIdentifier = LimbIdentifier.LEFTLEG;
			
			calculateLimbMaxEntensionValues();
		}

		//shifted because foot stands on top of hold, whereas hand grasps hold
		@Override
		Point getWallConnectionBodyPartPoint() {
			
			Point shiftFromBallOfFootToHoldCenter = new Point();
			
			shiftFromBallOfFootToHoldCenter.x = leftFootHoldContactPoint.x;
			shiftFromBallOfFootToHoldCenter.y = Math.round(leftFootHoldContactPoint.y + (((ClimbingSimulator.constants.HoldDiameter/2) * ClimbingSimulator.constants.SizeFactor) - 1));
			
			return shiftFromBallOfFootToHoldCenter;
		}

		//shifted because foot stands on top of hold, whereas hand grasps hold
		@Override
		void setWallConnectionBodyPartPoint(Point p) {
			
			Point shiftFromHoldCenterToBallOfFoot = new Point();
			
			shiftFromHoldCenterToBallOfFoot.x = p.x;
			shiftFromHoldCenterToBallOfFoot.y = Math.round(p.y - (((ClimbingSimulator.constants.HoldDiameter/2) * ClimbingSimulator.constants.SizeFactor) - 1));
			
			leftFootHoldContactPoint = shiftFromHoldCenterToBallOfFoot;
		}

		@Override
		Point getProximalStartPointForLimb() {
			return leftHipSocketPos;
		}

		@Override
		Point getProspectiveProximalStartPointForLimb() {
			return prospectiveLeftHipSocketPos;
		}
		
		@Override
		Point getDistalEndpointOfDistalSegment() {
			return leftAnklePos;
		}

		@Override
		void updateLimbPoints() {
			
			try {
				updateLeftLegWireframeValues();
			} catch (Exception e) {
				System.out.println("I'm having trouble moving my left leg only");
			}
			
			updateLeftLegDrawingPoints();
		}
		
		//constructs a triangle using the prospective route as a side that faces the torso, then tests whether unjammed point lies within the triangle
		boolean wouldMoveGoThroughAreaThatJamsLimb(Point prospectiveDestinationPoint) {
			
			updateCurrentHipBox();
			updateLeftUnjammedFootWaypoint();
			
			float distanceFromBottomOfFootToHoldCenter = (((ClimbingSimulator.constants.HoldDiameter/2) * ClimbingSimulator.constants.SizeFactor) - 1);
			
			Point prospectiveFootHoldContactPoint = CommonAlgorithms.roundPoint(prospectiveDestinationPoint.x, prospectiveDestinationPoint.y - distanceFromBottomOfFootToHoldCenter);
			
			int otherTrianglePointX = Math.min(leftFootHoldContactPoint.x, prospectiveFootHoldContactPoint.x);
			int otherTrianglePointY = Math.max(leftFootHoldContactPoint.y, prospectiveFootHoldContactPoint.y);
			
			Point otherTrianglePoint = new Point(otherTrianglePointX - 1, otherTrianglePointY + 1);
			
			Path2D.Float testingTrianglePerimeter = new Path2D.Float();
			testingTrianglePerimeter.moveTo(otherTrianglePoint.x, otherTrianglePoint.y);
			testingTrianglePerimeter.lineTo(leftFootHoldContactPoint.x, leftFootHoldContactPoint.y);
			testingTrianglePerimeter.lineTo(prospectiveFootHoldContactPoint.x, prospectiveFootHoldContactPoint.y);
			testingTrianglePerimeter.closePath();
			
			Area testingTriangleArea = new Area(testingTrianglePerimeter);
			
			return testingTriangleArea.contains(leftUnjammedFootWaypoint);
		}
		
		Point getCurrentUpperOuterHipBoxPoint() {
			
			return leftUpperOuterHipBoxPos;
					
		}
		
		Point getProspectiveUpperOuterHipBoxPoint() {
			
			return prospectiveLeftUpperOuterHipBoxPos;
					
		}
		
		void updateLeftUnjammedFootWaypoint() {
		
			leftUnjammedFootWaypoint = CommonAlgorithms.roundPoint((leftShoulderSocketPos.x - ((footLength * ClimbingSimulator.constants.RelativeDistanceFromHeelToBallOfFoot) - (footLength - footLengthHorizontalFromAnkleToToe))) - 1, (beltBucklePos.y + (waistToInseamCentralVerticalDistance + footHeight + (ClimbingSimulator.constants.HoldDiameter * ClimbingSimulator.constants.SizeFactor))) + 1);
		}
		
		void moveLimbToUnjammedWaypointFirst(Point ultimateDestinationPoint, boolean connectToWallAfterMakingMove) {
			
			moveLimbToPointBasic(leftUnjammedFootWaypoint, false);
			moveLimbToPointBasic(ultimateDestinationPoint, connectToWallAfterMakingMove);
		}
	}
		
	class RightLeg extends Leg implements Right {
		
		RightLeg() {
			
			limbIdentifier = LimbIdentifier.RIGHTLEG;
			
			calculateLimbMaxEntensionValues();
		}
		
		//shifted because foot stands on top of hold, whereas hand grasps hold
		@Override
		Point getWallConnectionBodyPartPoint() {
			
			Point shiftFromBallOfFootToHoldCenter = new Point();
			
			shiftFromBallOfFootToHoldCenter.x = rightFootHoldContactPoint.x;
			shiftFromBallOfFootToHoldCenter.y = Math.round(rightFootHoldContactPoint.y + (((ClimbingSimulator.constants.HoldDiameter/2) * ClimbingSimulator.constants.SizeFactor) - 1));
			
			return shiftFromBallOfFootToHoldCenter;
		}

		//shifted because foot stands on top of hold, whereas hand grasps hold
		@Override
		void setWallConnectionBodyPartPoint(Point p) {
			
			Point shiftFromHoldCenterToBallOfFoot = new Point();
			
			shiftFromHoldCenterToBallOfFoot.x = p.x;
			shiftFromHoldCenterToBallOfFoot.y = Math.round(p.y - (((ClimbingSimulator.constants.HoldDiameter/2) * ClimbingSimulator.constants.SizeFactor) - 1));
			
			rightFootHoldContactPoint = shiftFromHoldCenterToBallOfFoot;
		}

		@Override
		Point getProximalStartPointForLimb() {
			return rightHipSocketPos;
		}

		@Override
		Point getProspectiveProximalStartPointForLimb() {
			return prospectiveRightHipSocketPos;
		}
		
		@Override
		Point getDistalEndpointOfDistalSegment() {
			return rightAnklePos;
		}

		@Override
		void updateLimbPoints() {
			
			try {
				updateRightLegWireframeValues();
			} catch (Exception e) {
				System.out.println("I'm having trouble moving my right leg only");
			}
			
			updateRightLegDrawingPoints();
		}
		
		//constructs a triangle using the prospective route as a side that faces the torso, then tests whether unjammed point lies within the triangle
		boolean wouldMoveGoThroughAreaThatJamsLimb(Point prospectiveDestinationPoint) {
			
			updateCurrentHipBox();
			updateRightUnjammedFootWaypoint();
			
			float distanceFromBottomOfFootToHoldCenter = (((ClimbingSimulator.constants.HoldDiameter/2) * ClimbingSimulator.constants.SizeFactor) - 1);
			
			Point prospectiveFootHoldContactPoint = CommonAlgorithms.roundPoint(prospectiveDestinationPoint.x, prospectiveDestinationPoint.y - distanceFromBottomOfFootToHoldCenter);
			
			int otherTrianglePointX = Math.max(rightFootHoldContactPoint.x, prospectiveFootHoldContactPoint.x);
			int otherTrianglePointY = Math.max(rightFootHoldContactPoint.y, prospectiveFootHoldContactPoint.y);
			
			Point otherTrianglePoint = new Point(otherTrianglePointX + 1, otherTrianglePointY + 1);
			
			Path2D.Float testingTrianglePerimeter = new Path2D.Float();
			testingTrianglePerimeter.moveTo(otherTrianglePoint.x, otherTrianglePoint.y);
			testingTrianglePerimeter.lineTo(rightFootHoldContactPoint.x, rightFootHoldContactPoint.y);
			testingTrianglePerimeter.lineTo(prospectiveFootHoldContactPoint.x, prospectiveFootHoldContactPoint.y);
			testingTrianglePerimeter.closePath();
			
			Area testingTriangleArea = new Area(testingTrianglePerimeter);

			return testingTriangleArea.contains(rightUnjammedFootWaypoint);
		}
		
		Point getCurrentUpperOuterHipBoxPoint() {
			
			return rightUpperOuterHipBoxPos;
					
		}
		
		Point getProspectiveUpperOuterHipBoxPoint() {
			
			return prospectiveRightUpperOuterHipBoxPos;
					
		}
		
		void updateRightUnjammedFootWaypoint() {
			
			rightUnjammedFootWaypoint = CommonAlgorithms.roundPoint((rightShoulderSocketPos.x + ((footLength * ClimbingSimulator.constants.RelativeDistanceFromHeelToBallOfFoot) - (footLength - footLengthHorizontalFromAnkleToToe))) + 1, (beltBucklePos.y + (waistToInseamCentralVerticalDistance + footHeight + (ClimbingSimulator.constants.HoldDiameter * ClimbingSimulator.constants.SizeFactor))) + 1);
		}
		
		void moveLimbToUnjammedWaypointFirst(Point ultimateDestinationPoint, boolean connectToWallAfterMakingMove) {
			
			moveLimbToPointBasic(rightUnjammedFootWaypoint, false);
			moveLimbToPointBasic(ultimateDestinationPoint, connectToWallAfterMakingMove);
		}
	}
	
	class LimbsCoordinator<T extends Limb> extends ArrayList<T> {

		private static final long serialVersionUID = 1L;
		
		Point leftShoulderDestination;
		
		int connectionsCount;
		
		boolean thereIsADisconnectedLimb;
		int disconnectedLimb;
		
		boolean thereIsALimbThatNeedsToMoveOrDisconnect;
		int limbThatNeedsToMoveOrDisconnect;
								
		@SuppressWarnings("unchecked")
		LimbsCoordinator () {

			add((T) leftArm);
			add((T) rightArm);
			add((T) leftLeg);
			add((T) rightLeg);
			
			hasReachedSeeminglyImpossiblePosition = false;
		}
		
		void establishLegs() {
			
			for (int i = 0; i < size(); i++) {
				
				if(!get(i).isArm()) {
					
					get(i).isConnectedToSomething = true;
	
				}
			}
		}
		
		void moveEachLimbIntoItsStartingPosition() {
			
			updateDestinationPointForBeltBuckle();
													
			updateDesiredRadialDirectionOfTravel();
			
			for (int i = 0; i < size(); i++) {
									
				get(i).getSetToClimb();
				
			}
			
			updateWallConnectionStatuses();
		}
		
		void updateWallConnectionStatuses() {
			
			connectionsCount = 0;
			
			for (int i = 0; i < size(); i++) {
				
				if (get(i).isConnectedToSomething) {
					
					connectionsCount++;
					
				} else {
					
					thereIsADisconnectedLimb = true;
					disconnectedLimb = i;

				}
			}
			
			if (connectionsCount == 4) {
				
				thereIsADisconnectedLimb = false;
				disconnectedLimb = -1;
			}
			
			if (!iHaveLeftGroundAndConnectedToWallWithAllFourLimbs) {
				
				checkWhetherIHaveLeftGroundAndConnectedToWallWithAllFourLimbs();
			}
		}
		
		void checkWhetherIHaveLeftGroundAndConnectedToWallWithAllFourLimbs() {
			
			int numberOfLimbsThatHaveMadeFirstConnectionToTheWall = 0;
			
			for (int i = 0; i < size(); i++) {
				
				if (get(i).hasMadeFirstConnectionToTheWall) {
					numberOfLimbsThatHaveMadeFirstConnectionToTheWall++;
				}
			}
			
			if (numberOfLimbsThatHaveMadeFirstConnectionToTheWall == 4) {
				iHaveLeftGroundAndConnectedToWallWithAllFourLimbs = true;
			}
		}
		
		void moveClimberAlongShortestPathToTop() {

			updateDestinationPointForBeltBuckle();
			
			if (climberHasToStopClimbingBecauseThereAreNoAttemptsRemaining()) {
				return;			
			}
			
			updateDesiredRadialDirectionOfTravel();
			
			updateWallConnectionStatuses();
			
			updateWhetherClimberHasMovedPastASeeminglyImpossiblePositionAndConnectedWithAllFourLimbs();
			
			if (thereIsADisconnectedLimb) {
					
				try {
					
					moveBothTorsoAndDisconnectedLimbAsFarAsPossibleWhileConstantlyTryingToConnectDisconnectedLimb();
					
					return;
					
				} catch (ImpossibleRouteException e) {

					iAmTryingToClimb = false;
					
					return;
					
				}
					
			} else {
			
				moveOnlyTorsoAsFarAsPossible();
			
				if (thereIsALimbThatNeedsToMoveOrDisconnect) {
					
					tryToMoveLimbThatNeedsToBeMovedByUsingAReachableHoldThatHasNotBeenUsedByLimbDuringEitherAscentOrBacktrackingAndIsAsFarInDesiredDirectionOfTravelAsPossible();
					
					if (!thereIsALimbThatNeedsToMoveOrDisconnect) {
						
						return;
					
					} else {
						
						disconnectAndMoveToTheReadyPositionTheLimbThatNeedsToMoveOrDisconnect();
						
						return;
						
					}
				}	
			}
		}
		
		void updateDestinationPointForBeltBuckle() {
			
			if (!hasReachedSeeminglyImpossiblePosition) {
				
				updateDestinationPointForBeltBuckleUsingPathIterator();
				
			} else if (hasReachedSeeminglyImpossiblePosition && hasReturnedSafely) {
				
				averagePathIndexIterator = null;
				
				updateDestinationPointForBeltBuckleUsingPathIterator();
				
				hasReachedSeeminglyImpossiblePosition = false;
				
				difficultMoveTracker++;
				
			} else {
				
				desiredDestinationForBeltBuckle = mostRecentFourConnectionsPoint;
				
				difficultMoveTracker++;
				
				iAmBacktracking = true;
				
			}
		}
		
		boolean climberHasToStopClimbingBecauseThereAreNoAttemptsRemaining() {
			
			if (difficultMoveTracker > 5) {
				
				iAmTryingToClimb = false;
				return true;
				
			} else {
				
				return false;
				
			}
		}
		
		void updateWhetherClimberHasMovedPastASeeminglyImpossiblePositionAndConnectedWithAllFourLimbs() {
			
			if (hasReachedDestinationPointThatWasBeyondSeeminglyImpossiblePosition && !thereIsADisconnectedLimb) {
				
				hasReachedSeeminglyImpossiblePosition = false;
				difficultMoveTracker = 0;
				indexOfAveragePathDestinationPointAtTheSeeminglyImpossiblePosition = Integer.MAX_VALUE;
				
			}	
		}
		
		void moveOnlyTorsoAsFarAsPossible() {
			
			calculateLeftShoulderDestinationBasedOnBeltBuckleDestinationPoint();
							
			ArrayList<float[]> waypointsFormattedAsFloat = calculateWaypointFloatCoordinates(leftShoulderSocketPos, leftShoulderDestination, ClimbingSimulator.constants.TorsoMovementIncrement * ClimbingSimulator.constants.SizeFactor);

			Point waypoint = new Point();
			
			for (float[] waypointCoordinatesInFloat : waypointsFormattedAsFloat) {
				
				waypoint = CommonAlgorithms.roundPoint(waypointCoordinatesInFloat[0], waypointCoordinatesInFloat[1]);
				
				try {
					
					prospectiveLeftShoulderSocketPos = waypoint;
					testAllProspectiveProximalLimbSegmentOrientationsAndLimbConnectionReachableAreas();
					
				} catch (LimbMustMoveFirstException e) {
					
					LimbIdentifier limbIdentifierPassedByException = e.getLimbIdentifier();
					
					if (limbIdentifierPassedByException == LimbIdentifier.LEFTARM) {
						limbThatNeedsToMoveOrDisconnect = 0;
					} else if (limbIdentifierPassedByException == LimbIdentifier.RIGHTARM) {
						limbThatNeedsToMoveOrDisconnect = 1;
					} else if (limbIdentifierPassedByException == LimbIdentifier.LEFTLEG) {
						limbThatNeedsToMoveOrDisconnect = 2;
					} else if (limbIdentifierPassedByException == LimbIdentifier.RIGHTLEG) {
						limbThatNeedsToMoveOrDisconnect = 3;
					}
					
					thereIsALimbThatNeedsToMoveOrDisconnect = true;
					
					return;
				}
				
				leftShoulderSocketPos = waypoint;
												
				updateAllWireframeAndDrawingPointsInProperSequence();
				updateLimbUsageRecordForReachableHolds();
				
				pauseForAnimationTiming();
			}
		}
		
		void moveBothTorsoAndDisconnectedLimbAsFarAsPossibleWhileConstantlyTryingToConnectDisconnectedLimb() throws ImpossibleRouteException {
			
			calculateLeftShoulderDestinationBasedOnBeltBuckleDestinationPoint();
			
			int xOffsetBetweenLeftShoulderSocketPosAndDisconnectedLimb = leftShoulderSocketPos.x - get(disconnectedLimb).getWallConnectionBodyPartPoint().x;
			int yOffsetBetweenLeftShoulderSocketPosAndDisconnectedLimb = leftShoulderSocketPos.y - get(disconnectedLimb).getWallConnectionBodyPartPoint().y;
			
			Point destinationOfWallConnectionPointForDisconnectedLimb = new Point(leftShoulderDestination.x - xOffsetBetweenLeftShoulderSocketPosAndDisconnectedLimb, leftShoulderDestination.y - yOffsetBetweenLeftShoulderSocketPosAndDisconnectedLimb);
	
			ArrayList<float[]> torsoMovementWaypointsFormattedAsFloat = calculateWaypointFloatCoordinates(leftShoulderSocketPos, leftShoulderDestination, ClimbingSimulator.constants.TorsoMovementIncrement * ClimbingSimulator.constants.SizeFactor);
			ArrayList<float[]> disconnectedLimbMovementWaypointsFormattedAsFloat = calculateWaypointFloatCoordinates(get(disconnectedLimb).getWallConnectionBodyPartPoint(), destinationOfWallConnectionPointForDisconnectedLimb, ClimbingSimulator.constants.TorsoMovementIncrement * ClimbingSimulator.constants.SizeFactor);
	
			Point torsoWaypoint = new Point();
			Point disconnectedLimbWaypoint = new Point();
			
			Point previousDisconnectedAndReadiedLimbWallConnectionBodyPartPoint = get(disconnectedLimb).getWallConnectionBodyPartPoint();
			
			for (int i = 0; i < torsoMovementWaypointsFormattedAsFloat.size(); i++) {
				
				torsoWaypoint = CommonAlgorithms.roundPoint(torsoMovementWaypointsFormattedAsFloat.get(i)[0], torsoMovementWaypointsFormattedAsFloat.get(i)[1]);
				disconnectedLimbWaypoint = CommonAlgorithms.roundPoint(disconnectedLimbMovementWaypointsFormattedAsFloat.get(i)[0], disconnectedLimbMovementWaypointsFormattedAsFloat.get(i)[1]);
			
				try {
					
					prospectiveLeftShoulderSocketPos = torsoWaypoint;
					testAllProspectiveProximalLimbSegmentOrientationsAndLimbConnectionReachableAreas();
					
				} catch (LimbMustMoveFirstException e) {
	
					LimbIdentifier limbIdentifierPassedByException = e.getLimbIdentifier();
					
					if (limbIdentifierPassedByException == LimbIdentifier.LEFTARM) {
						limbThatNeedsToMoveOrDisconnect = 0;
					} else if (limbIdentifierPassedByException == LimbIdentifier.RIGHTARM) {
						limbThatNeedsToMoveOrDisconnect = 1;
					} else if (limbIdentifierPassedByException == LimbIdentifier.LEFTLEG) {
						limbThatNeedsToMoveOrDisconnect = 2;
					} else if (limbIdentifierPassedByException == LimbIdentifier.RIGHTLEG) {
						limbThatNeedsToMoveOrDisconnect = 3;
					}
					
					hasReachedSeeminglyImpossiblePosition = true;
					indexOfAveragePathDestinationPointAtTheSeeminglyImpossiblePosition = averagePathIndexIterator;
					hasReachedDestinationPointThatWasBeyondSeeminglyImpossiblePosition = false;
					hasReturnedSafely = false;

					if (iAmBacktracking) {
						
						throw new ImpossibleRouteException();
						
					}
					
					return;
				}
				
				leftShoulderSocketPos = torsoWaypoint;
				get(disconnectedLimb).setWallConnectionBodyPartPoint(disconnectedLimbWaypoint);
								
				updateAllWireframeAndDrawingPointsInProperSequence();
				updateLimbUsageRecordForReachableHolds();
				
				pauseForAnimationTiming();
			
				if (disconnectedLimbDoesHaveOptions()) {
							
					connectDisconnectedLimbToTheReachableHoldThatIsFurthestInDesiredRadialDirectionOfTravelAmongAllReachableHolds();
					
					break;
					
				} else if (CommonAlgorithms.findDistance(previousDisconnectedAndReadiedLimbWallConnectionBodyPartPoint, disconnectedLimbWaypoint) > 1) {
					
					get(disconnectedLimb).limbHasMovedThroughAnAreaWhereItHadNoOptions = true;
				}
			}
			
			if (hasReachedSeeminglyImpossiblePosition) {
				
				if (beltBucklePos.x == mostRecentFourConnectionsPoint.x && beltBucklePos.y == mostRecentFourConnectionsPoint.y) {
									
					hasReturnedSafely = true;
				}
			}
		}

		void tryToMoveLimbThatNeedsToBeMovedByUsingAReachableHoldThatHasNotBeenUsedByLimbDuringEitherAscentOrBacktrackingAndIsAsFarInDesiredDirectionOfTravelAsPossible(){
			
			if (get(limbThatNeedsToMoveOrDisconnect).receiveARequestToMoveLimbToTheHoldThatIsFurthestInDesiredDirectionOfTravelAmongReachableHoldsOtherThanHoldsThatHaveBeenUsedDuringEitherAscentOrBacktrackingAndConfirmIfAMoveWasMade()) {
				
				thereIsALimbThatNeedsToMoveOrDisconnect = false;
				
			}
		}
		
		void calculateLeftShoulderDestinationBasedOnBeltBuckleDestinationPoint() {
			
			leftShoulderDestination = CommonAlgorithms.roundPoint(desiredDestinationForBeltBuckle.x - (shoulderSocketSpan/2), (desiredDestinationForBeltBuckle.y - (torsoHeightFromTopOfTrapsToWaist - (distanceFromNeckBaseAtShoulderSocketLevelToBottomOfHeadCircle * ClimbingSimulator.constants.FactorForDeterminingHeightOfTrapsAgainstSideOfNeckDeterminedAsAFractionOfTheVerticalDistanceFromShoulderLevelToTheBottomOfTheHead))));
		}
		
		void testEachLimbForViolationOfReachableAreaUponProspectiveTorsoMove() throws LimbMustMoveFirstException {
			
			for (int i = 0; i < size(); i++) {
				
				if (i != disconnectedLimb) {
					
					if (!get(i).limbCanContinueToUseCurrentHoldIfTorsoMakesProspectiveMove()) {
						
						get(i).limbHadToAbandonHoldBecauseOfAViolationOfReachableArea = true;
						
						throw new LimbMustMoveFirstException(get(i).limbIdentifier);
						
					}
				}
			}
		}
		
		void disconnectAndMoveToTheReadyPositionTheLimbThatNeedsToMoveOrDisconnect() {
			
			mostRecentFourConnectionsPoint = (Point) beltBucklePos.clone();

			advanceAsMuchAsPossibleAllLimbsThatArentGoingToBeDisconnected();
			
			disconnectAndReadyALimb();
			
		}
				
		void advanceAsMuchAsPossibleAllLimbsThatArentGoingToBeDisconnected() {
			
			for (int i = 0; i < size(); i++) {
				
				if (i != limbThatNeedsToMoveOrDisconnect) {
						
					get(i).receiveARequestToAdvanceLimbFromCurrentHoldToAReachableHoldThatIsMoreExtremeInDesiredDirectionOfTravelThanIsCurrentHoldAndConfirmIfAMoveWasMade();
				}
			}
		}
		
		void disconnectAndReadyALimb () {
			
			if (connectionsCount == 4) {
				
				disconnectedLimb = limbThatNeedsToMoveOrDisconnect;
				
				get(limbThatNeedsToMoveOrDisconnect).moveToReadyPoint();
				
				thereIsALimbThatNeedsToMoveOrDisconnect = false;
				limbThatNeedsToMoveOrDisconnect = -1;
				
				
			} else {
				
				System.out.println("I can't disconnect a limb if there aren't four connections at the time");
			}
		}
		
		boolean disconnectedLimbDoesHaveOptions() {
			
			get(disconnectedLimb).updateReachableHoldPoints();
			
			if (get(disconnectedLimb).allReachableHoldPoints.size() > 0 && get(disconnectedLimb).limbHadToAbandonHoldBecauseOfAViolationOfReachableArea && get(disconnectedLimb).limbHasMovedThroughAnAreaWhereItHadNoOptions) {
				
				return true;
				
			}
			
			ArrayList<Point> filteredPoints = new ArrayList<>();
						
			if (!iAmBacktracking) {
					
				for (Point p : get(disconnectedLimb).allReachableHoldPoints) {
					
					int indexOfHoldAtPointPWithinFinalizedSetOfUserEnteredHolds = finalizedSetOfUserEnteredHolds.indexOf(p);
					
					if (!finalizedSetOfUserEnteredHolds.get(indexOfHoldAtPointPWithinFinalizedSetOfUserEnteredHolds).limbsThatHaveUsedHoldDuringAscent.contains(get(disconnectedLimb).limbIdentifier)) {
						
						filteredPoints.add(p);
						
					}
				}
				
			} else {
			
				for (Point p : get(disconnectedLimb).allReachableHoldPoints) {
					
					int indexOfHoldAtPointPWithinFinalizedSetOfUserEnteredHolds = finalizedSetOfUserEnteredHolds.indexOf(p);
					
					if (!finalizedSetOfUserEnteredHolds.get(indexOfHoldAtPointPWithinFinalizedSetOfUserEnteredHolds).limbsThatHaveUsedHoldDuringBacktrackingSequence.contains(get(disconnectedLimb).limbIdentifier)) {
						
						filteredPoints.add(p);
						
					}
				}
			}
				
			if (filteredPoints.size() > 0) {
				
				return true;
				
			} else {
				
				return false;
				
			}
		}
		
		void connectDisconnectedLimbToTheReachableHoldThatIsFurthestInDesiredRadialDirectionOfTravelAmongAllReachableHolds() {
			
			get(disconnectedLimb).moveLimbToTheReachableHoldThatIsFurthestInDesiredDirectionOfTravelAmongAllReachableHolds();
			
			thereIsADisconnectedLimb = false;
			disconnectedLimb = -1;
		}
		
		void updateAllCurrentReachableAreas() {
			
			for (int i = 0; i < size(); i++) {
				get(i).updateReachableAreaForCurrentPosition();
			}
		}
	}
	
	//I. METHODS RELATED TO SEQUENCING THE STEPS IN THE CLIMBING PROCESS
	
	@SuppressWarnings("unchecked")
	void receiveMessageThatTheWallIsReadyForClimber(ClimbingWall cw) {
		
		finalizedSetOfUserEnteredHolds = (ClimbingWall.SetOfHolds<ClimbingWall.ClimbingHold>) cw.setOfUserEnteredHolds.clone();
		
		findMostEfficientPathsUpTheWallThroughTheHoldPoints();
		
		findThePathThroughEachSecondIterationAveragePointOfHoldsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections();
		bookendThePathThroughEachSecondIterationAveragePointOfHoldsWithinMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections();
			
		initiallyOrientClimber();
		
		iAmOnTheWall = true;
		iAmTryingToClimb = true;
		
		cw.receiveAndProcessNoticeThatClimberIsBeginningClimbingProcess();
		
		executeStepsForClimbingTheWall();
	}
	
	void initiallyOrientClimber() {
		
		Point lowestPointInAveragePath = new Point(pathThroughEachSecondIterationAveragePointOfTheHoldPointsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections.get(0).x, ClimbingWall.heightOfWall);
		
		Shape startingAreaRectangle = new Rectangle2D.Float(lowestPointInAveragePath.x - widestPossibleExtensionFromHandToHand/2, ClimbingWall.heightOfWall - highestPossibleExtensionFromFootToHand, widestPossibleExtensionFromHandToHand, highestPossibleExtensionFromFootToHand);
		Area startingArea = new Area(startingAreaRectangle);
		
		Point averagePointInStartingArea = CommonAlgorithms.averageThePointsInAnArea(finalizedSetOfUserEnteredHolds, startingArea);

		anchorPoint = new Point(averagePointInStartingArea.x, 0);
		
		setLoweringPosition();
	}	
	
	void executeStepsForClimbingTheWall() {
		
		ClimbingMovesProcessor movesProcessor = new ClimbingMovesProcessor();
		movesProcessor.execute();
	}
	
	class ClimbingMovesProcessor extends SwingWorker<String,String> {
		
		protected String doInBackground() {
			
			String dummyString = "";
			
			try {
				
				lowerClimber();
				prepareToClimb();
				ascendWall();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return dummyString;
		}
	}
		
	void updateAllWireframeAndDrawingPointsInProperSequence() {
		
		updateTorsoPoints();
		updateAllCriticalPoints();
	}
	
	void updateAllCriticalPoints() {
		
		updateAllLimbWireframeValues();
		updateAllDrawingPoints();	
	}
	
	void setLoweringPosition(){
		
		leftShoulderSocketPos = CommonAlgorithms.roundPoint((anchorPoint.x - (shoulderSocketSpan/2)), ((-1) * (footHeight + calfLength + femurLength + torsoHeightFromTopOfTrapsToWaist)));
		updateTorsoPoints();
				
		leftHandCenterPos = CommonAlgorithms.roundPoint(anchorPoint.x, (leftShoulderSocketPos.y - (50 * ClimbingSimulator.constants.SizeFactor)));
		rightHandCenterPos = CommonAlgorithms.roundPoint(anchorPoint.x, (leftShoulderSocketPos.y - (20 * ClimbingSimulator.constants.SizeFactor)));
		leftFootHoldContactPoint = CommonAlgorithms.roundPoint((pelvisCenterPos.x - (30 * ClimbingSimulator.constants.SizeFactor)), (pelvisCenterPos.y + (66 * ClimbingSimulator.constants.SizeFactor)));
		rightFootHoldContactPoint = CommonAlgorithms.roundPoint((pelvisCenterPos.x + (30 * ClimbingSimulator.constants.SizeFactor)), (pelvisCenterPos.y + (66 * ClimbingSimulator.constants.SizeFactor)));
		
		updateAllCriticalPoints();
	}
	
	void lowerClimber() {
		
		int tD = Math.round(ClimbingSimulator.constants.LoweringTickDistance * ClimbingSimulator.constants.SizeFactor);
		
		while(leftFootHoldContactPoint.y < ClimbingWall.heightOfWall) {
			
			leftShoulderSocketPos.y += tD;
			leftHandCenterPos.y += tD;
			rightHandCenterPos.y += tD;
			leftFootHoldContactPoint.y += tD;
			rightFootHoldContactPoint.y += tD;
			
			try {
				updateAllWireframeAndDrawingPointsInProperSequence();
			} catch (Exception e) {
				System.out.println("I'm having trouble lowering");
			}
			
			pauseForAnimationTiming();
		}

		limbsCoordinator.establishLegs();
	}
	
	void prepareToClimb () {

		difficultMoveTracker = 0;
		
		limbsCoordinator.moveEachLimbIntoItsStartingPosition();
	}

	void ascendWall() {

		while (cannotTopOut() && iAmTryingToClimb) {
			
			limbsCoordinator.moveClimberAlongShortestPathToTop();
	
		}

		if (iAmTryingToClimb) {
		
			topOut();
		
		}
	}	

	boolean cannotTopOut() {
		
		return !(hasGottenWithinReachOfTop() && ((limbsCoordinator.connectionsCount == 4) || !leftArm.isConnectedToSomething));
		
	}
		
	void topOut() {
		
		Point leftHandTopout = new Point (leftShoulderSocketPos.x, 0);
		Point rightHandTopout = new Point (rightShoulderSocketPos.x, 0);
		
		leftArm.moveLimbToPointOverrideable(leftHandTopout, false);
		leftArm.isConnectedToSomething = true;
		rightArm.moveLimbToPointOverrideable(rightHandTopout, false);
		rightArm.isConnectedToSomething = true;
		
		int tD = Math.round(ClimbingSimulator.constants.ToppingOutDistance * ClimbingSimulator.constants.SizeFactor);
		
		while (beltBucklePos.y > 0) {
			
			leftShoulderSocketPos.y -= tD;

			leftLeg.updateOverallExtensionRatio();
			rightLeg.updateOverallExtensionRatio();
			
			if (leftLeg.overallExtensionRatio > ClimbingSimulator.constants.MaximumOverallExtension) {
				
				leftFootHoldContactPoint.y -= tD;
				
				if(leftAnklePos.x != leftHipSocketPos.x) {
					
					leftFootHoldContactPoint.x += ((leftHipSocketPos.x - leftAnklePos.x)/Math.abs(leftHipSocketPos.x - leftAnklePos.x));
				}
			}
			
			if (rightLeg.overallExtensionRatio > ClimbingSimulator.constants.MaximumOverallExtension) {
				
				rightFootHoldContactPoint.y -= tD;
				
				if(rightAnklePos.x != rightHipSocketPos.x) {
					
					rightFootHoldContactPoint.x += ((rightHipSocketPos.x - rightAnklePos.x)/Math.abs(rightHipSocketPos.x - rightAnklePos.x));
				}
			}
			
			try {
				updateAllWireframeAndDrawingPointsInProperSequence();
			} catch (Exception e) {
				System.out.println("I'm having trouble topping out");
			}
			
			pauseForAnimationTiming();
		}
		
		leftLeg.moveLimbToPointOverrideable(new Point(Math.round(leftShoulderSocketPos.x - (30 * ClimbingSimulator.constants.SizeFactor)), 0), false);
		leftLeg.isConnectedToSomething = true;
		
		while(rightLeg.getWallConnectionBodyPartPoint().y > 0) {
			
			leftShoulderSocketPos.y -= tD;
			rightFootHoldContactPoint.y -= tD;
			
			updateAllWireframeAndDrawingPointsInProperSequence();
			
			pauseForAnimationTiming();
		}
		
		iAmOnTheWall = false;
	}
		
	boolean hasGottenWithinReachOfTop() {
		
		return (leftShoulderSocketPos.y < leftArm.maximumReachLength);
	}
	
	public boolean isClimberOnTheWall() {
		
		return iAmOnTheWall;
	}
	
	public boolean isClimberTryingToClimb() {
		
		return iAmTryingToClimb;
	}
	
	//II. GENERAL METHODS TO CALCULATE ANGLES/SLOPE, DISTANCES, COORDINATES, AND RELATIVE ORIENTATION 
	
	//calculate the orientation of a limb's endpoints (e.g., shoulder and wrist) as a radial angle pivoting on proximal endpoint (shoulder)
	float calculateLimbOrSegmentEndpointOrientation(Point proximal, Point distal) {
		return CommonAlgorithms.findSlopeAsRadians(proximal, distal);
	}
	
	//calculate distance between endpoints of limb's two major segments (e.g., shoulder and wrist)
	float calculateDistanceBetweenLimbEndpoints(Point proximal, Point distal) {
		return (float) CommonAlgorithms.findDistance(proximal, distal);
	}
			
	//result will be between pi radians (180 degrees) and -pi radians (-180 degrees)
	float calculateProximalLimbSegmentOrientation (float proximalSegmentLength, float distalSegmentLength, float distanceBetweenEndpointsOfSegments, float radialSlopeCreatedByEndpoints, boolean hingeFormedByLimbSegmentsIsAboveJoint, boolean leftSide) throws LawOfCosinesException {
		
		float proximalAngleInJointTriangle = (float) CommonAlgorithms.lawOfCosines(proximalSegmentLength, distanceBetweenEndpointsOfSegments, distalSegmentLength);

		if (proximalAngleInJointTriangle != proximalAngleInJointTriangle) {
			
			throw new LawOfCosinesException();
		}
		
		//variable adjuster used to account for joint orientation and side of body
		float adjuster = 1.0f;
		
		if (!hingeFormedByLimbSegmentsIsAboveJoint) {adjuster = adjuster * (-1.0f);}
		if (!leftSide) {adjuster = adjuster * (-1.0f);}
		
		float result = radialSlopeCreatedByEndpoints + (proximalAngleInJointTriangle * adjuster);
		if (result > Pi) {result = result - (2*Pi);}
		if (result < -Pi) {result = result + (2*Pi);}
		return result;	
	}
	
	Point calculateMidlimbJointPoint (Point proximalEndpoint, float proximalSegmentLength, float proximalLimbSegmentOrientation) {
		return CommonAlgorithms.shiftBySlopeAsRadians(proximalEndpoint, proximalSegmentLength, proximalLimbSegmentOrientation, true);
	}
	
	//III. METHODS RELATED TO SPECIFIC BODY PART POSITIONING
	
	//update torso points (including drawing points) based on location of left shoulder
	void updateTorsoPoints() {
		
		rightShoulderSocketPos.x = Math.round(leftShoulderSocketPos.x + shoulderSocketSpan);
		rightShoulderSocketPos.y = leftShoulderSocketPos.y;
		
		leftWaistDrawingPoint.x = Math.round(leftShoulderSocketPos.x + (shoulderSocketSpan * ClimbingSimulator.constants.LatHorizontalCutInWidthAsRatioOfOverallShoulderSocket));
		leftWaistDrawingPoint.y = Math.round(leftShoulderSocketPos.y + (torsoHeightFromTopOfTrapsToWaist - (distanceFromNeckBaseAtShoulderSocketLevelToBottomOfHeadCircle * ClimbingSimulator.constants.FactorForDeterminingHeightOfTrapsAgainstSideOfNeckDeterminedAsAFractionOfTheVerticalDistanceFromShoulderLevelToTheBottomOfTheHead)));
		rightWaistDrawingPoint.x = Math.round(rightShoulderSocketPos.x - (shoulderSocketSpan * ClimbingSimulator.constants.LatHorizontalCutInWidthAsRatioOfOverallShoulderSocket));
		rightWaistDrawingPoint.y = Math.round(rightShoulderSocketPos.y + (torsoHeightFromTopOfTrapsToWaist - (distanceFromNeckBaseAtShoulderSocketLevelToBottomOfHeadCircle * ClimbingSimulator.constants.FactorForDeterminingHeightOfTrapsAgainstSideOfNeckDeterminedAsAFractionOfTheVerticalDistanceFromShoulderLevelToTheBottomOfTheHead)));
		
		leftLatTorsoInsertionPoint.x = leftWaistDrawingPoint.x;
		leftLatTorsoInsertionPoint.y = Math.round(leftWaistDrawingPoint.y - (torsoHeightFromTopOfTrapsToWaist * ClimbingSimulator.constants.LatInsertionPointRatioOfOverallTorsoHeightStartingFromWaist));
		rightLatTorsoInsertionPoint.x = rightWaistDrawingPoint.x;
		rightLatTorsoInsertionPoint.y = Math.round(rightWaistDrawingPoint.y - (torsoHeightFromTopOfTrapsToWaist * ClimbingSimulator.constants.LatInsertionPointRatioOfOverallTorsoHeightStartingFromWaist));
		
		leftTrapNeckIntersectionPoint.x = Math.round(leftShoulderSocketPos.x + (shoulderSocketSpan/2 - neckThickness/2));
		leftTrapNeckIntersectionPoint.y = Math.round(leftShoulderSocketPos.y - (distanceFromNeckBaseAtShoulderSocketLevelToBottomOfHeadCircle * ClimbingSimulator.constants.FactorForDeterminingHeightOfTrapsAgainstSideOfNeckDeterminedAsAFractionOfTheVerticalDistanceFromShoulderLevelToTheBottomOfTheHead));
		rightTrapNeckIntersectionPoint.x = Math.round(rightShoulderSocketPos.x - (shoulderSocketSpan/2 - neckThickness/2));
		rightTrapNeckIntersectionPoint.y = Math.round(rightShoulderSocketPos.y - (distanceFromNeckBaseAtShoulderSocketLevelToBottomOfHeadCircle * ClimbingSimulator.constants.FactorForDeterminingHeightOfTrapsAgainstSideOfNeckDeterminedAsAFractionOfTheVerticalDistanceFromShoulderLevelToTheBottomOfTheHead));
		
		beltBucklePos = CommonAlgorithms.findMidpoint(leftWaistDrawingPoint, rightWaistDrawingPoint);
		pelvisCenterPos.x = beltBucklePos.x;
		pelvisCenterPos.y = Math.round(beltBucklePos.y + beltBuckleToCenterPelvisDistance);
		leftHipSocketPos.x = Math.round(pelvisCenterPos.x - hipSocketSpan/2);
		leftHipSocketPos.y = pelvisCenterPos.y;
		rightHipSocketPos.x = Math.round(pelvisCenterPos.x + hipSocketSpan/2);
		rightHipSocketPos.y = pelvisCenterPos.y;
		
		inseam.x = beltBucklePos.x;
		inseam.y = Math.round(beltBucklePos.y + waistToInseamCentralVerticalDistance);
		
		leftOuterPelvisHinge.x = leftWaistDrawingPoint.x;
		leftOuterPelvisHinge.y = Math.round(leftWaistDrawingPoint.y + hipToOuterPelvisCreaseVerticalDistance);
		leftInnerPelvisHinge.x = Math.round(beltBucklePos.x - inseamToInnerPelvisCreaseHorizontalDistance);
		leftInnerPelvisHinge.y = inseam.y;

		rightOuterPelvisHinge.x = rightWaistDrawingPoint.x;
		rightOuterPelvisHinge.y = Math.round(rightWaistDrawingPoint.y + hipToOuterPelvisCreaseVerticalDistance);
		rightInnerPelvisHinge.x = Math.round(beltBucklePos.x + inseamToInnerPelvisCreaseHorizontalDistance);
		rightInnerPelvisHinge.y = inseam.y;
		
		torsoCOG.x = beltBucklePos.x;
		torsoCOG.y = Math.round(leftTrapNeckIntersectionPoint.y + (torsoHeightFromTopOfTrapsToWaist * ClimbingSimulator.constants.DistanceFromTopOfTrapsToTorsoCOGComparedToDistanceFromTopOfTrapsToWaistRatio));
	}

	void updateCurrentHipBox() {
		
		leftUpperOuterHipBoxPos = CommonAlgorithms.roundPoint(leftShoulderSocketPos.x - (footLength + (upperArmWidth/2)), torsoCOG.y);
		rightUpperOuterHipBoxPos = CommonAlgorithms.roundPoint(leftShoulderSocketPos.x + shoulderSocketSpan + (footLength + (upperArmWidth/2)), torsoCOG.y);
		Point lowerRightHipBoxVertex = CommonAlgorithms.roundPoint(leftShoulderSocketPos.x + shoulderSocketSpan + ((footLength * ClimbingSimulator.constants.RelativeDistanceFromHeelToBallOfFoot) - (footLength - footLengthHorizontalFromAnkleToToe)), beltBucklePos.y + waistToInseamCentralVerticalDistance + footHeight + (ClimbingSimulator.constants.HoldDiameter * ClimbingSimulator.constants.SizeFactor));
		Point lowerLeftHipBoxVertex = CommonAlgorithms.roundPoint(leftShoulderSocketPos.x - ((footLength * ClimbingSimulator.constants.RelativeDistanceFromHeelToBallOfFoot) - (footLength - footLengthHorizontalFromAnkleToToe)), beltBucklePos.y + waistToInseamCentralVerticalDistance + footHeight + (ClimbingSimulator.constants.HoldDiameter * ClimbingSimulator.constants.SizeFactor));
		
		Path2D.Float hipBoxShape = new Path2D.Float();
		
		hipBoxShape.moveTo(leftUpperOuterHipBoxPos.x, leftUpperOuterHipBoxPos.y);
		hipBoxShape.lineTo(rightUpperOuterHipBoxPos.x, rightUpperOuterHipBoxPos.y);
		hipBoxShape.lineTo(lowerRightHipBoxVertex.x, lowerRightHipBoxVertex.y);
		hipBoxShape.lineTo(lowerLeftHipBoxVertex.x, lowerLeftHipBoxVertex.y);
		hipBoxShape.closePath();
		
		currentHipBox = new Area(hipBoxShape);
	}
	
	void updateProspectiveHipBox() {
		
		prospectiveLeftUpperOuterHipBoxPos = CommonAlgorithms.roundPoint(prospectiveLeftShoulderSocketPos.x - (footLength + (upperArmWidth/2)), prospectiveTorsoCOG.y);
		prospectiveRightUpperOuterHipBoxPos = CommonAlgorithms.roundPoint(prospectiveLeftShoulderSocketPos.x + shoulderSocketSpan + (footLength + (upperArmWidth/2)), prospectiveTorsoCOG.y);
		Point prospectiveLowerRightHipBoxVertex = CommonAlgorithms.roundPoint(prospectiveLeftShoulderSocketPos.x + shoulderSocketSpan + ((footLength * ClimbingSimulator.constants.RelativeDistanceFromHeelToBallOfFoot) - (footLength - footLengthHorizontalFromAnkleToToe)), prospectiveBeltBucklePos.y + waistToInseamCentralVerticalDistance + footHeight + (ClimbingSimulator.constants.HoldDiameter * ClimbingSimulator.constants.SizeFactor));
		Point prospectiveLowerLeftHipBoxVertex = CommonAlgorithms.roundPoint(prospectiveLeftShoulderSocketPos.x - ((footLength * ClimbingSimulator.constants.RelativeDistanceFromHeelToBallOfFoot) - (footLength - footLengthHorizontalFromAnkleToToe)), prospectiveBeltBucklePos.y + waistToInseamCentralVerticalDistance + footHeight + (ClimbingSimulator.constants.HoldDiameter * ClimbingSimulator.constants.SizeFactor));
		
		Path2D.Float prospectiveHipBoxShape = new Path2D.Float();
		
		prospectiveHipBoxShape.moveTo(prospectiveLeftUpperOuterHipBoxPos.x, prospectiveLeftUpperOuterHipBoxPos.y);
		prospectiveHipBoxShape.lineTo(prospectiveRightUpperOuterHipBoxPos.x, prospectiveRightUpperOuterHipBoxPos.y);
		prospectiveHipBoxShape.lineTo(prospectiveLowerRightHipBoxVertex.x, prospectiveLowerRightHipBoxVertex.y);
		prospectiveHipBoxShape.lineTo(prospectiveLowerLeftHipBoxVertex.x, prospectiveLowerLeftHipBoxVertex.y);
		prospectiveHipBoxShape.closePath();
		
		prospectiveHipBox = new Area(prospectiveHipBoxShape);
	}
		
	void updateLeftArmWireframeValues() {
		
		float apparentLeftHumerusLength = humerusLength;
		float apparentLeftForearmLength = forearmLength + handRadius;
		
		float distanceBetweenLeftShoulderAndLeftHand = CommonAlgorithms.findDistance(leftShoulderSocketPos, leftHandCenterPos);
		float leftShoulderToLeftHandOrientation = calculateLimbOrSegmentEndpointOrientation(leftShoulderSocketPos, leftHandCenterPos);
		
		float humerusFreedomFactorDueToBeingJammedWithinForearmLengthAndHandRadiusInward = 1;
		float humerusFreedomFactorDueToBeingJammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket = 1;
		
		float forearmFreedomFactorDueToBeingJammedWithinHumerusLengthDownward = 1;
		float forearmFreedomFactorDueToBeingJammedByProximityToLowestElbowPoint = 1;
		
		if((leftShoulderSocketPos.x == leftHandCenterPos.x) && (leftShoulderSocketPos.y == leftHandCenterPos.y)) {
			leftHumerusOrientation = (-0.75f) * Pi;
			leftElbowPos = calculateMidlimbJointPoint(leftShoulderSocketPos, humerusLength, leftHumerusOrientation);
			leftForearmOrientation = calculateLimbOrSegmentEndpointOrientation(leftElbowPos, leftHandCenterPos);
			leftWristPivot = leftForearmOrientation;
			leftWristPos = findWristPointFromHandPoint(leftHandCenterPos, leftWristPivot);	
			return;
		}
		
		Point lowestLeftElbowPointPossible = CommonAlgorithms.roundPoint(leftShoulderSocketPos.x, (leftShoulderSocketPos.y + humerusLength));
		float distanceBetweenLeftHandAndLowestLeftElbowPoint = CommonAlgorithms.findDistance(lowestLeftElbowPointPossible, leftHandCenterPos);
		
		Point furthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket = CommonAlgorithms.roundPoint((leftShoulderSocketPos.x + (forearmLength + handRadius)), leftShoulderSocketPos.y);
		float distanceBetweenLeftHandAndFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket = CommonAlgorithms.findDistance(leftHandCenterPos, furthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket);
		
		float verticalJammingComponent = Math.abs(lowestLeftElbowPointPossible.y - leftHandCenterPos.y);
		float horizontalJammingComponent = Math.abs(furthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket.x - leftHandCenterPos.x);
		
		float verticalDistanceAwayFromShoulderXAxis = leftHandCenterPos.y - leftShoulderSocketPos.y;
		float horizontalDistanceAwayFromShoulderYAxis = leftHandCenterPos.x - leftShoulderSocketPos.x;
		
		boolean jammedWithinHumerusLengthDownward = (verticalJammingComponent < humerusLength && Math.abs(horizontalDistanceAwayFromShoulderYAxis) < (forearmLength + handRadius));
		boolean jammedWithinForearmAndHandLengthInward = (horizontalJammingComponent < (forearmLength + handRadius) && Math.abs(verticalDistanceAwayFromShoulderXAxis) < humerusLength);
		
		boolean jammedByProximityToLowestElbowPoint = (distanceBetweenLeftHandAndLowestLeftElbowPoint < (forearmLength + handRadius));
		boolean jammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulder = (distanceBetweenLeftHandAndFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket < (humerusLength));
		
		if (jammedWithinHumerusLengthDownward) {
			forearmFreedomFactorDueToBeingJammedWithinHumerusLengthDownward *= Math.abs((horizontalDistanceAwayFromShoulderYAxis)/(forearmLength + handRadius));
		}
		
		if (jammedByProximityToLowestElbowPoint) {
			forearmFreedomFactorDueToBeingJammedByProximityToLowestElbowPoint *= Math.sqrt((distanceBetweenLeftHandAndLowestLeftElbowPoint)/(forearmLength + handRadius));
		}
		
		if (jammedWithinForearmAndHandLengthInward) {
			humerusFreedomFactorDueToBeingJammedWithinForearmLengthAndHandRadiusInward *= Math.abs((verticalDistanceAwayFromShoulderXAxis)/(humerusLength));
		}
		
		if (jammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulder) {
			humerusFreedomFactorDueToBeingJammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket *= Math.sqrt((distanceBetweenLeftHandAndFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket)/(humerusLength));
		}
		
		float firstStageHumerusShortener = ((humerusLength - horizontalJammingComponent) * (1 - humerusFreedomFactorDueToBeingJammedWithinForearmLengthAndHandRadiusInward));
		float firstStageForearmShortener = (((forearmLength + handRadius) - verticalJammingComponent) * (1 - forearmFreedomFactorDueToBeingJammedWithinHumerusLengthDownward));
		
		apparentLeftHumerusLength -= firstStageHumerusShortener;
		apparentLeftForearmLength -= firstStageForearmShortener;
		
		float remainingHumerusLeeway = Math.min(((apparentLeftHumerusLength) + distanceBetweenLeftShoulderAndLeftHand) - apparentLeftForearmLength, (apparentLeftHumerusLength + apparentLeftForearmLength) - distanceBetweenLeftShoulderAndLeftHand);
		float remainingForearmLeeway = Math.min(((apparentLeftForearmLength) + distanceBetweenLeftShoulderAndLeftHand) - apparentLeftHumerusLength, ((apparentLeftForearmLength) + apparentLeftHumerusLength) - distanceBetweenLeftShoulderAndLeftHand);
		
		float secondStageHumerusShortener = remainingHumerusLeeway * (1- humerusFreedomFactorDueToBeingJammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket);
		float secondStageForearmShortener = remainingForearmLeeway * (1- forearmFreedomFactorDueToBeingJammedByProximityToLowestElbowPoint);
		
		apparentLeftHumerusLength -= secondStageHumerusShortener;		
		apparentLeftForearmLength -= secondStageForearmShortener;
		
		try {
			if (leftHandCenterPos.y >= leftShoulderSocketPos.y && leftHandCenterPos.x >= leftShoulderSocketPos.x ) {
				leftHumerusOrientation = calculateProximalLimbSegmentOrientation(apparentLeftHumerusLength, apparentLeftForearmLength, distanceBetweenLeftShoulderAndLeftHand, leftShoulderToLeftHandOrientation, false, true);
			} else {
				leftHumerusOrientation = calculateProximalLimbSegmentOrientation(apparentLeftHumerusLength, apparentLeftForearmLength, distanceBetweenLeftShoulderAndLeftHand, leftShoulderToLeftHandOrientation, true, true);
			}
		} catch (LawOfCosinesException e) {
			
		}
		
		leftElbowPos = calculateMidlimbJointPoint(leftShoulderSocketPos, apparentLeftHumerusLength, leftHumerusOrientation);
		
		leftForearmOrientation = calculateLimbOrSegmentEndpointOrientation(leftElbowPos, leftHandCenterPos);
		
		leftWristPivot = leftForearmOrientation;
	
		leftWristPos = findWristPointFromHandPoint(leftHandCenterPos, leftWristPivot);
	}
	
	void updateRightArmWireframeValues() {
		
		float apparentRightHumerusLength = humerusLength;
		float apparentRightForearmLength = forearmLength + handRadius;
		
		float distanceBetweenRightShoulderAndRightHand = CommonAlgorithms.findDistance(rightShoulderSocketPos, rightHandCenterPos);
		float rightShoulderToRightHandOrientation = calculateLimbOrSegmentEndpointOrientation(rightShoulderSocketPos, rightHandCenterPos);
		
		float humerusFreedomFactorDueToBeingJammedWithinForearmLengthAndHandRadiusInward = 1;
		float humerusFreedomFactorDueToBeingJammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket = 1;
		
		float forearmFreedomFactorDueToBeingJammedWithinHumerusLengthDownward = 1;
		float forearmFreedomFactorDueToBeingJammedByProximityToLowestElbowPoint = 1;

		if((rightShoulderSocketPos.x == rightHandCenterPos.x) && (rightShoulderSocketPos.y == rightHandCenterPos.y)) {
			rightHumerusOrientation = (-0.25f) * Pi;
			rightElbowPos = calculateMidlimbJointPoint(rightShoulderSocketPos, humerusLength, rightHumerusOrientation);
			rightForearmOrientation = calculateLimbOrSegmentEndpointOrientation(rightElbowPos, rightHandCenterPos);
			rightWristPivot = rightForearmOrientation;
			rightWristPos = findWristPointFromHandPoint(rightHandCenterPos, rightWristPivot);		
			return;
		}
		
		Point lowestRightElbowPointPossible = CommonAlgorithms.roundPoint(rightShoulderSocketPos.x, (rightShoulderSocketPos.y + humerusLength));
		float distanceBetweenRightHandAndLowestRightElbowPoint = CommonAlgorithms.findDistance(lowestRightElbowPointPossible, rightHandCenterPos);
		
		Point furthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket = CommonAlgorithms.roundPoint((rightShoulderSocketPos.x - (forearmLength + handRadius)), rightShoulderSocketPos.y);
		float distanceBetweenRightHandAndFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket = CommonAlgorithms.findDistance(rightHandCenterPos, furthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket);
		
		float verticalJammingComponent = Math.abs(lowestRightElbowPointPossible.y- rightHandCenterPos.y);
		float horizontalJammingComponent = Math.abs(rightHandCenterPos.x - furthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket.x);
		float verticalDistanceAwayFromShoulderXAxis = rightHandCenterPos.y - rightShoulderSocketPos.y;
		float horizontalDistanceAwayFromShoulderYAxis = rightShoulderSocketPos.x - rightHandCenterPos.x;
		
		boolean jammedWithinHumerusLengthDownward = (verticalJammingComponent < humerusLength && Math.abs(horizontalDistanceAwayFromShoulderYAxis) < (forearmLength + handRadius));
		boolean jammedWithinForearmAndHandLengthInward = (horizontalJammingComponent < (forearmLength + handRadius) && Math.abs(verticalDistanceAwayFromShoulderXAxis) < humerusLength);
		boolean jammedByProximityToLowestElbowPoint = (distanceBetweenRightHandAndLowestRightElbowPoint < (forearmLength + handRadius));
		boolean jammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulder = (distanceBetweenRightHandAndFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket < (humerusLength));
		
		if (jammedWithinHumerusLengthDownward) {
			forearmFreedomFactorDueToBeingJammedWithinHumerusLengthDownward *= Math.abs((horizontalDistanceAwayFromShoulderYAxis)/(forearmLength + handRadius));
		}
	
		if (jammedByProximityToLowestElbowPoint) {
			forearmFreedomFactorDueToBeingJammedByProximityToLowestElbowPoint *= Math.sqrt((distanceBetweenRightHandAndLowestRightElbowPoint)/(forearmLength + handRadius));
		}
		
		if (jammedWithinForearmAndHandLengthInward) {
			humerusFreedomFactorDueToBeingJammedWithinForearmLengthAndHandRadiusInward *= Math.abs((verticalDistanceAwayFromShoulderXAxis)/(humerusLength));
		}
		
		if (jammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulder) {
			humerusFreedomFactorDueToBeingJammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket *= Math.sqrt((distanceBetweenRightHandAndFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket)/(humerusLength));
		}

		float firstStageHumerusShortener = ((humerusLength - horizontalJammingComponent) * (1 - humerusFreedomFactorDueToBeingJammedWithinForearmLengthAndHandRadiusInward));
		float firstStageForearmShortener = (((forearmLength + handRadius) - verticalJammingComponent) * (1 - forearmFreedomFactorDueToBeingJammedWithinHumerusLengthDownward));
	
		apparentRightHumerusLength -= firstStageHumerusShortener;
		apparentRightForearmLength -= firstStageForearmShortener;
	
		float remainingHumerusLeeway = Math.min(((apparentRightHumerusLength) + distanceBetweenRightShoulderAndRightHand) - apparentRightForearmLength, (apparentRightHumerusLength + apparentRightForearmLength) - distanceBetweenRightShoulderAndRightHand);
		float remainingForearmLeeway = Math.min(((apparentRightForearmLength) + distanceBetweenRightShoulderAndRightHand) - apparentRightHumerusLength, ((apparentRightForearmLength) + apparentRightHumerusLength) - distanceBetweenRightShoulderAndRightHand);
			
		float secondStageHumerusShortener = remainingHumerusLeeway * (1 - humerusFreedomFactorDueToBeingJammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket);
		float secondStageForearmShortener = remainingForearmLeeway * (1- forearmFreedomFactorDueToBeingJammedByProximityToLowestElbowPoint);
		
		apparentRightHumerusLength -= secondStageHumerusShortener;
		apparentRightForearmLength -= secondStageForearmShortener;

		try { 
			if (rightHandCenterPos.y >= rightShoulderSocketPos.y && rightHandCenterPos.x <= rightShoulderSocketPos.x ) {
				rightHumerusOrientation = calculateProximalLimbSegmentOrientation(apparentRightHumerusLength, apparentRightForearmLength, distanceBetweenRightShoulderAndRightHand, rightShoulderToRightHandOrientation, false, false);
			} else {
				rightHumerusOrientation = calculateProximalLimbSegmentOrientation(apparentRightHumerusLength, apparentRightForearmLength, distanceBetweenRightShoulderAndRightHand, rightShoulderToRightHandOrientation, true, false);
			}
		} catch (LawOfCosinesException e) {
			
		}
			
		rightElbowPos = calculateMidlimbJointPoint(rightShoulderSocketPos, apparentRightHumerusLength, rightHumerusOrientation);
		
		rightForearmOrientation = calculateLimbOrSegmentEndpointOrientation(rightElbowPos, rightHandCenterPos);
		
		rightWristPivot = rightForearmOrientation;
		
		rightWristPos = findWristPointFromHandPoint(rightHandCenterPos, rightWristPivot);
	}
	
	void updateLeftLegWireframeValues() {
	
		leftAnklePos = findAnklePointFromFootPoint(leftFootHoldContactPoint,true);
		
		float pelvisToLeftAnkleOrientation = calculateLimbOrSegmentEndpointOrientation(leftHipSocketPos, leftAnklePos);
		float distanceBetweenPelvisAndLeftAnkle = CommonAlgorithms.findDistance(leftHipSocketPos, leftAnklePos);
		
		try {
			leftThighOrientation = calculateProximalLimbSegmentOrientation(femurLength, calfLength, distanceBetweenPelvisAndLeftAnkle, pelvisToLeftAnkleOrientation, false, true);
		} catch (LawOfCosinesException e) {
			
		}
				
		leftKneePos = calculateMidlimbJointPoint(leftHipSocketPos, femurLength, leftThighOrientation);
		
		leftCalfOrientation = calculateLimbOrSegmentEndpointOrientation(leftKneePos, leftAnklePos);
	}
	
	void updateRightLegWireframeValues() {
		
		rightAnklePos = findAnklePointFromFootPoint(rightFootHoldContactPoint,false);
		
		float pelvisToRightAnkleOrientation = calculateLimbOrSegmentEndpointOrientation(rightHipSocketPos, rightAnklePos);
		float distanceBetweenPelvisAndRightAnkle = CommonAlgorithms.findDistance(rightHipSocketPos, rightAnklePos);
		
		try {
			rightThighOrientation = calculateProximalLimbSegmentOrientation(femurLength, calfLength, distanceBetweenPelvisAndRightAnkle, pelvisToRightAnkleOrientation, false, false);
		} catch (LawOfCosinesException e) {
			
		}
			
			
		rightKneePos = calculateMidlimbJointPoint(rightHipSocketPos, femurLength, rightThighOrientation);
		
		rightCalfOrientation = calculateLimbOrSegmentEndpointOrientation(rightKneePos, rightAnklePos);
	}
	
	void testAllProspectiveProximalLimbSegmentOrientationsAndLimbConnectionReachableAreas() throws LimbMustMoveFirstException {
		
		updateProspectiveTorsoPoints();
		
		testAllProspectiveProximalLimbSegmentOrientationsForExceedingLimbExtensionLimits();
		
		limbsCoordinator.testEachLimbForViolationOfReachableAreaUponProspectiveTorsoMove();
	}
		
	void testAllProspectiveProximalLimbSegmentOrientationsForExceedingLimbExtensionLimits() throws LimbMustMoveFirstException {

		updateProspectiveLeftHumerusOrientationBasedOnProspectiveTorsoMove();
		updateProspectiveRightHumerusOrientationBasedOnProspectiveTorsoMove();
		updateProspectiveLeftThighOrientationBasedOnProspectiveTorsoMove();
		updateProspectiveRightThighOrientationBasedOnProspectiveTorsoMove();
	}
	
	void updateProspectiveTorsoPoints() {
		
		prospectiveRightShoulderSocketPos.x = Math.round(prospectiveLeftShoulderSocketPos.x + shoulderSocketSpan);
		prospectiveRightShoulderSocketPos.y = prospectiveLeftShoulderSocketPos.y;
		
		prospectiveBeltBucklePos.x = Math.round(prospectiveLeftShoulderSocketPos.x + (shoulderSocketSpan/2));
		prospectiveBeltBucklePos.y = Math.round(prospectiveLeftShoulderSocketPos.y + (torsoHeightFromTopOfTrapsToWaist - (distanceFromNeckBaseAtShoulderSocketLevelToBottomOfHeadCircle * ClimbingSimulator.constants.FactorForDeterminingHeightOfTrapsAgainstSideOfNeckDeterminedAsAFractionOfTheVerticalDistanceFromShoulderLevelToTheBottomOfTheHead)));
		
		prospectivePelvisCenterPos.x = prospectiveBeltBucklePos.x;
		prospectivePelvisCenterPos.y = Math.round(prospectiveBeltBucklePos.y + beltBuckleToCenterPelvisDistance);
		
		prospectiveLeftHipSocketPos.x = Math.round(prospectivePelvisCenterPos.x - hipSocketSpan/2);
		prospectiveLeftHipSocketPos.y = prospectivePelvisCenterPos.y;
		prospectiveRightHipSocketPos.x = Math.round(prospectivePelvisCenterPos.x + hipSocketSpan/2);
		prospectiveRightHipSocketPos.y = prospectivePelvisCenterPos.y;
		
		Point prospectiveLeftTrapNeckIntersectionPoint = new Point();
		
		prospectiveLeftTrapNeckIntersectionPoint.x = Math.round(prospectiveLeftShoulderSocketPos.x + (shoulderSocketSpan/2 - neckThickness/2));
		prospectiveLeftTrapNeckIntersectionPoint.y = Math.round(prospectiveLeftShoulderSocketPos.y - (distanceFromNeckBaseAtShoulderSocketLevelToBottomOfHeadCircle * ClimbingSimulator.constants.FactorForDeterminingHeightOfTrapsAgainstSideOfNeckDeterminedAsAFractionOfTheVerticalDistanceFromShoulderLevelToTheBottomOfTheHead));
		
		prospectiveTorsoCOG.x = prospectiveBeltBucklePos.x;
		prospectiveTorsoCOG.y = Math.round(prospectiveLeftTrapNeckIntersectionPoint.y + (torsoHeightFromTopOfTrapsToWaist * ClimbingSimulator.constants.DistanceFromTopOfTrapsToTorsoCOGComparedToDistanceFromTopOfTrapsToWaistRatio));
	}
	
	void updateProspectiveLeftHumerusOrientationBasedOnProspectiveTorsoMove() throws LimbMustMoveFirstException {
		
		@SuppressWarnings("unused")
		float prospectiveLeftHumerusOrientation;
		
		float prospectiveApparentLeftHumerusLength = humerusLength;
		float prospectiveApparentLeftForearmLength = forearmLength + handRadius;
		
		float prospectiveDistanceBetweenLeftShoulderAndLeftHand = CommonAlgorithms.findDistance(prospectiveLeftShoulderSocketPos, leftHandCenterPos);
		float prospectiveLeftShoulderToLeftHandOrientation = calculateLimbOrSegmentEndpointOrientation(prospectiveLeftShoulderSocketPos, leftHandCenterPos);
		

		float prospectiveHumerusFreedomFactorDueToBeingJammedWithinForearmLengthAndHandRadiusInward = 1;
		float prospectiveHumerusFreedomFactorDueToBeingJammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket = 1;
		
		float prospectiveForearmFreedomFactorDueToBeingJammedWithinHumerusLengthDownward = 1;
		float prospectiveForearmFreedomFactorDueToBeingJammedByProximityToLowestElbowPoint = 1;
		
		if((prospectiveLeftShoulderSocketPos.x == leftHandCenterPos.x) && (prospectiveLeftShoulderSocketPos.y == leftHandCenterPos.y)) {
			prospectiveLeftHumerusOrientation = (-0.75f) * Pi;
			return;
		}
		
		Point prospectiveLowestLeftElbowPointPossible = CommonAlgorithms.roundPoint(prospectiveLeftShoulderSocketPos.x, (prospectiveLeftShoulderSocketPos.y + humerusLength));
		float prospectiveDistanceBetweenLeftHandAndLowestLeftElbowPoint = CommonAlgorithms.findDistance(prospectiveLowestLeftElbowPointPossible, leftHandCenterPos);
		
		Point prospectiveFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket = CommonAlgorithms.roundPoint((prospectiveLeftShoulderSocketPos.x + (forearmLength + handRadius)), prospectiveLeftShoulderSocketPos.y);
		float prospectiveDistanceBetweenLeftHandAndFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket = CommonAlgorithms.findDistance(leftHandCenterPos, prospectiveFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket);
		
		float prospectiveVerticalJammingComponent = Math.abs(prospectiveLowestLeftElbowPointPossible.y - leftHandCenterPos.y);
		float prospectiveHorizontalJammingComponent = Math.abs(prospectiveFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket.x - leftHandCenterPos.x);
		
		float prospectiveVerticalDistanceAwayFromShoulderXAxis = leftHandCenterPos.y - prospectiveLeftShoulderSocketPos.y;
		float prospectiveHorizontalDistanceAwayFromShoulderYAxis = leftHandCenterPos.x - prospectiveLeftShoulderSocketPos.x;
		
		boolean jammedWithinHumerusLengthDownward = (prospectiveVerticalJammingComponent < humerusLength && Math.abs(prospectiveHorizontalDistanceAwayFromShoulderYAxis) < (forearmLength + handRadius));
		boolean jammedWithinForearmAndHandLengthInward = (prospectiveHorizontalJammingComponent < (forearmLength + handRadius) && Math.abs(prospectiveVerticalDistanceAwayFromShoulderXAxis) < humerusLength);
		
		boolean jammedByProximityToLowestElbowPoint = (prospectiveDistanceBetweenLeftHandAndLowestLeftElbowPoint < (forearmLength + handRadius));
		boolean jammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulder = (prospectiveDistanceBetweenLeftHandAndFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket < (humerusLength));
		
		if (jammedWithinHumerusLengthDownward) {
			prospectiveForearmFreedomFactorDueToBeingJammedWithinHumerusLengthDownward *= Math.abs((prospectiveHorizontalDistanceAwayFromShoulderYAxis)/(forearmLength + handRadius));
		}
		
		if (jammedByProximityToLowestElbowPoint) {
			prospectiveForearmFreedomFactorDueToBeingJammedByProximityToLowestElbowPoint *= Math.sqrt((prospectiveDistanceBetweenLeftHandAndLowestLeftElbowPoint)/(forearmLength + handRadius));
		}
		
		if (jammedWithinForearmAndHandLengthInward) {
			prospectiveHumerusFreedomFactorDueToBeingJammedWithinForearmLengthAndHandRadiusInward *= Math.abs((prospectiveVerticalDistanceAwayFromShoulderXAxis)/(humerusLength));
		}
		
		if (jammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulder) {
			prospectiveHumerusFreedomFactorDueToBeingJammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket *= Math.sqrt((prospectiveDistanceBetweenLeftHandAndFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket)/(humerusLength));
		}
		
		float firstStageHumerusShortener = ((humerusLength - prospectiveHorizontalJammingComponent) * (1 - prospectiveHumerusFreedomFactorDueToBeingJammedWithinForearmLengthAndHandRadiusInward));
		float firstStageForearmShortener = (((forearmLength + handRadius) - prospectiveVerticalJammingComponent) * (1 - prospectiveForearmFreedomFactorDueToBeingJammedWithinHumerusLengthDownward));
		
		prospectiveApparentLeftHumerusLength -= firstStageHumerusShortener;
		prospectiveApparentLeftForearmLength -= firstStageForearmShortener;
		
		float remainingHumerusLeeway = Math.min(((prospectiveApparentLeftHumerusLength) + prospectiveDistanceBetweenLeftShoulderAndLeftHand) - prospectiveApparentLeftForearmLength, (prospectiveApparentLeftHumerusLength + prospectiveApparentLeftForearmLength) - prospectiveDistanceBetweenLeftShoulderAndLeftHand);
		float remainingForearmLeeway = Math.min(((prospectiveApparentLeftForearmLength) + prospectiveDistanceBetweenLeftShoulderAndLeftHand) - prospectiveApparentLeftHumerusLength, ((prospectiveApparentLeftForearmLength) + prospectiveApparentLeftHumerusLength) - prospectiveDistanceBetweenLeftShoulderAndLeftHand);
		
		float secondStageHumerusShortener = remainingHumerusLeeway * (1 - prospectiveHumerusFreedomFactorDueToBeingJammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket);
		float secondStageForearmShortener = remainingForearmLeeway * (1 - prospectiveForearmFreedomFactorDueToBeingJammedByProximityToLowestElbowPoint);
		
		prospectiveApparentLeftHumerusLength -= secondStageHumerusShortener;		
		prospectiveApparentLeftForearmLength -= secondStageForearmShortener;
		
		try {
			if (leftHandCenterPos.y >= prospectiveLeftShoulderSocketPos.y && leftHandCenterPos.x >= prospectiveLeftShoulderSocketPos.x ) {
				prospectiveLeftHumerusOrientation = calculateProximalLimbSegmentOrientation(prospectiveApparentLeftHumerusLength, prospectiveApparentLeftForearmLength, prospectiveDistanceBetweenLeftShoulderAndLeftHand, prospectiveLeftShoulderToLeftHandOrientation, false, true);
			} else {
				prospectiveLeftHumerusOrientation = calculateProximalLimbSegmentOrientation(prospectiveApparentLeftHumerusLength, prospectiveApparentLeftForearmLength, prospectiveDistanceBetweenLeftShoulderAndLeftHand, prospectiveLeftShoulderToLeftHandOrientation, true, true);
			}
		} catch (LawOfCosinesException e) {
			
			leftArm.limbHadToAbandonHoldBecauseOfAViolationOfReachableArea = true;
			
			throw new LimbMustMoveFirstException(LimbIdentifier.LEFTARM);
		}
	}
	
	void updateProspectiveRightHumerusOrientationBasedOnProspectiveTorsoMove() throws LimbMustMoveFirstException {
		
		@SuppressWarnings("unused")
		float prospectiveRightHumerusOrientation;
		
		float prospectiveApparentRightHumerusLength = humerusLength;
		float prospectiveApparentRightForearmLength = forearmLength + handRadius;
		
		float prospectiveDistanceBetweenRightShoulderAndRightHand = CommonAlgorithms.findDistance(prospectiveRightShoulderSocketPos, rightHandCenterPos);
		float prospectiveRightShoulderToRightHandOrientation = calculateLimbOrSegmentEndpointOrientation(prospectiveRightShoulderSocketPos, rightHandCenterPos);
		
		float prospectiveHumerusFreedomFactorDueToBeingJammedWithinForearmLengthAndHandRadiusInward = 1;
		float prospectiveHumerusFreedomFactorDueToBeingJammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket = 1;
		
		float prospectiveForearmFreedomFactorDueToBeingJammedWithinHumerusLengthDownward = 1;
		float prospectiveForearmFreedomFactorDueToBeingJammedByProximityToLowestElbowPoint = 1;
		
		if((prospectiveRightShoulderSocketPos.x == rightHandCenterPos.x) && (prospectiveRightShoulderSocketPos.y == rightHandCenterPos.y)) {
			prospectiveRightHumerusOrientation = (-0.25f) * Pi;
			return;
		}
		
		Point prospectiveLowestRightElbowPointPossible = CommonAlgorithms.roundPoint(prospectiveRightShoulderSocketPos.x, (prospectiveRightShoulderSocketPos.y + humerusLength));
		float prospectiveDistanceBetweenRightHandAndLowestRightElbowPoint = CommonAlgorithms.findDistance(prospectiveLowestRightElbowPointPossible, rightHandCenterPos);
		
		Point prospectiveFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket = CommonAlgorithms.roundPoint((prospectiveRightShoulderSocketPos.x - (forearmLength + handRadius)), prospectiveRightShoulderSocketPos.y);
		float prospectiveDistanceBetweenRightHandAndFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket = CommonAlgorithms.findDistance(rightHandCenterPos, prospectiveFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket);
		
		float prospectiveVerticalJammingComponent = Math.abs(prospectiveLowestRightElbowPointPossible.y- rightHandCenterPos.y);
		float prospectiveHorizontalJammingComponent = Math.abs(rightHandCenterPos.x - prospectiveFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket.x);
		
		float prospectiveVerticalDistanceAwayFromShoulderXAxis = rightHandCenterPos.y - prospectiveRightShoulderSocketPos.y;
		float prospectiveHorizontalDistanceAwayFromShoulderYAxis = prospectiveRightShoulderSocketPos.x - rightHandCenterPos.x;
		
		boolean jammedWithinHumerusLengthDownward = (prospectiveVerticalJammingComponent < humerusLength && Math.abs(prospectiveHorizontalDistanceAwayFromShoulderYAxis) < (forearmLength + handRadius));
		boolean jammedWithinForearmAndHandLengthInward = (prospectiveHorizontalJammingComponent < (forearmLength + handRadius) && Math.abs(prospectiveVerticalDistanceAwayFromShoulderXAxis) < humerusLength);
		
		boolean jammedByProximityToLowestElbowPoint = (prospectiveDistanceBetweenRightHandAndLowestRightElbowPoint < (forearmLength + handRadius));
		boolean jammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulder = (prospectiveDistanceBetweenRightHandAndFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket < (humerusLength));
		
		if (jammedWithinHumerusLengthDownward) {
			prospectiveForearmFreedomFactorDueToBeingJammedWithinHumerusLengthDownward *= Math.abs((prospectiveHorizontalDistanceAwayFromShoulderYAxis)/(forearmLength + handRadius));
		}
	
		if (jammedByProximityToLowestElbowPoint) {
			prospectiveForearmFreedomFactorDueToBeingJammedByProximityToLowestElbowPoint *= Math.sqrt((prospectiveDistanceBetweenRightHandAndLowestRightElbowPoint)/(forearmLength + handRadius));
		}
		
		if (jammedWithinForearmAndHandLengthInward) {
			prospectiveHumerusFreedomFactorDueToBeingJammedWithinForearmLengthAndHandRadiusInward *= Math.abs((prospectiveVerticalDistanceAwayFromShoulderXAxis)/(humerusLength));
		}
		
		if (jammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulder) {
			prospectiveHumerusFreedomFactorDueToBeingJammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket *= Math.sqrt((prospectiveDistanceBetweenRightHandAndFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket)/(humerusLength));
		}

		float firstStageHumerusShortener = ((humerusLength - prospectiveHorizontalJammingComponent) * (1 - prospectiveHumerusFreedomFactorDueToBeingJammedWithinForearmLengthAndHandRadiusInward));
		float firstStageForearmShortener = (((forearmLength + handRadius) - prospectiveVerticalJammingComponent) * (1 - prospectiveForearmFreedomFactorDueToBeingJammedWithinHumerusLengthDownward));
	
		prospectiveApparentRightHumerusLength -= firstStageHumerusShortener;
		prospectiveApparentRightForearmLength -= firstStageForearmShortener;
	
		float remainingHumerusLeeway = Math.min(((prospectiveApparentRightHumerusLength) + prospectiveDistanceBetweenRightShoulderAndRightHand) - prospectiveApparentRightForearmLength, (prospectiveApparentRightHumerusLength + prospectiveApparentRightForearmLength) - prospectiveDistanceBetweenRightShoulderAndRightHand);
		float remainingForearmLeeway = Math.min(((prospectiveApparentRightForearmLength) + prospectiveDistanceBetweenRightShoulderAndRightHand) - prospectiveApparentRightHumerusLength, ((prospectiveApparentRightForearmLength) + prospectiveApparentRightHumerusLength) - prospectiveDistanceBetweenRightShoulderAndRightHand);
			
		float secondStageHumerusShortener = remainingHumerusLeeway * (1 - prospectiveHumerusFreedomFactorDueToBeingJammedByProximityToFurthestInteriorPointAlongShoulderHorizontalAxisWithElbowStillOutsideShoulderSocket);
		float secondStageForearmShortener = remainingForearmLeeway * (1- prospectiveForearmFreedomFactorDueToBeingJammedByProximityToLowestElbowPoint);
		
		prospectiveApparentRightHumerusLength -= secondStageHumerusShortener;
		prospectiveApparentRightForearmLength -= secondStageForearmShortener;

		try { 
			if (rightHandCenterPos.y >= prospectiveRightShoulderSocketPos.y && rightHandCenterPos.x <= prospectiveRightShoulderSocketPos.x ) {
				prospectiveRightHumerusOrientation = calculateProximalLimbSegmentOrientation(prospectiveApparentRightHumerusLength, prospectiveApparentRightForearmLength, prospectiveDistanceBetweenRightShoulderAndRightHand, prospectiveRightShoulderToRightHandOrientation, false, false);
			} else {
				prospectiveRightHumerusOrientation = calculateProximalLimbSegmentOrientation(prospectiveApparentRightHumerusLength, prospectiveApparentRightForearmLength, prospectiveDistanceBetweenRightShoulderAndRightHand, prospectiveRightShoulderToRightHandOrientation, true, false);
			}
		} catch (LawOfCosinesException e) {
			
			rightArm.limbHadToAbandonHoldBecauseOfAViolationOfReachableArea = true;
			
			throw new LimbMustMoveFirstException(LimbIdentifier.RIGHTARM);
		}
	}
	
	void updateProspectiveLeftThighOrientationBasedOnProspectiveTorsoMove() throws LimbMustMoveFirstException {
		
		@SuppressWarnings("unused")
		float prospectiveLeftThighOrientation;
		
		float prospectivePelvisToLeftAnkleOrientation = calculateLimbOrSegmentEndpointOrientation(prospectiveLeftHipSocketPos, leftAnklePos);
		float prospectiveDistanceBetweenPelvisAndLeftAnkle = CommonAlgorithms.findDistance(prospectiveLeftHipSocketPos, leftAnklePos);
		
		try {
			prospectiveLeftThighOrientation = calculateProximalLimbSegmentOrientation(femurLength, calfLength, prospectiveDistanceBetweenPelvisAndLeftAnkle, prospectivePelvisToLeftAnkleOrientation, false, true);
		} catch (LawOfCosinesException e) {
			
			leftLeg.limbHadToAbandonHoldBecauseOfAViolationOfReachableArea = true;
			
			throw new LimbMustMoveFirstException(LimbIdentifier.LEFTLEG);
		}
	}
	
	void updateProspectiveRightThighOrientationBasedOnProspectiveTorsoMove() throws LimbMustMoveFirstException {
		
		@SuppressWarnings("unused")
		float prospectiveRightThighOrientation;
		
		float prospectivePelvisToRightAnkleOrientation = calculateLimbOrSegmentEndpointOrientation(prospectiveRightHipSocketPos, rightAnklePos);
		float prospectiveDistanceBetweenPelvisAndRightAnkle = CommonAlgorithms.findDistance(prospectiveRightHipSocketPos, rightAnklePos);
		
		try {
			prospectiveRightThighOrientation = calculateProximalLimbSegmentOrientation(femurLength, calfLength, prospectiveDistanceBetweenPelvisAndRightAnkle, prospectivePelvisToRightAnkleOrientation, false, false);
		} catch (LawOfCosinesException e) {
			
			rightLeg.limbHadToAbandonHoldBecauseOfAViolationOfReachableArea = true;
			
			throw new LimbMustMoveFirstException(LimbIdentifier.RIGHTLEG);
		}
	}
	
	//find wrist point from center of hand point and angle of hand compared to wrist
	Point findWristPointFromHandPoint (Point centerOfHand, float radialSlopeFromWristToHand) {
		return CommonAlgorithms.shiftBySlopeAsRadians(centerOfHand, handRadius, radialSlopeFromWristToHand, false);
	}
	
	Point findAnklePointFromFootPoint(Point footStandingPoint, boolean isLeftSide) {
		
		float anklePointDeltaX = ((footLength * ClimbingSimulator.constants.RelativeDistanceFromHeelToBallOfFoot) - (footLength - footLengthHorizontalFromAnkleToToe));
		if (isLeftSide) {anklePointDeltaX = anklePointDeltaX * (-1);}
		
		float anklePointDeltaY = footHeight;
		
		return CommonAlgorithms.roundPoint((footStandingPoint.x - anklePointDeltaX),(footStandingPoint.y - anklePointDeltaY));
	}
	
	void updateHeadPoints(){
		neckBaseBetweenShoulderSocketsPos = CommonAlgorithms.findMidpoint(leftShoulderSocketPos, rightShoulderSocketPos);
		headBasePos.x = neckBaseBetweenShoulderSocketsPos.x;
		headBasePos.y = Math.round(neckBaseBetweenShoulderSocketsPos.y - distanceFromNeckBaseAtShoulderSocketLevelToBottomOfHeadCircle);
		headTopPos.x = headBasePos.x;
		headTopPos.y = Math.round(headBasePos.y - (2 * headRadius));
	}	
	
	void updateAllLimbWireframeValues() {
		
		updateLeftArmWireframeValues();
		updateRightArmWireframeValues();
		updateLeftLegWireframeValues();
		updateRightLegWireframeValues();
		updateCurrentHipBox();
		updateHeadPoints();
	}
	
	//IV. METHODS RELATED TO MOVEMENT
	void findMostEfficientPathsUpTheWallThroughTheHoldPoints() {
		
		findMostEfficientPathFromBottomOfWallToTopOfWallThroughTheHoldPointsUsingQuadrantRadiusLimitForEachHoldInTheZoneAtTheTopOfTheWall();
		
		assemblePointsOfMostEfficientPathThroughTheHoldPointsFromBottomOfWallToTopOfWallUsingQuadrantRadiusLimit();
	}
		
	//Dijkstra's Algorithm
	void findMostEfficientPathFromBottomOfWallToTopOfWallThroughTheHoldPointsUsingQuadrantRadiusLimitForEachHoldInTheZoneAtTheTopOfTheWall() {
		
		for (int i = 0; i < finalizedSetOfUserEnteredHolds.size(); i++) {
			finalizedSetOfUserEnteredHolds.get(i).shortestDistanceToABottomHoldViaQuadrantSizeLimitsOnConnections = Float.MAX_VALUE;
		}
		
		LinkedList<Integer> distanceAnalysisQueue = new LinkedList<Integer>();
		
		for (Integer i : finalizedSetOfUserEnteredHolds.holdsWithinBottomOfWallZone) {
			
			finalizedSetOfUserEnteredHolds.get(i).shortestDistanceToABottomHoldViaQuadrantSizeLimitsOnConnections = 0;
			distanceAnalysisQueue.add(i);
		}
		
		while (!distanceAnalysisQueue.isEmpty()) {
			
			Integer indexOfHoldBeingAnalyzed = distanceAnalysisQueue.removeFirst();
			
			float distanceFromBottomOfWallToHoldBeingAnalyzed = finalizedSetOfUserEnteredHolds.get(indexOfHoldBeingAnalyzed).shortestDistanceToABottomHoldViaQuadrantSizeLimitsOnConnections;
						
			for (int indexOfAdjacentHold : finalizedSetOfUserEnteredHolds.get(indexOfHoldBeingAnalyzed).holdsThatAreCloseEnoughToPossiblyBeInAQuadrant) {
				
				float interholdDistance = CommonAlgorithms.findDistance(finalizedSetOfUserEnteredHolds.get(indexOfHoldBeingAnalyzed), finalizedSetOfUserEnteredHolds.get(indexOfAdjacentHold));
				
				if ((distanceFromBottomOfWallToHoldBeingAnalyzed + interholdDistance) < finalizedSetOfUserEnteredHolds.get(indexOfAdjacentHold).shortestDistanceToABottomHoldViaQuadrantSizeLimitsOnConnections) {
					
					finalizedSetOfUserEnteredHolds.get(indexOfAdjacentHold).shortestDistanceToABottomHoldViaQuadrantSizeLimitsOnConnections = (distanceFromBottomOfWallToHoldBeingAnalyzed + interholdDistance);
					finalizedSetOfUserEnteredHolds.get(indexOfAdjacentHold).adjacentHoldThatBeginsShortestPathToBottomOfWallViaQuadrantSizeLimitsOnConnections = indexOfHoldBeingAnalyzed;
					distanceAnalysisQueue.add(indexOfAdjacentHold);
				}	
			}
		}
		
		float shortestPath = Float.MAX_VALUE;
		
		for (Integer i : finalizedSetOfUserEnteredHolds.holdsWithinTopOfWallZone) {
			
			if (finalizedSetOfUserEnteredHolds.get(i).shortestDistanceToABottomHoldViaQuadrantSizeLimitsOnConnections < shortestPath) {
				shortestPath = finalizedSetOfUserEnteredHolds.get(i).shortestDistanceToABottomHoldViaQuadrantSizeLimitsOnConnections;
			}
		}
	}
	
	void assemblePointsOfMostEfficientPathThroughTheHoldPointsFromBottomOfWallToTopOfWallUsingQuadrantRadiusLimit() {
		
		shortestPathThroughHoldPointsFromBottomToTopBasedOnQuadrantConnections = new LinkedList<>();
		
		float shortestPathDistanceThusfar = Float.MAX_VALUE;
		Integer indexOfShortestPathFinishingHoldAtTopOfWall = -1;
		
		for (Integer i : finalizedSetOfUserEnteredHolds.holdsWithinTopOfWallZone) {
			
			float distanceFromHoldInTopOfWallZoneToBottomOfWall = finalizedSetOfUserEnteredHolds.get(i).shortestDistanceToABottomHoldViaQuadrantSizeLimitsOnConnections;
			
			if (distanceFromHoldInTopOfWallZoneToBottomOfWall < shortestPathDistanceThusfar) {
				
				shortestPathDistanceThusfar = distanceFromHoldInTopOfWallZoneToBottomOfWall;
				indexOfShortestPathFinishingHoldAtTopOfWall = i;
			}
		}
		
		shortestPathThroughHoldPointsFromBottomToTopBasedOnQuadrantConnections.addFirst(indexOfShortestPathFinishingHoldAtTopOfWall);
			
		while (finalizedSetOfUserEnteredHolds.get(shortestPathThroughHoldPointsFromBottomToTopBasedOnQuadrantConnections.peekFirst()).adjacentHoldThatBeginsShortestPathToBottomOfWallViaQuadrantSizeLimitsOnConnections != -1) {
			
			Integer indexOfImmediatelyPreceedingHoldAlongPath = finalizedSetOfUserEnteredHolds.get(shortestPathThroughHoldPointsFromBottomToTopBasedOnQuadrantConnections.peekFirst()).adjacentHoldThatBeginsShortestPathToBottomOfWallViaQuadrantSizeLimitsOnConnections;
			shortestPathThroughHoldPointsFromBottomToTopBasedOnQuadrantConnections.addFirst(indexOfImmediatelyPreceedingHoldAlongPath);
		}
	}
	
	void findThePathThroughEachSecondIterationAveragePointOfHoldsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections() {
		
		LinkedList<Point> firstIterationAveragePointOfHoldsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections = new LinkedList<>();
		
		for (int i = 0; i < shortestPathThroughHoldPointsFromBottomToTopBasedOnQuadrantConnections.size(); i++) {
			
			Point milestoneAlongShortestPathThroughHoldPoints = finalizedSetOfUserEnteredHolds.get(shortestPathThroughHoldPointsFromBottomToTopBasedOnQuadrantConnections.get(i));
			
			Point averageFirstIterationPointOfTheHoldPointsWithinHalfTheReachOfBothArmsForPathMilestone = calculateAverageHoldPointWithinArmsReachOfAGivenPoint(milestoneAlongShortestPathThroughHoldPoints); 
			
			firstIterationAveragePointOfHoldsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections.add(averageFirstIterationPointOfTheHoldPointsWithinHalfTheReachOfBothArmsForPathMilestone);
		}
		
		for (int i = 0; i < firstIterationAveragePointOfHoldsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections.size(); i++) {
			
			Point milestoneAlongFirstIterationAveragePathThroughHoldPoints = firstIterationAveragePointOfHoldsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections.get(i);
			
			Point averageSecondIterationPointOfTheHoldPointsWithinHalfTheReachOfBothArmsForPathMilestone = calculateAverageHoldPointWithinArmsReachOfAGivenPoint(milestoneAlongFirstIterationAveragePathThroughHoldPoints); 
			
			pathThroughEachSecondIterationAveragePointOfTheHoldPointsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections.add(averageSecondIterationPointOfTheHoldPointsWithinHalfTheReachOfBothArmsForPathMilestone);
		}
	}
	
	void bookendThePathThroughEachSecondIterationAveragePointOfHoldsWithinMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections() {
		
		Point lowestPointExtended = new Point(pathThroughEachSecondIterationAveragePointOfTheHoldPointsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections.getFirst().x, ClimbingWall.heightOfWall + 1);
		Point highestPointExtended = new Point(pathThroughEachSecondIterationAveragePointOfTheHoldPointsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections.getLast().x, -1);
		
		pathThroughEachSecondIterationAveragePointOfTheHoldPointsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections.addFirst(lowestPointExtended);
		pathThroughEachSecondIterationAveragePointOfTheHoldPointsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections.addLast(highestPointExtended);
	}
	
	void updateDestinationPointForBeltBuckleUsingPathIterator() {
		
		iAmBacktracking = false;
		
		if (averagePathIndexIterator == null) {
			
			if (!iHaveLeftGroundAndConnectedToWallWithAllFourLimbs) {
				
				establishFirstPathIteratorIndex();
				
			} else {
				
				reestablishPathIteratorIndex();
				
			}
			
		} else if (beltBucklePos.x == desiredDestinationForBeltBuckle.x && beltBucklePos.y == desiredDestinationForBeltBuckle.y) {

			updateWhetherClimberHasReachedDestinationPointThatWasBeyondASeeminglyImpossiblePosition();
			
			moveIteratorAlongPath();
		}
		
		calculateDestinationPointForBeltBuckleBasedOnPathIterator();
	}
	
	void establishFirstPathIteratorIndex() {
		
		float closestDistance = Float.MAX_VALUE;
		float secondClosestDistance = Float.MAX_VALUE;
		
		Point closestPointOnPath = new Point();
		Point secondClosestPointOnPath = new Point();
		
		for (Point p : pathThroughEachSecondIterationAveragePointOfTheHoldPointsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections) {
			
			float distanceFromBeltBuckle = CommonAlgorithms.findDistance(beltBucklePos, p);
			boolean holdIsAboveBeltBuckle = (beltBucklePos.y - p.y) > 0;
			
			if (holdIsAboveBeltBuckle && (distanceFromBeltBuckle < secondClosestDistance)) {
				
				if (distanceFromBeltBuckle <= closestDistance) {
					
					secondClosestDistance = closestDistance;
					secondClosestPointOnPath = closestPointOnPath;
					
					closestDistance = distanceFromBeltBuckle;
					closestPointOnPath = p;
				
				} else {
					
					secondClosestDistance = distanceFromBeltBuckle;
					secondClosestPointOnPath = p;
		
				}		
			}
		}
		
		Integer theIndexWithinLinkedListOfAveragePointsNearEachPointAlongShortestPathThroughHoldPontsThatIsFurtherAlongBetweenTheTwoClosestPointToBeltBuckle = Math.max(pathThroughEachSecondIterationAveragePointOfTheHoldPointsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections.indexOf(closestPointOnPath), pathThroughEachSecondIterationAveragePointOfTheHoldPointsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections.indexOf(secondClosestPointOnPath));
		
		averagePathIndexIterator = theIndexWithinLinkedListOfAveragePointsNearEachPointAlongShortestPathThroughHoldPontsThatIsFurtherAlongBetweenTheTwoClosestPointToBeltBuckle;
	}
	
	void reestablishPathIteratorIndex() {
		
		float closestDistance = Float.MAX_VALUE;
		float secondClosestDistance = Float.MAX_VALUE;
		
		Point closestPointOnPath = new Point();
		Point secondClosestPointOnPath = new Point();
		
		for (Point p : pathThroughEachSecondIterationAveragePointOfTheHoldPointsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections) {
			
			float distanceFromBeltBuckle = CommonAlgorithms.findDistance(beltBucklePos, p);

			if (distanceFromBeltBuckle < secondClosestDistance) {
				
				if (distanceFromBeltBuckle <= closestDistance) {
					
					secondClosestDistance = closestDistance;
					secondClosestPointOnPath = closestPointOnPath;
					
					closestDistance = distanceFromBeltBuckle;
					closestPointOnPath = p;
				
				} else {
					
					secondClosestDistance = distanceFromBeltBuckle;
					secondClosestPointOnPath = p;
		
				}		
			}
		}
		
		Integer theIndexWithinLinkedListOfAveragePointsNearEachPointAlongShortestPathThroughHoldPontsThatIsFurtherAlongBetweenTheTwoClosestPointToBeltBuckle = Math.max(pathThroughEachSecondIterationAveragePointOfTheHoldPointsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections.indexOf(closestPointOnPath), pathThroughEachSecondIterationAveragePointOfTheHoldPointsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections.indexOf(secondClosestPointOnPath));
		
		averagePathIndexIterator = theIndexWithinLinkedListOfAveragePointsNearEachPointAlongShortestPathThroughHoldPontsThatIsFurtherAlongBetweenTheTwoClosestPointToBeltBuckle;
	}
	
	void updateWhetherClimberHasReachedDestinationPointThatWasBeyondASeeminglyImpossiblePosition() {
		
		if (averagePathIndexIterator >= indexOfAveragePathDestinationPointAtTheSeeminglyImpossiblePosition) {
			
			hasReachedDestinationPointThatWasBeyondSeeminglyImpossiblePosition = true;
		
		}		
	}
	
	void moveIteratorAlongPath() {
		
		averagePathIndexIterator++;
	}
	
	void calculateDestinationPointForBeltBuckleBasedOnPathIterator() {
		
		desiredDestinationForBeltBuckle = pathThroughEachSecondIterationAveragePointOfTheHoldPointsWithinHalfOfTheMaximumReachOfBothArmsOfEachHoldPointOnTheShortestPathFromTopToBottomBasedOnQuadrantConnections.get(averagePathIndexIterator);
	}
	
	void updateDesiredRadialDirectionOfTravel() {
		
		desiredRadialDirectionOfTravel = CommonAlgorithms.findSlopeAsRadians(beltBucklePos, desiredDestinationForBeltBuckle);
	}
	
	//provide a list of x and y coordinates (float formatted) of points on a line from start point to end point
	ArrayList<float[]> calculateWaypointFloatCoordinates(Point startingPoint, Point destinationPoint, float travelDistanceIncrement) {
		
		float distanceToTravel = CommonAlgorithms.findDistance(startingPoint, destinationPoint);
				
		int numberOfTicks = Math.round((distanceToTravel/(travelDistanceIncrement)));
		
		ArrayList<float[]> result = new ArrayList<float[]>();
		
		float[] coordinateChanges = floatIncrementalMovementAtRate(startingPoint, destinationPoint, travelDistanceIncrement);
		
		float xDelta = coordinateChanges[0];
		float yDelta = coordinateChanges[1];
		
		float waypointFloatCoordinateX = startingPoint.x;
		float waypointFloatCoordinateY = startingPoint.y;
		
		for (int i = 1; i <= numberOfTicks; i++) {
			
			waypointFloatCoordinateX += xDelta;
			waypointFloatCoordinateY += yDelta;
			
			float[] newWaypoint = {waypointFloatCoordinateX, waypointFloatCoordinateY};
			
			result.add(newWaypoint);
		}

		float[] finalPoint = {destinationPoint.x, destinationPoint.y};
		result.add(finalPoint);

		return result;
	}
	
	
	float[] floatIncrementalMovementAtRate (Point firstPoint, Point destinationPoint, float rate) {
			
		float[] coordinateChanges = singleUnitIncrementFromOnePointToAnother(firstPoint, destinationPoint);
		
		float changeInX = (coordinateChanges[0] * rate);
		float changeInY = (coordinateChanges[1] * rate);
		
		float[] result = {changeInX, changeInY};
	
		return result;
	}
	
	float[] singleUnitIncrementFromOnePointToAnother (Point firstPoint, Point destinationPoint) {
		
		float movementSlope = CommonAlgorithms.findSlopeAsRadians(firstPoint, destinationPoint);
		
		float xShift = (float) Math.cos(movementSlope);
		float yShift = (float) ((-1) * (Math.sin(movementSlope)));
				
		float[] result = {xShift, yShift};
		return result;
	}
	
	void updateCurrentlyReachableAreaAggregate() {
		
		limbsCoordinator.updateAllCurrentReachableAreas();
		
		currentReachableAreaAggregate = new Area();
		
		currentReachableAreaAggregate.add(leftArm.currentlyReachableArea);
		currentReachableAreaAggregate.add(rightArm.currentlyReachableArea);
		currentReachableAreaAggregate.add(leftLeg.currentlyReachableArea);
		currentReachableAreaAggregate.add(rightLeg.currentlyReachableArea);		
	}
	
	void updateLimbUsageRecordForReachableHolds() {
		
		if (iAmBacktracking) {
			
			updateCurrentlyReachableAreaAggregate();
			
			clearLimbUsageRecordForHoldsInCurrentReachableAreaAggregate();
			
		}		
	}
	
	void clearLimbUsageRecordForHoldsInCurrentReachableAreaAggregate() {
		
		for (ClimbingWall.ClimbingHold h : finalizedSetOfUserEnteredHolds) {
			
			if (currentReachableAreaAggregate.contains(h)) {
				
				h.limbsThatHaveUsedHoldDuringAscent = new HashSet<>();
				
			}
		}
	}
	
	Point calculateAverageHoldPointWithinArmsReachOfAGivenPoint(Point p) {
		
		Area circleWithRadiusOfHalfTheDistanceBetweenHandsWhenFullyOutstretched = calculateAreaWithinHalfTheMaximumReachOfArmsOfAGivenPoint(p);
		
		return CommonAlgorithms.averageThePointsInAnArea(finalizedSetOfUserEnteredHolds, circleWithRadiusOfHalfTheDistanceBetweenHandsWhenFullyOutstretched);		
	}
	
	Area calculateAreaWithinHalfTheMaximumReachOfArmsOfAGivenPoint(Point p) {
		
		Ellipse2D.Float circle = new Ellipse2D.Float(p.x - (widestPossibleExtensionFromHandToHand/2), p.y - (widestPossibleExtensionFromHandToHand/2), widestPossibleExtensionFromHandToHand, widestPossibleExtensionFromHandToHand);
		
		return new Area(circle);
	}
	
	//V. METHODS RELATED TO DRAWING
	
	//determine point that is a particular distance away from a body segment point along a ray that is perpendicular to the body segment's orientation, either clockwise or counterclockwise
	Point shiftByOrientationPerpendicularly(Point startingPoint, float bodySegmentWidth, float bodySegmentOrientation, boolean counterclockwise) {
		
		float ninetyDegreesInProperRotationalDirection = Pi/2;
		
		if (!counterclockwise) {
			
			ninetyDegreesInProperRotationalDirection = ninetyDegreesInProperRotationalDirection * (-1);
		
		}
		
		float halfWidth = bodySegmentWidth/2;
		
		return CommonAlgorithms.shiftPointByShiftedOrientation(startingPoint, halfWidth, bodySegmentOrientation, ninetyDegreesInProperRotationalDirection);
	
	}
	
	Point calculateCircleDrawingPoint(Point circleCenter, float circleRadius) {
		
		return CommonAlgorithms.roundPoint((circleCenter.x - circleRadius), (circleCenter.y - circleRadius));
		
	}
	
	Point calculateFootDrawingPoint(Point anklePoint, boolean leftSide) {
		
		float adjuster = footLengthHorizontalFromAnkleToToe;
		
		if (!leftSide) {
		
			adjuster = (footLength - adjuster);
		
		}
		
		return CommonAlgorithms.roundPoint((anklePoint.x - adjuster), anklePoint.y);
	
	}
	
	void updateLeftArmDrawingPoints() {
		leftShoulderDrawingPoint = calculateCircleDrawingPoint(leftShoulderSocketPos, upperArmWidth/2);
		leftElbowDrawingPoint = calculateCircleDrawingPoint(leftElbowPos, elbowWidth/2);
		leftHandDrawingPoint = calculateCircleDrawingPoint(leftHandCenterPos, handRadius);
		
		lateralProximalLeftHumerus = shiftByOrientationPerpendicularly(leftShoulderSocketPos, upperArmWidth, leftHumerusOrientation, false);
		medialProximalLeftHumerus = shiftByOrientationPerpendicularly(leftShoulderSocketPos, upperArmWidth, leftHumerusOrientation, true);
		lateralDistalLeftHumerus = shiftByOrientationPerpendicularly(leftElbowPos, elbowWidth, leftHumerusOrientation, false);
		medialDistalLeftHumerus = shiftByOrientationPerpendicularly(leftElbowPos, elbowWidth, leftHumerusOrientation, true);
		
		lateralProximalLeftForearm = shiftByOrientationPerpendicularly(leftElbowPos, elbowWidth, leftForearmOrientation, false);
		medialProximalLeftForearm = shiftByOrientationPerpendicularly(leftElbowPos, elbowWidth, leftForearmOrientation, true);
		lateralDistalLeftForearm = shiftByOrientationPerpendicularly(leftWristPos, wristWidth, leftForearmOrientation, false);
		medialDistalLeftForearm = shiftByOrientationPerpendicularly(leftWristPos, wristWidth, leftForearmOrientation, true);	
	}
	
	void updateRightArmDrawingPoints() {
		
		rightShoulderDrawingPoint = calculateCircleDrawingPoint(rightShoulderSocketPos, upperArmWidth/2);
		rightElbowDrawingPoint = calculateCircleDrawingPoint(rightElbowPos, elbowWidth/2);
		rightHandDrawingPoint = calculateCircleDrawingPoint(rightHandCenterPos, handRadius);
	
		lateralProximalRightHumerus = shiftByOrientationPerpendicularly(rightShoulderSocketPos, upperArmWidth, rightHumerusOrientation, true);
		medialProximalRightHumerus = shiftByOrientationPerpendicularly(rightShoulderSocketPos, upperArmWidth, rightHumerusOrientation, false);
		lateralDistalRightHumerus = shiftByOrientationPerpendicularly(rightElbowPos, elbowWidth, rightHumerusOrientation, true);
		medialDistalRightHumerus = shiftByOrientationPerpendicularly(rightElbowPos, elbowWidth, rightHumerusOrientation, false);
		
		lateralProximalRightForearm = shiftByOrientationPerpendicularly(rightElbowPos, elbowWidth, rightForearmOrientation, true);
		medialProximalRightForearm = shiftByOrientationPerpendicularly(rightElbowPos, elbowWidth, rightForearmOrientation, false);
		lateralDistalRightForearm = shiftByOrientationPerpendicularly(rightWristPos, wristWidth, rightForearmOrientation, true);
		medialDistalRightForearm = shiftByOrientationPerpendicularly(rightWristPos, wristWidth, rightForearmOrientation, false);
	}
	
	void updateLeftLegDrawingPoints() {
				
		leftFootDrawingPoint = calculateFootDrawingPoint(leftAnklePos, true);
		leftHipSocketDrawingPoint = calculateCircleDrawingPoint(leftHipSocketPos, upperThighWidth/2);
		leftKneeDrawingPoint = calculateCircleDrawingPoint(leftKneePos, kneeWidth/2);
		
		lateralProximalLeftThigh = shiftByOrientationPerpendicularly(leftHipSocketPos, upperThighWidth, leftThighOrientation, false);
		medialProximalLeftThigh = shiftByOrientationPerpendicularly(leftHipSocketPos, upperThighWidth, leftThighOrientation, true);
		lateralDistalLeftThigh = shiftByOrientationPerpendicularly(leftKneePos, kneeWidth, leftThighOrientation, false);
		medialDistalLeftThigh = shiftByOrientationPerpendicularly(leftKneePos, kneeWidth, leftThighOrientation, true);
		
		lateralProximalLeftCalf = shiftByOrientationPerpendicularly(leftKneePos, kneeWidth, leftCalfOrientation, false);
		medialProximalLeftCalf = shiftByOrientationPerpendicularly(leftKneePos, kneeWidth, leftCalfOrientation, true);
		lateralDistalLeftCalf = shiftByOrientationPerpendicularly(leftAnklePos, ankleWidth, leftCalfOrientation, false);
		medialDistalLeftCalf = shiftByOrientationPerpendicularly(leftAnklePos, ankleWidth, leftCalfOrientation, true);
		
	}
	
	void updateRightLegDrawingPoints() {
		
		rightFootDrawingPoint = calculateFootDrawingPoint(rightAnklePos, false);
		rightHipSocketDrawingPoint = calculateCircleDrawingPoint(rightHipSocketPos, upperThighWidth/2);
		rightKneeDrawingPoint = calculateCircleDrawingPoint(rightKneePos, kneeWidth/2);
		
		lateralProximalRightThigh = shiftByOrientationPerpendicularly(rightHipSocketPos, upperThighWidth, rightThighOrientation, true);
		medialProximalRightThigh = shiftByOrientationPerpendicularly(rightHipSocketPos, upperThighWidth, rightThighOrientation, false);
		lateralDistalRightThigh = shiftByOrientationPerpendicularly(rightKneePos, kneeWidth, rightThighOrientation, true);
		medialDistalRightThigh = shiftByOrientationPerpendicularly(rightKneePos, kneeWidth, rightThighOrientation, false);
		
		lateralProximalRightCalf = shiftByOrientationPerpendicularly(rightKneePos, kneeWidth, rightCalfOrientation, true);
		medialProximalRightCalf = shiftByOrientationPerpendicularly(rightKneePos, kneeWidth, rightCalfOrientation, false);
		lateralDistalRightCalf = shiftByOrientationPerpendicularly(rightAnklePos, ankleWidth, rightCalfOrientation, true);
		medialDistalRightCalf = shiftByOrientationPerpendicularly(rightAnklePos, ankleWidth, rightCalfOrientation, false);
		
	}
	
	void updateHeadDrawingPoints(){
		headVisualCenter = CommonAlgorithms.findMidpoint(headBasePos, headTopPos);
		headDrawingPoint = calculateCircleDrawingPoint(headVisualCenter, headRadius);
		neckDrawingPoint.x = Math.round(neckBaseBetweenShoulderSocketsPos.x -(neckThickness/2));
		neckDrawingPoint.y = Math.round(neckBaseBetweenShoulderSocketsPos.y - (distanceFromNeckBaseAtShoulderSocketLevelToBottomOfHeadCircle + (headRadius * ClimbingSimulator.constants.FactorForDeterminingAdditionalVerticalDistanceOfNeckDrawingAreaDeterminedAsAFractionOfHeadRadius)));		
	}
	
	void updateAllDrawingPoints () {
		updateLeftArmDrawingPoints();
		updateRightArmDrawingPoints();
		updateLeftLegDrawingPoints();
		updateRightLegDrawingPoints();
		updateHeadDrawingPoints();
	}
	
	void pauseForAnimationTiming() {
		
		while (!wallHasDrawnTheLatestClimberMoves) {
			try {
				Thread.sleep(ClimbingSimulator.constants.MovementPromptPauseLength);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		wallHasDrawnTheLatestClimberMoves = false;
	}
	
	void drawClimber(Graphics2D g2d) {
		
		Area upperBodyLimbsArea;
		Area lowerBodyArea;
		Area shirtTorsoArea;
		Area leftFoot;
		Area rightFoot;
		
		Path2D.Double leftHumerus;
		Path2D.Double rightHumerus;
		Path2D.Double leftForearm;
		Path2D.Double rightForearm;
		
		Path2D.Double pantsPelvis;
		Path2D.Double leftThigh;
		Path2D.Double rightThigh;
		Path2D.Double leftCalf;
		Path2D.Double rightCalf;
		
		Path2D.Double torsoShirt;
		
		//paint hands
		g2d.setPaint(ClimbingSimulator.constants.SkinTone);
		g2d.fill(new Ellipse2D.Float(leftHandDrawingPoint.x,leftHandDrawingPoint.y,2 * handRadius, 2 * handRadius));
		g2d.fill(new Ellipse2D.Float(rightHandDrawingPoint.x,rightHandDrawingPoint.y,2 * handRadius,2 * handRadius));
		
		if(!leftArm.isConnectedToSomething) {
			Point thumbTip = CommonAlgorithms.shiftBySlopeAsRadians(leftWristPos, thumbLength, (float) (leftWristPivot - Pi/3.8), true);
			Point indexTip = CommonAlgorithms.shiftBySlopeAsRadians(leftWristPos, (float) (fingerLength * 0.95), leftWristPivot - Pi/15, true);
			Point middleTip = CommonAlgorithms.shiftBySlopeAsRadians(leftWristPos, fingerLength, leftWristPivot, true);
			Point ringTip = CommonAlgorithms.shiftBySlopeAsRadians(leftWristPos, (float) (fingerLength * 0.95), leftWristPivot + Pi/18, true);
			Point pinkieTip = CommonAlgorithms.shiftBySlopeAsRadians(leftWristPos, (float) (fingerLength * 0.85), leftWristPivot + Pi/8, true);
			
			g2d.setStroke(new BasicStroke(thumbWidth));
			g2d.drawLine(leftWristPos.x, leftWristPos.y, thumbTip.x, thumbTip.y);
			
			g2d.setStroke(new BasicStroke(fingerWidth));
			g2d.drawLine(leftWristPos.x, leftWristPos.y, indexTip.x, indexTip.y);
			g2d.drawLine(leftWristPos.x, leftWristPos.y, middleTip.x, middleTip.y);
			g2d.drawLine(leftWristPos.x, leftWristPos.y, ringTip.x, ringTip.y);
			g2d.drawLine(leftWristPos.x, leftWristPos.y, pinkieTip.x, pinkieTip.y);
		}
		
		if(!rightArm.isConnectedToSomething) {
			Point thumbTip = CommonAlgorithms.shiftBySlopeAsRadians(rightWristPos, thumbLength, (float) (rightWristPivot + Pi/3.8), true);
			Point indexTip = CommonAlgorithms.shiftBySlopeAsRadians(rightWristPos, (float) (fingerLength * 0.95), rightWristPivot + Pi/15, true);
			Point middleTip = CommonAlgorithms.shiftBySlopeAsRadians(rightWristPos, fingerLength, rightWristPivot, true);
			Point ringTip = CommonAlgorithms.shiftBySlopeAsRadians(rightWristPos, (float) (fingerLength * 0.95), rightWristPivot - Pi/18, true);
			Point pinkieTip = CommonAlgorithms.shiftBySlopeAsRadians(rightWristPos, (float) (fingerLength * 0.85), rightWristPivot - Pi/8, true);
			
			g2d.setStroke(new BasicStroke(thumbWidth));
			g2d.drawLine(rightWristPos.x, rightWristPos.y, thumbTip.x, thumbTip.y);
			
			g2d.setStroke(new BasicStroke(fingerWidth));
			g2d.drawLine(rightWristPos.x, rightWristPos.y, indexTip.x, indexTip.y);
			g2d.drawLine(rightWristPos.x, rightWristPos.y, middleTip.x, middleTip.y);
			g2d.drawLine(rightWristPos.x, rightWristPos.y, ringTip.x, ringTip.y);
			g2d.drawLine(rightWristPos.x, rightWristPos.y, pinkieTip.x, pinkieTip.y);
		}
		
		g2d.setStroke(new BasicStroke());
		
		//paint shoes
		leftFoot = new Area(new Rectangle2D.Float(leftFootDrawingPoint.x, leftFootDrawingPoint.y, footLength, footHeight));
		leftFoot.add(new Area(new Ellipse2D.Float(Math.round(leftFootDrawingPoint.x - (footHeight/2)), leftFootDrawingPoint.y, footHeight, footHeight)));
		
		rightFoot = new Area(new Rectangle2D.Float(rightFootDrawingPoint.x, rightFootDrawingPoint.y, footLength, footHeight));
		rightFoot.add(new Area(new Ellipse2D.Float(Math.round(rightFootDrawingPoint.x + footLength - (footHeight/2)), rightFootDrawingPoint.y, footHeight, footHeight)));
			
		g2d.setPaint(Color.BLACK);
		g2d.fill(leftFoot);
		g2d.fill(rightFoot);
		
		//paint rope
		g2d.setPaint(ClimbingSimulator.constants.RopeColor);
		g2d.setStroke(new BasicStroke(ClimbingSimulator.constants.RopeDiameter * ClimbingSimulator.constants.SizeFactor));
		g2d.drawLine(anchorPoint.x, anchorPoint.y, beltBucklePos.x, beltBucklePos.y);
		
		g2d.setStroke(new BasicStroke());

		//paint shirt sleeves
		upperBodyLimbsArea = new Area();
		
		leftHumerus = new Path2D.Double();
		leftHumerus.moveTo(lateralProximalLeftHumerus.x, lateralProximalLeftHumerus.y);
		leftHumerus.lineTo(lateralDistalLeftHumerus.x, lateralDistalLeftHumerus.y);
		leftHumerus.lineTo(medialDistalLeftHumerus.x, medialDistalLeftHumerus.y);
		leftHumerus.lineTo(medialProximalLeftHumerus.x, medialProximalLeftHumerus.y);
		leftHumerus.closePath();
		Area leftHumerusArea = new Area(leftHumerus);
		upperBodyLimbsArea.add(leftHumerusArea);
		
		rightHumerus = new Path2D.Double();
		rightHumerus.moveTo(lateralProximalRightHumerus.x, lateralProximalRightHumerus.y);
		rightHumerus.lineTo(lateralDistalRightHumerus.x, lateralDistalRightHumerus.y);
		rightHumerus.lineTo(medialDistalRightHumerus.x, medialDistalRightHumerus.y);
		rightHumerus.lineTo(medialProximalRightHumerus.x, medialProximalRightHumerus.y);
		rightHumerus.closePath();
		Area rightHumerusArea = new Area(rightHumerus);
		upperBodyLimbsArea.add(rightHumerusArea);
		
		leftForearm = new Path2D.Double();
		leftForearm.moveTo(lateralProximalLeftForearm.x, lateralProximalLeftForearm.y);
		leftForearm.lineTo(lateralDistalLeftForearm.x, lateralDistalLeftForearm.y);
		leftForearm.lineTo(medialDistalLeftForearm.x, medialDistalLeftForearm.y);
		leftForearm.lineTo(medialProximalLeftForearm.x, medialProximalLeftForearm.y);
		leftForearm.closePath();
		Area leftForearmArea = new Area(leftForearm);
		upperBodyLimbsArea.add(leftForearmArea);
		
		rightForearm = new Path2D.Double();
		rightForearm.moveTo(lateralProximalRightForearm.x, lateralProximalRightForearm.y);
		rightForearm.lineTo(lateralDistalRightForearm.x, lateralDistalRightForearm.y);
		rightForearm.lineTo(medialDistalRightForearm.x, medialDistalRightForearm.y);
		rightForearm.lineTo(medialProximalRightForearm.x, medialProximalRightForearm.y);
		rightForearm.closePath();
		Area rightForearmArea = new Area(rightForearm);
		upperBodyLimbsArea.add(rightForearmArea);
		
		upperBodyLimbsArea.add(new Area(new Ellipse2D.Float(leftShoulderDrawingPoint.x, leftShoulderDrawingPoint.y, upperArmWidth, upperArmWidth)));
		upperBodyLimbsArea.add(new Area(new Ellipse2D.Float(rightShoulderDrawingPoint.x, rightShoulderDrawingPoint.y, upperArmWidth, upperArmWidth)));
		upperBodyLimbsArea.add(new Area(new Ellipse2D.Float(leftElbowDrawingPoint.x, leftElbowDrawingPoint.y, elbowWidth, elbowWidth)));
		upperBodyLimbsArea.add(new Area(new Ellipse2D.Float(rightElbowDrawingPoint.x, rightElbowDrawingPoint.y, elbowWidth, elbowWidth)));
		
		g2d.setPaint(ClimbingSimulator.constants.ShirtColor);
		g2d.fill(upperBodyLimbsArea);
				
		//paint pants
		lowerBodyArea = new Area();
		
		pantsPelvis = new Path2D.Double();
		pantsPelvis.moveTo(leftWaistDrawingPoint.x, leftWaistDrawingPoint.y);
		pantsPelvis.lineTo(leftOuterPelvisHinge.x, leftOuterPelvisHinge.y);
		pantsPelvis.lineTo(leftInnerPelvisHinge.x, leftInnerPelvisHinge.y);
		pantsPelvis.lineTo(inseam.x, inseam.y);
		pantsPelvis.lineTo(rightInnerPelvisHinge.x, rightInnerPelvisHinge.y);
		pantsPelvis.lineTo(rightOuterPelvisHinge.x, rightOuterPelvisHinge.y);
		pantsPelvis.lineTo(rightWaistDrawingPoint.x, rightWaistDrawingPoint.y);
		pantsPelvis.closePath();
		Area pantsPelvisArea = new Area(pantsPelvis);
		lowerBodyArea.add(pantsPelvisArea);
		
		leftThigh = new Path2D.Double();
		leftThigh.moveTo(lateralProximalLeftThigh.x, lateralProximalLeftThigh.y);
		leftThigh.lineTo(lateralDistalLeftThigh.x, lateralDistalLeftThigh.y);
		leftThigh.lineTo(medialDistalLeftThigh.x, medialDistalLeftThigh.y);
		leftThigh.lineTo(medialProximalLeftThigh.x, medialProximalLeftThigh.y);
		leftThigh.closePath();
		Area leftThighArea = new Area(leftThigh);
		lowerBodyArea.add(leftThighArea);
		
		rightThigh = new Path2D.Double();
		rightThigh.moveTo(lateralProximalRightThigh.x, lateralProximalRightThigh.y);
		rightThigh.lineTo(lateralDistalRightThigh.x, lateralDistalRightThigh.y);
		rightThigh.lineTo(medialDistalRightThigh.x, medialDistalRightThigh.y);
		rightThigh.lineTo(medialProximalRightThigh.x, medialProximalRightThigh.y);
		rightThigh.closePath();
		Area rightThighArea = new Area(rightThigh);
		lowerBodyArea.add(rightThighArea);
		
		leftCalf = new Path2D.Double();
		leftCalf.moveTo(lateralProximalLeftCalf.x, lateralProximalLeftCalf.y);
		leftCalf.lineTo(lateralDistalLeftCalf.x, lateralDistalLeftCalf.y);
		leftCalf.lineTo(medialDistalLeftCalf.x, medialDistalLeftCalf.y);
		leftCalf.lineTo(medialProximalLeftCalf.x, medialProximalLeftCalf.y);
		leftCalf.closePath();
		Area leftCalfArea = new Area(leftCalf);
		lowerBodyArea.add(leftCalfArea);
		
		rightCalf = new Path2D.Double();
		rightCalf.moveTo(lateralProximalRightCalf.x, lateralProximalRightCalf.y);
		rightCalf.lineTo(lateralDistalRightCalf.x, lateralDistalRightCalf.y);
		rightCalf.lineTo(medialDistalRightCalf.x, medialDistalRightCalf.y);
		rightCalf.lineTo(medialProximalRightCalf.x, medialProximalRightCalf.y);
		rightCalf.closePath();
		Area rightCalfArea = new Area(rightCalf);
		lowerBodyArea.add(rightCalfArea);
		
		lowerBodyArea.add(new Area(new Ellipse2D.Float(leftHipSocketDrawingPoint.x, leftHipSocketDrawingPoint.y, upperThighWidth, upperThighWidth)));
		lowerBodyArea.add(new Area(new Ellipse2D.Float(rightHipSocketDrawingPoint.x, rightHipSocketDrawingPoint.y, upperThighWidth, upperThighWidth)));
		lowerBodyArea.add(new Area(new Ellipse2D.Float(leftKneeDrawingPoint.x, leftKneeDrawingPoint.y, kneeWidth, kneeWidth)));
		lowerBodyArea.add(new Area(new Ellipse2D.Float(rightKneeDrawingPoint.x, rightKneeDrawingPoint.y, kneeWidth, kneeWidth)));

		g2d.setPaint(ClimbingSimulator.constants.PantsColor);
		g2d.fill(lowerBodyArea);
		
		//paint neck
		g2d.setPaint(ClimbingSimulator.constants.SkinTone);
		g2d.fill(new Rectangle2D.Float(neckDrawingPoint.x, neckDrawingPoint.y, neckThickness, (distanceFromNeckBaseAtShoulderSocketLevelToBottomOfHeadCircle + (headRadius * ClimbingSimulator.constants.FactorForDeterminingAdditionalVerticalDistanceOfNeckDrawingAreaDeterminedAsAFractionOfHeadRadius))));
		
		//paint head
		g2d.setPaint(ClimbingSimulator.constants.HairColor);
		g2d.fill(new Ellipse2D.Float(headDrawingPoint.x,headDrawingPoint.y,(2 * headRadius),(2 * headRadius)));
	
		//paint torso element of shirt
		torsoShirt = new Path2D.Double();
		torsoShirt.moveTo(Math.round(leftShoulderSocketPos.x - upperArmWidth/2), leftShoulderSocketPos.y);
		torsoShirt.lineTo(leftLatTorsoInsertionPoint.x, leftLatTorsoInsertionPoint.y);
		torsoShirt.lineTo(leftWaistDrawingPoint.x, leftWaistDrawingPoint.y);
		torsoShirt.lineTo(rightWaistDrawingPoint.x, rightWaistDrawingPoint.y);
		torsoShirt.lineTo(rightLatTorsoInsertionPoint.x, rightLatTorsoInsertionPoint.y);
		torsoShirt.lineTo(Math.round(rightShoulderSocketPos.x + upperArmWidth/2), rightShoulderSocketPos.y);
		torsoShirt.lineTo(rightTrapNeckIntersectionPoint.x, rightTrapNeckIntersectionPoint.y);
		torsoShirt.lineTo(leftTrapNeckIntersectionPoint.x, leftTrapNeckIntersectionPoint.y);
		torsoShirt.closePath();
		shirtTorsoArea = new Area(torsoShirt);
	
		g2d.setPaint(ClimbingSimulator.constants.ShirtColor);
		g2d.fill(shirtTorsoArea);

	}
}

class ClimbingWall extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private static float Pi = ClimbingSimulator.constants.Pi;
	
	public static int heightOfWall;
	
	static Climber wallClimber;
	
	SetOfHolds<ClimbingHold> setOfIntroductionHolds;
	SetOfHolds<ClimbingHold> setOfUserEnteredHolds;
	
	private boolean displayingIntroduction;
	private boolean requestForAssistanceVisible;
	private boolean startButtonVisible;
	
	private boolean showingInitialInstructions;
	
	private boolean holdsGoodIndividually;
	private boolean holdsGoodAsANetwork;
	private boolean showingWebOfConnections;
	
	private boolean displayingFeedback;
	private boolean tryingToAddHolds;
	private boolean waitingToBeFinalized;
	private boolean wallFinalized;
	private boolean showingClimber;
	private boolean showingAcceptanceOfDefeat;
	private boolean showingFinalMessageOfAppreciation;
	
	ClimbingWall() {
		
		establishWallHeight();

		wallClimber = new Climber();
		
		setOfUserEnteredHolds = new SetOfHolds<ClimbingHold>();
		
		setBackground(ClimbingSimulator.constants.WallColor);
		
		initializeWall();
		
		addMouseListener (new MouseAdapter () {
			
			@Override
			public void mousePressed (MouseEvent e) {

				analyzeMousePress(e);
				
			}
		});
		
		addKeyListener (new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				analyzeKeyRelease(e);
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		
		runIntroduction();
	}
	
	void establishWallHeight() {
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
		
		heightOfWall = (int) (screenSize.getHeight() - (50 + insets.bottom));

	}

	void initializeWall() {
		
		holdsGoodIndividually = false;
		holdsGoodAsANetwork = false;
		showingWebOfConnections = false;
		
		displayingFeedback = false;
		waitingToBeFinalized = false;
		wallFinalized = false;
		showingClimber = false;
		showingAcceptanceOfDefeat = false;
		showingFinalMessageOfAppreciation = false;
		
	}
	
	void analyzeMousePress(MouseEvent e) {
		
		if (displayingIntroduction) {
			
			possiblyBeginRouteSetting(e);
			
		} else {
		
			showingInitialInstructions = false;
			
			tryingToAddHolds = true;
			resetWallOrUpdateStateOfHolds(e);
			repaint();
		
		}		
	}
	
	void analyzeKeyRelease(KeyEvent e) {
		
		tryingToAddHolds = false;
		updateStateOfHolds(e);
		updateClimberOnStateOfWall();
		repaint();		
	}
	
	void possiblyBeginRouteSetting(MouseEvent e) {
		
		if (userWantsToBeginRouteSetting(e)) {
		
			displayingIntroduction = false;
			
			showingInitialInstructions = true;
			
			repaint();

		}
	}
		
	boolean userWantsToBeginRouteSetting(MouseEvent e) {
		
		int xValueForMouseClick = e.getX();
		int yValueForMouseClick = e.getY();
		
		if (xValueForMouseClick < ClimbingSimulator.constants.XValueForStartButtonLeftBorder) {
			return false;
		}
		
		if (xValueForMouseClick > ClimbingSimulator.constants.XValueForStartButtonLeftBorder + ClimbingSimulator.constants.WidthOfStartButton) {
			return false;
		}
		
		if (yValueForMouseClick < ClimbingSimulator.constants.YValueForStartButtonUpperBorder) {
			return false;
		}
		
		if (yValueForMouseClick > ClimbingSimulator.constants.YValueForStartButtonUpperBorder + ClimbingSimulator.constants.HeightOfStartButton) {
			return false;
		}
		
		return true;
		
	}
	
	void resetWallOrUpdateStateOfHolds(MouseEvent e) {
		
		if (userIsRequestingAWallReset(e)) {
			
			resetWall();
			
			return;
		}
		
		if (!wallFinalized) {
			
			if(e.getButton() == 1) {
				
				setOfUserEnteredHolds.addHold(e);
				
			} else {
				
				setOfUserEnteredHolds.addRandomSprayOfHolds(e);
			}
		}
		
		if (waitingToBeFinalized) {
			
			waitingToBeFinalized = false;
		}
	}
	
	boolean userIsRequestingAWallReset(MouseEvent e) {
		
		int xValueForMouseClick = e.getX();
		int yValueForMouseClick = e.getY();
		
		if (xValueForMouseClick < ClimbingSimulator.constants.XValueForResetButtonLeftBorder) {
			return false;
		}
		
		if (xValueForMouseClick > ClimbingSimulator.constants.XValueForResetButtonLeftBorder + ClimbingSimulator.constants.WidthOfResetButton) {
			return false;
		}
		
		if (yValueForMouseClick < ClimbingSimulator.constants.YValueForResetButtonUpperBorder) {
			return false;
		}
		
		if (yValueForMouseClick > ClimbingSimulator.constants.YValueForResetButtonUpperBorder + ClimbingSimulator.constants.HeightOfResetButton) {
			return false;
		}
		
		return true;
		
	}
	
	void resetWall() {
		
		wallClimber = new Climber();
		
		setOfUserEnteredHolds = new SetOfHolds<ClimbingHold>();
		
		showingInitialInstructions = true;
		
		try {
			Thread.sleep((1000/ClimbingSimulator.constants.DisplayTickRate) + 1);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		initializeWall();
		
	}
	
	void updateStateOfHolds(KeyEvent e) {
		
		setOfUserEnteredHolds.tooClose = false;
		
		if (waitingToBeFinalized  && !wallFinalized) {
			
			lockInFinalStateOfWall();
		}
				
		if (!wallFinalized) {
			
			seeIfHoldsAreSpacedProperly();
		}
	}
		
	void updateClimberOnStateOfWall() {
		
		if (wallFinalized && !wallClimber.isClimberOnTheWall()) {
			
			notifyClimberThatWallIsReady();
		}
	}
	
	void runIntroduction() {
		
		displayingIntroduction = true;
				
		randomlyGenerateSetOfIntroductionHolds();
		
		(new Thread() {public void run() {
			
			int animationLoop = 0;
			
			int indexOfHoldToMakeVisible = 0;
			int numberOfTicksPerHoldVisibilityChange = (int) (ClimbingSimulator.constants.DisplayTickRate/ClimbingSimulator.constants.numberOfHoldsToDisplayPerSecondOnIntroductionScreen);
			
			while(displayingIntroduction) {						
				
				if (animationLoop%numberOfTicksPerHoldVisibilityChange == 0) {
					
					setOfIntroductionHolds.get(indexOfHoldToMakeVisible).isVisibleInIntroductionAnimation = true;
					
					indexOfHoldToMakeVisible++;
					
				}
				
				if (!requestForAssistanceVisible && (animationLoop > (ClimbingSimulator.constants.DisplayTickRate * 3))) {
					
					requestForAssistanceVisible = true;
					
				}
				
				if (!startButtonVisible && (animationLoop > (ClimbingSimulator.constants.DisplayTickRate * 4))) {
					
					startButtonVisible = true;
					
				}
				
				animationLoop++;
				
				try {
					Thread.sleep(1000/ClimbingSimulator.constants.DisplayTickRate);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				repaint();

			}
			
			repaint();
			
		}}).start();		
	}
	
	void randomlyGenerateSetOfIntroductionHolds() {
		
		setOfIntroductionHolds = new SetOfHolds<ClimbingHold>();
		
		Random rand = new Random();
		
		float radianSlopeOfSegment;
		
		Point currentWholeSegmentStartingPoint = new Point();
		float lengthOfCurrentWholeSegment;
		Point currentWholeSegmentEndingPoint = new Point();
		
		float lengthOfCurrentPartialSegment;
		Point currentPartialSegmentEndingPoint = new Point();
		
		float lengthOfPerpendicularSegment;
		
		currentWholeSegmentStartingPoint.x = Math.round(ClimbingSimulator.constants.WidthOfBufferZoneForRandomHoldGeneration + (rand.nextFloat() * (ClimbingSimulator.constants.WallWidth - (2 * ClimbingSimulator.constants.WidthOfBufferZoneForRandomHoldGeneration)))); 
		currentWholeSegmentStartingPoint.y = heightOfWall;
		
		while (currentWholeSegmentStartingPoint.y > 0) {
			
			if (currentWholeSegmentStartingPoint.x < ClimbingSimulator.constants.WidthOfBufferZoneForRandomHoldGeneration) {
				
				radianSlopeOfSegment = (Pi/2) - (rand.nextFloat() * ClimbingSimulator.constants.RadianLimitOfCentralDirectionforRandomHoldGeneration);
				
			} else if (currentWholeSegmentStartingPoint.x > (ClimbingSimulator.constants.WallWidth - ClimbingSimulator.constants.WidthOfBufferZoneForRandomHoldGeneration)) {
			
				radianSlopeOfSegment = (Pi/2) + (rand.nextFloat() * ClimbingSimulator.constants.RadianLimitOfCentralDirectionforRandomHoldGeneration);
				
			} else {	
			
				radianSlopeOfSegment = (Pi/2 + ClimbingSimulator.constants.RadianLimitOfCentralDirectionforRandomHoldGeneration) - (rand.nextFloat() * (2 * ClimbingSimulator.constants.RadianLimitOfCentralDirectionforRandomHoldGeneration)); 
			
			}	
				
			lengthOfCurrentWholeSegment = ClimbingSimulator.constants.DistanceLimitOfWholeSegmentOfCentralDirectionForRandomHoldGeneration * rand.nextFloat();
			
			currentWholeSegmentEndingPoint = CommonAlgorithms.shiftBySlopeAsRadians(currentWholeSegmentStartingPoint, lengthOfCurrentWholeSegment, radianSlopeOfSegment, true);
			
			lengthOfCurrentPartialSegment = 0.0f;
			
			while (lengthOfCurrentPartialSegment < lengthOfCurrentWholeSegment) {
				
				lengthOfCurrentPartialSegment += rand.nextFloat() * ClimbingSimulator.constants.DistanceLimitOfPartialSegmentOfCentralDirectionForRandomHoldGeneration;
				
				currentPartialSegmentEndingPoint = CommonAlgorithms.shiftBySlopeAsRadians(currentWholeSegmentStartingPoint, lengthOfCurrentPartialSegment, radianSlopeOfSegment, true);
				
				lengthOfPerpendicularSegment = rand.nextFloat() * ClimbingSimulator.constants.DistanceLimitOfSegmentUsedToPlaceHoldPerpendicularToCentralDirectionForRandomHoldGeneration;
											
				float ninetyDegreesInRandomRotationalDirection;
				
				if (rand.nextBoolean()) {
					
					ninetyDegreesInRandomRotationalDirection = (Pi/2);
				
				} else {
					
					ninetyDegreesInRandomRotationalDirection = (Pi/2) * (-1.0f);
					
				}
				
				Point pointPerpendicularToRandomWholeSegment = CommonAlgorithms.shiftPointByShiftedOrientation (currentPartialSegmentEndingPoint, lengthOfPerpendicularSegment, radianSlopeOfSegment, ninetyDegreesInRandomRotationalDirection);
				
				ClimbingHold holdPerpendicularToRandomWholeSegment = new ClimbingHold(pointPerpendicularToRandomWholeSegment.x, pointPerpendicularToRandomWholeSegment.y);
				
				setOfIntroductionHolds.add(holdPerpendicularToRandomWholeSegment);
				
			}
			
			currentWholeSegmentStartingPoint = currentWholeSegmentEndingPoint;
		}
	}
	
	void receiveAndProcessNoticeThatClimberIsBeginningClimbingProcess() {
						
		showClimberMovingOnUserEnteredHolds();
	}
		
	class ClimbingHold extends Point {
		
		private static final long serialVersionUID = 1L;
		
		boolean isVisibleInIntroductionAnimation;
		
		boolean hasProperlySpacedHoldBasedOnReachableRangeInQuadrantI;
		boolean hasProperlySpacedHoldBasedOnReachableRangeInQuadrantII;
		boolean hasProperlySpacedHoldBasedOnReachableRangeInQuadrantIII;
		boolean hasProperlySpacedHoldBasedOnReachableRangeInQuadrantIV;

		boolean hasASetOfProperlySpacedHoldsBasedOnReachableRangeInRelevantQuadrants;
				
		boolean needsAHoldWithinReachableRangeSomewhereAbove;
		boolean needsAHoldWithinReachableRangeSomewhereBelow;
		boolean needsAHoldWithinReachableRangeInIOrIII;
		boolean needsAHoldWithinReachableRangeInIIOrIV;
		
		boolean withinTopOfWallZone;
		boolean withinBottomOfWallZone;
		
		float shortestDistanceToABottomHoldViaQuadrantSizeLimitsOnConnections;
		float shortestDistanceToATopHoldViaQuadrantSizeLimitsOnConnections;
		
		int adjacentHoldThatBeginsShortestPathToBottomOfWallViaQuadrantSizeLimitsOnConnections;
		
		boolean connectedToTop;
		boolean connectedToBottom;
		
		ConnectionAnalysisState connectionAnalysisState;
		
		boolean hasHoldInQuadrantI;
		boolean hasHoldInQuadrantII;
		boolean hasHoldInQuadrantIII;
		boolean hasHoldInQuadrantIV;
		
		boolean isABorderHold;

		HashSet<Integer> holdsThatAreCloseEnoughToPossiblyBeInAQuadrant;
		
		HashSet<LimbIdentifier> limbsThatHaveUsedHoldDuringAscent;
		HashSet<LimbIdentifier> limbsThatHaveUsedHoldDuringBacktrackingSequence;
		
		float opacityPercentage;
							
		ClimbingHold (int x, int y) {
			
			this.x = x;
			this.y = y;
			
			isVisibleInIntroductionAnimation = false;
			
			holdsThatAreCloseEnoughToPossiblyBeInAQuadrant = new HashSet<Integer>();
			
			hasASetOfProperlySpacedHoldsBasedOnReachableRangeInRelevantQuadrants = true;
			
			needsAHoldWithinReachableRangeSomewhereAbove = false;
			needsAHoldWithinReachableRangeSomewhereBelow = false;
			needsAHoldWithinReachableRangeInIOrIII = false;
			needsAHoldWithinReachableRangeInIIOrIV = false;
			
			connectedToTop = false;
			connectedToBottom = false;
			
			adjacentHoldThatBeginsShortestPathToBottomOfWallViaQuadrantSizeLimitsOnConnections = -1;
			
			limbsThatHaveUsedHoldDuringAscent = new HashSet<>();
			limbsThatHaveUsedHoldDuringBacktrackingSequence = new HashSet<>();
		}
	}
	
	class SetOfHolds<T extends ClimbingHold> extends ArrayList<T> {
		
		private static final long serialVersionUID = 1L;
		
		float interQuadrantGapInDegrees;
		
		boolean someHoldsDoNotHaveEnoughNeighbors;
		
		HashSet<Integer> holdsWithinBottomOfWallZone;
		HashSet<Integer> holdsWithinTopOfWallZone;
		
		boolean thereIsAHoldInTheZoneAtTheBottomOfTheWall;
		boolean thereIsAHoldInTheZoneAtTheTopOfTheWall;
		
		HashSet<Integer> holdsConnectedToBottomOfWall;
		HashSet<Integer> holdsConnectedToTopOfWall;
				
		boolean climberCannotReachTheTopFromTheBottomWithCurrentHoldNetwork;
			
		int latestHold;
		
		boolean tooClose;
		
		SetOfHolds () {
			
			latestHold = 0;
			tooClose = false;
			
			someHoldsDoNotHaveEnoughNeighbors = false;
			
			interQuadrantGapInDegrees = ClimbingSimulator.constants.InterQuadrantGapInDegrees;
			
			thereIsAHoldInTheZoneAtTheBottomOfTheWall = true;
			thereIsAHoldInTheZoneAtTheTopOfTheWall = true;
			
			holdsWithinBottomOfWallZone = new HashSet<Integer>();
			holdsWithinTopOfWallZone = new HashSet<Integer>();
			
			holdsConnectedToBottomOfWall = new HashSet<Integer>();
			holdsConnectedToTopOfWall = new HashSet<Integer>();
		}
			
		@SuppressWarnings("unchecked")
		void addHold(MouseEvent e) {
			
			ClimbingHold hold = new ClimbingHold(e.getX(),e.getY());
			
			if ((!contains(hold)) && (hold.y > 0) && (hold.y < heightOfWall) && (hold.x > 0) && (hold.x < ClimbingSimulator.constants.WallWidth)) {
				
				add(latestHold, (T) hold);
				
				checkWhetherSingleAddedHoldIsTooCloseToOthers(latestHold);
				
				latestHold++;
			}
		}
		
		@SuppressWarnings("unchecked")
		void addRandomSprayOfHolds(MouseEvent e) {
			
			ClimbingHold center = new ClimbingHold(e.getX(), e.getY());
			
			if((!contains(center)) && (center.y > 0) && (center.y < heightOfWall) && (center.x > 0) && (center.x < ClimbingSimulator.constants.WallWidth)) {
				
				add(latestHold, (T) center);
				latestHold++;
				
			} else {
				return;
			}
			
			int numberOfHoldsToAddOtherThanCenter = ClimbingSimulator.constants.NumberOfRandomHoldsInRandomSpray;
			
			Area quadrantI = CommonAlgorithms.createQuadrantOfProperHoldSpacingBasedOnReachableRange(center, Direction.UPANDRIGHT, interQuadrantGapInDegrees);
			Area quadrantII = CommonAlgorithms.createQuadrantOfProperHoldSpacingBasedOnReachableRange(center, Direction.UPANDLEFT, interQuadrantGapInDegrees);
			Area quadrantIII = CommonAlgorithms.createQuadrantOfProperHoldSpacingBasedOnReachableRange(center, Direction.DOWNANDLEFT, interQuadrantGapInDegrees);
			Area quadrantIV = CommonAlgorithms.createQuadrantOfProperHoldSpacingBasedOnReachableRange(center, Direction.DOWNANDRIGHT, interQuadrantGapInDegrees);
			
			while (true) {
				
				ClimbingHold randomHold = createARandomHoldWithinRandomSprayRadiusConstraintsOfCenterHold(center);
				
				if ((quadrantI.contains(randomHold) || quadrantIII.contains(randomHold)) && (!contains(randomHold)) && (randomHold.y > 0) && (randomHold.y < heightOfWall) && (randomHold.x > 0) && (randomHold.x < ClimbingSimulator.constants.WallWidth)) {
					
					add(latestHold, (T) randomHold);
					latestHold++;
					numberOfHoldsToAddOtherThanCenter--;
					break;
				}
			}
			
			while (true) {
				
				ClimbingHold randomHold = createARandomHoldWithinRandomSprayRadiusConstraintsOfCenterHold(center);
				
				if ((quadrantII.contains(randomHold) || quadrantIV.contains(randomHold)) && (!contains(randomHold)) && (randomHold.y > 0) && (randomHold.y < heightOfWall) && (randomHold.x > 0) && (randomHold.x < ClimbingSimulator.constants.WallWidth)) {
					
					add(latestHold, (T) randomHold);
					latestHold++;
					numberOfHoldsToAddOtherThanCenter--;
					break;
				}
			}
			
			for (int i = 0; i < numberOfHoldsToAddOtherThanCenter; i++) {
				
				ClimbingHold randomHold = createARandomHoldWithinRandomSprayRadiusConstraintsOfCenterHold(center);
				
				if((!contains(randomHold)) && (randomHold.y > 0) && (randomHold.y < heightOfWall) && (randomHold.x > 0) && (randomHold.x < ClimbingSimulator.constants.WallWidth)) {
					add(latestHold, (T) randomHold);
					latestHold++;
				}		
			}			
		}
		
		ClimbingHold createARandomHoldWithinRandomSprayRadiusConstraintsOfCenterHold (ClimbingHold centerHold) {
			
			Random rand = new Random();
			
			float randomRadius = ((rand.nextFloat() * (ClimbingSimulator.constants.RandomHoldSprayRadius - ClimbingSimulator.constants.InterholdDistanceTooClose)) + ClimbingSimulator.constants.InterholdDistanceTooClose) * ClimbingSimulator.constants.SizeFactor;
			float randomDirection = rand.nextFloat() * 2 * Pi;
			
			Point randomPoint = CommonAlgorithms.shiftBySlopeAsRadians(centerHold, randomRadius, randomDirection, true);
			
			return new ClimbingHold(randomPoint.x, randomPoint.y);
		}
		
		void checkWhetherSingleAddedHoldIsTooCloseToOthers (int holdIndex) {	
			
			(new Thread() {public void run() {
				
				for (int i = holdIndex - 1; i >= 0; i--) {
					
					float proximity = CommonAlgorithms.findDistance((Point) get(holdIndex), (Point) get(i));
					
					if (proximity < ClimbingSimulator.constants.InterholdDistanceTooClose * ClimbingSimulator.constants.SizeFactor) {
						
						tooClose = true;
						repaint();
						
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						tooClose = false;
						
						repaint();
						return;
					}
				}
			}}).start();
		}
		
		

		void analyzeBottomAndTopZones() {
		
			findHoldsInTheZoneAtTheBottomOfTheWall();
			findHoldsInTheZoneAtTheTopOfTheWall();
			
			checkTheZoneAtTheBottomOfTheWallForAHold();
			checkTheZoneAtTheTopOfTheWallForAHold();
		}
		
		void findHoldsInTheZoneAtTheBottomOfTheWall() {
			
			float bottomOfWall = heightOfWall;
			float bottomOfWallZoneBoundary = bottomOfWall - ClimbingSimulator.constants.HeightOfBottomOfWallZone * ClimbingSimulator.constants.SizeFactor;
			
			for(int i = 0; i < size(); i++) {
				
				if (get(i).y > bottomOfWallZoneBoundary && get(i).y < bottomOfWall) {
					
					get(i).withinBottomOfWallZone = true;
					holdsWithinBottomOfWallZone.add(i);
				}
			}
		}
		
		void findHoldsInTheZoneAtTheTopOfTheWall() {
			
			float topOfWallZoneHeight = ClimbingSimulator.constants.HeightOfTopOfWallZone * ClimbingSimulator.constants.SizeFactor;
			
			for(int i = 0; i < size(); i++) {
				
				if (get(i).y > 0 && (get(i).y < topOfWallZoneHeight)) {
					
					get(i).withinTopOfWallZone = true;
					holdsWithinTopOfWallZone.add(i);
				}
			}
		}
		
		void checkTheZoneAtTheBottomOfTheWallForAHold() {
			
			if (holdsWithinBottomOfWallZone.size() > 0) {
				
				thereIsAHoldInTheZoneAtTheBottomOfTheWall = true;
				
			} else {
				
				thereIsAHoldInTheZoneAtTheBottomOfTheWall = false;
				
				holdsGoodIndividually = false;
			}
		}		
		
		void checkTheZoneAtTheTopOfTheWallForAHold() {
			
			if (holdsWithinTopOfWallZone.size() > 0) {
				
				thereIsAHoldInTheZoneAtTheTopOfTheWall = true;
				
			} else {
				
				thereIsAHoldInTheZoneAtTheTopOfTheWall = false;
				
				holdsGoodIndividually = false;
			}
		}
		
		void analyzeQuadrantContentsForEachHold() {
		
			updateTheHoldsThatAreWithinMaxQuadrantDistance();
			
			updateQuadrantHoldProximityBasedOnReachableRangeForEachHold();
			
			updateQuadrantSufficiencyForEachHoldBasedOnReachableRange();
			
			markAnyHoldThatNeedsANeighborInAnyOfItsQuadrants();
		}
		
		void updateTheHoldsThatAreWithinMaxQuadrantDistance() {
			
			float maximumInterholdDistanceForOneHoldToPossiblyBeInQuadrantOfAnotherHold = ClimbingSimulator.constants.MaximumDistanceForProperSpacingInQuadrant * ClimbingSimulator.constants.SizeFactor;
			
			for (int i = 0; i < size() - 1; i++) {
				
				for (int j = i + 1; j < size(); j++) {
					
					float interholdDistance = CommonAlgorithms.findDistance(get(i), get(j));
					
					if (interholdDistance < maximumInterholdDistanceForOneHoldToPossiblyBeInQuadrantOfAnotherHold) {
						
						get(i).holdsThatAreCloseEnoughToPossiblyBeInAQuadrant.add(j);
						
						get(j).holdsThatAreCloseEnoughToPossiblyBeInAQuadrant.add(i);
					}
				}
			}
		}
				
		void updateQuadrantHoldProximityBasedOnReachableRangeForEachHold() {
			
			for (int i = 0; i < size(); i++) {
				
				Area quadrantI = CommonAlgorithms.createQuadrantOfProperHoldSpacingBasedOnReachableRange(get(i), Direction.UPANDRIGHT, interQuadrantGapInDegrees);
				Area quadrantII = CommonAlgorithms.createQuadrantOfProperHoldSpacingBasedOnReachableRange(get(i), Direction.UPANDLEFT, interQuadrantGapInDegrees);
				Area quadrantIII = CommonAlgorithms.createQuadrantOfProperHoldSpacingBasedOnReachableRange(get(i), Direction.DOWNANDLEFT, interQuadrantGapInDegrees);
				Area quadrantIV = CommonAlgorithms.createQuadrantOfProperHoldSpacingBasedOnReachableRange(get(i), Direction.DOWNANDRIGHT, interQuadrantGapInDegrees);
				
				for (int nh : get(i).holdsThatAreCloseEnoughToPossiblyBeInAQuadrant) {
					
					if (quadrantI.contains(get(nh))) {
						get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantI = true;
					}
					
					if (quadrantII.contains(get(nh))) {
						get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantII = true;
					}
					
					if (quadrantIII.contains(get(nh))) {
						get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantIII = true;
					}

					if (quadrantIV.contains(get(nh))) {
						get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantIV = true;
					}
				}
			}
		}
		
		void updateQuadrantSufficiencyForEachHoldBasedOnReachableRange() {
			
			for (int i = 0; i < size(); i++) {
				
				get(i).hasASetOfProperlySpacedHoldsBasedOnReachableRangeInRelevantQuadrants = false;
				
				if ((get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantI && get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantII) || (get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantII && get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantIII) || (get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantIII && get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantIV) || (get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantIV && get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantI)) {
					
					get(i).hasASetOfProperlySpacedHoldsBasedOnReachableRangeInRelevantQuadrants = true;
					
				} else {
					
					get(i).hasASetOfProperlySpacedHoldsBasedOnReachableRangeInRelevantQuadrants = false;
				}
			}
		}

		void markAnyHoldThatNeedsANeighborInAnyOfItsQuadrants () {
			
			someHoldsDoNotHaveEnoughNeighbors = false;
			
			omnibusHoldTest:
				
				for (int i = 0; i < size(); i++) {

					get(i).needsAHoldWithinReachableRangeSomewhereAbove = true;
					get(i).needsAHoldWithinReachableRangeSomewhereBelow = true;
					get(i).needsAHoldWithinReachableRangeInIOrIII = true;
					get(i).needsAHoldWithinReachableRangeInIIOrIV = true;
					
					if (get(i).withinTopOfWallZone) {
						
						if (!get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantIII && !get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantIV) {
							
							holdsGoodIndividually = false;
							someHoldsDoNotHaveEnoughNeighbors = true;
							
							get(i).hasASetOfProperlySpacedHoldsBasedOnReachableRangeInRelevantQuadrants = false;

							get(i).needsAHoldWithinReachableRangeSomewhereAbove = false;
							get(i).needsAHoldWithinReachableRangeInIOrIII = false;
							get(i).needsAHoldWithinReachableRangeInIIOrIV = false;
							
						} else {
							
							get(i).hasASetOfProperlySpacedHoldsBasedOnReachableRangeInRelevantQuadrants = true;
							
							get(i).needsAHoldWithinReachableRangeSomewhereBelow = false;
							get(i).needsAHoldWithinReachableRangeSomewhereAbove = false;
							get(i).needsAHoldWithinReachableRangeInIOrIII = false;
							get(i).needsAHoldWithinReachableRangeInIIOrIV = false;
						}
					
						continue omnibusHoldTest;
						
					} else if (get(i).withinBottomOfWallZone) {	
						
						if (!get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantI && !get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantII) {
							
							holdsGoodIndividually = false;
							someHoldsDoNotHaveEnoughNeighbors = true;
							
							get(i).hasASetOfProperlySpacedHoldsBasedOnReachableRangeInRelevantQuadrants = false;

							get(i).needsAHoldWithinReachableRangeSomewhereBelow = false;
							get(i).needsAHoldWithinReachableRangeInIOrIII = false;
							get(i).needsAHoldWithinReachableRangeInIIOrIV = false;
						
						} else {
							
							get(i).hasASetOfProperlySpacedHoldsBasedOnReachableRangeInRelevantQuadrants = true;
							
							get(i).needsAHoldWithinReachableRangeSomewhereBelow = false;
							get(i).needsAHoldWithinReachableRangeSomewhereAbove = false;
							get(i).needsAHoldWithinReachableRangeInIOrIII = false;
							get(i).needsAHoldWithinReachableRangeInIIOrIV = false;
						}
						
						continue omnibusHoldTest;
						
					} else {					
						
						get(i).needsAHoldWithinReachableRangeSomewhereBelow = false;
						get(i).needsAHoldWithinReachableRangeSomewhereAbove = false;
					}
					
					if(!get(i).hasASetOfProperlySpacedHoldsBasedOnReachableRangeInRelevantQuadrants) {
						
						holdsGoodIndividually = false;
						someHoldsDoNotHaveEnoughNeighbors = true;
						
						if(get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantI || get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantIII) {
									
							get(i).needsAHoldWithinReachableRangeInIOrIII = false;
												
						} else if (get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantII || get(i).hasProperlySpacedHoldBasedOnReachableRangeInQuadrantIV) {
							
							get(i).needsAHoldWithinReachableRangeInIIOrIV = false;
							
						}
					}
				}
		}
		
		void analyzeTheVerticalConnectivityOfTheHoldsBasedOnQuadrantLimits() {
			
			connectAndMarkHoldsFromTheBottomOfTheWallUpwardBasedOnQuadrantLimits();
			connectAndMarkHoldsFromTheTopOfTheWallDownwardBasedOnQuadrantLimits();
		}
		
		void connectAndMarkHoldsFromTheBottomOfTheWallUpwardBasedOnQuadrantLimits() {
			
			clearAllConnectionAnalysisStates();
			
			connectHoldsFromTheBottomOfTheWallUpwardBasedOnQuadrantLimits();
			
			updateStatusOfConnectionToTheBottomOfTheWallBasedOnQuadrantLimitsForEachHold();
		}
		
		void clearAllConnectionAnalysisStates() {
			
			for(int i=0; i < size(); i++) {
				
				get(i).connectionAnalysisState = ConnectionAnalysisState.NOTYETCONNECTED;
			}			
		}
		
		void connectHoldsFromTheBottomOfTheWallUpwardBasedOnQuadrantLimits() {
			
			LinkedList<Integer> connectionAnalysisQueue = new LinkedList<Integer>();
	
			for (int i : holdsWithinBottomOfWallZone) {
				
				connectionAnalysisQueue.add(i);
			}
			
			while(!connectionAnalysisQueue.isEmpty()) {
				
				Integer indexOfHoldBeingProcessed = connectionAnalysisQueue.removeFirst();
				
				if (get(indexOfHoldBeingProcessed).connectionAnalysisState == ConnectionAnalysisState.NOTYETCONNECTED) {
					
					get(indexOfHoldBeingProcessed).connectionAnalysisState = ConnectionAnalysisState.CONNECTING;
				
					for(int nh : get(indexOfHoldBeingProcessed).holdsThatAreCloseEnoughToPossiblyBeInAQuadrant) {
						
						connectionAnalysisQueue.add(nh);
					}
					
				} else if (get(indexOfHoldBeingProcessed).connectionAnalysisState == ConnectionAnalysisState.CONNECTING) {
					
					get(indexOfHoldBeingProcessed).connectionAnalysisState = ConnectionAnalysisState.CONNECTED;
					
				} else if (get(indexOfHoldBeingProcessed).connectionAnalysisState == ConnectionAnalysisState.CONNECTED) {
					
					continue;
				}
			}
		}
		
		void updateStatusOfConnectionToTheBottomOfTheWallBasedOnQuadrantLimitsForEachHold() {
			
			for(int i=0; i < size(); i++) {
				
				if (get(i).connectionAnalysisState == ConnectionAnalysisState.NOTYETCONNECTED) {
					
					holdsGoodAsANetwork = false;
					showingWebOfConnections = true;

					get(i).connectedToBottom = false;
					
				} else {
					
					get(i).connectedToBottom = true;
					holdsConnectedToBottomOfWall.add(i);
				}
			}
		}
		
		void connectAndMarkHoldsFromTheTopOfTheWallDownwardBasedOnQuadrantLimits() {
			
			clearAllConnectionAnalysisStates();
			
			connectHoldsFromTheTopOfTheWallDownwardBasedOnQuadrantLimits();
			
			updateStatusOfConnectionToTheTopOfTheWallBasedOnQuadrantLimitsForEachHold();		
		}
		
		void connectHoldsFromTheTopOfTheWallDownwardBasedOnQuadrantLimits() {
			
			LinkedList<Integer> connectionAnalysisQueue = new LinkedList<Integer>();

			for (int i : holdsWithinTopOfWallZone) {
				
				connectionAnalysisQueue.add(i);
			}
			
			while(!connectionAnalysisQueue.isEmpty()) {
				
				Integer indexOfHoldBeingProcessed = connectionAnalysisQueue.removeFirst();
				
				if (get(indexOfHoldBeingProcessed).connectionAnalysisState == ConnectionAnalysisState.NOTYETCONNECTED) {
					
					get(indexOfHoldBeingProcessed).connectionAnalysisState = ConnectionAnalysisState.CONNECTING;
				
					for(int nh : get(indexOfHoldBeingProcessed).holdsThatAreCloseEnoughToPossiblyBeInAQuadrant) {
						
						connectionAnalysisQueue.add(nh);
					}
					
				} else if (get(indexOfHoldBeingProcessed).connectionAnalysisState == ConnectionAnalysisState.CONNECTING) {
					
					get(indexOfHoldBeingProcessed).connectionAnalysisState = ConnectionAnalysisState.CONNECTED;
					
				} else if (get(indexOfHoldBeingProcessed).connectionAnalysisState == ConnectionAnalysisState.CONNECTED) {
					
					continue;
				}
			}
		}
		
		void updateStatusOfConnectionToTheTopOfTheWallBasedOnQuadrantLimitsForEachHold() {
			
			for(int i=0; i < size(); i++) {
				
				if (get(i).connectionAnalysisState == ConnectionAnalysisState.NOTYETCONNECTED) {
					
					holdsGoodAsANetwork = false;
					showingWebOfConnections = true;

					get(i).connectedToTop = false;
					
				} else {
					
					get(i).connectedToTop = true;
					holdsConnectedToTopOfWall.add(i);
				}
			}
		}
		
		void assignBorderStatusForEachHold() {
		
			updateQuadrantHoldProximityForDeterminingBorderHolds();
			
			identifyBorderHolds();
		}
				
		void updateQuadrantHoldProximityForDeterminingBorderHolds() {
			
			for (int i = 0; i < size(); i++) {
				
				Area quadrantI = CommonAlgorithms.createQuadrantForDeterminingBorderHolds(get(i), Direction.UPANDRIGHT);
				Area quadrantII = CommonAlgorithms.createQuadrantForDeterminingBorderHolds(get(i), Direction.UPANDLEFT);
				Area quadrantIII = CommonAlgorithms.createQuadrantForDeterminingBorderHolds(get(i), Direction.DOWNANDLEFT);
				Area quadrantIV = CommonAlgorithms.createQuadrantForDeterminingBorderHolds(get(i), Direction.DOWNANDRIGHT);
				
				for (int nh : get(i).holdsThatAreCloseEnoughToPossiblyBeInAQuadrant) {
					
					if (quadrantI.contains(get(nh))) {
						get(i).hasHoldInQuadrantI = true;
					}
					
					if (quadrantII.contains(get(nh))) {
						get(i).hasHoldInQuadrantII = true;
					}
					
					if (quadrantIII.contains(get(nh))) {
						get(i).hasHoldInQuadrantIII = true;
					}

					if (quadrantIV.contains(get(nh))) {
						get(i).hasHoldInQuadrantIV = true;
					}
				}
				
				if (get(i).withinBottomOfWallZone || get(i).withinTopOfWallZone) {
					
					if(get(i).hasHoldInQuadrantI) {
						get(i).hasHoldInQuadrantIV = true;
					}
					
					if(get(i).hasHoldInQuadrantII) {
						get(i).hasHoldInQuadrantIII = true;
					}
					
					if(get(i).hasHoldInQuadrantIII) {
						get(i).hasHoldInQuadrantII = true;
					}
					
					if(get(i).hasHoldInQuadrantIV) {
						get(i).hasHoldInQuadrantI = true;
					}
				}
			}
		}
		
		void identifyBorderHolds() {
			
			for (int i = 0; i < size(); i++) {
				
				ClimbingHold h = get(i);
				
				if (!(h.hasHoldInQuadrantI && h.hasHoldInQuadrantII && h.hasHoldInQuadrantIII && h.hasHoldInQuadrantIV)) {
					
					get(i).isABorderHold = true;
				}
			}
		}		
	}
	
	synchronized void seeIfHoldsAreSpacedProperly() {
		
		holdsGoodIndividually = true;
		holdsGoodAsANetwork = true;
		showingWebOfConnections = false;

		setOfUserEnteredHolds.analyzeBottomAndTopZones();
						
		setOfUserEnteredHolds.analyzeQuadrantContentsForEachHold();
		
		if (holdsGoodIndividually) {
			
			setOfUserEnteredHolds.analyzeTheVerticalConnectivityOfTheHoldsBasedOnQuadrantLimits();
		}

		if (holdsGoodIndividually && holdsGoodAsANetwork) {

			waitingToBeFinalized = true;
			displayingFeedback = false;
			
		} else {

			waitingToBeFinalized = false;
			displayingFeedback = true;
		}
	}
	
	void lockInFinalStateOfWall() {
		
		setOfUserEnteredHolds.assignBorderStatusForEachHold();
		
		wallFinalized = true;
		waitingToBeFinalized = false;
		
	}

	void notifyClimberThatWallIsReady() {
	
		wallClimber.receiveMessageThatTheWallIsReadyForClimber(this); 
		
	}
	
	void showClimberMovingOnUserEnteredHolds() {
		
		showingClimber = true;
		repaint();
		
		(new Thread() {public void run() {
			
			while(wallClimber.isClimberOnTheWall() && wallClimber.isClimberTryingToClimb()) {						
		
				try {
					Thread.sleep(1000/ClimbingSimulator.constants.DisplayTickRate);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				repaint();
				
				wallClimber.wallHasDrawnTheLatestClimberMoves = true;
			}
			
			if(!wallClimber.isClimberOnTheWall()) {
			
				showingClimber = false;
			
			}
			
			if(!wallClimber.isClimberTryingToClimb()) {
				
				showingAcceptanceOfDefeat = true;
				
			}
			
			showingFinalMessageOfAppreciation = true;
			
			repaint();
			
		}}).start();
	}
	
	protected void paintComponent (Graphics g) {
		
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		if (displayingIntroduction) {
			
			showIntroductionElements(g2);
			
		}
		
		if (showingInitialInstructions) {
			
			drawInitialInstructions(g2);
			
		}
		
		if (!displayingIntroduction) {
		
			drawUserEnteredHoldsBoundriesAndNetworking(g2);
			
			drawResetButton(g2);
			
		}
		
		if (!wallFinalized) {
			
			showWallPreparationPrompts(g2);
		}
				
		if (showingClimber) {
			
			wallClimber.drawClimber(g2);
		}
		
		
		if (showingAcceptanceOfDefeat) {
			
			showMessageOfAcceptanceOfDefeat(g2);
		}
		
		
		if (showingFinalMessageOfAppreciation) {
			
			showFinalMessageOfAppreciation(g2);
		}
		
		paintOverAreaOutsideOfWall(g2);
		
		g2.dispose();
	}
	
	
	void showIntroductionElements (Graphics2D g2d) {

		drawTitle(g2d);
		
		if (requestForAssistanceVisible) {
			
			drawRequestForAssistance(g2d);	
		
		}
		
		if (startButtonVisible) {
			
			drawButtonUserCanPushToBeginRouteSetting(g2d);	
		
		}
		
		drawIntroductionHolds(g2d);
	}
	
	void drawTitle (Graphics2D g2d) {
		
		g2d.setPaint(Color.BLUE);
		g2d.setFont((new Font("SansSerif",1,36)));
		g2d.drawString("Rock Climbing", 215, 125);
		g2d.drawString("Simulator", 253, 175);

		g2d.setFont((new Font("SansSerif",1,12)));
		g2d.drawString("by C. Parker", 295, 250);

		g2d.setFont((new Font("SansSerif",1,10)));
		g2d.drawString("Copyright 2016. All rights reserved.", 243, heightOfWall - 25);
		
	}
	
	void drawRequestForAssistance (Graphics2D g2d) {
		
		g2d.setPaint(Color.BLUE);
		g2d.setFont((new Font("SansSerif",1,16)));
		g2d.drawString("Will you create a challenging route for me to climb?", 145, 400);
		g2d.drawString("Please click on the button below to begin.", 175, 450);
		
	}
	
	void drawButtonUserCanPushToBeginRouteSetting (Graphics2D g2d) {
		
		g2d.setPaint(Color.black);
		g2d.drawRect(ClimbingSimulator.constants.XValueForStartButtonLeftBorder, ClimbingSimulator.constants.YValueForStartButtonUpperBorder, ClimbingSimulator.constants.WidthOfStartButton, ClimbingSimulator.constants.HeightOfStartButton);
		g2d.setPaint(Color.white);
		g2d.fillRect(ClimbingSimulator.constants.XValueForStartButtonLeftBorder + 1, ClimbingSimulator.constants.YValueForStartButtonUpperBorder + 1, ClimbingSimulator.constants.WidthOfStartButton - 2, ClimbingSimulator.constants.HeightOfStartButton - 2);
		
		g2d.setPaint(Color.black);
		g2d.setFont((new Font("SansSerif",1,12)));
		g2d.drawString("Start Setting a Route", ClimbingSimulator.constants.XValueForStartButtonLeftBorder + 15, ClimbingSimulator.constants.YValueForStartButtonUpperBorder + 25);
		
	}
	
	void drawIntroductionHolds (Graphics2D g2d) {
		
		float hd = ClimbingSimulator.constants.HoldDiameter * ClimbingSimulator.constants.SizeFactor;
		
		for (ClimbingHold h: setOfIntroductionHolds) {
			
			if (h.isVisibleInIntroductionAnimation) {
				
				g2d.setPaint(Color.BLACK);
				g2d.fill(new Ellipse2D.Float(h.x - (hd/2), h.y - (hd/2), hd, hd));
								
			}
		}
	}
	
	void drawInitialInstructions (Graphics2D g2d) {
		
		g2d.setPaint(Color.BLUE);
		g2d.setFont((new Font("SansSerif",1,16)));
		g2d.drawString("Design a route on the wall, and I'll try to figure it out.", 115, 275);
		g2d.drawString("Be as imaginative as you want!", 193, 325);
		
	}
	
	void drawUserEnteredHoldsBoundriesAndNetworking (Graphics2D g2d) {
		
		g2d.setPaint(Color.BLACK);
		g2d.draw(new Rectangle2D.Float(-1, -1, ClimbingSimulator.constants.WallWidth + 2, heightOfWall + 2));
		
		if (!setOfUserEnteredHolds.thereIsAHoldInTheZoneAtTheTopOfTheWall) {
			
			g2d.setPaint(ClimbingSimulator.constants.BoundaryDesignationColor);
			g2d.fill(new Rectangle2D.Float(0, ClimbingSimulator.constants.HeightOfTopOfWallZone * ClimbingSimulator.constants.SizeFactor, ClimbingSimulator.constants.WallWidth + 1, ClimbingSimulator.constants.ThicknessOfTheBoundaryMarkerForTheZonesAtTheTopAndTheBottomOfTheWall));
		}
		
		if (!setOfUserEnteredHolds.thereIsAHoldInTheZoneAtTheBottomOfTheWall) {

			g2d.setPaint(ClimbingSimulator.constants.BoundaryDesignationColor);
			g2d.fill(new Rectangle2D.Float(0,(heightOfWall - ((ClimbingSimulator.constants.HeightOfBottomOfWallZone * ClimbingSimulator.constants.SizeFactor) + ClimbingSimulator.constants.ThicknessOfTheBoundaryMarkerForTheZonesAtTheTopAndTheBottomOfTheWall)), ClimbingSimulator.constants.WallWidth + 1, ClimbingSimulator.constants.ThicknessOfTheBoundaryMarkerForTheZonesAtTheTopAndTheBottomOfTheWall));
		}
		
		float hd = ClimbingSimulator.constants.HoldDiameter * ClimbingSimulator.constants.SizeFactor;
		
		if (!showingWebOfConnections) {
			
			theDrawingProcessForEachHold:
				
				for (ClimbingHold h: setOfUserEnteredHolds) {
								
					if (h.hasASetOfProperlySpacedHoldsBasedOnReachableRangeInRelevantQuadrants && !h.needsAHoldWithinReachableRangeSomewhereAbove && !h.needsAHoldWithinReachableRangeSomewhereBelow) {
						
						g2d.setPaint(Color.BLACK);
						g2d.fill(new Ellipse2D.Float(h.x - (hd/2), h.y - (hd/2), hd, hd));
						
						continue theDrawingProcessForEachHold;
										
					} else {
						
						displayingFeedback = true;
						
						g2d.setPaint(Color.RED);
						g2d.fill(new Ellipse2D.Float(h.x - (hd/2), h.y - (hd/2), hd, hd));
					}
						
						
					if (h.needsAHoldWithinReachableRangeSomewhereBelow) {
						
						Area quadrantIII = CommonAlgorithms.createQuadrantOfProperHoldSpacingBasedOnReachableRange(h, Direction.DOWNANDLEFT, setOfUserEnteredHolds.interQuadrantGapInDegrees);
						Area quadrantIV = CommonAlgorithms.createQuadrantOfProperHoldSpacingBasedOnReachableRange(h, Direction.DOWNANDRIGHT, setOfUserEnteredHolds.interQuadrantGapInDegrees);
						
						Area lowQuadrants = new Area();
						
						lowQuadrants.add(quadrantIII);
						lowQuadrants.add(quadrantIV);
						
						g2d.setPaint(ClimbingSimulator.constants.TransparentBlue);
						
						g2d.fill(lowQuadrants);
					}
					
					if (h.needsAHoldWithinReachableRangeSomewhereAbove) {
						
						Area quadrantI = CommonAlgorithms.createQuadrantOfProperHoldSpacingBasedOnReachableRange(h, Direction.UPANDRIGHT, setOfUserEnteredHolds.interQuadrantGapInDegrees);
						Area quadrantII = CommonAlgorithms.createQuadrantOfProperHoldSpacingBasedOnReachableRange(h, Direction.UPANDLEFT, setOfUserEnteredHolds.interQuadrantGapInDegrees);
						
						Area highQuadrants = new Area();
						
						highQuadrants.add(quadrantI);
						highQuadrants.add(quadrantII);
						
						g2d.setPaint(ClimbingSimulator.constants.TransparentBlue);
						
						g2d.fill(highQuadrants);
					}
					
					if (h.needsAHoldWithinReachableRangeInIOrIII) {
						
						Area quadrantI = CommonAlgorithms.createQuadrantOfProperHoldSpacingBasedOnReachableRange(h, Direction.UPANDRIGHT, setOfUserEnteredHolds.interQuadrantGapInDegrees);
						Area quadrantIII = CommonAlgorithms.createQuadrantOfProperHoldSpacingBasedOnReachableRange(h, Direction.DOWNANDLEFT, setOfUserEnteredHolds.interQuadrantGapInDegrees);
						
						Area lowLeftToHighRightQuadrants = new Area();
						
						lowLeftToHighRightQuadrants.add(quadrantI);
						lowLeftToHighRightQuadrants.add(quadrantIII);
						
						g2d.setPaint(ClimbingSimulator.constants.TransparentRed);
						
						g2d.fill(lowLeftToHighRightQuadrants);
					}
					
					if (h.needsAHoldWithinReachableRangeInIIOrIV) {
						
						Area quadrantII = CommonAlgorithms.createQuadrantOfProperHoldSpacingBasedOnReachableRange(h, Direction.UPANDLEFT, setOfUserEnteredHolds.interQuadrantGapInDegrees);
						Area quadrantIV = CommonAlgorithms.createQuadrantOfProperHoldSpacingBasedOnReachableRange(h, Direction.DOWNANDRIGHT, setOfUserEnteredHolds.interQuadrantGapInDegrees);
						
						Area highLeftToLowRightQuadrants = new Area();
						
						highLeftToLowRightQuadrants.add(quadrantII);
						highLeftToLowRightQuadrants.add(quadrantIV);
						
						g2d.setPaint(ClimbingSimulator.constants.TransparentBlue);
						
						g2d.fill(highLeftToLowRightQuadrants);
					}
				}
		}
		
		if (showingWebOfConnections) {
			
			if (tryingToAddHolds) {
				
				for (ClimbingHold h: setOfUserEnteredHolds) {
					
					g2d.setPaint(Color.BLACK);
					g2d.fill(new Ellipse2D.Float(h.x - (hd/2),h.y - (hd/2),hd,hd));
				}
				
			} else {
				
				for (ClimbingHold h: setOfUserEnteredHolds) {
					
					if (h.connectedToBottom || h.connectedToTop) {
						
						g2d.setPaint(Color.BLACK);
						g2d.fill(new Ellipse2D.Float(h.x - (hd/2),h.y - (hd/2),hd,hd));
										
					} else {
						
						g2d.setPaint(Color.RED);
						g2d.fill(new Ellipse2D.Float(h.x - (hd/2),h.y - (hd/2),hd,hd));
					}
				}
			}
						
			float limitRadius = ClimbingSimulator.constants.MaximumDistanceForProperSpacingInQuadrant * ClimbingSimulator.constants.SizeFactor;
			
			g2d.setPaint(Color.GRAY);
			
			for (ClimbingHold h : setOfUserEnteredHolds) {
				
				g2d.draw(new Ellipse2D.Float(h.x - limitRadius, h.y - limitRadius, 2 * limitRadius, 2 * limitRadius));
			}
			
			g2d.setPaint(Color.DARK_GRAY);
			
			for (Integer i : setOfUserEnteredHolds.holdsConnectedToBottomOfWall) {
				
				for(Integer j : setOfUserEnteredHolds.get(i).holdsThatAreCloseEnoughToPossiblyBeInAQuadrant) {
					
					g2d.draw(new Line2D.Float(setOfUserEnteredHolds.get(i), setOfUserEnteredHolds.get(j)));
				}
			}
			
			for (Integer i : setOfUserEnteredHolds.holdsConnectedToTopOfWall) {
				
				for(Integer j : setOfUserEnteredHolds.get(i).holdsThatAreCloseEnoughToPossiblyBeInAQuadrant) {
					
					g2d.draw(new Line2D.Float(setOfUserEnteredHolds.get(i), setOfUserEnteredHolds.get(j)));
				}
			}
		}
	}
	
	void drawResetButton(Graphics2D g2d) {
		
		g2d.setPaint(Color.black);
		g2d.drawRect(ClimbingSimulator.constants.XValueForResetButtonLeftBorder, ClimbingSimulator.constants.YValueForResetButtonUpperBorder, ClimbingSimulator.constants.WidthOfResetButton, ClimbingSimulator.constants.HeightOfResetButton);
		g2d.setPaint(Color.white);
		g2d.fillRect(ClimbingSimulator.constants.XValueForResetButtonLeftBorder + 1, ClimbingSimulator.constants.YValueForResetButtonUpperBorder + 1, ClimbingSimulator.constants.WidthOfResetButton - 2, ClimbingSimulator.constants.HeightOfResetButton - 2);
		
		g2d.setPaint(Color.black);
		g2d.setFont((new Font("SansSerif",1,12)));
		g2d.drawString("Reset the Wall", ClimbingSimulator.constants.XValueForResetButtonLeftBorder + 5, ClimbingSimulator.constants.YValueForResetButtonUpperBorder + 17);
		
	}
	
	void showWallPreparationPrompts(Graphics2D g2d) {
		
		if (!holdsGoodIndividually || !holdsGoodAsANetwork) {
			promptForACheckOfUserEnteredHolds (g2d);
		}
		
		if (!holdsGoodAsANetwork) {
			explainTheNeedForAContinuousConnection(g2d);
		}
		
		if (setOfUserEnteredHolds.tooClose) {
			requestUserForAChallenge(setOfUserEnteredHolds, g2d);
		}
		
		if (!setOfUserEnteredHolds.thereIsAHoldInTheZoneAtTheTopOfTheWall || !setOfUserEnteredHolds.thereIsAHoldInTheZoneAtTheBottomOfTheWall) {
			promptForExtremeHolds(g2d);
		}
		
		if (setOfUserEnteredHolds.someHoldsDoNotHaveEnoughNeighbors) {
			promptForCloserHolds(g2d);
		}
		
		if (waitingToBeFinalized) {
			promptForFinalApprovalOfHolds(g2d);
		}
	}
	
	void promptForACheckOfUserEnteredHolds (Graphics2D g2d) {
		
		if (!displayingIntroduction && !wallFinalized && (tryingToAddHolds || !displayingFeedback)) {
			
			g2d.setFont(new Font("SansSerif",1,16));
			g2d.setColor(Color.BLUE);
			
			g2d.drawString("Click on the wall to add climbing holds.", 60, heightOfWall - 70);
			g2d.drawString("Press any key to check your placements.", 60, heightOfWall - 50);
			g2d.drawString("Right-click to include a random clustering.", 60, heightOfWall - 30);
		}
	}
	
	void explainTheNeedForAContinuousConnection(Graphics2D g2d) {
		
		int textStartingHeight = 200;
		int textStartingWidth = 20;
		
		int textBoxWidth = 450;
		int textBoxHeight = 300;
		
		if (!wallFinalized && displayingFeedback && !tryingToAddHolds) {
			
			g2d.setPaint(ClimbingSimulator.constants.MostOpaqueTransparentWallColor);
			g2d.fillRect(textStartingWidth - 5, textStartingHeight - 21, textBoxWidth, textBoxHeight);
			
			g2d.setFont(new Font("SansSerif",1,16));
			g2d.setColor(Color.BLUE);
			
			g2d.drawString("This is starting to look really fun.", textStartingWidth, textStartingHeight);
			g2d.drawString("Thank you for giving all the individual holds excellent", textStartingWidth, textStartingHeight + 40);
			g2d.drawString("neighbors!", textStartingWidth, textStartingHeight + 60);
			
			g2d.drawString("As things stand, though, I won't be able to move from", textStartingWidth, textStartingHeight + 100);
			g2d.drawString("all the holds that reach the bottom of the wall to all the", textStartingWidth, textStartingHeight + 120);
			g2d.drawString("holds that reach the top of the wall.", textStartingWidth, textStartingHeight + 140);
			
			g2d.drawString("Please address the gap by adding more holds", textStartingWidth, textStartingHeight + 180);
			g2d.drawString("in whatever pattern you'd like.", textStartingWidth, textStartingHeight + 200);
			
			g2d.drawString("The proximity circles and the connection network", textStartingWidth, textStartingHeight + 240);
			g2d.drawString("are shown for your convenience", textStartingWidth, textStartingHeight + 260);
  		}
	}
	
	void requestUserForAChallenge (ArrayList<ClimbingHold> al, Graphics2D g2d) {
		
		g2d.setPaint(ClimbingSimulator.constants.MostOpaqueTransparentWallColor);
		g2d.fillRect(45, 169, 300, 60);
		
		g2d.setFont(new Font("SansSerif",1,16));
		g2d.setColor(Color.RED);
		
		g2d.drawString("You're putting them too close together.", 50, 190);
		g2d.drawString("I want a challenge!", 50, 220);
	}
	
	void promptForExtremeHolds(Graphics2D g2d) {
			
		if (!setOfUserEnteredHolds.thereIsAHoldInTheZoneAtTheTopOfTheWall) {
			
			g2d.setPaint(ClimbingSimulator.constants.MostOpaqueTransparentWallColor);
			g2d.fillRect(45, 49, 350, 60);
			
			g2d.setFont(new Font("SansSerif",1,16));
			g2d.setColor(ClimbingSimulator.constants.BoundaryDesignationColor);
			
			g2d.drawString("I'm not sure I'll be able to reach the top.", 50, 70);
			g2d.drawString("Please add at least one hold above the line.", 50, 100);
		}
		
		if (!setOfUserEnteredHolds.thereIsAHoldInTheZoneAtTheBottomOfTheWall) {
			
			g2d.setPaint(ClimbingSimulator.constants.MostOpaqueTransparentWallColor);
			g2d.fillRect(45, (heightOfWall - 121), 350, 60);
			
			g2d.setFont(new Font("SansSerif",1,16));
			g2d.setColor(ClimbingSimulator.constants.BoundaryDesignationColor);
			
			g2d.drawString("I won't be able to start on these high holds.", 50, (heightOfWall - 100));
			g2d.drawString("Please add at least one hold below the line.", 50, (heightOfWall - 70));
		}
	}
	
	void promptForCloserHolds(Graphics2D g2d) {

		if (tryingToAddHolds) {
			
			g2d.setPaint(ClimbingSimulator.constants.ClearestTransparentWallColor);
			g2d.fillRect(15, 239, 550, 330);
			
			g2d.setFont(new Font("SansSerif",1,16));
			g2d.setColor(ClimbingSimulator.constants.TransparentNeedCloserHoldsColor);
		
		} else {
			
			g2d.setPaint(ClimbingSimulator.constants.MediumTransparentWallColor);
			g2d.fillRect(15, 239, 550, 330);
			
			g2d.setFont(new Font("SansSerif",1,16));
			g2d.setColor(ClimbingSimulator.constants.OpaqueNeedCloserHoldsColor);
			
		}
			
		g2d.drawString("Unfortunately, I can't climb the wall unless you add more holds.", 20, 260);
		
		g2d.drawString("For each colored area around a hold, add at least one reachable neighboring hold", 20, 300);
		g2d.drawString("within the zone indicated by a specific color.", 20, 320);
		
		g2d.drawString("In other words, a hold surrounded by two differently", 20, 360);
		g2d.drawString("colored areas will need at least two more neighboring holds.", 20, 380);
		
		g2d.drawString("If you can't see the colors, it may be helpful to know that", 20, 420);
		g2d.drawString("holds normally need at least one neighboring hold for each diagonal.", 20, 440);
		g2d.drawString("The exception is for holds at the top and bottom of the wall,", 20, 480);
		g2d.drawString("which need only one neighboring hold for the pair of highlighted areas.", 20, 500);
		
		g2d.drawString("Press any key when you want me to check again.", 20, 540);
	}
	
	void promptForFinalApprovalOfHolds(Graphics2D g2d) {
		
		g2d.setPaint(ClimbingSimulator.constants.MostOpaqueTransparentWallColor);
		g2d.fillRect(95, heightOfWall/2 - 121, 300, 200);
		
		g2d.setFont(new Font("SansSerif",1,16));
		g2d.setColor(ClimbingSimulator.constants.FinalizeHoldsConfirmationTextColor);
		
		g2d.drawString("This looks good to me.", 100, heightOfWall/2 - 100);
		
		g2d.drawString("Can I try to climb it?", 100, heightOfWall/2 - 50);
		
		g2d.drawString("Press any key to finalize your holds.", 100, heightOfWall/2);
		
		g2d.drawString("Otherwise, click to add more.", 100, heightOfWall/2 + 50);
	}
	
	
	void showMessageOfAcceptanceOfDefeat(Graphics2D g2d) {
	
		g2d.setPaint(ClimbingSimulator.constants.MostOpaqueTransparentWallColor);
		g2d.fillRect(35, 79, 410, 85);
		
		g2d.setFont(new Font("SansSerif",1,16));
		g2d.setColor(Color.BLUE);
		
		g2d.drawString("Well, you stumped me!", 40, 100);
		g2d.drawString("Have you ever considered a career in route-setting?", 40, 160);

	}
		
	void showFinalMessageOfAppreciation(Graphics2D g2d) {
		
		g2d.setPaint(ClimbingSimulator.constants.MostOpaqueTransparentWallColor);
		g2d.fillRect(35, (heightOfWall/2) - 21, 400, 175);
				
		g2d.setFont(new Font("SansSerif",1,16));
		g2d.setColor(Color.BLUE);
		
		g2d.drawString("Thank you so much for giving me this opportunity", 40, heightOfWall/2);
		g2d.drawString("to show you how I solve problems.", 40, heightOfWall/2 + 24);
		
		g2d.drawString("I had a BLAST!", 40, heightOfWall/2 + 144);
		
	}
	
	void paintOverAreaOutsideOfWall(Graphics2D g2d) {
		
		g2d.setPaint (ClimbingSimulator.constants.WallColor);
		
		g2d.fillRect (0, heightOfWall + 2, ClimbingSimulator.constants.WallWidth + 1, heightOfWall);
		g2d.fillRect (ClimbingSimulator.constants.WallWidth + 2, 0, ClimbingSimulator.constants.WallWidth, heightOfWall + 1);
		
	}
	
	public Dimension getPreferredSize () {
		return new Dimension(ClimbingSimulator.constants.WallWidth,heightOfWall);
	}
	
	public Dimension getMinimumSize () {
		return new Dimension(ClimbingSimulator.constants.WallWidth,heightOfWall);
	}
}
