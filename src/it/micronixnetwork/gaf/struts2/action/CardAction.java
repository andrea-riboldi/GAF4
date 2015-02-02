/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.micronixnetwork.gaf.struts2.action;

import it.micronixnetwork.gaf.domain.CardConf;
import it.micronixnetwork.gaf.exception.ActionException;
import it.micronixnetwork.gaf.exception.ApplicationException;
import it.micronixnetwork.gaf.exception.ServiceException;
import it.micronixnetwork.gaf.struts2.gui.model.CardModel;
import it.micronixnetwork.gaf.struts2.gui.model.CardModelsCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;

/**
 * 
 * @author kobo
 */
public class CardAction extends WebAppAction implements AjaxAction {

    private static final long serialVersionUID = 1L;

    private String cardId;

    public String getCardId() {
	return cardId;
    }

    public void setCardId(String cardId) {
	this.cardId = cardId;
    }

    /**
     * Recupera il parametro di una CARD dalla mappa
     * 
     * @param paramName
     *            il nome del parametro
     * @param evaluate
     *            se true cerca vi trovare il valore del parametro dal
     *            ValueStack
     * @return il valore del parametro
     */
    public String getCardParam(String paramName, boolean evaluate) {
	ActionContext context = ActionContext.getContext();
	if (context == null)
	    return null;
	CardModelsCache cardMap = getCardModels();
	if (cardMap != null) {
	    CardModel wmodel = cardMap.get(getCardId());
	    if (wmodel == null)
		return null;
	    Map<String, Object> params = wmodel.getParams();
	    if (params == null)
		return null;
	    Object value = params.get(paramName);
	    if (value != null) {
		if (evaluate) {
		    Object evaluated = context.getValueStack().findValue(value.toString());
		    if (evaluated != null) {
			return evaluated.toString();
		    }
		} else {
		    return value.toString();
		}
	    }
	}
	return null;
    }

    public String getCardParam(String paramName) {
	return getCardParam(paramName, false);
    }

    /**
     * Restituisce il modello di una CARD
     * 
     * @return
     */
    public CardModel getCardModel() {
	CardModelsCache cardMap = getCardModels();
	if (cardMap != null && getCardId() != null) {
	    return cardMap.get(getCardId());
	}
	return null;
    }


    @Override
    public void validate() {
	validInput();
    }

    protected void validInput() {
    }

    @Override
    protected String exe() throws ApplicationException {
	return SUCCESS;
    }

    /**
     * Ritorna la lista delle CARD (dominio,nome) dello stesso tipo della CARD
     * corrente
     * 
     * @return
     */
    public List<CardConf> getCardsWithSameType() throws ActionException {
	CardModel cardModel = getCardModel();
	if (cardModel == null)
	    return null;
	String sql = "Select c.id.domain,c.id.cardname From CardConf c where c.type=:type GROUP BY c.id.domain,c.id.cardname ";
	HashMap<String, Object> values = new HashMap<String, Object>();
	if (cardModel.getType() != null) {
	    values.put("type", cardModel.getType());
	    try {
		return queryService.search(sql, values, false);
	    } catch (Exception e) {
		error("Query error: ",e);
		throw new ActionException(e);
	    }
	}
	return new ArrayList<CardConf>(0);
    }

}
