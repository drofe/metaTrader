package org.bergefall.iobase.web;

public interface WebReqHandler {

    Object handle(MetaTraderWebRequest request, MetaTraderWebResponse response) throws Exception;

}