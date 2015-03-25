
/**
 * A Cluster Pair is a simple class that contains two clusters
 * @Author Andrew Elenbogen and Quang Tran
 * @Version March 2, 2015
 */
public class ClusterPair implements Comparable<ClusterPair>
{
	private Cluster cluster1;
	private Cluster cluster2;
	
	public ClusterPair(Cluster cluster1, Cluster cluster2) {
		this.cluster1 = cluster1;
		this.cluster2 = cluster2;
	}
	
	/**
	 * Returns whether or not this ClusterPair should have a higher or lower priority than the other. 
	 * Positive this gets more Prioirty
	 * Negative other gets more Prioirty
	 */
	@Override
	public int compareTo(ClusterPair other)
	{
		return Math.round( other.getDistanceBetween()-getDistanceBetween());
	}
	/**
	 * Returns the distance between the two clusters in this Cluster Pair.
	 * @return
	 */
	public float getDistanceBetween()
	{
		return cluster1.getDistance(cluster2);
	}
	/**
	 * Returns whether or not this ClusterPair's cluster match the other ClusterPairs clusters in uniqueId.
	 */
	@Override
	public boolean equals(Object other)
	{
		if(!(other instanceof ClusterPair))
		{
			return false;
		}
		ClusterPair otherPair=(ClusterPair) other;
		return (cluster1.equals(otherPair.getCluster1()) && cluster2.equals(otherPair.getCluster2())) ||
			   (cluster1.equals(otherPair.getCluster2()) && cluster2.equals(otherPair.getCluster1()));
	}
	/**
	 * Simple getters for the two clusters.
	 */
	public Cluster getCluster1()
	{
		return cluster1;
	}
	public Cluster getCluster2()
	{
		return cluster2;
	}
	
	

}
