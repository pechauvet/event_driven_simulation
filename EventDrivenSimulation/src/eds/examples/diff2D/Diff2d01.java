/**
 * 
 */
package eds.examples.diff2D;

import eds.core.ASimUnit;
import eds.core.EDSimulator;
import eds.core.EDSimulatorEvt;
import eds.core.EDSimulatorListener;
import pde.PDE2dDiffA;

/**
 * Example of simulation of a diffusion equation in 2D 
 *  
 * @author Pierre E. Chauvet
 * @version 1.0
 * @see     PDE2dDiffA , ASimUnit
 *
 */
public class Diff2d01 extends ASimUnit {

	public float[][] VU=null;
	private float[][] Vsource=null;
	private PDE2dDiffA pde;
	
	// Constructor
	public Diff2d01(int nx, int ny) {
		pde=new PDE2dDiffA(nx,ny);
		VU=new float[nx+1][ny+1];
		Vsource=new float[nx+1][ny+1];
	}
	
	// Get the time step value
	public float getTau() {
		return pde.tau;
	}
	
	// Set the time step value
	public void setTau(float tau) {
		pde.tau=tau;
	}
	
	// Simulation of an emission of matter (or heat...) at the center of the map
	protected void computeSource(float t) {
		Vsource[pde.getNx()/2][pde.getNy()/2]= 500f*(float) Math.abs(Math.sin(Math.PI*t/(10f*pde.tau)));
	}
	
	// Printing a 2-dimensional array to System.out
	public static void print(float[][] v) {
		StringBuffer sb;
		for(int i=0;i<v.length;i++) {
			sb=new StringBuffer();
			for(int j=0;j<v[i].length;j++) {
				sb.append(Float.toString(v[i][j])+" ");
			}
			System.out.println(sb.toString());
		}
	}

	@Override
	public boolean init(double beginTime, double endTime) {
		for(int i=0;i<=pde.getNx();i++) {
			for(int j=0;j<=pde.getNy();j++) {
				Vsource[i][j]=0f;
				pde.Vd[i][j]=0;
				VU[i][j]=0.1f;
			}
		}
		pde.calculMat();
		this.addEvent(pde.tau, "Solve");		
		return true;
	}

	@Override
	public boolean play(String action) {
		double time=this.getTime();
		computeSource((float)time);
		pde.solve(Vsource,VU);
		this.addEvent(pde.tau, "Solve");
		return true;
	}

	/**
	 * The main program
	 * @param args
	 */
	public static void main(String[] args) {
		/* Model definition */
		float tau=0.01f; // value of the time step
		int nx=10; // number of steps in space in the x direction
		int ny=10; // number of steps in space in the y direction
		final Diff2d01 diff=new Diff2d01(nx,ny);
		diff.setTau(tau);
		/* Carrying out the simulation */
		final EDSimulator simulator=new EDSimulator(0,10*tau);
		simulator.addSimUnit(diff);
		simulator.setNbRuns(1);
		simulator.setStopForEndTime(true);
		simulator.addSimulatorListener(new EDSimulatorListener() {
			@Override
			public void simulationStepped(EDSimulatorEvt e) {
				System.out.println(e.getMessage()+": "+e.getPercent()+"%");
				Diff2d01.print(diff.VU);
				System.out.println();
			}

			@Override
			public void simulationTerminated(EDSimulatorEvt e) {
				System.out.println(e.getMessage());
			}

			@Override
			public void simulationRunEnded(EDSimulatorEvt e) {
				// TODO Auto-generated method stub
				
			}			
		});
		simulator.start();
	}

}
