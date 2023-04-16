package pbm.com.exchange.app.rest.respone;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pbm.com.exchange.service.dto.ProductDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterProductRes {
    public List<ProductDTO> products;
    
    public PaginateRes paginate;
}
