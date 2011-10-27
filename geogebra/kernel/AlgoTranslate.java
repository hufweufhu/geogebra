/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoTranslatePoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;
import geogebra.kernel.Matrix.Coords;
import geogebra.main.Application;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoTranslate extends AlgoTransformation {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Translateable out;   
    private GeoElement inGeo, outGeo;
    protected GeoElement v;  // input      
    
            
    /**
     * Creates unlabeled translation algo
     * @param cons
     * @param in
     * @param v
     */
    public AlgoTranslate(Construction cons, GeoElement in, GeoElement v) {
        super(cons);        
        this.v = v;
        
        inGeo = in;
                
        // create out
        if(inGeo instanceof GeoPolyLineInterface || inGeo.isLimitedPath()){
        	
	        outGeo = copyInternal(cons, inGeo);
	        out = (Translateable) outGeo;
        }
        else if(in.isGeoList()){
        	outGeo = new GeoList(cons);
        }else {
        	outGeo = copy(inGeo);
        	out = (Translateable) outGeo;
        }
        
        setInputOutput();               
        compute();               
    } 
    
    protected GeoElement copy(GeoElement geo){
    	return geo.copy();
    }
    
    protected GeoElement copyInternal(Construction cons, GeoElement geo){
    	return geo.copyInternal(cons);
    }
    
    public String getClassName() {
        return "AlgoTranslate";
    }
    
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_TRANSLATE_BY_VECTOR;
    }

    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = inGeo;        
        input[1] = v;        
        
        setOutputLength(1);        
        setOutput(0,outGeo);        
        setDependencies(); // done by AlgoElement
    }           
        
    GeoElement getResult() { return outGeo; }
        
    // calc translated point
    protected final void compute() {
    	if(inGeo.isGeoList()){
    		transformList((GeoList)inGeo,(GeoList)outGeo);
    		return;
    	}
        outGeo.set(inGeo);
        out.translate(getVectorCoords());
        if(inGeo.isLimitedPath())
        	this.transformLimitedPath(inGeo, outGeo);
    }   
    
    protected Coords getVectorCoords(){
    	GeoVec3D vec = (GeoVec3D) v;
    	return new Coords(vec.x,vec.y,vec.z);
    }

    
    final public String toString() {

        // Michael Borcherds 2008-03-24 simplified code!
        return app.getPlain("TranslationOfAbyB",inGeo.getLabel(),v.getLabel());
    }

	@Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
		if(!(outGeo instanceof GeoList))
			out = (Translateable)outGeo;
		
	}
}
