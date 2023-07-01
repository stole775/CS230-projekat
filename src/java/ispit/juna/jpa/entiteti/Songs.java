/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ispit.juna.jpa.entiteti;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Mladen
 */
@Entity
@Table(name = "songs")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Songs.findAll", query = "SELECT s FROM Songs s")
    , @NamedQuery(name = "Songs.findBySongID", query = "SELECT s FROM Songs s WHERE s.songID = :songID")
    , @NamedQuery(name = "Songs.findBySongName", query = "SELECT s FROM Songs s WHERE s.songName = :songName")
    , @NamedQuery(name = "Songs.findByDurationInSeconds", query = "SELECT s FROM Songs s WHERE s.durationInSeconds = :durationInSeconds")})
public class Songs implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "SongID")
    private Integer songID;
    @Size(max = 100)
    @Column(name = "SongName")
    private String songName;
    @Column(name = "DurationInSeconds")
    private Integer durationInSeconds;
    @ManyToMany(mappedBy = "songsList")
    private List<Playlists> playlistsList;
    @JoinColumn(name = "AlbumID", referencedColumnName = "AlbumID")
    @ManyToOne
    private Albums albumID;

    public Songs() {
    }

    public Songs(Integer songID) {
        this.songID = songID;
    }

    public Integer getSongID() {
        return songID;
    }

    public void setSongID(Integer songID) {
        this.songID = songID;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public Integer getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(Integer durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    @XmlTransient
    public List<Playlists> getPlaylistsList() {
        return playlistsList;
    }

    public void setPlaylistsList(List<Playlists> playlistsList) {
        this.playlistsList = playlistsList;
    }

    public Albums getAlbumID() {
        return albumID;
    }

    public void setAlbumID(Albums albumID) {
        this.albumID = albumID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (songID != null ? songID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Songs)) {
            return false;
        }
        Songs other = (Songs) object;
        if ((this.songID == null && other.songID != null) || (this.songID != null && !this.songID.equals(other.songID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ispit.juna.jpa.entiteti.Songs[ songID=" + songID + " ]";
    }
    
}
