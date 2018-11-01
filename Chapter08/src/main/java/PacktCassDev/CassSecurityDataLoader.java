package PacktCassDev;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSetFuture;

public class CassSecurityDataLoader {
	//capping the loader to a max number of threads in-flight at any given time
    private final static int maxThreads = 10;
    
	public static void main(String[] args) {
		String[] nodes = {"192.168.0.101"};
		CassandraConnection conn = new CassandraConnection(nodes, "cassdba", "flynnLives", 
		  "ClockworkAngels");
		
		//define thread structures
		List<ResultSetFuture> listenableFutures = new ArrayList<ResultSetFuture>();
		int threadCount = 0;
		
		//define query
		StringBuilder insertQuery = new StringBuilder("INSERT INTO packt_ch3.security_logs_by_location ");
		insertQuery.append("(location_id,day,time_in,employee_id,mailstop) ");
		insertQuery.append("VALUES (?,?,?,?,?)");
		PreparedStatement statement = conn.prepare(insertQuery.toString());
		
		//define static parameters
		String location = "MKE0";
		int day = 20200314;
		
		//write to cassandra
		for (Integer employeeName = 0; employeeName < 1000; employeeName++) {
		  StringBuilder mailstop = new StringBuilder("MK").append(threadCount);
	      BoundStatement boundStatement = statement.bind(location,day,new Date(),employeeName.toString(),mailstop.toString());
	      ResultSetFuture future = conn.asyncOp(boundStatement);
	      listenableFutures.add(future);
	      threadCount++;
	        
	      if (threadCount >= maxThreads) {
	    	System.out.println(employeeName + " sent!  Max threads in-flight, waiting ");
	        listenableFutures.forEach(ResultSetFuture::getUninterruptibly);
	        listenableFutures = new ArrayList<ResultSetFuture>();
	        threadCount = 0;
	      }
		}

		//wait for remaining insert threads to complete
		listenableFutures.forEach(ResultSetFuture::getUninterruptibly);
		
		System.out.println("Loader complete!");
		
		conn.close();
	}
}
