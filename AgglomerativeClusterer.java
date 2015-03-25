import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

/**
 * Agglomerative Clustering Assignment
 * @Author Andrew Elenbogen and Quang Tran
 * @Version March 2, 2015
 * This class generates clusters using the Agglomerative method.
 */

public class AgglomerativeClusterer 
{
	private ArrayList<DataPoint> data = new ArrayList<DataPoint>();
	private static final String DATAFILE_LOCATION = "/tmp/portfoliodata.txt";
	private Random rand;
	private HashMap<String, Float> standardDevs;
	private HashMap<String, Float> means;
	
	public AgglomerativeClusterer()
	{
		rand = new Random();
		standardDevs= new HashMap<String, Float>();
		means=new HashMap<String, Float>(); 
		readFile();		
		standardize();
	}
	
	/**
	 * Function to standardize the data.
	 */
	public void standardize()
	{
		ArrayList<DataPoint> standardizedData=new ArrayList<DataPoint>(data.size());
		for(int i=0; i<data.size(); i++)
			standardizedData.add(new DataPoint());
		
		for(String key: DataPoint.getDefaultFieldNamesInOrder())
		{
			int nonNullSize=data.size();
			
			float total=0;
			for(DataPoint currentPoint: data)
			{
				if(currentPoint.get(key)==null)
					nonNullSize--;
				else
					total+=currentPoint.get(key);
			}
			float mean=total/nonNullSize;
			
			float stdTotal=0;
			for(DataPoint currentPoint: data)
			{
				if(currentPoint.get(key)!=null)
					stdTotal+=Math.pow(currentPoint.get(key)-mean, 2);
			}
			float stdDev=(float) Math.sqrt(stdTotal/nonNullSize);
			
			standardDevs.put(key, stdDev);
			means.put(key, mean);
			
			for(int i=0; i<data.size(); i++)
			{
				Float currentValFromCurrentPoint=data.get(i).get(key);
				if(currentValFromCurrentPoint!=null)
					standardizedData.get(i).put(key, (currentValFromCurrentPoint-mean)/stdDev);
				else
					standardizedData.get(i).put(key, 0f);
			}
		}
		data=standardizedData;
	}
	
	/**
	 * Method to convert the standardized data back to normal
	 */
	private void unstandardize(DataPoint point)
	{
		for(String key: point.getMap().keySet())
		{
			point.getMap().put(key, point.get(key)*standardDevs.get(key)+means.get(key));
		}
	}
	
	

	
	/**
	 * Reads the initial data file
	 */
	public void readFile(){
		try(Scanner scanner=new Scanner(new File(DATAFILE_LOCATION)))
		{
			scanner.nextLine();
			while (scanner.hasNextLine())
			{
				String[] split=scanner.nextLine().split("\t");
				HashMap<String, Float> currentMap=new HashMap<String, Float>();
				ArrayList<String> fieldNames=DataPoint.getDefaultFieldNamesInOrder();
				
				//System.out.println(Arrays.toString(split));
				
				for(int i=0; i<split.length; i++)
				{
					try
					{
						float value=Float.parseFloat(split[i]);
						if(Math.abs(value-9999.99)<1)
							currentMap.put(fieldNames.get(i), null);
						else
							currentMap.put(fieldNames.get(i), value);
					}
					catch(NumberFormatException e)
					{
						currentMap.put(fieldNames.get(i), null);
					}
				}
				data.add(new DataPoint(currentMap));
			}
		}
		catch (IOException e){
			System.out.println(e);
			System.exit(0);
		}
	}
	
	/*
	 * Method that clusters the data until getting k clusters
	 */
	
	public ArrayList<Cluster> cluster(int k)
	{
		// Keeping track of our clusters
		ArrayList<Cluster> clusters=new ArrayList<Cluster>();
		// Keeping track of the blacklist, which contains clusters that have already been taken fom the priority queue and merged
		HashMap<Cluster, Boolean> blackList=new HashMap<Cluster, Boolean>();
		
		for(DataPoint current: data)
		{
			Cluster newCluster=new Cluster(current);
			clusters.add(newCluster);
			blackList.put(newCluster, false);
		}

		// Keeping track of cluster pairs, to add into the priority queue
		ArrayList<ClusterPair> combinations=new ArrayList<ClusterPair>();
		for(Cluster first: clusters)
		{
			for(Cluster second: clusters)
			{
				if(!first.equals(second))
					combinations.add(new ClusterPair(first, second));
			}
		}
		
		PriorityQueue<ClusterPair> clusterQueue= new PriorityQueue<ClusterPair>(combinations);
		
		while(clusters.size()>k)
		{
			ClusterPair result=clusterQueue.remove();
			while(blackList.get(result.getCluster1()) || blackList.get(result.getCluster2()))
			{
				result=clusterQueue.remove();
			}
			blackList.put(result.getCluster1(), true);
			blackList.put(result.getCluster2(), true);
			clusters.remove(result.getCluster1());
			clusters.remove(result.getCluster2());
			
			Cluster newCluster=result.getCluster1().merge(result.getCluster2());
			for(Cluster current: clusters)
			{
				clusterQueue.add(new ClusterPair(current, newCluster));
			}
			clusters.add(newCluster);
			blackList.put(newCluster, false);
			
			System.out.println(calcSSE(clusters));
			if(clusters.size()<20)
			{
				System.out.println("K="+clusters.size()+"\tDistance between merged points: "+result.getCluster1().getDistance(result.getCluster2()));
			}
		}
		
		for(Cluster currentCluster: clusters)
		{
			for(DataPoint currentPoint: currentCluster.getEntireCluster())
			{
				this.unstandardize(currentPoint);
			}
		}
		
		
		return clusters;
	}
	
	/*
	 * Method to calculate SSE given the array list of clusters
	 */
	private float calcSSE(ArrayList<Cluster> clusters){
		float sse = 0;
		for(Cluster currentCluster: clusters)
		{
			for(DataPoint currentPoint: currentCluster.getEntireCluster())
			{
				sse+=currentCluster.getCenter().getSquaredEuclideanDistance(currentPoint);
			}
		}
		return sse;

	}
	
	/**
	 * Prints the cluster centers in tab seperated format
	 */
	public static void printClusters(ArrayList<Cluster> clusters)
	{
		for(Cluster currentCluster: clusters)
		{
			System.out.print(currentCluster.getCenter()+"\t");
		}
	}

	
	public static void main(String[] args)
	{
		AgglomerativeClusterer clusterer= new AgglomerativeClusterer();
		Scanner scanner=new Scanner(System.in);
		System.out.print("Enter k>");
		ArrayList<Cluster> result=clusterer.cluster(scanner.nextInt());
		printClusters(result);
		
		
		
	}
	

}
