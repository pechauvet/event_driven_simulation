/**
 * 
 */
package eds.core;

import java.util.EventListener;

/**
 * @author Pierre E. Chauvet
 * @version 1.0
 * @see     EDSimulatorEvt
 */
public interface EDSimulatorListener extends EventListener {

	void simulationStepped(EDSimulatorEvt e);
	
	void simulationRunEnded(EDSimulatorEvt e);

	void simulationTerminated(EDSimulatorEvt e);
	

}
