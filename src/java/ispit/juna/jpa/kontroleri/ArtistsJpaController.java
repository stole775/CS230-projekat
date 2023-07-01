/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ispit.juna.jpa.kontroleri;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import ispit.juna.jpa.entiteti.Albums;
import ispit.juna.jpa.entiteti.Artists;
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
public class ArtistsJpaController implements Serializable {

    public ArtistsJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Artists artists) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (artists.getAlbumsList() == null) {
            artists.setAlbumsList(new ArrayList<Albums>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Albums> attachedAlbumsList = new ArrayList<Albums>();
            for (Albums albumsListAlbumsToAttach : artists.getAlbumsList()) {
                albumsListAlbumsToAttach = em.getReference(albumsListAlbumsToAttach.getClass(), albumsListAlbumsToAttach.getAlbumID());
                attachedAlbumsList.add(albumsListAlbumsToAttach);
            }
            artists.setAlbumsList(attachedAlbumsList);
            em.persist(artists);
            for (Albums albumsListAlbums : artists.getAlbumsList()) {
                Artists oldArtistIDOfAlbumsListAlbums = albumsListAlbums.getArtistID();
                albumsListAlbums.setArtistID(artists);
                albumsListAlbums = em.merge(albumsListAlbums);
                if (oldArtistIDOfAlbumsListAlbums != null) {
                    oldArtistIDOfAlbumsListAlbums.getAlbumsList().remove(albumsListAlbums);
                    oldArtistIDOfAlbumsListAlbums = em.merge(oldArtistIDOfAlbumsListAlbums);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findArtists(artists.getArtistID()) != null) {
                throw new PreexistingEntityException("Artists " + artists + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Artists artists) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Artists persistentArtists = em.find(Artists.class, artists.getArtistID());
            List<Albums> albumsListOld = persistentArtists.getAlbumsList();
            List<Albums> albumsListNew = artists.getAlbumsList();
            List<Albums> attachedAlbumsListNew = new ArrayList<Albums>();
            for (Albums albumsListNewAlbumsToAttach : albumsListNew) {
                albumsListNewAlbumsToAttach = em.getReference(albumsListNewAlbumsToAttach.getClass(), albumsListNewAlbumsToAttach.getAlbumID());
                attachedAlbumsListNew.add(albumsListNewAlbumsToAttach);
            }
            albumsListNew = attachedAlbumsListNew;
            artists.setAlbumsList(albumsListNew);
            artists = em.merge(artists);
            for (Albums albumsListOldAlbums : albumsListOld) {
                if (!albumsListNew.contains(albumsListOldAlbums)) {
                    albumsListOldAlbums.setArtistID(null);
                    albumsListOldAlbums = em.merge(albumsListOldAlbums);
                }
            }
            for (Albums albumsListNewAlbums : albumsListNew) {
                if (!albumsListOld.contains(albumsListNewAlbums)) {
                    Artists oldArtistIDOfAlbumsListNewAlbums = albumsListNewAlbums.getArtistID();
                    albumsListNewAlbums.setArtistID(artists);
                    albumsListNewAlbums = em.merge(albumsListNewAlbums);
                    if (oldArtistIDOfAlbumsListNewAlbums != null && !oldArtistIDOfAlbumsListNewAlbums.equals(artists)) {
                        oldArtistIDOfAlbumsListNewAlbums.getAlbumsList().remove(albumsListNewAlbums);
                        oldArtistIDOfAlbumsListNewAlbums = em.merge(oldArtistIDOfAlbumsListNewAlbums);
                    }
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
                Integer id = artists.getArtistID();
                if (findArtists(id) == null) {
                    throw new NonexistentEntityException("The artists with id " + id + " no longer exists.");
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
            Artists artists;
            try {
                artists = em.getReference(Artists.class, id);
                artists.getArtistID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The artists with id " + id + " no longer exists.", enfe);
            }
            List<Albums> albumsList = artists.getAlbumsList();
            for (Albums albumsListAlbums : albumsList) {
                albumsListAlbums.setArtistID(null);
                albumsListAlbums = em.merge(albumsListAlbums);
            }
            em.remove(artists);
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

    public List<Artists> findArtistsEntities() {
        return findArtistsEntities(true, -1, -1);
    }

    public List<Artists> findArtistsEntities(int maxResults, int firstResult) {
        return findArtistsEntities(false, maxResults, firstResult);
    }

    private List<Artists> findArtistsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Artists.class));
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

    public Artists findArtists(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Artists.class, id);
        } finally {
            em.close();
        }
    }

    public int getArtistsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Artists> rt = cq.from(Artists.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
