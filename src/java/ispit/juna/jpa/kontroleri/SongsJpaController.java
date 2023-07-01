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
import ispit.juna.jpa.entiteti.Playlists;
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
public class SongsJpaController implements Serializable {

    public SongsJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Songs songs) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (songs.getPlaylistsList() == null) {
            songs.setPlaylistsList(new ArrayList<Playlists>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Albums albumID = songs.getAlbumID();
            if (albumID != null) {
                albumID = em.getReference(albumID.getClass(), albumID.getAlbumID());
                songs.setAlbumID(albumID);
            }
            List<Playlists> attachedPlaylistsList = new ArrayList<Playlists>();
            for (Playlists playlistsListPlaylistsToAttach : songs.getPlaylistsList()) {
                playlistsListPlaylistsToAttach = em.getReference(playlistsListPlaylistsToAttach.getClass(), playlistsListPlaylistsToAttach.getPlaylistID());
                attachedPlaylistsList.add(playlistsListPlaylistsToAttach);
            }
            songs.setPlaylistsList(attachedPlaylistsList);
            em.persist(songs);
            if (albumID != null) {
                albumID.getSongsList().add(songs);
                albumID = em.merge(albumID);
            }
            for (Playlists playlistsListPlaylists : songs.getPlaylistsList()) {
                playlistsListPlaylists.getSongsList().add(songs);
                playlistsListPlaylists = em.merge(playlistsListPlaylists);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findSongs(songs.getSongID()) != null) {
                throw new PreexistingEntityException("Songs " + songs + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Songs songs) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Songs persistentSongs = em.find(Songs.class, songs.getSongID());
            Albums albumIDOld = persistentSongs.getAlbumID();
            Albums albumIDNew = songs.getAlbumID();
            List<Playlists> playlistsListOld = persistentSongs.getPlaylistsList();
            List<Playlists> playlistsListNew = songs.getPlaylistsList();
            if (albumIDNew != null) {
                albumIDNew = em.getReference(albumIDNew.getClass(), albumIDNew.getAlbumID());
                songs.setAlbumID(albumIDNew);
            }
            List<Playlists> attachedPlaylistsListNew = new ArrayList<Playlists>();
            for (Playlists playlistsListNewPlaylistsToAttach : playlistsListNew) {
                playlistsListNewPlaylistsToAttach = em.getReference(playlistsListNewPlaylistsToAttach.getClass(), playlistsListNewPlaylistsToAttach.getPlaylistID());
                attachedPlaylistsListNew.add(playlistsListNewPlaylistsToAttach);
            }
            playlistsListNew = attachedPlaylistsListNew;
            songs.setPlaylistsList(playlistsListNew);
            songs = em.merge(songs);
            if (albumIDOld != null && !albumIDOld.equals(albumIDNew)) {
                albumIDOld.getSongsList().remove(songs);
                albumIDOld = em.merge(albumIDOld);
            }
            if (albumIDNew != null && !albumIDNew.equals(albumIDOld)) {
                albumIDNew.getSongsList().add(songs);
                albumIDNew = em.merge(albumIDNew);
            }
            for (Playlists playlistsListOldPlaylists : playlistsListOld) {
                if (!playlistsListNew.contains(playlistsListOldPlaylists)) {
                    playlistsListOldPlaylists.getSongsList().remove(songs);
                    playlistsListOldPlaylists = em.merge(playlistsListOldPlaylists);
                }
            }
            for (Playlists playlistsListNewPlaylists : playlistsListNew) {
                if (!playlistsListOld.contains(playlistsListNewPlaylists)) {
                    playlistsListNewPlaylists.getSongsList().add(songs);
                    playlistsListNewPlaylists = em.merge(playlistsListNewPlaylists);
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
                Integer id = songs.getSongID();
                if (findSongs(id) == null) {
                    throw new NonexistentEntityException("The songs with id " + id + " no longer exists.");
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
            Songs songs;
            try {
                songs = em.getReference(Songs.class, id);
                songs.getSongID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The songs with id " + id + " no longer exists.", enfe);
            }
            Albums albumID = songs.getAlbumID();
            if (albumID != null) {
                albumID.getSongsList().remove(songs);
                albumID = em.merge(albumID);
            }
            List<Playlists> playlistsList = songs.getPlaylistsList();
            for (Playlists playlistsListPlaylists : playlistsList) {
                playlistsListPlaylists.getSongsList().remove(songs);
                playlistsListPlaylists = em.merge(playlistsListPlaylists);
            }
            em.remove(songs);
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

    public List<Songs> findSongsEntities() {
        return findSongsEntities(true, -1, -1);
    }

    public List<Songs> findSongsEntities(int maxResults, int firstResult) {
        return findSongsEntities(false, maxResults, firstResult);
    }

    private List<Songs> findSongsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Songs.class));
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

    public Songs findSongs(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Songs.class, id);
        } finally {
            em.close();
        }
    }

    public int getSongsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Songs> rt = cq.from(Songs.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
