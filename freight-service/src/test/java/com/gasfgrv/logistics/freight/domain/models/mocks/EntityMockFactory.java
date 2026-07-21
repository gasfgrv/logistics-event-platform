package com.gasfgrv.logistics.freight.domain.models.mocks;

import com.gasfgrv.logistics.freight.domain.models.enuns.FreightStatus;
import com.gasfgrv.logistics.freight.domain.models.freight.Freight;
import com.gasfgrv.logistics.freight.domain.models.order.Order;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Select;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityMockFactory {

    private static final String ZIP_CODE_PATTERN = "#d#d#d#d#d-#d#d#d";

    public static Order buildOrder() {
        return Instancio.of(Order.class)
                .set(Select.field(Order::getId), UUID.randomUUID())
                .set(Select.field(Order::getCustomerId), UUID.randomUUID())
                .generate(Select.field(Order::getOrigin), gen -> gen.text().pattern(ZIP_CODE_PATTERN))
                .generate(Select.field(Order::getDestination), gen -> gen.text().pattern(ZIP_CODE_PATTERN))
                .generate(Select.field(Order::getWeight), gen -> gen.floats().range(1.0f, 100.0f))
                .generate(Select.field(Order::getCreatedAt), gen -> gen.temporal().localDateTime().past())
                .create();

    }

    public static Freight buildFreight(Order order) {
        if (order == null) {
            order = Instancio.create(Order.class);
        }

        return Instancio.of(Freight.class)
                .set(Select.field(Freight::getId), UUID.randomUUID())
                .generate(Select.field(Freight::getDistanceKm), gen -> gen.doubles().range(1.0, 100.0))
                .generate(Select.field(Freight::getPrice), gen -> gen.math().bigDecimal().range(BigDecimal.ONE, BigDecimal.valueOf(1000L)).scale(2))
                .set(Select.field(Freight::getStatus), FreightStatus.CALCULATED)
                .generate(Select.field(Freight::getCreatedAt), gen -> gen.temporal().localDateTime().past())
                .set(Select.field(Freight::getOrder), order)
                .create();
    }

}
