package pbm.com.exchange.app.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pbm.com.exchange.domain.enumeration.Condition;
import pbm.com.exchange.domain.enumeration.ProductType;
import pbm.com.exchange.domain.enumeration.PurposeType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriteriaDTO {

    private Long categoryId;

    private String search;

    private ProductType type;

    private Condition condition;

    private String location;
    
    private PurposeType purposeType;
}
