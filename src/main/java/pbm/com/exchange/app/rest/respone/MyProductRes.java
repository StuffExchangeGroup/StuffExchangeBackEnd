package pbm.com.exchange.app.rest.respone;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyProductRes {

    private Long id;
    private String title;
    private String thumbnail;
    private String description;
    private Boolean isSwapAvailable;
    private ZonedDateTime createdDate;
}
