package buildings;

import cecs277.Simulation;
import elevators.Elevator;
import elevators.ElevatorObserver;

import java.util.*;

public class Building implements ElevatorObserver, FloorObserver {
	private List<Elevator> mElevators = new ArrayList<>();
	private List<Floor> mFloors = new ArrayList<>();
	private Simulation mSimulation;
	private Queue<Integer> mWaitingFloors = new ArrayDeque<>();
	
	public Building(int floors, int elevatorCount, Simulation sim) {
		mSimulation = sim;
		
		// Construct the floors, and observe each one.
		for (int i = 0; i < floors; i++) {
			Floor f = new Floor(i + 1, this);
			f.addObserver(this);
			mFloors.add(f);
		}
		
		// Construct the elevators, and observe each one. and cry while it doesnt work
		for (int i = 0; i < elevatorCount; i++) {
			Elevator elevator = new Elevator(i + 1, this);
			elevator.addObserver(this);
			for (Floor f : mFloors) {
				elevator.addObserver(f);
			}
			mElevators.add(elevator);
		}
	}
	

	// TODO: recreate your toString() here.
	//[DONE]

	public String toString(){

		String returnString = "";

		for (int f = 0; f < this.getFloorCount(); f++){
			String temp = "";
			//padding for the string
			String biggestDigit = "" + this.getFloorCount();
			String padding = "" + f + 1;
			while (padding.length() < biggestDigit.length()){
				temp += " ";
				padding += " ";
			}

			//constructing the string
			if ((this.getFloorCount() - f) < 10){
				temp += " " + (this.getFloorCount() - f) + ": ";
			}
			else{
				temp += this.getFloorCount() - f + ": ";
			}

			for (Elevator elevator : this.mElevators){
				if (this.getFloorCount() - f == elevator.getCurrentFloor().getNumber()){
					temp += "| X ";
				}
				else{
					temp += "|   ";
				}
			}
			//getting waiting passengers
			String floorString = "";
			for (int i = 0; i < this.getFloor(this.getFloorCount()-f).getWaitingPassengers().size(); i++) {
				floorString += this.getFloor(this.getFloorCount()-f).getWaitingPassengers().get(i).getDestination() + " ";
			}
			temp += "| " + floorString.substring(0, floorString.length());
			returnString += temp + "\n";
		}
		for (Elevator elevator : this.mElevators){
			returnString += elevator.toString() + "\n";
		}

		return returnString;
	}
	
	public int getFloorCount() {
		return mFloors.size();
	}
	
	public Floor getFloor(int floor) {
		return mFloors.get(floor - 1);
	}
	
	public Simulation getSimulation() {
		return mSimulation;
	}
	
	
	@Override
	public void elevatorDecelerating(Elevator elevator) {
		// Have to implement all interface methods even if we don't use them.
	}
	
	@Override
	public void elevatorDoorsOpened(Elevator elevator) {
		// Don't care.
	}
	
	@Override
	public void elevatorWentIdle(Elevator elevator) {
		// TODO: if mWaitingFloors is not empty, remove the first entry from the queue and dispatch the elevator to that floor.
		//[ATTEMPTED]
		if (!mWaitingFloors.isEmpty()){
			System.out.println(mWaitingFloors);
			int floor = mWaitingFloors.remove();
			elevator.dispatchTo(getFloor(floor));
		}
	}
	
	@Override
	public void elevatorArriving(Floor sender, Elevator elevator) {
		// TODO: add the floor mWaitingFloors if it is not already in the queue.
		if (!mWaitingFloors.contains(sender.getNumber()-1)){
			mWaitingFloors.add(sender.getNumber()-1);
		}
	}
	
	@Override
	public void directionRequested(Floor floor, Elevator.Direction direction) {
		// TODO: go through each elevator. If an elevator is idle, dispatch it to the given floor.
		// TODO: if no elevators are idle, then add the floor number to the mWaitingFloors queue.
		//[ATTEMPTED]
		boolean elevatorSent = false;
		for (Elevator e : mElevators){
			if (e.isIdle()) {
				e.dispatchTo(floor);
				elevatorSent = true;
			}
		}
		if (!elevatorSent){
			mWaitingFloors.add(floor.getNumber()-1);
		}
	}
}
