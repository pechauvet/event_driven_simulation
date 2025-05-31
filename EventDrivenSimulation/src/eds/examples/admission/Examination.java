/**
 * 
 */
package eds.examples.admission;

import java.util.ArrayList;
import java.util.Random;
import eds.core.ASimUnit;


/**
 * The examination part of the medical analysis laboratory.
 * 
 * @author Pierre E. Chauvet
 * @see    MedLab, ASimUnit
 */
public class Examination extends ASimUnit {

	private class TimeVal {
		double time;
		int value;
		public TimeVal(double t, int v) {
			this.time=t;
			this.value=v;
		}
	}
	
	private transient Random random=new Random();
	// State variables
	private int nb_dispo; // current number of unoccupied nurses
	private int queue; // number of users in the queue
	// Parameter values of this system
	private int nb_nurse; // maximum number of nurses 
	private float exam_mean; // average length of examination
	private float exam_std; // standard deviation of length of examination
	// Observation variables
	private int nb_users_day; // number of users examined per day
	private ArrayList<TimeVal> trace_nurse_usage; // trace nb_dispo value, i.e. the number of unoccupied nurses 
	private double[] percent_busy_nurse; // percentage of time that n nurses are busy, for n=0 to nb_nurse

	/**
	 * 
	 */
	public Examination(int nb_nurse) {
		// Number of employees
		this.nb_nurse=nb_nurse;
		// Default parameter values (in seconds)
		exam_mean=7*60;
		exam_std=60;
	}

	@Override
	public boolean play(String action) {
		// Response to events
		if(action.equals("UserEntrance")) {
			nb_users_day++;
			if(queue>0) {queue++;}
			else {
				if(nb_dispo==0) {queue=1;}
				else {
					nb_dispo--;
					addTrace();
					addEvent(exam_std*(float)random.nextGaussian()+exam_mean,"EndService");
				}
			}
		}
		else if (action.equals("EndService")) {
			if(queue>0) {
				queue--;
				addEvent(exam_std*(float)random.nextGaussian()+exam_mean,"EndService");
			}
			else {
				nb_dispo++;
				addTrace();
			}
		}
		return true;
	}

	@Override
	public boolean init(double beginTime, double endTime) {
		  // Initialization of variables
		  nb_dispo=nb_nurse;
		  queue=0;
		  nb_users_day=0;
		  trace_nurse_usage = new ArrayList<TimeVal>();
		  percent_busy_nurse=new double[nb_nurse+1];
		  addTrace();
		  return true;
	}

	private void addTrace() {
		trace_nurse_usage.add(new TimeVal(this.getTime(), this.nb_dispo));
	}

	public void computePercentBusy() {
		TimeVal prev;
		TimeVal current=trace_nurse_usage.get(0);
		for(int i=1; i<trace_nurse_usage.size();i++) {
			prev=current;
			current=trace_nurse_usage.get(i);
			percent_busy_nurse[nb_nurse-prev.value]+=(current.time-prev.time)/simulator.getLastEventTime();
		}
	}

	/**
	 * @return the nb_users_day
	 */
	public final int getNb_users_day() {
		return nb_users_day;
	}

	/**
	 * @return the percent_busy_nurse
	 */
	public final double[] getPercent_busy_nurse() {
		return percent_busy_nurse;
	}

}
