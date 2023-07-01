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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
@Table(name = "albums")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Albums.findAll", query = "SELECT a FROM Albums a")
    , @NamedQuery(name = "Albums.findByAlbumID", query = "SELECT a FROM Albums a WHERE a.albumID = :albumID")
    , @NamedQuery(name = "Albums.findByAlbumName", query = "SELECT a FROM Albums a WHERE a.albumName = :albumName")
    , @NamedQuery(name = "Albums.findByReleaseYear", query = "SELECT a FROM Albums a WHERE a.releaseYear = :releaseYear")})
public class Albums implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "AlbumID")
    private Integer albumID;
    @Size(max = 100)
    @Column(name = "AlbumName")
    private String albumName;
    @Column(name = "ReleaseYear")
    private Integer releaseYear;
    @JoinColumn(name = "ArtistID", referencedColumnName = "ArtistID")
    @ManyToOne
    private Artists artistID;
    @OneToMany(mappedBy = "albumID")
    private List<Songs> songsList;

    public Albums() {
    }

    public Albums(Integer albumID) {
        this.albumID = albumID;
    }

    public Integer getAlbumID() {
        return albumID;
    }

    public void setAlbumID(Integer albumID) {
        this.albumID = albumID;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Artists getArtistID() {
        return artistID;
    }

    public void setArtistID(Artists artistID) {
        this.artistID = artistID;
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
        hash += (albumID != null ? albumID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Albums)) {
            return false;
        }
        Albums other = (Albums) object;
        if ((this.albumID == null && other.albumID != null) || (this.albumID != null && !this.albumID.equals(other.albumID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ispit.juna.jpa.entiteti.Albums[ albumID=" + albumID + " ]";
    }
    
}
