/**
 * 
 */
package eds.core;

import java.util.Vector;

import javax.swing.event.EventListenerList;

/**
 * the final class EDSimulator implements the event loop on an
 * EventSchedule in the run() method, to simulate a discrete
 * event system. The simulation is running in its own thread.
 *  
 * @author Pierre E. Chauvet
 * @version 1.0
 * @see     EDSimulatorEvt , EDSimulatorListener , EventSchedule , ASimUnit
*/
public final class EDSimulator implements Runnable {

	// Constants that represent the different states of the simulator
	public static final int STOPPED_STATE=0;
	public static final int RUNNING_STATE=1;
	public static final int PAUSED_STATE=2;
	
	// Simulator status (running, paused or stopped)
	private int state=STOPPED_STATE;

	// Declaration-creation of the schedule
	private final EventSchedule schedule=new EventSchedule();
	
	// Declaration-creation of the list of listeners
	private final EventListenerList listeners = new EventListenerList();
	
	// Declaration-creation of the list of ASimUnit units to be simulated
	private final Vector<ASimUnit> simUnits=new Vector<ASimUnit>();

	// Thread in which the simulation is done
	private Thread thread;

	// Variables to set the time window to simulate
	private double beginTime=0; // initial time
	private double endTime=1; // final time
	private boolean stopForEndTime=false; // stop option: True -> force stop when schedule.time>=endTime, False otherwise 
	
	// Number of runs to be performed (number of system simulations)
	private int nbRuns=20;
	
	// Number of runs actually completed
	private int runsCount;
	
	// Actual duration of the simulation
	private double duration;
	
	// Variables to track simulation progress
	private double step;
    private double threshold;
    private int stepCount;


	/**
	 * @param beginTime
	 * @param endTime
	 * 
	 */
	public EDSimulator(float beginTime, float endTime) {
		super();
		this.beginTime=beginTime;
		this.endTime=endTime;
		schedule.setBeginTime(beginTime);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Event event=null;
		long begin = System.currentTimeMillis();
		while((state==RUNNING_STATE)&&(runsCount<nbRuns)) {
			// Main loop for one simulation (as long as there is one event remaining or the simulation end time is not reached)
			while((state==RUNNING_STATE)&&((!stopForEndTime)||(schedule.getTime()<=endTime))&&(schedule.size()>0)) {
				event=schedule.getEvent();
				event.unit.play(event.action);
				if(schedule.getTime()>=threshold) {
					stepCount++;
					threshold+=step;
					fireSimulationStepped("Simulation running (time="+Double.toString(schedule.getTime())+")",Math.round(10f*stepCount/nbRuns));
				}
			}
			if(state==RUNNING_STATE) {
				runsCount++;
				threshold=step;
				fireSimulationRunEnded("Simulation Run #"+Integer.toString(runsCount)+" finished");
				schedule.reset();
				for(ASimUnit unit:simUnits) {
					unit.init(beginTime, endTime);
				}
			}
		}
		duration+=((float)(System.currentTimeMillis() - begin)) / 1000.0f;
		state=STOPPED_STATE;
		fireSimulationTerminated();
	}
	
	public void start() {
		// Stops the thread if it is running
		if(state==RUNNING_STATE) {
			state=STOPPED_STATE;
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Initializes the simulation if in STOPPED mode
		if(state==STOPPED_STATE) {
			schedule.reset();
			for(ASimUnit unit:simUnits) {
				unit.init(beginTime, endTime);
			}
			duration=0f;
		    step=(endTime-beginTime)/10;
			threshold=step;
			stepCount=0;
			runsCount=0;
		}
		// Starts the simulator calculation thread
		state=RUNNING_STATE;
		thread=new Thread(this);
		thread.start();
	}
	
	public void stop() {
		if(state==RUNNING_STATE) {
			state=STOPPED_STATE;
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else if(state==PAUSED_STATE) {
			state=STOPPED_STATE;			
		}
	}
	
	public void pause() {
		if(state==RUNNING_STATE) {
			state=PAUSED_STATE;
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param stopForEndTime the stopForEndTime to set
	 */
	public void setStopForEndTime(boolean stopForEndTime) {
		this.stopForEndTime = stopForEndTime;
	}

	/**
	 * @return the stopForEndTime
	 */
	public boolean isStopForEndTime() {
		return stopForEndTime;
	}

	/**
	 * @param nbRuns the nbRuns to set
	 */
	public void setNbRuns(int nbRuns) {
		if(nbRuns>0) this.nbRuns = nbRuns;
	}

	/**
	 * @return the nbRuns
	 */
	public int getNbRuns() {
		return nbRuns;
	}

	/**
	 * @return the runsCount
	 */
	public int getRunsCount() {
		return runsCount;
	}

	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}

	/**
	 * @return the duration
	 */
	public double getDuration() {
		return duration;
	}
	
	public void addSimUnit(ASimUnit unit) {
		simUnits.add(unit);
		unit.linkToSchedule(schedule);
	}
	
	public void removeSimUnit(ASimUnit unit) {
		simUnits.remove(unit);
	}

	public void addSimulatorListener(EDSimulatorListener listener) {
		listeners.add(EDSimulatorListener.class, listener);
	}

	public void removeSimulatorListener(EDSimulatorListener listener) {
		listeners.remove(EDSimulatorListener.class, listener);
	}

	public EDSimulatorListener[] getSimulatorListeners() {
		return listeners.getListeners(EDSimulatorListener.class);
	}

	protected void fireSimulationTerminated() {
		for(EDSimulatorListener listener : getSimulatorListeners()) {
			listener.simulationTerminated(new EDSimulatorEvt(this,EDSimulatorEvt.TERMINATED_ID,"Simulation terminated",0));
		}
	}

	protected void fireSimulationRunEnded(String message) {
		for(EDSimulatorListener listener : getSimulatorListeners()) {
			listener.simulationRunEnded(new EDSimulatorEvt(this,EDSimulatorEvt.RUNENDED_ID,message,runsCount));
		}
	}

	protected void fireSimulationStepped(String message,int percent) {
		for(EDSimulatorListener listener : getSimulatorListeners()) {
			listener.simulationStepped(new EDSimulatorEvt(this,EDSimulatorEvt.STEP_ID,message,percent));
		}
	}

}
