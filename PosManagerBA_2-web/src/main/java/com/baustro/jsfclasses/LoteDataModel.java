/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baustro.jsfclasses;

import com.baustro.model.Lote;
import com.baustro.sessionbean.LoteFacade;
import com.baustro.thread.LoteCallableList;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

public class LoteDataModel extends LazyDataModel<Lote> {

    @Inject
    private LoteFacade ejbFacade;

    public LoteDataModel() {
        this.setRowCount(ejbFacade.getLoteTotalCount());
    }

    LoteDataModel(LoteFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
        this.setRowCount(ejbFacade.getLoteTotalCount());
    }

    @Override
    public List<Lote> load(int first, int pageSize, String sortField,
            SortOrder sortOrder, Map<String, Object> filters) {

        try {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            Future<List<Lote>> resultado = executor.submit(new LoteCallableList(ejbFacade,
                    first, pageSize, sortField,
                    sortOrder, filters));
            List<Lote> listResp = (List<Lote>) resultado.get();
            executor.shutdown();

//        List<Lote> list = ejbFacade.getLoteList(first, pageSize, sortField, sortOrder);
            List<Lote> list = listResp;
            return list;
        } catch (InterruptedException ex) {
            Logger.getLogger(LoteDataModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(LoteDataModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
