/*
* Copyright (C) 2008, 2009 ECOSUR, Andrew Waterman
*
* Licensed under the Academic Free License v. 3.2.
* http://www.opensource.org/licenses/afl-3.0.php
*/

/**
 * @author awaterma@ecosur.mx
 */
package mx.ecosur.multigame.impl.entity.manantiales;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import mx.ecosur.multigame.grid.model.GridPlayer;
import mx.ecosur.multigame.impl.enums.manantiales.Mode;
import mx.ecosur.multigame.impl.enums.manantiales.TokenType;
import mx.ecosur.multigame.grid.model.GridMove;

@Entity
public class ManantialesMove extends GridMove implements Comparable {

    private static final long serialVersionUID = 1L;

    private boolean swap;

    private TokenType type, replacementType;

    private boolean badYear, premium;

    private Mode mode;

    public ManantialesMove () {
        super();
        badYear = false;
        premium = false;
        swap = false;
    }

    public ManantialesMove (GridPlayer player, ManantialesFicha destination) {
        super (player, destination);
    }

    public ManantialesMove (GridPlayer player, ManantialesFicha current, ManantialesFicha destination)
    {
        super (player, current, destination);
    }

    public ManantialesMove(ManantialesPlayer player, ManantialesFicha current, ManantialesFicha destination,
            Boolean swap) {
        super (player, current, destination);
        this.swap = swap;
    }

    public boolean isSwap() {
        return swap;
    }

    public void setSwap(boolean swap) {
        this.swap = swap;
    }

    public TokenType getType () {
        if (getDestinationCell() == null)
            type = TokenType.UNKNOWN;
        else {
            ManantialesFicha destination = (ManantialesFicha) getDestinationCell();
            type = destination.getType();
        }

        return type;
    }

    public void setType (TokenType type) {
        this.type = type;
    }

    public TokenType getReplacementType() {
        if (replacementType == null) {
            replacementType = TokenType.UNKNOWN;
            if (getCurrentCell() instanceof ManantialesFicha) {
                    ManantialesFicha current = (ManantialesFicha) getCurrentCell();
                    replacementType = current.getType();
            }
        }

        return replacementType;
    }
    
    public void setReplacementType(TokenType replacementType) {
        this.replacementType = replacementType;
    }

    public boolean isBadYear () {
        return badYear;
    }

    public void setBadYear (boolean year) {
        badYear = year;
    }

    public boolean isPremium () {
        return premium;
    }

    public void setPremium (boolean premium) {
        this.premium = premium;
    }

    /**
     * @return the mode
     */
    @Enumerated(EnumType.STRING)
    public Mode getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(Mode mode) {
        this.mode = mode;
    }


    @Override
    public String toString() {
       String ret = super.toString();
       return "Mode = " + this.getMode() + ", BadYear=" + this.isBadYear() + ", " + ret; 
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
        if (obj instanceof ManantialesMove) {
            ManantialesMove comparison = (ManantialesMove) obj;
            if (current != null && destination !=null) {
                if (comparison.getCurrentCell() != null && comparison.getDestinationCell() != null) {
                    ret = current.equals( (comparison.getCurrentCell())) &&
                      destination.equals(comparison.getDestinationCell());
                }
            } else if (destination != null) {
                if (comparison.getDestinationCell() != null)
                    ret = destination.equals(comparison.getDestinationCell());
              }
        }

        return ret;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
       throw new CloneNotSupportedException ();
    }

    public int compareTo(Object o) {
        int ret = 0;
        if (o instanceof ManantialesMove) {
            ManantialesMove comparator = (ManantialesMove) o;
            if (comparator.getId() > getId())
                ret = 1;
            else if (comparator.getId() < getId())
                ret = -1;
        }

        return ret;
    }
}
