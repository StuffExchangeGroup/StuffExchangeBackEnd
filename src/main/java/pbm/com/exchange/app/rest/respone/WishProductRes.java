package pbm.com.exchange.app.rest.respone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WishProductRes {

    private Long id;

    private String thumbnail;

    private String title;

    private String displayName;

    private String avatar;

    private Boolean isFavorite;
}
