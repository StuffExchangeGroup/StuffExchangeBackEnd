package pbm.com.exchange.app.rest.respone;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaginateRes {
    public int itemsPerPage;
    
    public int currentPage;
    
    public int totalItems;
}
