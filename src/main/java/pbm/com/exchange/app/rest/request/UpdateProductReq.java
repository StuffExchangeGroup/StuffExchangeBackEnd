package pbm.com.exchange.app.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pbm.com.exchange.domain.enumeration.Condition;
import pbm.com.exchange.domain.enumeration.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProductReq {

    private Long id;

    private Long categoryId;

    private String title;

    private String description;

    private Status status;

    @JsonProperty("condition")
    private Condition condition;

    private Long cityId;

    private String location;

    private String notice;

    private String[] imageIds;

    private List<Long> purposeIds = new ArrayList<>();
}
