package ru.nstu.labs.net.ClientTrades;

import ru.nstu.trades.TradeInners;

public class TradeOut extends SmolTrade{
    public final int targetId;

    public TradeOut(int id, TradeInners inners, int targetId){
        super(id, inners);
        this.targetId = targetId;
    }
}
