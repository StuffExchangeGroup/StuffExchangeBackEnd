package pbm.com.exchange.app.rest.request;

import javax.validation.constraints.NotNull;
import lombok.Data;
import pbm.com.exchange.domain.enumeration.ExchangeAction;

/**
 * ****************************************************
 * * Description :
 * * File        : ConfirmSwapReq.java
 * * Author      : PHuc
 * * Date        : April 4, 2022
 * ****************************************************
 **/
@Data
public class ConfirmSwapReq {

    @NotNull
    private Long exchangeId;

    @NotNull
    //    @EnumValidator(enumClass = ExchangeAction.class)
    private String action;
}
