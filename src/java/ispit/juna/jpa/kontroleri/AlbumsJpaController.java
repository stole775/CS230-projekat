/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ispit.juna.jpa.kontroleri;

import ispit.juna.jpa.entiteti.Albums;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import ispit.juna.jpa.entiteti.Artists;
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
public class AlbumsJpaController implements Serializable {

    public AlbumsJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Albums albums) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (albums.getSongsList() == null) {
            albums.setSongsList(new ArrayList<Songs>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Artists artistID = albums.getArtistID();
            if (artistID != null) {
                artistID = em.getReference(artistID.getClass(), artistID.getArtistID());
                albums.setArtistID(artistID);
            }
            List<Songs> attachedSongsList = new ArrayList<Songs>();
            for (Songs songsListSongsToAttach : albums.getSongsList()) {
                songsListSongsToAttach = em.getReference(songsListSongsToAttach.getClass(), songsListSongsToAttach.getSongID());
                attachedSongsList.add(songsListSongsToAttach);
            }
            albums.setSongsList(attachedSongsList);
            em.persist(albums);
            if (artistID != null) {
                artistID.getAlbumsList().add(albums);
                artistID = em.merge(artistID);
            }
            for (Songs songsListSongs : albums.getSongsList()) {
                Albums oldAlbumIDOfSongsListSongs = songsListSongs.getAlbumID();
                songsListSongs.setAlbumID(albums);
                songsListSongs = em.merge(songsListSongs);
                if (oldAlbumIDOfSongsListSongs != null) {
                    oldAlbumIDOfSongsListSongs.getSongsList().remove(songsListSongs);
                    oldAlbumIDOfSongsListSongs = em.merge(oldAlbumIDOfSongsListSongs);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findAlbums(albums.getAlbumID()) != null) {
                throw new PreexistingEntityException("Albums " + albums + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Albums albums) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Albums persistentAlbums = em.find(Albums.class, albums.getAlbumID());
            Artists artistIDOld = persistentAlbums.getArtistID();
            Artists artistIDNew = albums.getArtistID();
            List<Songs> songsListOld = persistentAlbums.getSongsList();
            List<Songs> songsListNew = albums.getSongsList();
            if (artistIDNew != null) {
                artistIDNew = em.getReference(artistIDNew.getClass(), artistIDNew.getArtistID());
                albums.setArtistID(artistIDNew);
            }
            List<Songs> attachedSongsListNew = new ArrayList<Songs>();
            for (Songs songsListNewSongsToAttach : songsListNew) {
                songsListNewSongsToAttach = em.getReference(songsListNewSongsToAttach.getClass(), songsListNewSongsToAttach.getSongID());
                attachedSongsListNew.add(songsListNewSongsToAttach);
            }
            songsListNew = attachedSongsListNew;
            albums.setSongsList(songsListNew);
            albums = em.merge(albums);
            if (artistIDOld != null && !artistIDOld.equals(artistIDNew)) {
                artistIDOld.getAlbumsList().remove(albums);
                artistIDOld = em.merge(artistIDOld);
            }
            if (artistIDNew != null && !artistIDNew.equals(artistIDOld)) {
                artistIDNew.getAlbumsList().add(albums);
                artistIDNew = em.merge(artistIDNew);
            }
            for (Songs songsListOldSongs : songsListOld) {
                if (!songsListNew.contains(songsListOldSongs)) {
                    songsListOldSongs.setAlbumID(null);
                    songsListOldSongs = em.merge(songsListOldSongs);
                }
            }
            for (Songs songsListNewSongs : songsListNew) {
                if (!songsListOld.contains(songsListNewSongs)) {
                    Albums oldAlbumIDOfSongsListNewSongs = songsListNewSongs.getAlbumID();
                    songsListNewSongs.setAlbumID(albums);
                    songsListNewSongs = em.merge(songsListNewSongs);
                    if (oldAlbumIDOfSongsListNewSongs != null && !oldAlbumIDOfSongsListNewSongs.equals(albums)) {
                        oldAlbumIDOfSongsListNewSongs.getSongsList().remove(songsListNewSongs);
                        oldAlbumIDOfSongsListNewSongs = em.merge(oldAlbumIDOfSongsListNewSongs);
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
                Integer id = albums.getAlbumID();
                if (findAlbums(id) == null) {
                    throw new NonexistentEntityException("The albums with id " + id + " no longer exists.");
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
            Albums albums;
            try {
                albums = em.getReference(Albums.class, id);
                albums.getAlbumID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The albums with id " + id + " no longer exists.", enfe);
            }
            Artists artistID = albums.getArtistID();
            if (artistID != null) {
                artistID.getAlbumsList().remove(albums);
                artistID = em.merge(artistID);
            }
            List<Songs> songsList = albums.getSongsList();
            for (Songs songsListSongs : songsList) {
                songsListSongs.setAlbumID(null);
                songsListSongs = em.merge(songsListSongs);
            }
            em.remove(albums);
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

    public List<Albums> findAlbumsEntities() {
        return findAlbumsEntities(true, -1, -1);
    }

    public List<Albums> findAlbumsEntities(int maxResults, int firstResult) {
        return findAlbumsEntities(false, maxResults, firstResult);
    }

    private List<Albums> findAlbumsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Albums.class));
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

    public Albums findAlbums(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Albums.class, id);
        } finally {
            em.close();
        }
    }

    public int getAlbumsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Albums> rt = cq.from(Albums.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
