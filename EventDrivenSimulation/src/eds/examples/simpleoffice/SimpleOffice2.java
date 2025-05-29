/**
 * 
 */
package eds.examples.simpleoffice;

import java.util.ArrayList;
import java.util.Random;

import eds.core.ASimUnit;
import eds.core.EDSimulator;
import eds.core.EDSimulatorEvt;
import eds.core.EDSimulatorListener;

/**
* We consider a queue in front of an office with nres employees to provide the service, nres>0.
* The office is open 8 hours a day.
* The queue is FIFO-like, and the service time at the office follows a normal distribution
* with a mean of 6 minutes and a standard deviation of 1 minute. Users have an inter-arrival 
* time of uniform distribution between 2 and 4 minutes. Every user in the queue is served, 
* even after closing. All times are expressed in seconds.
 * 
 * @author Pierre E. Chauvet
 * @see    ASimUnit
 *
 */
public final class SimpleOffice2 extends ASimUnit {

	private class TimeVal {
		double time;
		int value;
		public TimeVal(double t, int v) {
			this.time=t;
			this.value=v;
		}
	}
	
	private transient Random random=new Random();
	private transient boolean closing;
	// State variables
	private int office; // current number of unoccupied employees
	private int queue; // number of users in the queue
	// Parameter values of this system
	private int nres; // number of resources, i.e. number of employees
	private float duration_opening; // opening hours (1 working day)
	private float service_mean; // average length of service
	private float service_std; // standard deviation of length of service
	private float arrival_min; // minimum inter-arrival time
	private float arrival_max; // maximum inter-arrival time
	// Observation variables
	private int nb_users_day; // number of users served per day
	private int nb_users_closing; // number of users served after closing
	private ArrayList<TimeVal> trace_employee_usage; // trace office value, i.e. the number of unoccupied employees 
	private double[] percent_busy_employee; // percentage of time that n employees are busy, for n=0 to nres
	// Final statistics
	private float nb_users_day_mean=0; // average number of users served per day
	private float nb_users_closing_mean=0; // average number of users served after closing
	private double[] percent_busy_employee_mean;

	/**
	 * 
	 */
	public SimpleOffice2(int nres) {
		// Number of employees
		this.nres=nres;
		// Default parameter values (in seconds)
		duration_opening=4*60*60;
		service_mean=6*60;
		service_std=60;
		arrival_min=2*60;
		arrival_max=4*60;
	}

	/* (non-Javadoc)
	 * @see eds.core.ASimUnit#init(float, float)
	 */
	@Override
	public boolean init(double beginTime, double endTime) {
		  // Initialization of variables
		  closing=false;
		  office=nres;
		  queue=0;
		  nb_users_day=0;
		  nb_users_closing=0;
		  trace_employee_usage = new ArrayList<TimeVal>();
		  percent_busy_employee=new double[nres+1];
		  addTrace();
		  // Initializing the event stack (the scheduler)
		  addEvent(duration_opening,"Closing");
		  addEvent(arrival_min+(arrival_max-arrival_min)*random.nextFloat(),"UserEntrance");
		  return true;
	}

	/* (non-Javadoc)
	 * @see eds.core.ASimUnit#play(java.lang.String)
	 */
	@Override
	public boolean play(String action) {
		// Response to events
		if(action.equals("UserEntrance")) {
			nb_users_day++;
			if(queue>0) {queue++;}
			else {
				if(office==0) {queue=1;}
				else {
					office--;
					addTrace();
					if(closing) {nb_users_closing++;}
					addEvent(service_std*(float)random.nextGaussian()+service_mean,"EndService");
				}
			}
			if(!closing) {addEvent(arrival_min+(arrival_max-arrival_min)*random.nextFloat(),"UserEntrance");}
		}
		else if (action.equals("EndService")) {
			if(queue>0) {
				queue--;
				if(closing) {nb_users_closing++;}
				addEvent(service_std*(float)random.nextGaussian()+service_mean,"EndService");
			}
			else {
				office++;
				addTrace();
			}
		}
		else if(action.equals("Closing")) {closing=true;}
		return true;
	}

	private void addTrace() {
		trace_employee_usage.add(new TimeVal(this.getTime(), this.office));
	}
	
	private void computePercentBusy() {
		TimeVal prev;
		TimeVal current=trace_employee_usage.get(0);
		for(int i=1; i<trace_employee_usage.size();i++) {
			prev=current;
			current=trace_employee_usage.get(i);
			percent_busy_employee[nres-prev.value]+=(current.time-prev.time)/simulator.getLastEventTime();
		}
	}
	
	/**
	 * @return the nres
	 */
	public final int getNres() {
		return nres;
	}

	/**
	 * @param nres the nres to set
	 */
	public final void setNres(int nres) {
		this.nres = nres;
	}

	/**
	 * @return the duration_opening
	 */
	public final float getDuration_opening() {
		return duration_opening;
	}

	/**
	 * @param duration_opening the duration_opening to set
	 */
	public final void setDuration_opening(float duration_opening) {
		this.duration_opening = duration_opening;
	}

	/**
	 * @return the service_mean
	 */
	public final float getService_mean() {
		return service_mean;
	}

