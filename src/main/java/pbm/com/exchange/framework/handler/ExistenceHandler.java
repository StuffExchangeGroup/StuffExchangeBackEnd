package pbm.com.exchange.framework.handler;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pbm.com.exchange.domain.Product;
import pbm.com.exchange.exception.BadRequestException;
import pbm.com.exchange.message.Message;
import pbm.com.exchange.message.MessageHelper;
import pbm.com.exchange.repository.ProductRepository;

@Component
public class ExistenceHandler {

    @Autowired
    private ProductRepository productRepository;

    public Product getProductFromId(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0071, productId), new Throwable());
        }
        return productOptional.get();
    }
}
