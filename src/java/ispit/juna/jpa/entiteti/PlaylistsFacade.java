/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ispit.juna.jpa.entiteti;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Mladen
 */
@Stateless
public class PlaylistsFacade extends AbstractFacade<Playlists> {

    @PersistenceContext(unitName = "IspitJunA-Mladen-Stolic-5319PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PlaylistsFacade() {
        super(Playlists.class);
    }
    
}
