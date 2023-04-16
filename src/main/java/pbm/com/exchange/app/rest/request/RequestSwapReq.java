package pbm.com.exchange.app.rest.request;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * ****************************************************
 * * Description :
 * * File        : RequestSwapReq.java
 * * Author      : PHuc
 * * Date        : April 04, 2022
 * ****************************************************
 **/
@Data
public class RequestSwapReq {

    @NotNull
    private Long sendProductId;

    @NotNull
    private Long receiveProductId;
}
