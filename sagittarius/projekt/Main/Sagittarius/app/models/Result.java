/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author johan
 */
public class Result {
    public Result(int pHits, int pTargets){
        hits = pHits;
        targets = pTargets;
    }

    public int hits;
    public int targets;
}