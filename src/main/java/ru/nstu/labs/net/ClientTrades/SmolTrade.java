package ru.nstu.labs.net.ClientTrades;

import ru.nstu.trades.TradeInners;
import ru.nstu.trades.Transaction;

public class SmolTrade {
    public final int id;
    private TradeInners inners;

    public SmolTrade(int id, TradeInners inners){
        this.id = id;
        this.inners = inners;
    }

    public TradeInners getInners() {
        return inners;
    }

}
