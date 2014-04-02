package ut.mpc.kdt;

/*
 * Spatiotemporal Data Storage Interface
 */
public interface STStore {
	public int getSize();
	public void insert(Temporal point);
	public double getPointProbability(double[] point, int optLevel);
	public double windowQuery(boolean printWindow, int optLevel);
	public double windowQuery(double[] lowk, double[] uppk, boolean printWindow, int optLevel);
	public double windowQueryExt(double[] lowk, double[] uppk, boolean printWindow, int optLevel);
}
