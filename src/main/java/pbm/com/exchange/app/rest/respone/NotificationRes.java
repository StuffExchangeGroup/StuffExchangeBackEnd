package pbm.com.exchange.app.rest.respone;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import pbm.com.exchange.app.rest.vm.NotificationDTO;

@Data
@AllArgsConstructor
public class NotificationRes {
    List<NotificationDTO> list;
    
    PaginateRes pagination;
}
