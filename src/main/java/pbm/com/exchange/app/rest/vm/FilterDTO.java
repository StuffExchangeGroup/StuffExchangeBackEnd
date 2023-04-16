package pbm.com.exchange.app.rest.vm;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pbm.com.exchange.domain.enumeration.Condition;
import pbm.com.exchange.domain.enumeration.ProductType;
import pbm.com.exchange.domain.enumeration.PurposeType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDTO {

    private String search;

    private List<Long> categoryIds;

    private List<Condition> productConditions;

    private List<Long> cityIds;

    private List<PurposeType> purposeTypes;
    
    private ProductType type;
}
