/*
* Copyright (C) 2010 ECOSUR, Andrew Waterman and Max Pimm
*
* Licensed under the Academic Free License v. 3.0.
* http://www.opensource.org/licenses/afl-3.0.php
*/

/**
 * GentePlayer extends GridRegistrant to provide several Pente specific methods
 * for use by the Pente rule set.
 * 
 * @author awaterma@ecosur.mx
 */
package mx.ecosur.multigame.impl.entity.gente;

import javax.persistence.*;

import mx.ecosur.multigame.grid.model.BeadString;
import mx.ecosur.multigame.grid.model.GridRegistrant;
import mx.ecosur.multigame.grid.Color;
import mx.ecosur.multigame.grid.model.GridPlayer;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class GentePlayer extends GridPlayer {
        
    private static final long serialVersionUID = -7540174337729169503L;

    private int points;

    private Set<BeadString> trias, tesseras;

    private GentePlayer partner;

    public GentePlayer () {
        super ();
        points = 0;
        trias = new LinkedHashSet<BeadString>();
        tesseras = new LinkedHashSet<BeadString>();
        partner = null;
    }

    /**
     * @param player
     * @param color
     */
    public GentePlayer(GridRegistrant player, Color color) {
        super (player, color);
    }

    @Basic
    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @OneToMany(cascade={CascadeType.ALL}, fetch= FetchType.EAGER)
    @JoinColumn(nullable=true)
    public Set<BeadString> getTrias() {
        return trias;
    }

    public void setTrias(Set<BeadString> trias) {
        this.trias = trias;
    }

    @Transient
    public void addTria (BeadString tria) {
        if (trias == null) {
            trias = new HashSet<BeadString> ();
        }

        if (tria.size() == 3)
            trias.add (tria);
    }

    @OneToMany(cascade={CascadeType.ALL}, fetch= FetchType.EAGER)
    @JoinColumn(nullable=true)
    public Set<BeadString> getTesseras() {
        return tesseras;
    }

    public void setTesseras(Set<BeadString> tesseras) {
        this.tesseras = tesseras;
    }

    @Transient
    public void addTessera (BeadString tessera) {
        if (tesseras == null) {
            tesseras = new LinkedHashSet<BeadString>();
        }

        if (tessera.size() == 4)
            tesseras.add(tessera);
    }

    @OneToOne (cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    public GentePlayer getPartner () {
        return partner;
    }

    public void setPartner (GentePlayer partner) {
        this.partner = partner;
    }


    public boolean containsString (BeadString comparison) {
        boolean ret = false;

        for (BeadString string : getTrias()) {
            if (comparison.contains(string))
                ret = true;
        }

        for (BeadString string : getTesseras()) {
            if (comparison.contains(string))
                ret = true;
        }

        return ret;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        GentePlayer ret = new GentePlayer ();
        ret.setId(getId());
        ret.setPoints(getPoints());
        if (getTesseras() != null) {
            HashSet<BeadString> clones = new HashSet<BeadString>();
            for (BeadString tessera : getTesseras()) {
                BeadString clone = (BeadString) tessera.clone();
                clones.add(clone);
            }
            ret.setTesseras(clones);
        }

        if (getTrias() != null) {
            HashSet<BeadString> clones = new HashSet<BeadString>();
            for (BeadString tria : getTrias()) {
                BeadString clone = (BeadString) tria.clone();
                clones.add(clone);
            }
            ret.setTrias(clones);
        }

        ret.setColor(getColor());

        GridRegistrant clone = (GridRegistrant) getRegistrant().clone();
        ret.setRegistrant(clone);
        ret.setTurn(isTurn());
        ret.setPoints(getPoints());

        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        boolean ret = super.equals(obj);
        if (obj instanceof GentePlayer) {
            GentePlayer comp = (GentePlayer) obj;
            ret = ret && comp.points == this.points;
        }

        return ret;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).append(points).append(tesseras).append(trias).append(getColor()).toHashCode();
    }
}
