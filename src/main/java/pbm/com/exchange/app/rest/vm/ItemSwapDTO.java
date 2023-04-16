package pbm.com.exchange.app.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pbm.com.exchange.domain.enumeration.ConfirmStatus;
import pbm.com.exchange.domain.enumeration.ExchangeStatus;
import pbm.com.exchange.domain.enumeration.ExchangeType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemSwapDTO {

    private Long exchangeId;

    private ConfirmStatus ownerConfirm;

    private ConfirmStatus exchangerConfirm;

    private ExchangeStatus exchangeStatus;

    private boolean chatting;

    private ExchangeType swapType;

    private ProductSwapDTO myProduct;

    private ProductSwapDTO exchangeProduct;
}
