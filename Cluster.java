import java.util.ArrayList;
/**
 * Cluster is basically just an ArrayList of DataPoints with a uniqueID and some other methods for convenience.
* @Author Andrew Elenbogen and Quang Tran
* @Version March 2, 2015
*/
public class Cluster 
{
	private ArrayList<DataPoint> cluster;
	
	private static int uniqueIdVal=0;
	
	private int uniqueId;
	
	public Cluster(ArrayList<DataPoint> cluster)
	{
		this.cluster=cluster;
		assignUniqueId();
	}
	public Cluster(DataPoint point)
	{
		cluster=new ArrayList<DataPoint>();
		cluster.add(point);
		assignUniqueId();
	}
	
	/**
	 * Assigns this cluster the first unassigned Id.
	 */
	private void assignUniqueId()
	{
		uniqueId=uniqueIdVal;
		uniqueIdVal++;
	}
	
	/**
	 * Returns the clusters Id.
	 */
	public int getUniqueId()
	{
		return uniqueId;
	}
	
	/**
	 * Gets the center of the cluster as a DataPoint
	 */
	public DataPoint getCenter()
	{
		DataPoint center=new DataPoint();
		for(String key: DataPoint.getDefaultFieldNamesInOrder())
		{
			float total=0;
			for(DataPoint currentPoint: cluster)
			{
				total+=currentPoint.get(key);
			}
			center.put(key, total/cluster.size());
		}
		return center;
	}
	
	/**
	 * Gets and returns the distance from this cluster's center to the center of the other cluster
	 */
	public float getDistance(Cluster otherCluster)
	{
		return this.getCenter().getSquaredEuclideanDistance(otherCluster.getCenter());
	}
	
	
	/**
	 * Returns the full list of points in  the cluster
	 */
	public ArrayList<DataPoint> getEntireCluster()
	{
		return cluster;
	}
	
	/**
	 * Returns a new cluster created by merging this cluster with the other cluster.
	 */
	public Cluster merge(Cluster otherCluster)
	{
		ArrayList<DataPoint> mergedList=new ArrayList<DataPoint>();
		mergedList.addAll(this.getEntireCluster());
		mergedList.addAll(otherCluster.getEntireCluster());
		return new Cluster(mergedList);
	}
	
	/**
	 * Returns whether or not this cluster has the same uniqueId as the given object if said object is a cluster.s
	 */
	@Override
	public boolean equals(Object other)
	{
		if(!(other instanceof Cluster))
		{
			return false;
		}
		Cluster otherCluster=(Cluster) other;
		return (uniqueId==otherCluster.getUniqueId());
	}
	/**
	 * Returns the uniqueId. A perfect Hash!
	 */
	@Override
	public int hashCode()
	{
		return uniqueId;
	}
	/**
	 * Converts the center of the cluster to a String and returns it.
	 */
	public String toString()
	{
		return this.getCenter().toString();
	}
}
