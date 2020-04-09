package passengers;

import elevators.Elevator;
import cecs277.Simulation;
import events.PassengerNextDestinationEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * A WorkerPassenger visits many floors in succession. They have a list of destination floors and a list of durations,
 * each duration corresponding to the time they "disappear" after reaching each of the destination floors.
 */
public class WorkerPassenger extends Passenger {
	// TODO: add fields for the list of destination floors, and the list of duration amounts.
	// [DONE]
	private List<Integer> mDestinationList = new ArrayList<>();
	private List<Long> mDurationList = new ArrayList<>();
	
	public WorkerPassenger(List<Integer> destinations, List<Long> durations) {
		super();
		mDestinationList = destinations;
		mDurationList = durations;
		// TODO: finish the constructor.
		//[DONE]
	}
	// TODO: implement this method. Return the current destination, which is the first element of the destinations list.
	//[DONE]
	@Override
	public int getDestination() {
		return mDestinationList.get(0);
	}
	
	// TODO: implement this template method variant. A Worker will only join an elevator with at most 3 people on it.
	// [DONE]
	@Override
	protected boolean willBoardElevator(Elevator elevator) {
		return (elevator.getPassengerCount() <= 3);
	}
	
	/*
	 TODO: implement this template method variant, which is called when the worker is leaving the elevator it
	 is on. A Worker that is departing on floor 1 just leaves the building, printing a message to System.out.
	 A Worker that is departing on any other floor removes the first destination in their list, and then schedules a
	 PassengerNextDestinationEvent to occur when they are supposed to "reappear" (the first element of the durations list,
	 which is also removed.)
	*/
	// [ATTEMPTED], needs to leave building if this does not happen in another class otherwise should be done
	@Override
	protected void leavingElevator(Elevator elevator) {
		if (elevator.getCurrentFloor().getNumber() == 1) {
			System.out.println("Leaving building (Placeholder for now)");
		}
		else {
			mDestinationList.remove(0);
			Simulation s = elevator.getBuilding().getSimulation();
			PassengerNextDestinationEvent ev = new PassengerNextDestinationEvent(s.currentTime() + mDurationList.get(0), this,
			 elevator.getCurrentFloor());
			s.scheduleEvent(ev);
		}
	}
	
	@Override
	public void elevatorDecelerating(Elevator elevator) {
		// Don't care.
	}
	
	// TODO: return "Worker heading to floor {destination}", replacing {destination} with the first destination floor number.
	// [DONE]
	@Override
	public String toString() {
		return "Worker heading to floor " + mDestinationList.get(0);
	}
	
}
