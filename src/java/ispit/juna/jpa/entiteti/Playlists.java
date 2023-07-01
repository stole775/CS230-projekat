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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "playlists")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Playlists.findAll", query = "SELECT p FROM Playlists p")
    , @NamedQuery(name = "Playlists.findByPlaylistID", query = "SELECT p FROM Playlists p WHERE p.playlistID = :playlistID")
    , @NamedQuery(name = "Playlists.findByPlaylistName", query = "SELECT p FROM Playlists p WHERE p.playlistName = :playlistName")
    , @NamedQuery(name = "Playlists.findByDescription", query = "SELECT p FROM Playlists p WHERE p.description = :description")})
public class Playlists implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "PlaylistID")
    private Integer playlistID;
    @Size(max = 100)
    @Column(name = "PlaylistName")
    private String playlistName;
    @Size(max = 255)
    @Column(name = "Description")
    private String description;
    @JoinTable(name = "playlistsongs", joinColumns = {
        @JoinColumn(name = "PlaylistID", referencedColumnName = "PlaylistID")}, inverseJoinColumns = {
        @JoinColumn(name = "SongID", referencedColumnName = "SongID")})
    @ManyToMany
    private List<Songs> songsList;

    public Playlists() {
    }

    public Playlists(Integer playlistID) {
        this.playlistID = playlistID;
    }

    public Integer getPlaylistID() {
        return playlistID;
    }

    public void setPlaylistID(Integer playlistID) {
        this.playlistID = playlistID;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<Songs> getSongsList() {
        return songsList;
    }

    public void setSongsList(List<Songs> songsList) {
        this.songsList = songsList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (playlistID != null ? playlistID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Playlists)) {
            return false;
        }
        Playlists other = (Playlists) object;
        if ((this.playlistID == null && other.playlistID != null) || (this.playlistID != null && !this.playlistID.equals(other.playlistID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ispit.juna.jpa.entiteti.Playlists[ playlistID=" + playlistID + " ]";
    }
    
}
