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
@Table(name = "artists")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Artists.findAll", query = "SELECT a FROM Artists a")
    , @NamedQuery(name = "Artists.findByArtistID", query = "SELECT a FROM Artists a WHERE a.artistID = :artistID")
    , @NamedQuery(name = "Artists.findByArtistName", query = "SELECT a FROM Artists a WHERE a.artistName = :artistName")
    , @NamedQuery(name = "Artists.findByUsername", query = "SELECT a FROM Artists a WHERE a.username = :username")
    , @NamedQuery(name = "Artists.findByPassword", query = "SELECT a FROM Artists a WHERE a.password = :password")
    , @NamedQuery(name = "Artists.findByGenre", query = "SELECT a FROM Artists a WHERE a.genre = :genre")
    , @NamedQuery(name = "Artists.findByCountry", query = "SELECT a FROM Artists a WHERE a.country = :country")})
public class Artists implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ArtistID")
    private Integer artistID;
    @Size(max = 100)
    @Column(name = "ArtistName")
    private String artistName;
    @Size(max = 50)
    @Column(name = "Username")
    private String username;
    @Size(max = 50)
    @Column(name = "Password")
    private String password;
    @Size(max = 50)
    @Column(name = "Genre")
    private String genre;
    @Size(max = 50)
    @Column(name = "Country")
    private String country;
    @OneToMany(mappedBy = "artistID")
    private List<Albums> albumsList;

    public Artists() {
    }

    public Artists(Integer artistID) {
        this.artistID = artistID;
    }

    public Integer getArtistID() {
        return artistID;
    }

    public void setArtistID(Integer artistID) {
        this.artistID = artistID;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @XmlTransient
    public List<Albums> getAlbumsList() {
        return albumsList;
    }

    public void setAlbumsList(List<Albums> albumsList) {
        this.albumsList = albumsList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (artistID != null ? artistID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Artists)) {
            return false;
        }
        Artists other = (Artists) object;
        if ((this.artistID == null && other.artistID != null) || (this.artistID != null && !this.artistID.equals(other.artistID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ispit.juna.jpa.entiteti.Artists[ artistID=" + artistID + " ]";
    }
    
}
