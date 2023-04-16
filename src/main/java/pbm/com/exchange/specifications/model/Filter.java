package pbm.com.exchange.specifications.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pbm.com.exchange.domain.enumeration.QueryOperator;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Filter {

    private String field;

    private QueryOperator operator;

    private String value;

    private List<String> values;
}
