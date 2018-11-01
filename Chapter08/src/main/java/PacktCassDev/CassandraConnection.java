package PacktCassDev;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.QueryOptions;

public class CassandraConnection {
	private Cluster cluster;
	private Session session;
		
	public CassandraConnection(String[] nodes, String user, String pwd, String dc) {
	  connect(nodes,user,pwd,dc);
	}

	public void connect(String[] nodes, String user, String pwd, String dc) {
	  QueryOptions qo = new QueryOptions();
	  qo.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);

	  cluster = Cluster.builder()
	    .addContactPoints(nodes)
	    .withCredentials(user,pwd)
	    .withQueryOptions(qo)
	    .withLoadBalancingPolicy(
	      new TokenAwarePolicy(
	        DCAwareRoundRobinPolicy.builder()
	        .withLocalDc(dc)
	        .build()
	      ))
	      .build();
	    session = cluster.connect();
	}

	public void connectAsync(String[] nodes, String user, String pwd, String dc) {
		  QueryOptions qo = new QueryOptions();
		  qo.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);

		  cluster = Cluster.builder()
		    .addContactPoints(nodes)
		    .withCredentials(user,pwd)
		    .withQueryOptions(qo)
		    .withLoadBalancingPolicy(
		      new TokenAwarePolicy(
		        DCAwareRoundRobinPolicy.builder()
		        .withLocalDc(dc)
		        .build()
		      ))
		      .build();
		    session = cluster.newSession();
		}
	
	public ResultSet query(String strQuery) {
	  return session.execute(strQuery);
	}
	    	    
	public ResultSet query(BoundStatement bStatement) {
	  return session.execute(bStatement);
	}
			
	public void insert(BoundStatement bStatement) {
	  session.execute(bStatement);
	}
			
	public ResultSetFuture asyncOp(BoundStatement bStatement) {
	  return session.executeAsync(bStatement);
	}
	
	public PreparedStatement prepare(String strCQL) {
	  return session.prepare(strCQL);
	}
		
	public Session getSession() {
	  return session;
	}
		
	public void close() {
	  cluster.close();
	}
}