	/**
	 * @param service_mean the service_mean to set
	 */
	public final void setService_mean(float service_mean) {
		this.service_mean = service_mean;
	}

	/**
	 * @return the service_std
	 */
	public final float getService_std() {
		return service_std;
	}

	/**
	 * @param service_std the service_std to set
	 */
	public final void setService_std(float service_std) {
		this.service_std = service_std;
	}

	/**
	 * @return the arrival_min
	 */
	public final float getArrival_min() {
		return arrival_min;
	}

	/**
	 * @param arrival_min the arrival_min to set
	 */
	public final void setArrival_min(float arrival_min) {
		this.arrival_min = arrival_min;
	}

	/**
	 * @return the arrival_max
	 */
	public final float getArrival_max() {
		return arrival_max;
	}

	/**
	 * @param arrival_max the arrival_max to set
	 */
	public final void setArrival_max(float arrival_max) {
		this.arrival_max = arrival_max;
	}

	/**
	 * @return the office
	 */
	public final int getOffice() {
		return office;
	}

	/**
	 * @return the queue
	 */
	public final int getQueue() {
		return queue;
	}

	/**
	 * @return the nb_users_day
	 */
	public final int getNb_users_day() {
		return nb_users_day;
	}

	/**
	 * @return the nb_users_closing
	 */
	public final int getNb_users_closing() {
		return nb_users_closing;
	}

	/**
	 * @return the nb_users_day_mean
	 */
	public final float getNb_users_day_mean() {
		return nb_users_day_mean;
	}

	/**
	 * @return the nb_users_closing_mean
	 */
	public final float getNb_users_closing_mean() {
		return nb_users_closing_mean;
	}

	/**
	 * @param nb_users_day_mean the nb_users_day_mean to set
	 */
	public final void setNb_users_day_mean(float nb_users_day_mean) {
		this.nb_users_day_mean = nb_users_day_mean;
	}

	/**
	 * @param nb_users_closing_mean the nb_users_closing_mean to set
	 */
	public final void setNb_users_closing_mean(float nb_users_closing_mean) {
		this.nb_users_closing_mean = nb_users_closing_mean;
	}

	/**
	 * @return the percent_busy_employee_mean
	 */
	public final double[] getPercent_busy_employee_mean() {
		return percent_busy_employee_mean;
	}

	/**
	 * @param percent_busy_employee_mean the percent_busy_employee_mean to set
	 */
	public final void setPercent_busy_employee_mean(double[] percent_busy_employee_mean) {
		this.percent_busy_employee_mean = percent_busy_employee_mean;
	}

	/**
	 * The main program
	 * @param args
	 */
	public static void main(String[] args) {
		final SimpleOffice2 model=new SimpleOffice2(2);
		final EDSimulator simulator=new EDSimulator(0,model.getDuration_opening());
		simulator.addSimUnit(model);
		simulator.setNbRuns(2);
		simulator.addSimulatorListener(new EDSimulatorListener() {
			@Override
			public void simulationStepped(EDSimulatorEvt e) {
				int p=e.getPercent();
				if(p>100) p=100;
				System.out.println(e.getMessage()+": "+p+"%");
			}
			@Override
			public void simulationTerminated(EDSimulatorEvt e) {
				// Compute mean observations
				model.nb_users_day_mean=model.nb_users_day_mean/simulator.getRunsCount();
				model.nb_users_closing_mean=model.nb_users_closing_mean/simulator.getRunsCount();
				for(int i=0;i<=model.nres;i++) {
					model.percent_busy_employee_mean[i]=model.percent_busy_employee_mean[i]/simulator.getRunsCount();;
				}	
				// Show result
				System.out.println(e.getMessage());
				System.out.println("Average number of users per day = "+model.nb_users_day_mean);
				System.out.println("Average number of users after closing = "+model.nb_users_closing_mean);
				for(int i=0;i<=model.nres;i++) {
					System.out.println("Average duration (in %) with "+i+" employee(s) busy = "+model.percent_busy_employee_mean[i]);
				}	
				System.out.println("Simulation duration = "+simulator.getDuration()+"s");
			}
			@Override
			public void simulationRunEnded(EDSimulatorEvt e) {
				System.out.println(e.getMessage());
				System.out.println("Run # "+e.getPercent());
				System.out.println("Number of users per day = "+model.nb_users_day);
				System.out.println("Number of users after closing = "+model.nb_users_closing);
				System.out.println("Working time (mn) after closing = "+(simulator.getLastEventTime()-model.duration_opening)/60);
				/*for(TimeVal tv:model.trace_employee_usage) 
					System.out.println("t="+tv.time+" nb="+tv.value);
				*/
				model.computePercentBusy();
				for(int i=0;i<=model.nres;i++) {
					System.out.println("Duration (in %) with "+i+" employee(s) busy = "+model.percent_busy_employee[i]);
					model.percent_busy_employee_mean[i]+=model.percent_busy_employee[i];
				}	
				System.out.println();
				model.nb_users_day_mean+=model.nb_users_day;
				model.nb_users_closing_mean+=model.nb_users_closing;
			}	
		});
		model.setNb_users_day_mean(0);
		model.setNb_users_closing_mean(0);
		model.setPercent_busy_employee_mean(new double[model.getNres()+1]);
		simulator.start();
	}

}
