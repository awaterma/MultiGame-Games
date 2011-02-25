/*
* Copyright (C) 2008 ECOSUR, Andrew Waterman and Max Pimm
* 
* Licensed under the Academic Free License v. 3.0. 
* http://www.opensource.org/licenses/afl-3.0.php
*/

/**
 * GenteMove extends Move to add some Pente specific methods for use by the 
 * Pente/Gente rules.  
 * 
 * @author awaterma@ecosur.mx
 */
package mx.ecosur.multigame.impl.entity.gente;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import javax.persistence.Entity;

import mx.ecosur.multigame.impl.Color;
import mx.ecosur.multigame.impl.model.GridPlayer;
import mx.ecosur.multigame.impl.model.GridCell;
import mx.ecosur.multigame.impl.model.GridMove;

import mx.ecosur.multigame.impl.util.BeadString;

import mx.ecosur.multigame.model.interfaces.GamePlayer;


@Entity
public class GenteMove extends GridMove {
        
        private static final long serialVersionUID = -6635578671376146204L;

        public enum CooperationQualifier {
            COOPERATIVE, SELFISH, NEUTRAL
        }
        
        private HashSet<BeadString> trias, tesseras;

        private CooperationQualifier qualifier = null;
        
        private ArrayList<Color> teamColors;
        
        public GenteMove () {
            super ();
        }
        
        /**
         * @param player
         * @param cell
         */
        public GenteMove(GridPlayer player, GridCell cell) {
                super (player, cell);
        }
        
        public void addTria(BeadString t) {
            boolean contained = false;

            if (trias == null)
                trias = new HashSet<BeadString> ();
            /* Only add trias that are not contained by other trias */
            for (BeadString tria : trias) {
                if (tria.contains(t)) {
                    contained = true;
                    break;
                }
            }

            if (!contained)
                trias.add(t);
        }

        public void addTrias (BeadString... trias) {
            for (BeadString tria : trias) {
                addTria (tria);
            }
        }

        /**
         * Gets the Trias that this move created.
         */
        public HashSet<BeadString> getTrias () {
            if (trias == null)
                trias = new HashSet<BeadString> ();
            return trias;
        }
        
        public void setTrias (HashSet<BeadString> new_trias) {
            trias = new_trias;
        }
        
        public void addTessera (BeadString t) {
            boolean contained = false;
                
            if (tesseras == null)
                tesseras = new HashSet<BeadString> ();
            for (BeadString tessera : tesseras) {
                if (tessera.contains(t)) {
                    contained = true;
                    break;
                }
                                
            }
                
            if (!contained)
                tesseras.add(t);
        }

        public void addTesseras (BeadString... tesseras) {
            for (BeadString tessera : tesseras) {
                addTessera (tessera);
            }
        }
    
        public ArrayList<Color> getTeamColors () {
                if (teamColors == null) {
                        teamColors = new ArrayList<Color>();
                        GentePlayer player = (GentePlayer) this.player;
            teamColors.add (player.getPartner().getColor());
                    teamColors.add(this.player.getColor());
                }
                
                return teamColors;
        }

    public void setTeamColors (ArrayList<Color> colors) {
        teamColors = colors;
    }
        
        /**
         * Gets the Tesseras that this move created.  
         */
        public HashSet<BeadString> getTesseras () {
                if (tesseras == null)
                        tesseras = new HashSet<BeadString> ();          
                return tesseras;
        }
        
        public void setTesseras (HashSet<BeadString> new_tesseras) {
                tesseras = new_tesseras;
        }
        
        /**
         * Gets the qualifier
         * 
         * @return the qualifier
         */
        public String getQualifier() {
        if (qualifier == null)
            qualifier = CooperationQualifier.NEUTRAL;
                return qualifier.name();
        }

        /**
         * Sets the cooperation qualifier
         * 
         * @param qualifier
         *            the cooperation qualifier
         */
        public void setQualifier(String qualifier) {
        if (qualifier != null)
                    this.qualifier = CooperationQualifier.valueOf(qualifier);
        }

        /* (non-Javadoc)
         * @see mx.ecosur.multigame.model.interfaces.Move#setPlayer(mx.ecosur.multigame.model.interfaces.Agent)
         */
        public void setPlayer(GamePlayer player) {
                this.player = (GentePlayer) player;
        }

        /* (non-Javadoc)
         * @see mx.ecosur.multigame.impl.model.GridMove#toString()
         */
        @Override
        public String toString() {
            StringBuffer ret = new StringBuffer();
            ret.append("GenteMove: ");
            ret.append("Player=" + getPlayer() + ", ");
            ret.append("current=" + getCurrentCell() + ", ");
            ret.append("destination=" + getDestinationCell() + ", ");
            ret.append("qualifier=" + getQualifier() + ", ");
            ret.append("trias=" + getTrias().size() + ", ");
            ret.append("tesseras=" + getTesseras().size() + ", ");
            return ret.toString();
        }

@Override
    public int hashCode() {
       int curCode = 1, destCode = 1;
       if (current != null)
        curCode = curCode - current.hashCode();
       if (destination != null)
         destCode = destCode + destination.hashCode();
       return 31 * curCode + destCode;
    }

    @Override
    public boolean equals(Object obj) {
        boolean ret = false;
        if (obj instanceof GenteMove) {
            GenteMove comparison = (GenteMove) obj;
            if (current != null && destination !=null) {
                ret = current.equals( (comparison.getCurrentCell())) &&
                      destination.equals(comparison.getDestinationCell());
            } else if (destination != null) {
                ret = destination.equals(comparison.getDestinationCell());
              }
        }

        return ret;
    }

    @Override
    protected Object clone() {
       GenteMove ret = new GenteMove ();
        try {
            if (current != null)
                ret.current = current.clone();
            if (destination != null)
                ret.destination = destination.clone();
            GentePlayer p = (GentePlayer) this.player;
            ret.player = (GridPlayer) p.clone();
            ret.qualifier = this.qualifier;
            ret.teamColors = new ArrayList<Color>();            
            ret.tesseras = new LinkedHashSet<BeadString>();
            for (BeadString string : getTesseras()) {
                ret.tesseras.add((BeadString) string.clone());
            }

            ret.trias = new LinkedHashSet<BeadString>();
            for (BeadString string : getTrias()) {
                ret.trias.add((BeadString) string.clone());
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();  
        }

        return ret;
    }
}

