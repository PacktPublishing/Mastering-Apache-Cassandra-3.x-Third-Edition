package PacktCassDev;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Table(keyspace = "packt_ch3", name = "security_logs_by_location",
readConsistency = "LOCAL_ONE",
writeConsistency = "LOCAL_QUORUM",
caseSensitiveKeyspace = false,
caseSensitiveTable = false)
public class SecurityLogsByLocation {
	@PartitionKey(0)
    private String location_id;
	@PartitionKey(1)
    private int day;
	@ClusteringColumn(0)
    private Date time_in;
	@ClusteringColumn(1)
    private String employee_id;
    private String mailstop;
	
	public SecurityLogsByLocation(String locationId, int day, Date timeIn, String employeeId, String mailstop) {
		this.location_id = locationId;
		this.day = day;
		this.time_in = timeIn;
		this.employee_id = employeeId;
		this.mailstop = mailstop;
	}
	
	public SecurityLogsByLocation(String locationId, String employeeId, String mailstop) {
		Date rightNow = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		
		this.location_id = locationId;
		this.day = Integer.parseInt(dateFormat.format(rightNow));
		this.time_in = rightNow;
		this.employee_id = employeeId;
		this.mailstop = mailstop;
	}

	//Need this for the accessor!  Throws NoSuchMethodException SecurityLogsByLocation.<init>
	public SecurityLogsByLocation() {
	}
	
	public String getLocation_id() {
		return location_id;
	}

	public void setLocation_id(String location_id) {
		this.location_id = location_id;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public Date getTime_in() {
		return time_in;
	}

	public void setTime_in(Date time_in) {
		this.time_in = time_in;
	}

	public String getEmployee_id() {
		return employee_id;
	}

	public void setEmployee_id(String employee_id) {
		this.employee_id = employee_id;
	}

	public String getMailstop() {
		return mailstop;
	}

	public void setMailstop(String mailstop) {
		this.mailstop = mailstop;
	}
    
	public String toString() {
		StringBuilder returnValue = new StringBuilder();

		returnValue.append(this.location_id);
		returnValue.append(" - ").append(this.time_in.toString());
		returnValue.append(" - ").append(this.employee_id);
		returnValue.append(" - mstp=").append(this.mailstop);
		return returnValue.toString();
	}
}
