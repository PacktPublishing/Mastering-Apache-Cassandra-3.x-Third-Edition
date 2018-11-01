package PacktCassDev;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;  
import com.datastax.driver.mapping.annotations.Param;  
import com.datastax.driver.mapping.annotations.Query;

import java.util.Date;

import com.datastax.driver.core.Session;

@Accessor
interface SecurityLogsAccessor {
	@Query("SELECT * FROM packt_ch3.security_logs_by_location WHERE location_id=:location_id AND day=:day")
    Result<SecurityLogsByLocation> getAllByLocationDay(@Param("location_id") String locationId, 
    		@Param("day") int day);
}

public class SecurityLogService {
	private Mapper<SecurityLogsByLocation> mapper;
	private SecurityLogsAccessor accessor;
	
	public SecurityLogService(Session session) {
		MappingManager manager = new MappingManager(session);
		mapper = manager.mapper(SecurityLogsByLocation.class);
		accessor = manager.createAccessor(SecurityLogsAccessor.class);
	}
	
	public SecurityLogsByLocation getSecurityLogEntry(String locationId, int day, Date timeIn, String employeeId) {
		return mapper.get(locationId,day,timeIn,employeeId);
	}
	
	public Result<SecurityLogsByLocation> getAllForDay(String locationId, int day) {
		return accessor.getAllByLocationDay(locationId,day);
	}
	
	public void saveSecurityLogEntry(SecurityLogsByLocation entry) {
		mapper.save(entry);
	}
}
