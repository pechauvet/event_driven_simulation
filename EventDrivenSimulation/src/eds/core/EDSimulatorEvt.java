/**
 * 
 */
package eds.core;

/**
 * @author Pierre E. Chauvet
 * @version 1.0
 * @see     EDSimulator
 */
public final class EDSimulatorEvt {

	public static final String STEP_ID="step";
	public static final String TERMINATED_ID="terminated";
	public static final String RUNENDED_ID="run ended";
	
	private String id=""; 
	private EDSimulator source;
	private String message;
	private int percent;
	
	
	/**
	 * @param source
	 * 
	 */
	public EDSimulatorEvt(EDSimulator source,String id) {
		super();
		this.source=source;
		this.id=id;
		this.message="";
		this.percent=0;
	}
	
	
	/**
	 * @param source
	 * @param message
	 * @param percent
	 */
	public EDSimulatorEvt(EDSimulator source, String id, String message, int percent) {
		super();
		this.source = source;
		this.id=id;
		this.message = message;
		this.percent = percent;
	}


	/**
	 * @return the source
	 */
	public final EDSimulator getSource() {
		return source;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	/**
	 * @return the message
	 */
	public final String getMessage() {
		return message;
	}
	/**
	 * @return the percent
	 */
	public final int getPercent() {
		return percent;
	}

}
