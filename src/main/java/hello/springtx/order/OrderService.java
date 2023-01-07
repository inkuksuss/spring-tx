package hello.springtx.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public void order(Order order) throws NotEnoughMoneyException {
        log.info("order");
        orderRepository.save(order);

        log.info("pay");
        if (order.getUsername().equals("예외")) {
            log.info("system ex");
            throw new RuntimeException("시스템 예외");
        } else if (order.getUsername().equals("잔고부족")) {
            log.info("biz ex");
            order.setPayStatus("대기");
            throw new NotEnoughMoneyException("잔고 부족");
        } else {
            log.info("pass");
            order.setPayStatus("완료");
        }
        log.info("end");
    }
}
