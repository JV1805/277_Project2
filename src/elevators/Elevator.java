package elevators;

import cecs277.Simulation;
import buildings.Building;
import buildings.Floor;
import buildings.FloorObserver;
import events.ElevatorStateEvent;
import passengers.Passenger;

import java.util.*;
import java.util.stream.Collectors;

public class Elevator implements FloorObserver {
	
	public enum ElevatorState {
		IDLE_STATE,
		DOORS_OPENING,
		DOORS_CLOSING,
		DOORS_OPEN,
		ACCELERATING,
		DECELERATING,
		MOVING
	}
	
	public enum Direction {
		NOT_MOVING,
		MOVING_UP,
		MOVING_DOWN
	}
	
	
	private int mNumber;
	private Building mBuilding;

	private ElevatorState mCurrentState = ElevatorState.IDLE_STATE;
	private Direction mCurrentDirection = Direction.NOT_MOVING;
	private Floor mCurrentFloor; //THIS IS AN INDEX
	private List<Passenger> mPassengers = new ArrayList<>();
	
	private List<ElevatorObserver> mObservers = new ArrayList<>();
	
	// TODO: declare a field to keep track of which floors have been requested by passengers.
	// [DONE]

	private boolean[] mRequestedFloors;

	public Elevator(int number, Building bld) {
		mNumber = number;
		mBuilding = bld;
		mCurrentFloor = bld.getFloor(1);
		mRequestedFloors = new boolean[mBuilding.getFloorCount()];
		scheduleStateChange(ElevatorState.IDLE_STATE, 0);
	}
	
	/**
	 * Helper method to schedule a state change in a given number of seconds from now.
	 */
	private void scheduleStateChange(ElevatorState state, long timeFromNow) {
		Simulation sim = mBuilding.getSimulation();
		sim.scheduleEvent(new ElevatorStateEvent(sim.currentTime() + timeFromNow, state, this));
	}
	
	/**
	 * Adds the given passenger to the elevator's list of passengers, and requests the passenger's destination floor.
	 */
	public void addPassenger(Passenger passenger) {
		// TODO: add the passenger's destination to the set of requested floors.
		// Might not be right, to my understanding all i have to do is get the floor destination and mark it as true
        // in the array, if so then
		// [DONE]
        mPassengers.add(passenger);
		mRequestedFloors[passenger.getDestination()-1] =true;
	}
	
