   package pde;

/**
 * 
 * This method allows to solve the general partial differential equation:
 *	 dU/dt(x,y,t) = a11(x,y)d/dx(b11(x,y)dU/dx)(x,y,t) + a12(x,y)d/dx(b12(x,y)dU/dy)(x,y,t)
 *                       a21(x,y)d/dy(b21(x,y)dU/dx)(x,y,t) + a22(x,y)d/dy(b22(x,y)dU/dy)(x,y,t)
 *                       +c1(x,y) * dU/dx (x,y,t) + c2(x,y) * dU/dy (x,y,t)
 *                       + d(x,y) * U(x,y,t) + F(x,y,t)
 *        alfa * U + beta * [ dU/dx * n1 + dU/dy * n2 ] = Uex
 *        U(x,y,0) = U0(x,y).
 * for (x,y) in [0,Lx]x[0,Ly].
 *
 * The discretization in space is done by a finite difference method.
 * Time discretization is done according to the Crank-Nicolson scheme.
 *     
 * All functions are passed and used in tabulated form in space. In particular:
 *   hx=step in space in the x direction, i=0...nx-1,
 *   hy=step in space in the y direction, i=0...ny-1,
 *   Va11,Va22 = tabulated diffusion terms
 *   Vd[i][j]=d(i*hx,j*hy) = tabulated loss/gain term 
 *   VSource=tabulated source term
 *      
 * @author Pierre E. Chauvet
 * @version 1.1
 */
   public final class PDE2d {
   
      public float Lx=1;      // length of the spatial domain (x-length)
      public float Ly=1;      // width of the spatial domain (y-length)   
      public float tau=0.01f; // time step
      public float hx;        // space step along the x direction
      public float hy;        // space step along the y direction
      public float eps=0.0001f;// accuracy of the resolution of the linear system
      public int max_iter=100; // max number of iterations in solving the linear system
   
      protected int nx=0;   // number of space steps along the x direction
      protected int ny=0;   // number of space steps along the y direction
   
      public float[] Valfa_bottom;// coefficient Cond.Lim. on the bottom edge
      public float[] Valfa_top;   // coefficient Cond.Lim. on the top edge
      public float[] Valfa_left;  // coefficient Cond.Lim. on the left edge
      public float[] Valfa_right; // coefficient Cond.Lim. on the right edge
      public float[] Vbeta_bottom;// coefficient Cond.Lim. on the bottom edge
      public float[] Vbeta_top;   // coefficient Cond.Lim. on the top edge
      public float[] Vbeta_left;  // coefficient Cond.Lim. on the left edge
      public float[] Vbeta_right; // coefficient Cond.Lim. on the right edge
      public float[] VUex_bottom; // tabulated function Cond.Lim. on the bottom edge
      public float[] VUex_top;    // tabulated function Cond.Lim. on the top edge
      public float[] VUex_left;   // tabulated function Cond.Lim. on the left edge
      public float[] VUex_right;  // tabulated function Cond.Lim. on the right edge
      public float[][] Va11=null; // function in front of tabulated diffusion
      public float[][] Va12=null; // function in front of tabulated diffusion
      public float[][] Va21=null; // function in front of tabulated diffusion
      public float[][] Va22=null; // function in front of tabulated diffusion
      public float[][] Vb11=null; // tabulated diffusion
      public float[][] Vb12=null; // tabulated diffusion
      public float[][] Vb21=null; // tabulated diffusion 
      public float[][] Vb22=null; // tabulated diffusion
      public float[][] Vc1=null;  // tabulated convection (d/dx)
      public float[][] Vc2=null;  // tabulated convection (d/dy)
      public float[][] Vd=null;   // tabulated gain/loss
   
      protected float[][][] MA=null; // matrix of the left term of the discretized system
    //fMA [i,0]=lower diagonal
    //fMA [i,1]=diagonal
    //fMA [i,2]=higher diagonal
      protected float[][] VF0=null;  // source term at t-tau
      protected float[][] VFtau=null;// source term at t
      protected float[][] Vf=null;   // Right-hand side vector
   
      // Constructor
      public PDE2d(int nb_x,int nb_y) {
         setNbStep(nb_x,nb_y);
      }
   
      // Sets the number of steps in x and y, and builds the necessary tables
      public boolean setNbStep(int nb_x,int nb_y) {
         if ((nb_x<=0)||(nb_y<=0)) {
            return false;}
         else {
            int i,j;
            nx=nb_x;
            ny=nb_y;
            hx=Lx/nx;
            hy=Ly/ny;
            MA=null;MA=new float[nx+1][ny+1][9];
            Valfa_bottom=null;Valfa_bottom=new float[nx+1];
            Valfa_top=null;Valfa_top=new float[nx+1];
            Valfa_left=null;Valfa_left=new float[ny+1];
            Valfa_right=null;Valfa_right=new float[ny+1];
            Vbeta_bottom=null;Vbeta_bottom=new float[nx+1];
            Vbeta_top=null;Vbeta_top=new float[nx+1];
            Vbeta_left=null;Vbeta_left=new float[ny+1];
            Vbeta_right=null;Vbeta_right=new float[ny+1];
            VUex_bottom=null;VUex_bottom=new float[nx+1];
            VUex_top=null;VUex_top=new float[nx+1];
            VUex_left=null;VUex_left=new float[ny+1];
            VUex_right=null;VUex_right=new float[ny+1];
            Va11=null;Va11=new float[nx+1][ny+1];
            Va12=null;Va12=new float[nx+1][ny+1];
            Va21=null;Va21=new float[nx+1][ny+1];
            Va22=null;Va22=new float[nx+1][ny+1];
            Vb11=null;Vb11=new float[nx+1][ny+1];
            Vb12=null;Vb12=new float[nx+1][ny+1];
            Vb21=null;Vb21=new float[nx+1][ny+1];
            Vb22=null;Vb22=new float[nx+1][ny+1];
            Vc1=null;Vc1=new float[nx+1][ny+1];
            Vc2=null;Vc2=new float[nx+1][ny+1];
            Vd=null;Vd=new float[nx+1][ny+1];
            VF0=null;VF0=new float[nx+1][ny+1];
            VFtau=null;VFtau=new float[nx+1][ny+1];
            Vf=null;Vf=new float[nx+1][ny+1];
            for(i=0;i<=nx;i++) {
               for(j=0;j<=ny;j++) {
                  Va11[i][j]=1;Va12[i][j]=0;Va21[i][j]=0;Va22[i][j]=1;
                  Vb11[i][j]=1;Vb12[i][j]=0;Vb21[i][j]=0;Vb22[i][j]=1;
                  Vc1[i][j]=0;Vc2[i][j]=0;
                  Vd[i][j]=-1;
               }}
            for(i=0;i<=nx;i++) {Valfa_bottom[i]=0;Vbeta_bottom[i]=1;}
            for(j=0;j<=ny;j++) {Valfa_right[j]=0;Vbeta_right[j]=-1;}
            for(i=0;i<=nx;i++) {Valfa_top[i]=0;Vbeta_top[i]=-1;}
            for(j=0;j<=ny;j++) {Valfa_left[j]=0;Vbeta_left[j]=1;}
            return true;
         }
      }
   
      public int getNx() {
      	return nx;
      }
      
      public int getNy() {
      	return ny;
      }

     /* Calculation of the MA matrix, the system to be solved 
        being of the form:
            MA.VU(t+tau)=Vf(t,t+tau) ;
        If diffusion, velocity, and loss are time independent, 
        calculMat only needs to be called once, outside the 
        time-solving loop.
      */
      public boolean calculMat() {
         if ((nx<=0)||(ny<=0)) {
            return false;}
         else {
            int i,j;
            float invtau=1/tau;
            float hx2=hx*hx;
            float hy2=hy*hy;
            for(i=0;i<=nx;i++) {
               for(j=0;j<=ny;j++) {
                  if(j==0) { // bottom edge
                     MA[i][j][0]=0;MA[i][j][1]=0;
                     MA[i][j][2]=0;MA[i][j][3]=0;
                     MA[i][j][4]=Valfa_bottom[i]+Vbeta_bottom[i]/hy ; //A[i,i]
                     MA[i][j][5]=0;MA[i][j][6]=0;
                     MA[i][j][7]=-Vbeta_bottom[i]/hy ; //A[i,i+mxx]
                     MA[i][j][8]=0;
                  }
                  else if(i==nx) { // right edge
                     MA[i][j][0]=0;MA[i][j][1]=0;
                     MA[i][j][2]=0;
                     MA[i][j][3]=Vbeta_right[j]/hx ; //A[i,i-1]
                     MA[i][j][4]=Valfa_right[j]-Vbeta_right[j]/hx ; //A[i,i]
                     MA[i][j][5]=0;MA[i][j][6]=0;MA[i][j][7]=0;MA[i][j][8]=0;
                  }
                  else if(j==ny) { // top edge
                     MA[i][j][0]=0;
                     MA[i][j][1]=Vbeta_top[i]/hy ; //A[i,i-mxx]
                     MA[i][j][2]=0;MA[i][j][3]=0;
                     MA[i][j][4]=Valfa_top[i]-Vbeta_top[i]/hy ; //A[i,i]
                     MA[i][j][5]=0;MA[i][j][6]=0;MA[i][j][7]=0;MA[i][j][8]=0;
                  }
                  else if(i==0) { // left edge
                     MA[i][j][0]=0;MA[i][j][1]=0;
                     MA[i][j][2]=0;MA[i][j][3]=0;
                     MA[i][j][4]=Valfa_left[j]+Vbeta_left[j]/hx ; //A[i,i]
                     MA[i][j][5]=-Vbeta_left[j]/hx ; //A[i,i+1]
                     MA[i][j][6]=0;MA[i][j][7]=0;MA[i][j][8]=0;
                  }
                  else { // interior points
                     float b11x=(Vb11[i+1][j]-Vb11[i-1][j])/(2*hx);
                     float b12x=(Vb12[i+1][j]-Vb12[i-1][j])/(2*hx);
                     float b21y=(Vb21[i][j+1]-Vb21[i][j-1])/(2*hy);
                     float b22y=(Vb22[i][j+1]-Vb22[i][j-1])/(2*hy);
                     MA[i][j][0]=- (Va12[i][j]*Vb12[i][j]+Va21[i][j]*Vb21[i][j])/(4*hx*hy) ; //-A(i,i-mxx-1)
                     MA[i][j][1]=- Va22[i][j]*Vb22[i][j]/hy2+(Va12[i][j]*b12x+Va22[i][j]*b22y+Vc2[i][j])/(2*hy) ; //-A(i,i-mxx)
                     MA[i][j][2]=- MA[i][j][0] ; //-A(i,i-mxx+1)
                     MA[i][j][3]=- Va11[i][j]*Vb11[i][j]/hx2 + (Va11[i][j]*b11x+Va21[i][j]*b21y+Vc1[i][j])/(2*hx) ; //-A(i,i-1)
                     MA[i][j][4]=2*invtau - Vd[i][j] + 2*Va11[i][j]*Vb11[i][j]/hx2 + 2*Va22[i][j]*Vb22[i][j]/hy2 ; //2/tau - A(i,i)
                     MA[i][j][5]=-Va11[i][j]*Vb11[i][j]/hx2 - (Va11[i][j]*b11x+Va21[i][j]*b21y+Vc1[i][j])/(2*hx) ; //-A(i,i+1)
                     MA[i][j][6]= MA[i][j][2] ; //-A(i,i+mxx-1)
                     MA[i][j][7]=- Va22[i][j]*Vb22[i][j]/hy2 - (Va12[i][j]*b12x+Va22[i][j]*b22y+Vc2[i][j])/(2*hy) ; //-A(i,i+mxx)
                     MA[i][j][8]= MA[i][j][0] ; //-A(i,i+mxx+1)
                  } 
               }}
            return true;
         }
      }
   
      public boolean calculMat(float[][] fVa11,float[][] fVa12,float[][] fVa21,float[][] fVa22,
                        float[][] fVb11,float[][] fVb12,float[][] fVb21,float[][] fVb22,
                        float[][] fVc1,float[][] fVc2,float[][] fVd) {
         setNbStep(fVd.length,fVd[0].length);
         Va11=fVa11;Va12=fVa12;Va21=fVa21;Va22=fVa22;
         Vb11=fVb11;Vb12=fVb12;Vb21=fVb21;Vb22=fVb22;
         Vc1=fVc1;Vc2=fVc2;
         Vd=fVd;
         return calculMat();
      }
   
   
      /* Resolution (see document)
		 VSource is the vector of tabulated values of F(x,y,t), 
		 where F represents the source term.
      */
      public boolean solve(float[][] VSource,float[][] VU) {
         if((nx<=0)||(ny<=0)) {
            return false;}
         else {
            int i,j,k=0;
            float s1=0,s2;
            // Saving VFtau in VF0
            for(i=0;i<=nx;i++) {
               for(j=0;j<=ny;j++) {
                  VF0[i][j]=VFtau[i][j];
                  VFtau[i][j]=VSource[i][j];
               }}
            // Construction of the equation's right side 
            for(i=0;i<=nx;i++) {
               for(j=0;j<=ny;j++) {
                  if(j==0) { // bottom edge
                     Vf[i][j]=VUex_bottom[i];
                  }
                  else if(i==nx) { // right edge
                     Vf[i][j]=VUex_right[j];
                  }
                  else if(j==ny) { // top edge
                     Vf[i][j]=VUex_top[i];
                  }
                  else if(i==0) { // left edge
                     Vf[i][j]=VUex_left[j];
                  }
                  else { // interior points
                     Vf[i][j]=-MA[i][j][0]*VU[i-1][j-1]-MA[i][j][1]*VU[i][j-1]-MA[i][j][2]*VU[i+1][j-1]
                        -MA[i][j][3]*VU[i-1][j]+(4/tau - MA[i][j][4])*VU[i][j]-MA[i][j][5]*VU[i+1][j]
                        -MA[i][j][6]*VU[i-1][j+1]-MA[i][j][7]*VU[i][j+1]-MA[i][j][8]*VU[i+1][j+1]
                        +VF0[i][j]+VFtau[i][j];
                  }
               }}
            // Inversion of the linear system by the iterative Gauss-Seidel method
            do {
               s2=s1;
               s1=0;
               k++;
               for(i=0;i<=nx;i++) {
                  for(j=0;j<=ny;j++) {
                     if(j==0) { // bottom edge
                        VU[i][j]=(Vf[i][j]-MA[i][j][7]*VU[i][j+1])/MA[i][j][4];
                     }
                     else if(i==nx) { // right edge
                        VU[i][j]=(Vf[i][j]-MA[i][j][3]*VU[i-1][j])/MA[i][j][4];
                     }
                     else if(j==ny) { // top edge
                        VU[i][j]=(Vf[i][j]-MA[i][j][1]*VU[i][j-1])/MA[i][j][4];
                     }
                     else if(i==0) { // left edge
                        VU[i][j]=(Vf[i][j]-MA[i][j][5]*VU[i+1][j])/MA[i][j][4];
                     }
                     else { // interior points
                        VU[i][j]=(Vf[i][j]-MA[i][j][0]*VU[i-1][j-1]-MA[i][j][1]*VU[i][j-1]-MA[i][j][2]*VU[i+1][j-1]-MA[i][j][3]*VU[i-1][j]
                                 -MA[i][j][5]*VU[i+1][j]-MA[i][j][6]*VU[i-1][j+1]-MA[i][j][7]*VU[i][j+1]-MA[i][j][8]*VU[i+1][j+1])/MA[i][j][4];
                     }
                     s1=s1+Math.abs(VU[i][j]) ;
                  }}
            } while((Math.abs(s2-s1)>eps)|(k<max_iter)) ;
            return true;
         }
      }
   
   }