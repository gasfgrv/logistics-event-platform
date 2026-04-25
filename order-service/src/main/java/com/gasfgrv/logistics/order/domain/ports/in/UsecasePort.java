package com.gasfgrv.logistics.order.domain.ports.in;

public interface UsecasePort<C extends UsecaseCommand> {
    void execute(C command);
}
