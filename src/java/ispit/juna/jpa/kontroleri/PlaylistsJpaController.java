/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ispit.juna.jpa.kontroleri;

import ispit.juna.jpa.entiteti.Playlists;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import ispit.juna.jpa.entiteti.Songs;
import ispit.juna.jpa.kontroleri.exceptions.NonexistentEntityException;
import ispit.juna.jpa.kontroleri.exceptions.PreexistingEntityException;
import ispit.juna.jpa.kontroleri.exceptions.RollbackFailureException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Mladen
 */
public class PlaylistsJpaController implements Serializable {

    public PlaylistsJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Playlists playlists) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (playlists.getSongsList() == null) {
            playlists.setSongsList(new ArrayList<Songs>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Songs> attachedSongsList = new ArrayList<Songs>();
            for (Songs songsListSongsToAttach : playlists.getSongsList()) {
                songsListSongsToAttach = em.getReference(songsListSongsToAttach.getClass(), songsListSongsToAttach.getSongID());
                attachedSongsList.add(songsListSongsToAttach);
            }
            playlists.setSongsList(attachedSongsList);
            em.persist(playlists);
            for (Songs songsListSongs : playlists.getSongsList()) {
                songsListSongs.getPlaylistsList().add(playlists);
                songsListSongs = em.merge(songsListSongs);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPlaylists(playlists.getPlaylistID()) != null) {
                throw new PreexistingEntityException("Playlists " + playlists + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Playlists playlists) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Playlists persistentPlaylists = em.find(Playlists.class, playlists.getPlaylistID());
            List<Songs> songsListOld = persistentPlaylists.getSongsList();
            List<Songs> songsListNew = playlists.getSongsList();
            List<Songs> attachedSongsListNew = new ArrayList<Songs>();
            for (Songs songsListNewSongsToAttach : songsListNew) {
                songsListNewSongsToAttach = em.getReference(songsListNewSongsToAttach.getClass(), songsListNewSongsToAttach.getSongID());
                attachedSongsListNew.add(songsListNewSongsToAttach);
            }
            songsListNew = attachedSongsListNew;
            playlists.setSongsList(songsListNew);
            playlists = em.merge(playlists);
            for (Songs songsListOldSongs : songsListOld) {
                if (!songsListNew.contains(songsListOldSongs)) {
                    songsListOldSongs.getPlaylistsList().remove(playlists);
                    songsListOldSongs = em.merge(songsListOldSongs);
                }
            }
            for (Songs songsListNewSongs : songsListNew) {
                if (!songsListOld.contains(songsListNewSongs)) {
                    songsListNewSongs.getPlaylistsList().add(playlists);
                    songsListNewSongs = em.merge(songsListNewSongs);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = playlists.getPlaylistID();
                if (findPlaylists(id) == null) {
                    throw new NonexistentEntityException("The playlists with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Playlists playlists;
            try {
                playlists = em.getReference(Playlists.class, id);
                playlists.getPlaylistID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The playlists with id " + id + " no longer exists.", enfe);
            }
            List<Songs> songsList = playlists.getSongsList();
            for (Songs songsListSongs : songsList) {
                songsListSongs.getPlaylistsList().remove(playlists);
                songsListSongs = em.merge(songsListSongs);
            }
            em.remove(playlists);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Playlists> findPlaylistsEntities() {
        return findPlaylistsEntities(true, -1, -1);
    }

    public List<Playlists> findPlaylistsEntities(int maxResults, int firstResult) {
        return findPlaylistsEntities(false, maxResults, firstResult);
    }

    private List<Playlists> findPlaylistsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Playlists.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Playlists findPlaylists(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Playlists.class, id);
        } finally {
            em.close();
        }
    }

    public int getPlaylistsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Playlists> rt = cq.from(Playlists.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