	public void removePassenger(Passenger passenger) {
		mPassengers.remove(passenger);
	}
	
	
	/**
	 * Schedules the elevator's next state change based on its current state.
	 */
	public void tick() {
		// TODO: port the logic of your state changes from Project 1, accounting for the adjustments in the spec.
		// TODO: State changes are no longer immediate; they are scheduled using scheduleStateChange().

        if (this.mCurrentState.equals(ElevatorState.IDLE_STATE)){
            this.getCurrentFloor().addObserver(this);
            for (ElevatorObserver eObserver : this.mObservers){
                eObserver.elevatorWentIdle(this);
            }
        }
        else if (this.mCurrentState.equals(ElevatorState.DOORS_OPENING)) {
            scheduleStateChange(ElevatorState.DOORS_OPEN, 2);
            for (ElevatorObserver eObserver : this.mObservers){
                eObserver.elevatorDoorsOpened(this);
            }
        }

        else if (this.mCurrentState.equals(ElevatorState.DOORS_OPEN)){
            int initialElevatorCount = this.mPassengers.size();
            int initialFloorCount = this.mCurrentFloor.getWaitingPassengers().size();

            for (int i = 0; i < this.getCurrentFloor().getWaitingPassengers().size(); i++) {
            	this.getCurrentFloor().getWaitingPassengers().get(i).elevatorDoorsOpened(this);
            }


            int endElevatorCount = this.mPassengers.size();
            int endFloorCount = this.mCurrentFloor.getWaitingPassengers().size();

            int totalEntered = initialFloorCount - endFloorCount;
            int totalLeft = (initialElevatorCount + totalEntered) - endElevatorCount;
            int totalChangeTime = (int) Math.floor((totalEntered + totalLeft)/2);

            scheduleStateChange(ElevatorState.DOORS_CLOSING, 1 + totalChangeTime);
        }
        
        else if (this.mCurrentState.equals(ElevatorState.DOORS_CLOSING)){
        	for (int i = 0; i < mRequestedFloors.length; i++) {
        		if (mRequestedFloors[i] == true) {
        			i = mRequestedFloors.length;
        		}
        		if (i == mRequestedFloors.length-1) {
        			mCurrentDirection = Direction.NOT_MOVING;
        			scheduleStateChange(ElevatorState.IDLE_STATE, 2);
        		}
        	}
        	switch(mCurrentDirection.ordinal()) {
        	case 1:
        		for (int i = mCurrentFloor.getNumber()-1; i < mBuilding.getFloorCount(); i++) {
            		if (mCurrentDirection == Direction.MOVING_UP && mRequestedFloors[i] == true) {
            			scheduleStateChange(ElevatorState.ACCELERATING, 2);
            			i = mBuilding.getFloorCount();
            		}
            	}
        		break;
        	case 2:
        		for (int i = mCurrentFloor.getNumber()-1; i < mBuilding.getFloorCount(); i--) {
            		if (mCurrentDirection == Direction.MOVING_DOWN && mRequestedFloors[i] == true) {
            			scheduleStateChange(ElevatorState.DOORS_OPENING, 2);
            			i = mBuilding.getFloorCount();
            		}
            	}
        		break;
        	default:
        		System.out.println("Something went wrong. Uh oh!");
        	}
        }
        
        else if (this.mCurrentState.equals(ElevatorState.ACCELERATING)){
        	this.getCurrentFloor().removeObserver(this);
        	scheduleStateChange(ElevatorState.MOVING, 2);
        }
        
        else if (this.mCurrentState.equals(ElevatorState.MOVING)){
        	if (mCurrentDirection == Direction.MOVING_UP) { //MAY CAUSE LOGIC ERROR LATER
        		mCurrentFloor = mBuilding.getFloor(mCurrentFloor.getNumber()+1); //+1 because going up
        		if(mRequestedFloors[mCurrentFloor.getNumber()-1]) { //-1 bc getting index of "next", now current, floor
        			scheduleStateChange(ElevatorState.DECELERATING, 2);
        		}
        		else {
        			scheduleStateChange(ElevatorState.MOVING, 2);
        		}
        	}
        	
        	if (mCurrentDirection == Direction.MOVING_DOWN) { //MAY CAUSE LOGIC ERROR LATER
        		mCurrentFloor = mBuilding.getFloor(mCurrentFloor.getNumber()-1); //-1 because going down
        		if(mRequestedFloors[mCurrentFloor.getNumber()-1]) { //-1 because getting index of "next", now current, floor
        			scheduleStateChange(ElevatorState.DECELERATING, 2);
        		}
        		else {
        			scheduleStateChange(ElevatorState.MOVING, 2);
        		}
        	}
        }
        
        else if (this.mCurrentState.equals(ElevatorState.DECELERATING)){
        	mRequestedFloors[mCurrentFloor.getNumber()-1] = false;
        	int changeDirectionFlag = 3; // 3 means nothing was changed; 2 means dont change direction; 1 means change direction
        	if (mCurrentDirection == Direction.MOVING_UP) {
        		for (int i = 0; i < this.getCurrentFloor().getWaitingPassengers().size(); i++) {
                	if (this.getCurrentFloor().getWaitingPassengers().get(i).getDestination() < mCurrentFloor.getNumber()
                			&& !mCurrentFloor.mUpButton) { //if a passenger wants a floor lower and the up button is not pressed
                		changeDirectionFlag = 1;
                	}
                	if (this.getCurrentFloor().getWaitingPassengers().get(i).getDestination() > mCurrentFloor.getNumber()) {
                		changeDirectionFlag = 2;
                	}
                }
        		if (changeDirectionFlag == 0) {
            		mCurrentDirection = Direction.NOT_MOVING;
            	}
            	else if (changeDirectionFlag == 1) {
            		mCurrentDirection = Direction.MOVING_UP;
            	}
        	}
        	if (mCurrentDirection == Direction.MOVING_DOWN) {
        		for (int i = 0; i < this.getCurrentFloor().getWaitingPassengers().size(); i++) {
                	if (this.getCurrentFloor().getWaitingPassengers().get(i).getDestination() > mCurrentFloor.getNumber()
                			&& !mCurrentFloor.mDownButton) { //if a passenger wants a floor higher and the down button is not pressed
                		changeDirectionFlag = 1;
                	}
                	if (this.getCurrentFloor().getWaitingPassengers().get(i).getDestination() < mCurrentFloor.getNumber()) {
                		changeDirectionFlag = 2;
                	}
                }
        		if (changeDirectionFlag == 0) {
            		mCurrentDirection = Direction.NOT_MOVING;
            	}
            	else if (changeDirectionFlag == 1) {
            		mCurrentDirection = Direction.MOVING_UP;
            	}
        	}
        	for (ElevatorObserver eObserver : this.mObservers){
                eObserver.elevatorDecelerating(this);
            }
        	scheduleStateChange(ElevatorState.DOORS_OPENING, 3);
        }
        
        
		// Example of how to trigger a state change:
		// scheduleStateChange(ElevatorState.MOVING, 3); // switch to MOVING and call tick(), 3 seconds from now.
		
	}
	
	
	/**
	 * Sends an idle elevator to the given floor.
	 */
	public void dispatchTo(Floor floor) {
		// TODO: if we are currently idle and not on the given floor, change our direction to move towards the floor.
		// TODO: set a floor request for the given floor, and schedule a state change to ACCELERATING immediately.
		
	}
	
