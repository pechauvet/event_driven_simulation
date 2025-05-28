/**
 * 
 */
package eds.core;

import java.util.Vector;

/**
 * a final class which contain the list of events in the correct
 * order in time (decreasing order of time), and the current time.
 *
 * @author  Pierre E. Chauvet
 * @version 1.0
 * @see     Event , ASimUnit
 */
public final class EventSchedule extends Vector<Event> {

	private static final long serialVersionUID = -7186124215264955615L;

    protected double time=0; // current time of the schedule
    
    private double beginTime=0; // simulation start time

	public EventSchedule() {
		super();
	}

	/**
	 * @return the time
	 */
	public final double getTime() {
		return time;
	}
	
	public final void reset() {
		time=beginTime;
		this.clear();
	}
	
	/**
	 * @return the beginTime
	 */
	public final double getBeginTime() {
		return beginTime;
	}

	/**
	 * @param beginTime the beginTime to set
	 */
	public final void setBeginTime(double beginTime) {
		this.beginTime = beginTime;
	}

	// Inserts an event dichotomously so that the list of events is arranged 
	// in descending order of times of occurrence
	public void addEvent(Event evt) {
		int n=size();
		if(n==0) {add(evt);}
		else {
			double tmp=0;
			int k=0,i;
			--n;
			// Allows priorities to be taken into account when actions 
			// must occur at identical times
			if(evt.unit.priority!=0) {
				tmp=evt.time;        
				evt.time=tmp-evt.unit.priority*0.0000001f;
			}
			// Insertion by dichotomy into sorted list of actions
			while(k<=n) {
				i=(k+n)/2;
				if(evt.preceding((Event) get(i)))  {k=i+1;}
				else {n=i-1;}
			}
			add(k,evt);
			// Restoration of the exact moment of the action
			if(evt.unit.priority!=0) {evt.time=tmp;}
		}       
	}
	
	public void addEvent(double step,ASimUnit unit,String action) {
		if (unit!=null) {
			Event act=new Event(time+step,unit,action);
			addEvent(act);
		}
	}

	// Pops the action at (size-1) from the list, which is the one 
	// that must occur as soon as possible from the current time. 
	// The current time becomes the time of the popped action.
	public Event getEvent() {
		if(size()==0) {
			return null;}
		else {
			Event evt=(Event) remove(size()-1);
			time=evt.time;        
			return evt;
		}
	}

	// Removes all events relating to unit from the instant fromTime 
	public void removeEvents(ASimUnit unit,double fromTime) {
		if(size()!=0) {
			int i=size();double t=0;Event evt;
			do {
				i--;
				evt=(Event) get(i);
				t=evt.time;
			}
			while((t<fromTime)&(i!=0));
			if((i==0)&(fromTime<=t)&(evt.unit==unit))
			{remove(0);}
			else {
				int j;
				for(j=i;0<=j;j--) {
					evt=(Event) get(j);
					if(evt.unit==unit) {remove(j);}
				}
			} 	    	
		}
	}

}
