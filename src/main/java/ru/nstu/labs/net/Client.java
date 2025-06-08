package ru.nstu.labs.net;

import ru.nstu.events.Event;
import ru.nstu.events.ParamEvent;
import ru.nstu.labs.net.ClientTrades.SmolTrade;
import ru.nstu.labs.net.ClientTrades.TradeIn;
import ru.nstu.labs.net.ClientTrades.TradeOut;
import ru.nstu.messages.*;
import ru.nstu.network.*;
import ru.nstu.requests.*;
import ru.nstu.responses.ConnectResponse;
import ru.nstu.responses.TradeOfferResponse;
import ru.nstu.trades.TradeInners;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    public enum Status {
        READY,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
        CLOSING,
        CLOSED
    }

    private NetClient networkClient;
    private int id = -1;
    private String name = "Unreg";
    private final Map<Integer, NetClient> neighbours = new HashMap<>();
    private final Map<Integer, SmolTrade> trades = new HashMap<>();

    public final Event connectionClosed = new Event();
    public final Event connected = new Event();
    public final Event tradesUpdated = new Event();
    public final ParamEvent<SmolTrade> tradeAccepted = new ParamEvent<>();

    private Status status = Status.READY;

    public void connect(InetAddress address, int port, String name) throws IOException {
        if(status != Status.READY)
            return;
        status = Status.CONNECTING;
        var socket = new Socket();
        socket.connect(new InetSocketAddress(address, port));
        networkClient = new NetClient(socket);
        networkClient.receivedData.subscribe(this::processMessage);
        networkClient.closeEvent.subscribe(this::onConnectionClosed);
        networkClient.startListen();
        networkClient.send(new ConnectRequest(name));
    }

    public void disconnect() {
        if(status != Status.CONNECTED)
            return;
        status = Status.DISCONNECTING;
        name = "";
        id = -1;
        neighbours.clear();
        trades.clear();
        tradesUpdated.invoke(this);
        networkClient.send(new DisconnectRequest(id));
        networkClient.close();
        networkClient = null;
        status = Status.READY;
    }

    public Status getStatus() {
        return status;
    }

    public List<SmolTrade> getTrades(){
        return trades.values().stream().toList();
    }

    public List<NetClient> getNeighbours() {
        return neighbours.values().stream().toList();
    }

    public NetClient findNeighbour(int id) {
        return neighbours.getOrDefault(id, null);
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public boolean isRegistered() {
        return id != -1;
    }

    private void onConnectionClosed(Object sender) {
        connectionClosed.invoke(this);
    }

    private void processMessage(Object sender, Serializable data) {
        if(data instanceof ConnectResponse response) {
            onConnected(response);
            return;
        }
        if(data instanceof ClientConnectMessage message) {
            onClientConnected(message);
            return;
        }
        if(data instanceof ClientDisconnectedMessage message) {
            onClientDisconnected(message);
            return;
        }
        if(data instanceof TradeOfferResponse response) {
            onTradeSent(response);
            return;
        }
        if(data instanceof OfferTradeMessage message) {
            onTradeOffered(message);
            return;
        }
        if(data instanceof AcceptTradeMessage message) {
            onTradeAccepted(message);
            return;
        }
        if(data instanceof RejectTradeMessage message){
            onTradeRejected(message);
            return;
        }
        if(data instanceof CancellTradeMessage message) {
            onTradeCanceled(message);
            return;
        }
    }

    public void offerTrade(int target, TradeInners inners) {
        if(status != Status.CONNECTED)
            return;
        var request = new OfferTradeRequest(target, inners);
        networkClient.send(request);
    }

    public void acceptTrade(TradeIn trade) {
        if(status != Status.CONNECTED)
            return;
        var request = new AcceptTradeRequest(trade.id, trade.getInners().request().objectCount());
        networkClient.send(request);
        synchronized (trades) {
            trades.remove(trade.id);
            tradesUpdated.invoke(this);
        }
    }

    public void rejectTrade(SmolTrade trade) {
        if(status != Status.CONNECTED)
            return;
        var request = new RejectTradeRequest(trade.id);
        networkClient.send(request);
        synchronized (trades) {
            trades.remove(trade.id);
            tradesUpdated.invoke(this);
        }
    }

    public void cancelTrade(TradeOut trade) {
        if(status != Status.CONNECTED)
            return;
        var request = new CancelTradeRequest(trade.id);
        networkClient.send(request);
        synchronized (trades) {
            trades.remove(trade.id);
            tradesUpdated.invoke(this);
        }
    }

    public void close() {
        if(status == Status.CONNECTED)
            disconnect();
        status = Status.CLOSING;
        connected.clearListeners();
        connectionClosed.clearListeners();
        tradesUpdated.clearListeners();
        tradeAccepted.clearListeners();
        status = Status.CLOSED;
    }

    private void onConnected(ConnectResponse response) {
        if(isRegistered())
            return;
        id = response.id();
        name = response.name();
        synchronized (neighbours) {
            for (var client : response.clients())
                neighbours.put(id, client);
        }
        status = Status.CONNECTED;
        connected.invoke(this);
    }

    private void onClientConnected(ClientConnectMessage message) {
        if(message.clientId() == id && message.name().equals(name))
            return;
        var client = new NetClient(message.clientId(), message.name());
        synchronized (neighbours) {
            neighbours.put(id, client);
        }
    }

    private void onClientDisconnected(ClientDisconnectedMessage message) {
        synchronized (neighbours) {
            neighbours.remove(message.clientId());
        }
    }

    private void onTradeSent(TradeOfferResponse response) {
        var trade = new TradeOut(response.id(), response.inners(),  response.targetId());
        synchronized (trades) {
            trades.put(response.id(), trade);
        }
        tradesUpdated.invoke(this);
    }

    private void onTradeOffered(OfferTradeMessage message) {
        var trade = new TradeIn(message.id(),message.inners(), message.senderId());
        synchronized (trades) {
            trades.put(message.id(), trade);
        }
        tradesUpdated.invoke(this);
    }

    private void onTradeAccepted(AcceptTradeMessage message) {
        var trade = trades.get(message.id());
        tradeAccepted.invoke(this, trade);
        synchronized (trades) {
            trades.remove(message.id());
        }
        tradesUpdated.invoke(this);
    }

    private void onTradeRejected(RejectTradeMessage message) {
        synchronized (trades) {
            trades.remove(message.id());
        }
        tradesUpdated.invoke(this);
    }

    private void onTradeCanceled(CancellTradeMessage message) {
        synchronized (trades) {
            trades.remove(message.id());
        }
        tradesUpdated.invoke(this);
    }
}