	// Simple accessors
	public Floor getCurrentFloor() {
		return mCurrentFloor;
	}
	
	public Direction getCurrentDirection() {
		return mCurrentDirection;
	}
	
	public Building getBuilding() {
		return mBuilding;
	}
	
	/**
	 * Returns true if this elevator is in the idle state.
	 * @return
	 */
	public boolean isIdle() {
		// TODO: complete this method.
        boolean idle = true;
        if(mCurrentState != ElevatorState.IDLE_STATE){
            idle = false;
        }
		return idle;
	}
	
	// All elevators have a capacity of 10, for now.
	public int getCapacity() {
		return 10;
	}

	public int getPassengerCount() {
		return mPassengers.size();
	}
	
	// Simple mutators
	public void setState(ElevatorState newState) {
		mCurrentState = newState;
	}
	
	public void setCurrentDirection(Direction direction) {
		mCurrentDirection = direction;
	}
	
	public void setCurrentFloor(Floor floor) {
		mCurrentFloor = floor;
	}
	
	// Observers
	public void addObserver(ElevatorObserver observer) {
		mObservers.add(observer);
	}
	
	public void removeObserver(ElevatorObserver observer) {
		mObservers.remove(observer);
	}
	
	
	// FloorObserver methods
	@Override
	public void elevatorArriving(Floor floor, Elevator elevator) {
		// Not used.
	}
	
	/**
	 * Triggered when our current floor receives a direction request.
	 */
	@Override
	public void directionRequested(Floor sender, Direction direction) {
		// TODO: if we are currently idle, change direction to match the request. Then alert all our observers.
        //  that we are decelerating,
        //[DONE]
		// TODO: then schedule an immediate state change to DOORS_OPENING.
        //[DONE]

        //Changing direction
        if (this.mCurrentState.equals(ElevatorState.IDLE_STATE)){
            this.mCurrentDirection = direction;
        //Announcing deceleration
            for (ElevatorObserver eObserver : this.mObservers){
                eObserver.elevatorDecelerating(this);
            }
        //Schedule state change
           scheduleStateChange(ElevatorState.DOORS_OPENING, 0);
        }
	}
	
	
	
	
	// Voodoo magic.
	@Override
	public String toString() {
		return "Elevator " + mNumber + " - " + mCurrentFloor + " - " + mCurrentState + " - " + mCurrentDirection + " - "
		 + "[" + mPassengers.stream().map(p -> Integer.toString(p.getDestination())).collect(Collectors.joining(", "))
		 + "]";
	}
	
}
