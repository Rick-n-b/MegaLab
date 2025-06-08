package ru.nstu.labs.net.ClientTrades;

import ru.nstu.trades.TradeInners;

import java.io.Serializable;

public class TradeIn extends SmolTrade implements Serializable {
    public final int senderId;

    public TradeIn(int id, TradeInners inners, int senderId){
        super(id, inners);
        this.senderId = senderId;
    }
}
