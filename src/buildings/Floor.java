package buildings;

import elevators.ElevatorObserver;
import passengers.Passenger;
import elevators.Elevator;

import java.util.*;

public class Floor implements ElevatorObserver {
	private Building mBuilding;
	private List<Passenger> mPassengers = new ArrayList<>();
	private ArrayList<FloorObserver> mObservers = new ArrayList<>();
	private int mNumber; // THIS IS THE ACTUAL FLOOR NUMBER

	// TODO: declare a field(s) to help keep track of which direction buttons are currently pressed.
	// You can assume that every floor has both up and down buttons, even the ground and top floors.
	//[DONE]

	public boolean mUpButton;
	public boolean mDownButton;

	public Floor(int number, Building building) {
		mNumber = number;
		mBuilding = building;
		mUpButton = false;
		mDownButton = false;
	}


	/**
	 * Sets a flag that the given direction has been requested by a passenger on this floor. If the direction
	 * had NOT already been requested, then all observers of the floor are notified that directionRequested.
	 * @param direction
	 */
	public void requestDirection(Elevator.Direction direction) {
		// TODO: implement this method as described in the comment.
		//[DONE]
		if (direction.equals(Elevator.Direction.MOVING_UP)){
			this.mUpButton = true;
		}
		else if (direction.equals(Elevator.Direction.MOVING_DOWN)){
			this.mDownButton = true;
		}
		for (FloorObserver fObserver: mObservers){
			fObserver.directionRequested(this, direction);
		}
	}

	/**
	 * Returns true if the given direction button has been pressed.
	 */
	public boolean directionIsPressed(Elevator.Direction direction) {
		// TODO: complete this method.
		//[DONE]
		boolean directionPressed = false;
		if (direction.equals(Elevator.Direction.MOVING_UP) && this.mUpButton){
			directionPressed = this.mUpButton;
		}
		else if (direction.equals(Elevator.Direction.MOVING_DOWN) && mDownButton){
			directionPressed = this.mDownButton;
		}
		return directionPressed;
	}

	/**
	 * Clears the given direction button so it is no longer pressed.
	 */
	public void clearDirection(Elevator.Direction direction) {
		// TODO: complete this method.
		//[DONE]
		if (direction.equals(Elevator.Direction.MOVING_DOWN) && directionIsPressed(Elevator.Direction.MOVING_DOWN)){
			this.mDownButton = false;
		}
		else if (direction.equals(Elevator.Direction.MOVING_UP) && directionIsPressed(Elevator.Direction.MOVING_UP)){
			this.mUpButton = false;
		}
	}

	/**
	 * Adds a given Passenger as a waiting passenger on this floor, and presses the passenger's direction button.
	 */
	public void addWaitingPassenger(Passenger p) {
		mPassengers.add(p);
		addObserver(p);
		p.setState(Passenger.PassengerState.WAITING_ON_FLOOR);

		// TODO: call requestDirection with the appropriate direction for this passenger's destination.
		//[DONE]

		Elevator.Direction direction = Elevator.Direction.NOT_MOVING;

		if (p.getDestination() > mNumber){
			direction = Elevator.Direction.MOVING_UP;
		}
		else if (p.getDestination() < mNumber){
			direction = Elevator.Direction.MOVING_DOWN;
		}

		requestDirection(direction);
	}

	/**
	 * Removes the given Passenger from the floor's waiting passengers.
	 */
	public void removeWaitingPassenger(Passenger p) {
		mPassengers.remove(p);
	}


	// Simple accessors.
	public int getNumber() {
		return mNumber;
	}

	public List<Passenger> getWaitingPassengers() {
		return mPassengers;
	}

	@Override
	public String toString() {
		return "Floor " + mNumber;
	}

	// Observer methods.
	public void removeObserver(FloorObserver observer) {
		mObservers.remove(observer);
	}

	public void addObserver(FloorObserver observer) {
		mObservers.add(observer);
	}

	// Observer methods.
	@Override
	public void elevatorDecelerating(Elevator elevator) {
		// TODO: if the elevator is arriving at THIS FLOOR, alert all the floor's observers that elevatorArriving.
		// TODO:    then clear the elevator's current direction from this floor's requested direction buttons.
		//[ATTEMPTED] CONDITION MOST LIKELY WRONG


		if (elevator.getCurrentFloor().getNumber() == mNumber){
			for (FloorObserver fObserver : mObservers){
				fObserver.elevatorArriving(this, elevator);
				clearDirection(elevator.getCurrentDirection());
			}
		}
	}

	@Override
	public void elevatorDoorsOpened(Elevator elevator) {
		// Not needed.
	}

	@Override
	public void elevatorWentIdle(Elevator elevator) {
		// Not needed.
	}
}